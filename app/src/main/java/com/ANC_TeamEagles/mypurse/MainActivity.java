package com.ANC_TeamEagles.mypurse;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton addButton ;
    TextView addBal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button test = (Button) findViewById(R.id.test);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Categories.class);
                startActivity(i);
            }
        });



        Toolbar toolbar = (Toolbar) findViewById(R.id.myhometoolbar);
        setSupportActionBar(toolbar);
        

        addBal = (TextView) findViewById(R.id.homeStartBal);

        addBal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog();
            }
        });

      addButton = (FloatingActionButton) findViewById(R.id.fab);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog();
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

    final public void Dialog(){
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        final View inflator = inflater.inflate(R.layout.add_startbalance_alert_dialog, null);
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setView(inflator);

        final EditText startBal = (EditText) inflator.findViewById(R.id.add_start_bal);
        startBal.addTextChangedListener(new NumberTextWatcherForThousand(startBal));


        alert.setPositiveButton(R.string.addBalance, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id)
            {
                String balance = startBal.getText().toString();
                addBal.setText(" "+ balance);
            }
        });

        alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        alert.show();

    }

}