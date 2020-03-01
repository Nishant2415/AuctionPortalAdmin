package in.nishant.auctionportaladmin.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import in.nishant.auctionportaladmin.R;
import in.nishant.auctionportaladmin.utils.CustomToast;

public class ProductDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView txtProductName, txtMinimalPrice, txtEndDate, txtEndTime, txtHighestBid, txtDescription;
    private ImageView imgProductImage;
    private DatabaseReference rootRef;
    private String userId;
    private FirebaseAuth mAuth;
    private Button btnBid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        initComponents();

        btnBid.setOnClickListener(ProductDetailActivity.this);
    }

    private void initComponents() {
        // Toolbar
        Toolbar toolbar = findViewById(R.id.productDetail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Product Detail");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtProductName = findViewById(R.id.productDetail_txtProductName);
        txtMinimalPrice = findViewById(R.id.productDetail_txtMinimalPrice);
        txtDescription = findViewById(R.id.productDetail_txtDescription);
        txtEndDate = findViewById(R.id.productDetail_txtEndDate);
        txtEndTime = findViewById(R.id.productDetail_txtEndTime);
        txtHighestBid = findViewById(R.id.productDetail_txtHighestBid);
        btnBid = findViewById(R.id.productDetail_btnBid);
        imgProductImage = findViewById(R.id.productDetail_imgProductImage);

        rootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        userId = getIntent().getStringExtra("userId");
    }

    @Override
    protected void onStart() {
        super.onStart();

        rootRef.child("Products").child("Current auction").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()){
                    txtProductName.setText(dataSnapshot.child("productName").getValue(String.class));
                    txtMinimalPrice.setText("Minimal price : "+dataSnapshot.child("minimalPrice").getValue(String.class));
                    txtDescription.setText("Description : "+dataSnapshot.child("description").getValue(String.class));
                    txtEndDate.setText("End date : "+dataSnapshot.child("endDate").getValue(String.class));
                    txtEndTime.setText("End time : "+dataSnapshot.child("endTime").getValue(String.class));
                    txtHighestBid.setText("Highest bid : "+dataSnapshot.child("highestBid").getValue(String.class));
                    Glide.with(getApplicationContext())
                            .load(dataSnapshot.child("productImage").getValue(String.class))
                            .placeholder(R.drawable.default_image)
                            .into(imgProductImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v==btnBid){
            AlertDialog.Builder builder = new AlertDialog.Builder(ProductDetailActivity.this);
            // Adding edit text to alert dialog
            LinearLayout linearLayout = new LinearLayout(ProductDetailActivity.this);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setPadding(40,0,40,0);
            final TextInputEditText txtBidAmount = new TextInputEditText(ProductDetailActivity.this);
            txtBidAmount.setInputType(InputType.TYPE_CLASS_NUMBER);
            txtBidAmount.setFilters(new InputFilter[] {new InputFilter.LengthFilter(6)});
            linearLayout.addView(txtBidAmount);

            builder.setTitle("Enter bid amount")
                    .setView(linearLayout)
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (txtBidAmount.getText().toString().isEmpty()) {
                                CustomToast.show(ProductDetailActivity.this, 0, "Please enter bid amount");
                            } else {
                                final Map<String, Object> bidMap = new HashMap<>();
                                bidMap.put("highestBid", txtBidAmount.getText().toString().trim());

                                rootRef.child("Users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        bidMap.put("highestBidUser",dataSnapshot.child("username").getValue(String.class));
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                rootRef.child("Products").child("Current auction").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(!dataSnapshot.child("highestBid").getValue(String.class).equals("No bid yet")){
                                            if (Integer.parseInt(txtBidAmount.getText().toString().trim()) <= Integer.parseInt(dataSnapshot.child("highestBid").getValue(String.class))) {
                                                CustomToast.show(ProductDetailActivity.this, 0, "Please enter minimal bid amount!");
                                            } else {
                                                rootRef.child("Products").child("Current auction").child(userId).updateChildren(bidMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            rootRef.child("Products").child("My auction").child(userId).updateChildren(bidMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        CustomToast.show(ProductDetailActivity.this, 1, "Bid successful!");
                                                                    } else {
                                                                        CustomToast.show(ProductDetailActivity.this, 0, "Something wrong!");
                                                                        Log.d("Auction portal", task.getException().toString());
                                                                    }
                                                                }
                                                            });
                                                        } else {
                                                            CustomToast.show(ProductDetailActivity.this, 0, "Something wrong!");
                                                            Log.d("Auction portal", task.getException().toString());
                                                        }
                                                    }
                                                });
                                            }
                                        } else {
                                            if (Integer.parseInt(txtBidAmount.getText().toString().trim()) <= Integer.parseInt(dataSnapshot.child("minimalPrice").getValue(String.class))) {
                                                CustomToast.show(ProductDetailActivity.this, 0, "Please enter minimal bid amount!");
                                            } else {
                                                rootRef.child("Products").child("Current auction").child(userId).updateChildren(bidMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            rootRef.child("Products").child("My auction").child(userId).updateChildren(bidMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        CustomToast.show(ProductDetailActivity.this, 1, "Bid successful!");
                                                                    } else {
                                                                        CustomToast.show(ProductDetailActivity.this, 0, "Something wrong!");
                                                                        Log.d("Auction portal", task.getException().toString());
                                                                    }
                                                                }
                                                            });
                                                        } else {
                                                            CustomToast.show(ProductDetailActivity.this, 0, "Something wrong!");
                                                            Log.d("Auction portal", task.getException().toString());
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).show();
        }
    }
}
