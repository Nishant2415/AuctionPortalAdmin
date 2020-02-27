package in.nishant.auctionportaladmin.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import in.nishant.auctionportaladmin.R;
import in.nishant.auctionportaladmin.adapter.AuctionListPagerAdapter;

public class AuctionListActivity extends AppCompatActivity {

    private TabLayout.Tab tab;
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
        if(getIntent().getBooleanExtra("isAdmin",false))
            tabLayout.removeTabAt(1);
        else {
            tab = tabLayout.getTabAt(1);
            tab.select();
        }
    }

    private void setUpViewPager(ViewPager viewPager) {
        AuctionListPagerAdapter adapter = new AuctionListPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        int position = tabLayout.getSelectedTabPosition();
        if(position!=1){
            tab = tabLayout.getTabAt(1);
            tab.select();
        } else {
            super.onBackPressed();
        }
    }
}
