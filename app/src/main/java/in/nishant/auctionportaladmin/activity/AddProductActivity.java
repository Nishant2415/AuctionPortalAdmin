package in.nishant.auctionportaladmin.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import id.zelory.compressor.Compressor;
import in.nishant.auctionportaladmin.R;
import in.nishant.auctionportaladmin.utils.CustomToast;

public class AddProductActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private EditText edtProductName,edtMinimalPrice,edtDescription, edtEndDate, edtEndTime;
    private Button btnAddProduct;
    private ImageView imgProductImage;
    private SimpleDateFormat sdf;
    private Calendar calendar = Calendar.getInstance();
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    private boolean isImage = false;
    private DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        initComponents();

        setListeners();
    }

    private void initComponents() {
        edtProductName = findViewById(R.id.addProduct_edtProductName);
        edtMinimalPrice = findViewById(R.id.addProduct_edtMinimalPrice);
        edtDescription = findViewById(R.id.addProduct_edtDescription);
        edtEndDate = findViewById(R.id.addProduct_edtEndDate);
        edtEndTime = findViewById(R.id.addProduct_edtEndTime);
        btnAddProduct = findViewById(R.id.addProduct_btnAddProduct);
        imgProductImage = findViewById(R.id.addProduct_imgProductImage);

        // Toolbar
        toolbar = findViewById(R.id.addProduct_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sell Product");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Fire base
        mStorageRef = FirebaseStorage.getInstance().getReference();
        rootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    private void setListeners() {
        edtEndDate.setOnClickListener(AddProductActivity.this);
        edtEndTime.setOnClickListener(AddProductActivity.this);
        btnAddProduct.setOnClickListener(AddProductActivity.this);
        imgProductImage.setOnClickListener(AddProductActivity.this);
    }

    @Override
    public void onClick(View v) {
        if(v==edtEndDate){
            int calYear = calendar.get(Calendar.YEAR);
            int calMonth = calendar.get(Calendar.MONTH);
            int calDay = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dpd =new DatePickerDialog(AddProductActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    calendar.set(year,month,dayOfMonth);
                    sdf = new SimpleDateFormat("dd MMM yyyy",Locale.US);
                    edtEndDate.setText(sdf.format(calendar.getTime()));
                }
            },calYear,calMonth,calDay);
            dpd.getDatePicker().setMinDate(calendar.getTimeInMillis());
            dpd.show();

        } else if(v==edtEndTime){
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            new TimePickerDialog(AddProductActivity.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                    calendar.set(Calendar.MINUTE,minute);
                    sdf = new SimpleDateFormat("h:mm a",Locale.US);
                    edtEndTime.setText(sdf.format(calendar.getTime()));
                }
            },hour,minute,false).show();
        } else if(v==btnAddProduct){
            if ( !isImage |
                    edtProductName.getText().toString().trim().isEmpty() |
                    edtMinimalPrice.getText().toString().trim().isEmpty() |
                    edtDescription.getText().toString().trim().isEmpty() |
                    edtEndDate.getText().toString().isEmpty() |
                    edtEndDate.getText().toString().isEmpty()){
                CustomToast.show(AddProductActivity.this,0,"Please enter product details!");
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddProductActivity.this);
                builder.setCancelable(false)
                        .setMessage("Are you sure you want to sell this product?")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final ProgressDialog pd = new ProgressDialog(AddProductActivity.this,R.style.DialogTheme);
                                pd.setCanceledOnTouchOutside(false);
                                pd.setMessage("Please wait");
                                pd.show();

                                final Map<String,Object> productMap = new HashMap<>();
                                productMap.put("productName",edtProductName.getText().toString().trim());
                                productMap.put("minimalPrice",edtMinimalPrice.getText().toString().trim());
                                productMap.put("description",edtDescription.getText().toString().trim());
                                productMap.put("highestBidUser","none");
                                productMap.put("endDate",edtEndDate.getText().toString());
                                productMap.put("endTime",edtEndTime.getText().toString());
                                productMap.put("highestBid","No bid yet");

                                rootRef.child("Products").child("My auction").child(mAuth.getCurrentUser().getUid()).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){

                                            rootRef.child("Products").child("Current auction").child(mAuth.getCurrentUser().getUid()).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        pd.dismiss();
                                                        CustomToast.show(AddProductActivity.this,1,"Product added!");
                                                        startActivity(new Intent(AddProductActivity.this,MainActivity.class));
                                                        finish();
                                                    } else {
                                                        pd.dismiss();
                                                        CustomToast.show(AddProductActivity.this,0,"Something wrong!");
                                                        Log.d("Auction portal",task.getException().toString());
                                                    }
                                                }
                                            });
                                        } else {
                                            pd.dismiss();
                                            CustomToast.show(AddProductActivity.this,0,"Something wrong!");
                                            Log.d("Auction portal",task.getException().toString());
                                        }
                                    }
                                });
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
            }
        } else if (v==imgProductImage) {
            CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(AddProductActivity.this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK) {
            final ProgressDialog pd = new ProgressDialog(AddProductActivity.this,R.style.DialogTheme);
            pd.setCanceledOnTouchOutside(false);
            pd.setMessage("Uploading image");
            pd.show();
            sdf = new SimpleDateFormat("ddMMyyyyhhmm",Locale.US);

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Uri imageUri = result.getUri();

            // Compress image
            byte[] imageByte = null;

            try {
                File imageFile = new File(imageUri.getPath());
                Bitmap imageBitmap = new Compressor(AddProductActivity.this)
                        .setMaxWidth(200)
                        .setMaxHeight(200)
                        .setQuality(90)
                        .compressToBitmap(imageFile);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
                imageByte = baos.toByteArray();

            } catch (IOException e) {
                e.printStackTrace();
            }

            // Adding image to storage
            final StorageReference productRef = mStorageRef.child("ProductImages").child("IMG"+sdf.format(new Date())+".jpg");
            productRef.putBytes(imageByte).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        productRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(final Uri uri) {
                                // Adding image to database -> my auction
                                rootRef.child("Products").child("My auction").child(mAuth.getCurrentUser().getUid()).child("productImage").setValue(uri.toString())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    // Adding image to database -> current auction
                                                    rootRef.child("Products").child("Current auction").child(mAuth.getCurrentUser().getUid()).child("productImage").setValue(uri.toString());
                                                    // Showing image to image view
                                                    rootRef.child("Products").child("My auction").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            if(dataSnapshot.hasChildren()){
                                                                String imageUrl = dataSnapshot.child("productImage").getValue(String.class);
                                                                Glide.with(getApplicationContext())
                                                                        .load(imageUrl)
                                                                        .placeholder(R.drawable.default_image)
                                                                        .into(imgProductImage);
                                                                isImage = true;
                                                                pd.dismiss();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                                            CustomToast.show(AddProductActivity.this, 1, "Something wrong!");
                                                        }
                                                    });
                                                } else {
                                                    pd.dismiss();
                                                    CustomToast.show(AddProductActivity.this, 1, "Something wrong!");
                                                    Log.d("Auction portal",task.getException().toString());
                                                }
                                            }
                                        });
                            }
                        });
                    } else {
                        pd.dismiss();
                        CustomToast.show(AddProductActivity.this, 1, "Something wrong!");
                        Log.d("Auction Portal",task.getException().toString());
                    }
                }
            });
        }
    }
}
