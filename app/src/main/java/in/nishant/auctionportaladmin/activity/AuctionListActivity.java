package in.nishant.auctionportaladmin.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import in.nishant.auctionportaladmin.R;
import in.nishant.auctionportaladmin.adapter.AuctionListPagerAdapter;
import in.nishant.auctionportaladmin.fragment.CurrentAuctionFragment;
import in.nishant.auctionportaladmin.fragment.MyAuctionFragment;
import in.nishant.auctionportaladmin.fragment.PreviousAuctionFragment;

public class AuctionListActivity extends AppCompatActivity {

    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auction_list);

        initComponents();
    }

    private void initComponents() {
        // Toolbar
        Toolbar toolbar = findViewById(R.id.auctionList_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Auction List");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ViewPager viewPager = findViewById(R.id.auctionList_viewPager);
        tabLayout = findViewById(R.id.auctionList_tabLayout);
        setUpViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setUpViewPager(ViewPager viewPager) {
        AuctionListPagerAdapter adapter = new AuctionListPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new PreviousAuctionFragment(),"Previous");
        if(!getIntent().getBooleanExtra("isAdmin",false))
            adapter.addFragment(new MyAuctionFragment(),"My auction");
        adapter.addFragment(new CurrentAuctionFragment(),"Current");
        viewPager.setAdapter(adapter);
    }
}
