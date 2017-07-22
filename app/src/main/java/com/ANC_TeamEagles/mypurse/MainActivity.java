package com.ANC_TeamEagles.mypurse;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton addButton ;
    TextView startBal;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.myhometoolbar);
        setSupportActionBar(toolbar);

        startBal = (TextView) findViewById(R.id.homeStartBal);

      addButton = (FloatingActionButton) findViewById(R.id.fab);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Add start Balance", Toast.LENGTH_LONG).show();
                startBal.setText("N100,000");
            }
        });


        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem =  menu.getItem(0);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.income:
                        Intent intent1 = new Intent(MainActivity.this,Income.class);
                        startActivity(intent1);
                        break;
                    case R.id.summary:
                        Intent intent2 = new Intent(MainActivity.this,Summary.class);
                        startActivity(intent2);
                        break;
                    case R.id.expenditure:
                        Intent intent3 = new Intent(MainActivity.this,Expenditure.class);
                        startActivity(intent3);
                        break;

                }
                return false;
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();
        if (itemThatWasClickedId == R.id.allItems) {
            return true;
        }
        return super.onOptionsItemSelected(item);

    }


}