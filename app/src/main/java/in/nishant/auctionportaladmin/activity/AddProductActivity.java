package in.nishant.auctionportaladmin.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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
    private TextInputEditText edtProductName,edtMinimalPrice,edtDescription;
    private Button btnEndDate,btnAddProduct,btnEndTime;
    private ImageView imgProductImage;
    private String mEndDate,mEndTime;
    private SimpleDateFormat sdf;
    private Calendar calendar = Calendar.getInstance();
    private ProgressDialog pd;
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
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
        btnEndDate = findViewById(R.id.addProduct_btnEndDate);
        btnEndTime = findViewById(R.id.addProduct_btnEndTime);
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

    private void showProgressDialog(String msg) {
        pd = new ProgressDialog(AddProductActivity.this,R.style.DialogTheme);
        pd.setMessage(msg);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
    }

    private void setListeners() {
        btnEndDate.setOnClickListener(AddProductActivity.this);
        btnEndTime.setOnClickListener(AddProductActivity.this);
        btnAddProduct.setOnClickListener(AddProductActivity.this);
        imgProductImage.setOnClickListener(AddProductActivity.this);
    }

    @Override
    public void onClick(View v) {
        if(v==btnEndDate){
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dpd = new DatePickerDialog(AddProductActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    mEndDate = dayOfMonth+"-"+(month+1)+"-"+year;
                    Toast.makeText(AddProductActivity.this, mEndDate, Toast.LENGTH_SHORT).show();
                }
            },year,month,day);
            dpd.show();
        } else if(v==btnEndTime){
            final int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            TimePickerDialog tpd = new TimePickerDialog(AddProductActivity.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                    calendar.set(Calendar.MINUTE,minute);
                    sdf = new SimpleDateFormat("h:mm a",Locale.US);
                    mEndTime = sdf.format(calendar.getTime());
                    Toast.makeText(AddProductActivity.this, mEndTime, Toast.LENGTH_SHORT).show();
                }
            },hour,minute,false);
            tpd.show();
        } else if(v==btnAddProduct){
            if(edtProductName.getText().toString().trim().equals("") |
                    edtMinimalPrice.getText().toString().trim().equals("") |
                    edtDescription.getText().toString().trim().equals("") |
                    TextUtils.isEmpty(mEndDate) |
                    TextUtils.isEmpty(mEndTime)){
                CustomToast.show(AddProductActivity.this,0,"Please enter product details!");
            } else {
                showProgressDialog("Please wait");
                Map<String,Object> productMap = new HashMap<>();
                productMap.put("productName",edtProductName.getText().toString().trim());
                productMap.put("minimalPrice",edtMinimalPrice.getText().toString().trim());
                productMap.put("description",edtDescription.getText().toString().trim());
                productMap.put("endDate",mEndDate);
                productMap.put("endTime",mEndTime);

                rootRef.child("Products").child(mAuth.getCurrentUser().getUid()).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            CustomToast.show(AddProductActivity.this,1,"Product added!");
                        } else {
                            CustomToast.show(AddProductActivity.this,0,"Something wrong!");
                            Log.d("Auction portal",task.getException().toString());
                        }
                        pd.hide();
                    }
                });
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
            sdf = new SimpleDateFormat("ddMMyyyyhhmm",Locale.US);

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Uri imageUri = result.getUri();

            showProgressDialog("Uploading image");

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

            final StorageReference productRef = mStorageRef.child("ProductImages").child("IMG"+sdf.format(new Date())+".jpg");
            productRef.putBytes(imageByte).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        productRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                rootRef.child("Products").child(mAuth.getCurrentUser().getUid()).child("productImage").setValue(uri.toString())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    rootRef.child("Products").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            if(dataSnapshot.hasChildren()){
                                                                String imageUrl = dataSnapshot.child("productImage").getValue(String.class);
                                                                Glide.with(AddProductActivity.this)
                                                                        .load(imageUrl)
                                                                        .placeholder(R.drawable.default_image)
                                                                        .into(imgProductImage);
                                                                pd.hide();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                                            CustomToast.show(AddProductActivity.this, 1, "Something wrong!");
                                                        }
                                                    });
                                                } else {
                                                    CustomToast.show(AddProductActivity.this, 1, "Something wrong!");
                                                    Log.d("Auction portal",task.getException().toString());
                                                }
                                            }
                                        });
                            }
                        });
                    } else {
                        CustomToast.show(AddProductActivity.this, 1, "Something wrong!");
                        Log.d("Auction Portal",task.getException().toString());
                    }
                }
            });
        }
    }
}
