package in.nishant.auctionportaladmin.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import in.nishant.auctionportaladmin.R;
import in.nishant.auctionportaladmin.fragment.PreviousAuctionFragment;
import in.nishant.auctionportaladmin.model.ProductsModel;

public class WinnerListActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DatabaseReference rootRef;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private String highestBidUser, userId, uId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winner_list);

        initComponents();
    }

    private void initComponents() {
        //toolbar
        toolbar = findViewById(R.id.winnerList_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Winner List");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.winnerList_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(WinnerListActivity.this));

        rootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<ProductsModel, WinnerListActivity.WinnerListViewHolder> adapter = new FirebaseRecyclerAdapter<ProductsModel, WinnerListActivity.WinnerListViewHolder>(
                ProductsModel.class,
                R.layout.layout_current_auction,
                WinnerListActivity.WinnerListViewHolder.class,
                rootRef.child("Products").child("Previous auction")
        ) {
            @Override
            protected void populateViewHolder(WinnerListActivity.WinnerListViewHolder holder, ProductsModel productsModel, final int position) {
                userId = getRef(position).getKey();

                holder.setAuction(productsModel.getProductName(),
                        productsModel.getMinimalPrice(),
                        productsModel.getHighestBidUser(),
                        productsModel.getProductImage(),
                        productsModel.getHighestBid());

            }
        };
        recyclerView.setAdapter(adapter);
    }

    public static class WinnerListViewHolder extends RecyclerView.ViewHolder {

        View view;

        public WinnerListViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }

        private void setAuction(String productName, String minimalPrice, String highestBidUser, String productImage, String highestBid) {
            TextView txtProductName = view.findViewById(R.id.layoutCurrentAuction_txtProductName);
            TextView txtHighestBid = view.findViewById(R.id.layoutCurrentAuction_txtHighestBid);
            TextView txtMinimalBid = view.findViewById(R.id.layoutCurrentAuction_txtMinimalBid);
            TextView txtDateTime = view.findViewById(R.id.layoutCurrentAuction_txtDateTime);
            ImageView imgProductImage = view.findViewById(R.id.layoutCurrentAuction_imgProductImage);

            txtProductName.setText(productName);
            if (!highestBid.equals("No bid yet")) txtHighestBid.setText(("Highest bid " + highestBid));
            txtMinimalBid.setText(("Minimal price " + minimalPrice));
            if(!highestBidUser.equals("none")) txtDateTime.setText(("Winner : "+highestBidUser));
            else txtDateTime.setText("No Winner");
            Glide.with(view).load(productImage).placeholder(R.drawable.default_image).into(imgProductImage);
        }
    }
}
