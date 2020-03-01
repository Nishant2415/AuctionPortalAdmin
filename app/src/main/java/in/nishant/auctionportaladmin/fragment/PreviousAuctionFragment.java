package in.nishant.auctionportaladmin.fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import in.nishant.auctionportaladmin.R;
import in.nishant.auctionportaladmin.activity.ProductDetailActivity;
import in.nishant.auctionportaladmin.model.ProductsModel;

public class PreviousAuctionFragment extends Fragment {

    private DatabaseReference productRef;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private Date currentDate, currentTime;
    private View view;
    private Calendar calendar = Calendar.getInstance();

    public PreviousAuctionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_previous_auction, container, false);
        initComponents();
        return view;
    }

    private void initComponents() {
        recyclerView = view.findViewById(R.id.previousAF_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        productRef = FirebaseDatabase.getInstance().getReference().child("Products");
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        final SimpleDateFormat sdfDate = new SimpleDateFormat("dd MMM yyyy", Locale.US);
        final SimpleDateFormat sdfTime = new SimpleDateFormat("h:mm a", Locale.US);

        // Removing current auction based on time
        productRef.child("Current auction").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> userSnapshot = dataSnapshot.getChildren();
                for (DataSnapshot userKey : userSnapshot) {
                    final String userId = userKey.getKey();
                    productRef.child("Current auction").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child(userId).hasChild("endDate")) {
                                try {
                                    currentDate = sdfDate.parse(sdfDate.format(calendar.getTime()));
                                    currentTime = sdfTime.parse(sdfTime.format(calendar.getTime()));
                                    final Date dEndDate = sdfDate.parse(dataSnapshot.child(userId).child("endDate").getValue(String.class));
                                    final Date dEndTime = sdfTime.parse(dataSnapshot.child(userId).child("endTime").getValue(String.class));

                                    if ((currentDate.after(dEndDate) & currentTime.after(dEndTime)) |
                                            (currentDate.equals(dEndDate) & currentTime.after(dEndTime)) |
                                            (currentDate.equals(dEndDate) & currentTime.equals(dEndTime))) {
                                        productRef.child("Previous auction").child(userId).setValue(dataSnapshot.child(userId).getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    productRef.child("Current auction").child(userId).removeValue();
                                                } else {
                                                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                } catch (Exception e) {
                                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Show all previous auctions
        FirebaseRecyclerAdapter<ProductsModel, PreviousAuctionFragment.PreviousAuctionViewHolder> adapter = new FirebaseRecyclerAdapter<ProductsModel, PreviousAuctionFragment.PreviousAuctionViewHolder>(
                ProductsModel.class,
                R.layout.layout_current_auction,
                PreviousAuctionFragment.PreviousAuctionViewHolder.class,
                productRef.child("Previous auction")
        ) {
            @Override
            protected void populateViewHolder(PreviousAuctionFragment.PreviousAuctionViewHolder holder, ProductsModel productsModel, final int position) {
                final String userId = getRef(position).getKey();

                holder.setAuction(productsModel.getProductName(),
                        productsModel.getMinimalPrice(),
                        productsModel.getEndDate(),
                        productsModel.getEndTime(),
                        productsModel.getProductImage(),
                        productsModel.getHighestBid());

                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //startActivity(new Intent(getActivity(), ProductDetailActivity.class).putExtra("userId", userId));
                    }
                });

            }
        };
        recyclerView.setAdapter(adapter);
    }

    public static class PreviousAuctionViewHolder extends RecyclerView.ViewHolder {

        View view;

        public PreviousAuctionViewHolder(@NonNull View itemView) {
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
            if (!highestBid.equals("No bid yet"))
                txtHighestBid.setText(("Highest bid " + highestBid));
            txtMinimalBid.setText(("Minimal price " + minimalPrice));
            txtDateTime.setText(("Ended on " + endDate + " " + endTime));
            Glide.with(view).load(productImage).placeholder(R.drawable.default_image).into(imgProductImage);
        }
    }
}
