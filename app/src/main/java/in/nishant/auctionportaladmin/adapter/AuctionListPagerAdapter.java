package in.nishant.auctionportaladmin.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import in.nishant.auctionportaladmin.fragment.CurrentAuctionFragment;
import in.nishant.auctionportaladmin.fragment.MyAuctionFragment;
import in.nishant.auctionportaladmin.fragment.PreviousAuctionFragment;

public class AuctionListPagerAdapter extends FragmentPagerAdapter {

    public AuctionListPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment previousAF,myAF,currentAF;
        switch (position){
            case 0:
                previousAF = new PreviousAuctionFragment();
                return previousAF;
            case 1:
                myAF = new MyAuctionFragment();
                return myAF;
            case 2:
                currentAF = new CurrentAuctionFragment();
                return currentAF;
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Previous";
            case 1:
                return "My auction";
            case 2:
                return "Current";
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
