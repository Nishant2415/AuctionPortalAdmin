package in.nishant.auctionportaladmin.fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.nishant.auctionportaladmin.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PreviousAuctionFragment extends Fragment {


    public PreviousAuctionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_previous_auction, container, false);
    }

}
