package com.ANC_TeamEagles.mypurse;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class Summary extends AppCompatActivity {
    private ViewPager viewPager;
    private SectionPagerAdapter SectionPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);


        SectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(viewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        String tab1 = "All";
        String tab2 = "Charts";

        tabLayout.getTabAt(0).setText(tab1);
        tabLayout.getTabAt(1).setText(tab2);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem =  menu.getItem(0);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.income:
                        Intent intent1 = new Intent(Summary.this,Income.class);
                        startActivity(intent1);
                        break;
                    case R.id.summary:

                        break;
                    case R.id.expenditure:
                        Intent intent3 = new Intent(Summary.this,Expenditure.class);
                        startActivity(intent3);
                        break;

                }
                return false;
            }
        });


    }

    private void setupViewPager(ViewPager viewpager) {

        SectionPagerAdapter adapter = new SectionPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new SummaryListFragment());
        adapter.addFragment(new SummaryGraphFragment());
        viewPager.setAdapter(adapter);
    }
}
