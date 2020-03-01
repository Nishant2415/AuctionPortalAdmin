package in.nishant.auctionportaladmin.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import in.nishant.auctionportaladmin.fragment.CurrentAuctionFragment;
import in.nishant.auctionportaladmin.fragment.MyAuctionFragment;
import in.nishant.auctionportaladmin.fragment.PreviousAuctionFragment;

public class AuctionListPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragmentList = new ArrayList<>();
    private List<String> stringList = new ArrayList<>();

    public AuctionListPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return stringList.get(position);
    }

    @Override
    public int getCount() {
        return stringList.size();
    }

    public void addFragment(Fragment fragment, String s){
        fragmentList.add(fragment);
        stringList.add(s);
    }
}
