package in.nishant.auctionportaladmin.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

import in.nishant.auctionportaladmin.R;

public class WinnerListActivity extends AppCompatActivity {

    private Toolbar toolbar;

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
    }
}
