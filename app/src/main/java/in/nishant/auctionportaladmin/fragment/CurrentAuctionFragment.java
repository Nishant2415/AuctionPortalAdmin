package in.nishant.auctionportaladmin.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import in.nishant.auctionportaladmin.R;
import in.nishant.auctionportaladmin.activity.ProductDetailActivity;
import in.nishant.auctionportaladmin.model.ProductsModel;

public class CurrentAuctionFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private DatabaseReference rootRef;
    private FirebaseAuth mAuth;

    public CurrentAuctionFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_current_auction, container, false);
        initComponents(view);
        return view;
    }

    private void initComponents(View view) {
        rootRef = FirebaseDatabase.getInstance().getReference();
        recyclerView = view.findViewById(R.id.currentAF_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<ProductsModel,CurrentAuctionViewHolder> adapter = new FirebaseRecyclerAdapter<ProductsModel, CurrentAuctionViewHolder>(
                ProductsModel.class,
                R.layout.layout_current_auction,
                CurrentAuctionViewHolder.class,
                rootRef.child("Products").child("Current auction")
        ) {
            @Override
            protected void populateViewHolder(CurrentAuctionViewHolder holder, ProductsModel productsModel, final int position) {
                final String userId = getRef(position).getKey();

                if(userId.equals(mAuth.getCurrentUser().getUid())){
                    holder.view.setLayoutParams(new RecyclerView.LayoutParams(0,0));
                }

                holder.setAuction(productsModel.getProductName(),
                        productsModel.getMinimalPrice(),
                        productsModel.getEndDate(),
                        productsModel.getEndTime(),
                        productsModel.getProductImage(),
                        productsModel.getHighestBid());

                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getActivity(),ProductDetailActivity.class).putExtra("userId",userId));
                    }
                });

            }
        };
        recyclerView.setAdapter(adapter);
    }
    public static class CurrentAuctionViewHolder extends RecyclerView.ViewHolder{

        View view;

        public CurrentAuctionViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }

        private void setAuction(String productName, String minimalPrice, String endDate, String endTime, String productImage, String highestBid) {
            TextView txtProductName = view.findViewById(R.id.layoutCurrentAuction_txtProductName);
            TextView txtHighestBid = view.findViewById(R.id.layoutCurrentAuction_txtHighestBid);
            TextView txtMinimalBid = view.findViewById(R.id.layoutCurrentAuction_txtMinimalBid);
            TextView txtDateTime = view.findViewById(R.id.layoutCurrentAuction_txtDateTime);
            ImageView imgProductImage = view.findViewById(R.id.layoutCurrentAuction_imgProductImage);

            txtProductName.setText(productName);
            if(!highestBid.equals("No bid yet"))
                txtHighestBid.setText(("Highest bid "+highestBid));
            txtMinimalBid.setText(("Minimal price "+minimalPrice));
            txtDateTime.setText(("Ends on "+endDate+" "+endTime));
            Glide.with(view).load(productImage).placeholder(R.drawable.default_image).into(imgProductImage);
        }
    }
}
