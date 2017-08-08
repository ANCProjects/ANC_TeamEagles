package com.ANC_TeamEagles.mypurse;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ANC_TeamEagles.mypurse.utils.Constants;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ui.ResultCodes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.joaquimley.faboptions.FabOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private ViewPager viewPager;
    private SectionPagerAdapter sectionPagerAdapter;

    private DatabaseReference transactionReference;
    private DatabaseReference userReference;
    private DatabaseReference monthlyTransactionReference;
    private DatabaseReference dayTransactionRef;

    private ValueEventListener transactionListener;
    private ValueEventListener monthlyListener;
    private ValueEventListener dailyListener;



    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private static final int rcSignIn = 100;
    private Intent loginSuccessIntent;

    @BindView(R.id.fab_transaction)
    FabOptions transactionsFab;

    TextView addBal;
    public BottomNavigationViewHelper helper;
    MenuItem menuItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawerlayout);
        ButterKnife.bind(this);



        navigationDrawer();

        addBal = (TextView) findViewById(R.id.homeStartBal);

        addBal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog();
            }
        });



        sectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.body);
        setupViewPager(viewPager);
        setupBottomView();

        setupFirebaseAuth();


    }

    public void setupFirebaseAuth(){
        auth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user == null){
                    // not signed in
                    startActivityForResult(AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setProviders(
                                            AuthUI.EMAIL_PROVIDER,
                                            AuthUI.GOOGLE_PROVIDER)
                                    .setTheme(R.style.LoginTheme)
                                    .setLogo(R.drawable.logo)
                                    .build(),
                            rcSignIn);
                }
                else {
                    //logged in

                    attachDatabaseListeners();
                }
            }
        };
    }

    private void attachDatabaseListeners() {
        userReference = App.appDatabase.getReference().child(Constants.KEY_EMAIL);
        transactionReference = userReference.child(Constants.NODE_TRANSACTION);
        monthlyTransactionReference = userReference.child(Constants.NODE_MONTHLY);
        dayTransactionRef = userReference.child(Constants.NODE_DAY);



    }

    @Override
    protected void onStop() {
        super.onStop();
        auth.removeAuthStateListener(authStateListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();
        if (itemThatWasClickedId == R.id.share) {
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @OnClick({R.id.fab_add, R.id.fab_remove})
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
    private void setupViewPager(ViewPager viewpager) {

        SectionPagerAdapter adapter = new SectionPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new IncomeFragment());
        adapter.addFragment(new OverviewFragment());
        adapter.addFragment(new ChartsFragment());
        adapter.addFragment(new ExpenditureFragment());
        viewPager.setAdapter(adapter);
    }

  public void setupBottomView(){

      final BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
      helper.removeShiftMode(bottomNavigationView);
      bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
          @Override
          public boolean onNavigationItemSelected(@NonNull MenuItem item) {

              switch (item.getItemId()) {

                  case R.id.income: {
                      viewPager.setCurrentItem(0);
                      break;
                  }
                  case R.id.overview: {
                      viewPager.setCurrentItem(1);
                      break;
                  }
                  case R.id.charts: {
                      viewPager.setCurrentItem(2);
                      break;
                  }
                  case R.id.expenditure: {
                      viewPager.setCurrentItem(3);
                      break;

                  }


              }
              return false;
          }
      });
      viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
          @Override
          public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

          }

          @Override
          public void onPageSelected(int position) {
              if (menuItem != null) {
                  menuItem.setChecked(false);
              }
              else
              {
                  bottomNavigationView.getMenu().getItem(0).setChecked(false);

              }
              Log.d("page", "onPageSelected: "+position);
              bottomNavigationView.getMenu().getItem(position).setChecked(true);
              menuItem = bottomNavigationView.getMenu().getItem(position);

          }

          @Override
          public void onPageScrollStateChanged(int state) {

          }
      });

  }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void navigationDrawer(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.myhometoolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == rcSignIn){
            handleSignInResponse(resultCode, data);
            return;
        }
    }


    @MainThread
    private void handleSignInResponse(int resultCode, Intent data) {
        IdpResponse response = IdpResponse.fromResultIntent(data);

        // Successfully signed in
        if (resultCode == RESULT_OK) {
            //startActivity(loginSuccessIntent);
            //finish();
            return;
        }
        // Sign in failed
        if (resultCode == RESULT_CANCELED) {
            // User pressed back button
            Toast.makeText(getApplicationContext(), "sign_in_cancelled", Toast.LENGTH_SHORT).show();
            return;
        }
        // No internet connection
        if (resultCode == ResultCodes.RESULT_NO_NETWORK) {
            Toast.makeText(getApplicationContext(), "no_internet_connection", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}