package in.nishant.auctionportaladmin.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import in.nishant.auctionportaladmin.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private CardView cardTotalUsers,cardAuctionList,cardWinnerList;
    private DatabaseReference rootRef;
    private ProgressDialog pd;
    private TextView txtHelloUser;
    private FloatingActionButton fabAddProduct;
    private boolean isAdmin = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();

        setListeners();
    }

    private void initComponents() {
        // Toolbar
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        cardTotalUsers = findViewById(R.id.main_cardTotalUsers);
        cardAuctionList = findViewById(R.id.main_cardAuctionList);
        cardWinnerList = findViewById(R.id.main_cardWinnerList);
        fabAddProduct = findViewById(R.id.main_fabAddProduct);
        txtHelloUser = findViewById(R.id.main_txtHelloUser);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Auction Portal");

        pd = new ProgressDialog(MainActivity.this,R.style.DialogTheme);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("Please wait");

        // Fire base
        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();
    }

    private void setListeners() {
        cardTotalUsers.setOnClickListener(MainActivity.this);
        cardAuctionList.setOnClickListener(MainActivity.this);
        cardWinnerList.setOnClickListener(MainActivity.this);
        fabAddProduct.setOnClickListener(MainActivity.this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser()==null){
            startActivity(new Intent(MainActivity.this,LoginActivity.class));
            finish();
        } else {
            pd.show();
            rootRef.child("Users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChildren()) {
                        Iterable<DataSnapshot> userSnapShot = dataSnapshot.getChildren();
                        for (DataSnapshot userKey : userSnapShot) {
                            String userId = userKey.getKey();
                            if (mAuth.getCurrentUser().getUid().equals(userId)) {
                                cardTotalUsers.setVisibility(View.INVISIBLE);
                                fabAddProduct.setVisibility(View.VISIBLE);
                                txtHelloUser.setText("Hello, " + dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("username").getValue(String.class));
                                isAdmin = false;
                            }
                        }
                        if (isAdmin) {
                            rootRef.child("Admin").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    txtHelloUser.setText("Hello, "+dataSnapshot.child("username").getValue(String.class));
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                        pd.dismiss();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.menu_logout){
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this,LoginActivity.class));
            finish();
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if(v==cardTotalUsers){
            startActivity(new Intent(MainActivity.this,TotalUsersActivity.class));
        } else if (v==cardAuctionList) {
            startActivity(new Intent(MainActivity.this,AuctionListActivity.class).putExtra("isAdmin",isAdmin));
        } else if (v==cardWinnerList) {
            startActivity(new Intent(MainActivity.this,WinnerListActivity.class));
        } else  if (v==fabAddProduct){
            startActivity(new Intent(MainActivity.this,AddProductActivity.class));
            finish();
        }
    }
}
