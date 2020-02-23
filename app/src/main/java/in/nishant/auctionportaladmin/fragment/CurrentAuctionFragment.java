package in.nishant.auctionportaladmin.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
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
import in.nishant.auctionportaladmin.model.ProductsModel;
import in.nishant.auctionportaladmin.utils.CustomToast;

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
            protected void populateViewHolder(CurrentAuctionViewHolder holder, ProductsModel currentAuctionModel, final int position) {
                final String userId = getRef(position).getKey();

                if(userId.equals(mAuth.getCurrentUser().getUid())){
                    holder.view.setLayoutParams(new RecyclerView.LayoutParams(0,0));
                }

                holder.setAuction(currentAuctionModel.getProductName(),
                        currentAuctionModel.getMinimalPrice(),
                        currentAuctionModel.getEndDate(),
                        currentAuctionModel.getEndTime(),
                        currentAuctionModel.getProductImage(),
                        currentAuctionModel.getHighestBid());

                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        // Adding edit text to alert dialog
                        LinearLayout linearLayout = new LinearLayout(getActivity());
                        linearLayout.setOrientation(LinearLayout.VERTICAL);
                        linearLayout.setPadding(40,0,40,0);
                        final TextInputEditText txtBidAmount = new TextInputEditText(getActivity());
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
                                            CustomToast.show(getActivity(), 0, "Please enter bid amount");
                                        } else {
                                            final Map<String, Object> bidMap = new HashMap<>();
                                            bidMap.put("highestBid", txtBidAmount.getText().toString().trim());
                                            bidMap.put("highestBidUser",mAuth.getCurrentUser().getUid());

                                            rootRef.child("Products").child("Current auction").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if(!dataSnapshot.child("highestBid").getValue(String.class).equals("No bid yet")){
                                                        if (Integer.parseInt(txtBidAmount.getText().toString().trim()) <= Integer.parseInt(dataSnapshot.child("highestBid").getValue(String.class))) {
                                                            CustomToast.show(getActivity(), 0, "Please enter minimal bid amount!");
                                                        } else {
                                                            rootRef.child("Products").child("Current auction").child(userId).updateChildren(bidMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        rootRef.child("Products").child("My auction").child(userId).updateChildren(bidMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    CustomToast.show(getActivity(), 1, "Bid successful!");
                                                                                } else {
                                                                                    CustomToast.show(getActivity(), 0, "Something wrong!");
                                                                                    Log.d("Auction portal", task.getException().toString());
                                                                                }
                                                                            }
                                                                        });
                                                                    } else {
                                                                        CustomToast.show(getActivity(), 0, "Something wrong!");
                                                                        Log.d("Auction portal", task.getException().toString());
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    } else {
                                                        if (Integer.parseInt(txtBidAmount.getText().toString().trim()) <= Integer.parseInt(dataSnapshot.child("minimalPrice").getValue(String.class))) {
                                                            CustomToast.show(getActivity(), 0, "Please enter minimal bid amount!");
                                                        } else {
                                                            rootRef.child("Products").child("Current auction").child(userId).updateChildren(bidMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        rootRef.child("Products").child("My auction").child(userId).updateChildren(bidMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    CustomToast.show(getActivity(), 1, "Bid successful!");
                                                                                } else {
                                                                                    CustomToast.show(getActivity(), 0, "Something wrong!");
                                                                                    Log.d("Auction portal", task.getException().toString());
                                                                                }
                                                                            }
                                                                        });
                                                                    } else {
                                                                        CustomToast.show(getActivity(), 0, "Something wrong!");
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
            txtDateTime.setText(("Will ends on "+endDate+" "+endTime));
            Glide.with(view).load(productImage).placeholder(R.drawable.default_image).into(imgProductImage);
        }
    }
}
