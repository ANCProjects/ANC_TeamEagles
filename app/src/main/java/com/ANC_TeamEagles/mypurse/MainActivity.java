package com.ANC_TeamEagles.mypurse;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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

import com.ANC_TeamEagles.mypurse.pojo.TransactionItem;
import com.ANC_TeamEagles.mypurse.utils.Constants;
import com.ANC_TeamEagles.mypurse.utils.PrefManager;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ui.ResultCodes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.joaquimley.faboptions.FabOptions;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private ViewPager viewPager;
    private SectionPagerAdapter sectionPagerAdapter;

    private DatabaseReference transactionReference;

    private DatabaseReference monthlyTransactionReference;
    private DatabaseReference weeklyTransactionRef;
    private DatabaseReference accountBalanceRef;
    private DatabaseReference todayExpenseRef;
    private DatabaseReference thisMonthExpenseRef;

    private ValueEventListener accBalListener;
    private ValueEventListener todayExpenseListener;
    private ValueEventListener thisMonthExpenseListener;



    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private static final int rcSignIn = 100;

    @BindView(R.id.fab_transaction)
    FabOptions transactionsFab;

    private static final String TAG = MainActivity.class.getSimpleName();

    TextView addBal;
    public BottomNavigationViewHelper helper;
    MenuItem menuItem;
    private PrefManager manager;

    private double previousTodayTotal;
    private double previousThisMonthTotal;

    private View navHeaderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawerlayout);
        ButterKnife.bind(this);



        navigationDrawer();
        manager = new PrefManager(MainActivity.this);
        setUpFirebaseListeners();

        addBal = (TextView) findViewById(R.id.homeStartBal);

        addBal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchStartBalanceDialog();
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
                    Log.e(TAG, "not logged in");
                    startActivityForResult(AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setProviders(
                                            AuthUI.EMAIL_PROVIDER,
                                            AuthUI.GOOGLE_PROVIDER)
                                    .setTheme(R.style.LoginTheme)
                                    .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                                    .setLogo(R.drawable.logo)
                                    .build(),
                            rcSignIn);
                }
                else {
                    //logged in

                    manager.saveUserEmail(user.getEmail());
                    setUserCredentials(user);



                }
            }
        };
    }

    private void setUpFirebaseListeners() {
        DatabaseReference userReference = App.userRef;
        transactionReference = userReference.child(Constants.NODE_TRANSACTION);
        monthlyTransactionReference = userReference.child(Constants.NODE_MONTHLY);
        weeklyTransactionRef = userReference.child(Constants.NODE_THIS_WEEK);
        accountBalanceRef = userReference.child(Constants.ACCOUNT_TOTAL);
        todayExpenseRef = userReference.child(Constants.NODE_EXPENDITURE_TODAY);
        thisMonthExpenseRef = userReference.child(Constants.NODE_EXPENDITURE_THIS_MONTH);

        accBalListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Double.class) != null){
                    double total = dataSnapshot.getValue(Double.class);

                    addBal.setText(new DecimalFormat("#,###,###,###")
                            .format(Double.valueOf(total)));
                }
                else
                    addBal.setText("0.00");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        };
        todayExpenseListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                previousTodayTotal = dataSnapshot.getValue(Double.class) == null ? 0
                        : dataSnapshot.getValue(Double.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        thisMonthExpenseListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                previousThisMonthTotal = dataSnapshot.getValue(Double.class) == null? 0
                        : dataSnapshot.getValue(Double.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };







    }

    @Override
    protected void onStop() {
        super.onStop();
        auth.removeAuthStateListener(authStateListener);
        detachFirebaseListeners();

    }

    public void attachFirebaseListeners(){
        accountBalanceRef.addValueEventListener(accBalListener);
        todayExpenseRef.addValueEventListener(todayExpenseListener);
        thisMonthExpenseRef.addValueEventListener(thisMonthExpenseListener);
    }
    public void detachFirebaseListeners(){
        accountBalanceRef.removeEventListener(accBalListener);
        todayExpenseRef.removeEventListener(todayExpenseListener);
        thisMonthExpenseRef.removeEventListener(thisMonthExpenseListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
        if (auth.getCurrentUser() !=null)
            attachFirebaseListeners();

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
        if (itemThatWasClickedId == R.id.notifications) {
            return true;
        }
        return super.onOptionsItemSelected(item);

    }


    final public void launchStartBalanceDialog(){
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        final View inflator = inflater.inflate(R.layout.add_startbalance_alert_dialog, null);
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setView(inflator);

        final EditText startBal = (EditText) inflator.findViewById(R.id.add_start_bal);
        //startBal.addTextChangedListener(new NumberTextWatcherForThousand(startBal));


        alert.setPositiveButton(R.string.addBalance, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id)
            {
                String balance = startBal.getText().toString();
                accountBalanceRef.setValue(Double.valueOf(balance));
//                addBal.setText(" "+ balance);

            }
        });

        alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        alert.show();

    }

    @OnClick({R.id.fab_add, R.id.fab_remove})
    public void addTransaction(final View view){

        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        final View dialogView = inflater.inflate(R.layout.dialog_transaction, null);
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setView(dialogView);

        final EditText amtText = (EditText) dialogView.findViewById(R.id.transac_amt);
        //amtText.addTextChangedListener(new NumberTextWatcherForThousand(amtText));

        final EditText transacDetails = (EditText)  dialogView.findViewById(R.id.transac_desc);


        alert.setPositiveButton(R.string.addBalance, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id)
            {
                String amount = amtText.getText().toString();
                String desc = transacDetails.getText().toString();
                boolean isIncome = view.getId() == R.id.fab_add;
                Calendar calendar = Calendar.getInstance();
                long now = System.currentTimeMillis();
                calendar.setTimeInMillis(now);
                String day = calendar.getDisplayName(Calendar.DAY_OF_WEEK,Calendar.LONG, Locale
                    .ENGLISH);
                String month = calendar.getDisplayName(Calendar.MONTH,Calendar.LONG, Locale
                        .ENGLISH);
                String prevBal = addBal.getText().toString();
                prevBal = prevBal.replace(",","");

                Double currentBal = Double.valueOf(prevBal);
                Double transacAmt = Double.valueOf(amount);

                if (isIncome){
                    currentBal += transacAmt;
                }
                else{
                    currentBal -= transacAmt;
                    weeklyTransactionRef.child(day).setValue(previousTodayTotal + transacAmt);
                    monthlyTransactionReference.child(month).setValue(previousThisMonthTotal +
                            transacAmt);
                    todayExpenseRef.setValue(previousTodayTotal + transacAmt);
                    thisMonthExpenseRef.setValue(previousThisMonthTotal + transacAmt);


                }



                TransactionItem item = new TransactionItem(transacAmt,desc,day+"_"+month,
                        currentBal,isIncome,now);

                accountBalanceRef.setValue(currentBal);
                String key = transactionReference.push().getKey();

                HashMap<String, Object> map= new HashMap<>();
                map.put("/"+key, item);
                transactionReference.updateChildren(map);
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

        if (id == R.id.nav_overview) {
            viewPager.setCurrentItem(1);
        } else if (id == R.id.nav_chart) {
            viewPager.setCurrentItem(2);

        } else if (id == R.id.nav_reset) {
            clearUserData();

        } else if (id == R.id.nav_about) {

        } else if (id == R.id.nav_rate){

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_signout) {
            AuthUI.getInstance().signOut(this);
            Toast.makeText(getApplicationContext(), "Succesfully signed out", Toast.LENGTH_SHORT).show();
            startActivityForResult(AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setProviders(
                                    AuthUI.EMAIL_PROVIDER,
                                    AuthUI.GOOGLE_PROVIDER)
                            .setTheme(R.style.LoginTheme)
                            .setLogo(R.drawable.logo)
                            .build(),
                    rcSignIn);
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
        navHeaderView = navigationView.getHeaderView(0);
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

    private void setUserCredentials(FirebaseUser user){
        String photoUrl = user.getPhotoUrl() == null? "" : user.getPhotoUrl().toString();
        String fullName = user.getDisplayName();
        String email = user.getEmail();

        TextView fullNameTextView = (TextView) navHeaderView.findViewById(R.id.tv_header_username);
        TextView emailTextview = (TextView) navHeaderView.findViewById(R.id.tv_header_email);
        CircleImageView userProfile = (CircleImageView) navHeaderView.findViewById(R.id.iv_header_pic);

        fullNameTextView.setText(fullName);
        emailTextview.setText(email);
        Picasso.with(this).load(photoUrl)
                .placeholder(R.drawable.profile)
                .error(R.drawable.profile)
                .into(userProfile);


    }

    private void clearUserData(){
        new AlertDialog.Builder(this)
                .setMessage("This will clear all transaction details\n Continue?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        App.userRef.removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError == null)
                                    Snackbar.make(getWindow().getDecorView().getRootView(),"Reset " +
                                            "successful",Snackbar.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).setCancelable(true)
                .show();
    }
}