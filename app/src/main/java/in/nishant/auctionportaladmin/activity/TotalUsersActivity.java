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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import in.nishant.auctionportaladmin.R;
import in.nishant.auctionportaladmin.model.TotalUsersModel;

public class TotalUsersActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private TotalUsersModel totalUsersModel;
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

        //Initializing recycler view
        recyclerView = findViewById(R.id.totalUsers_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(TotalUsersActivity.this));

        totalUsersModel = new TotalUsersModel();

        //Fire base variable
        rootRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Fire base Recycler view
        FirebaseRecyclerAdapter<TotalUsersModel,UserViewHolder> adapter = new FirebaseRecyclerAdapter<TotalUsersModel, UserViewHolder>(
                TotalUsersModel.class,
                R.layout.layout_total_users,
                UserViewHolder.class,
                rootRef.child("Users")
        ) {
            @Override
            protected void populateViewHolder(UserViewHolder holder, TotalUsersModel totalUsers, int position) {
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
