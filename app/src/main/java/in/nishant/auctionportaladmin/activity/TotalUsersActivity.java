package in.nishant.auctionportaladmin.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import in.nishant.auctionportaladmin.R;
import in.nishant.auctionportaladmin.model.TotalUsers;

public class TotalUsersActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private TotalUsers totalUsers;
    private ProgressDialog pd;
    private DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_users);

        initComponents();
    }

    private void initComponents() {
        // Toolbar
        toolbar = findViewById(R.id.totalUsers_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Total Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Progress dialog
        pd = new ProgressDialog(TotalUsersActivity.this,R.style.DialogTheme);
        pd.setMessage("Please wait");
        pd.setCanceledOnTouchOutside(false);
        //pd.show();

        //Initializing recycler view
        recyclerView = findViewById(R.id.totalUsers_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(TotalUsersActivity.this));

        totalUsers = new TotalUsers();

        //Fire base variable
        rootRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Fire base Recycler view
        FirebaseRecyclerAdapter<TotalUsers,UserViewHolder> adapter = new FirebaseRecyclerAdapter<TotalUsers, UserViewHolder>(
                TotalUsers.class,
                R.layout.layout_total_users,
                UserViewHolder.class,
                rootRef.child("Users")
        ) {
            @Override
            protected void populateViewHolder(UserViewHolder holder, TotalUsers totalUsers, int position) {
                holder.setAll(totalUsers.getUsername(),totalUsers.getMobileNo());
            }
        };
        recyclerView.setAdapter(adapter);
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView txtUsername, txtMobileNo;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }

        private void setAll(String username, String mobileNo) {
            txtUsername = view.findViewById(R.id.totalUser_txtUsername);
            txtMobileNo = view.findViewById(R.id.totalUser_txtMobileNo);
            txtUsername.setText(username);
            txtMobileNo.setText(mobileNo);
        }
    }
}
