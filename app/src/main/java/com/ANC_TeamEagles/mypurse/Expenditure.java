package com.ANC_TeamEagles.mypurse;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class Expenditure extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenditure);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem =  menu.getItem(2);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.income:
                        Intent intent1 = new Intent(Expenditure.this,Income.class);
                        startActivity(intent1);
                        break;
                    case R.id.summary:
                        Intent intent2 = new Intent(Expenditure.this,Summary.class);
                        startActivity(intent2);
                        break;
                    case R.id.expenditure:

                        break;

                }
                return false;
            }
        });

    }
}
