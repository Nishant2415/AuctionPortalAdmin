package in.nishant.auctionportaladmin.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

import in.nishant.auctionportaladmin.R;

public class AuctionListActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auction_list);

        initComponents();
    }

    private void initComponents() {
        // Toolbar
        toolbar = findViewById(R.id.auctionList_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Auction List");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
