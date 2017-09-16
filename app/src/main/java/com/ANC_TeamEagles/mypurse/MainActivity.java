package com.ANC_TeamEagles.mypurse;

import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ANC_TeamEagles.mypurse.pojo.TransactionItem;
import com.ANC_TeamEagles.mypurse.settings.SettingsActivity;
import com.ANC_TeamEagles.mypurse.settings.SettingsFragment;
import com.ANC_TeamEagles.mypurse.toBuy.ToBuyFragment;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.ANC_TeamEagles.mypurse.App.accountBalanceRef;
import static com.ANC_TeamEagles.mypurse.App.expendableAmtRef;
import static com.ANC_TeamEagles.mypurse.App.lowAmountRef;
import static com.ANC_TeamEagles.mypurse.App.monthlyTransactionReference;
import static com.ANC_TeamEagles.mypurse.App.thisMonthExpenseRef;
import static com.ANC_TeamEagles.mypurse.App.todayExpenseRef;
import static com.ANC_TeamEagles.mypurse.App.transactionReference;
import static com.ANC_TeamEagles.mypurse.App.weeklyTransactionRef;
import static com.ANC_TeamEagles.mypurse.utils.Helpers.formatAmount;

public class MainActivity extends AppCompatActivity implements NavigationView
        .OnNavigationItemSelectedListener, SharedPreferences.OnSharedPreferenceChangeListener{

    private ViewPager viewPager;
    private SectionPagerAdapter sectionPagerAdapter;


    private ValueEventListener accBalListener;
    private ValueEventListener todayExpenseListener;
    private ValueEventListener thisMonthExpenseListener;
    private ValueEventListener expendableAmountListener;
    private ValueEventListener lowAmtListener;

    private SharedPreferences preferences;



    private OverviewFragment overviewFragment;
    private ChartsFragment chartsFragment;
    private ToBuyFragment toBuyFragment;


    public static MainActivity instance;

    private AlertDialog networkDialog;


    private IntentFilter filter = new IntentFilter();
    private NetworkChecker networkChecker;


    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private static final int rcSignIn = 100;

    @BindView(R.id.fab_transaction)
    FabOptions transactionsFab;

    @BindView(R.id.expendable_amt)
    TextView expendableAmtTextView;

    public static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.homeStartBal)
    TextView addBal;

    public BottomNavigationViewHelper helper;
    MenuItem menuItem;
    private PrefManager manager;

    private static boolean isNotificationSent;

    private static double previousTodayTotal = 0;
    private static double previousThisMonthTotal = 0;
    public static double expendableAmtLeft = 0;
    private static double currentAccountBalance = 0;
    private static double lowAmt = 0;

    private View navHeaderView;
    private Calendar calendar = Calendar.getInstance();

    private static SectionPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawerlayout);
        ButterKnife.bind(this);
        instance = this;
        Log.d(TAG," on create");
        preferences = getSharedPreferences(
                SettingsFragment.SETTINGS_SHARED_PREFERENCES_FILE_NAME,
                Context.MODE_PRIVATE);


        navigationDrawer();
        manager = new PrefManager(MainActivity.this);
        setUpFirebaseListeners();

        sectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.body);
        setupViewPager();
        setupBottomView();


        setupFirebaseAuth();

        if (getIntent().hasExtra(Constants.WHICH_FRAG)){
            String frag = getIntent().getStringExtra(Constants.WHICH_FRAG);

            switch (frag){
                case Constants.FRAG_OVERVIEW:
                    viewPager.setCurrentItem(0);
                    break;
                case Constants.FRAG_CHART:
                    viewPager.setCurrentItem(1);
                    break;

                case Constants.FRAG_TO_BUY:
                    viewPager.setCurrentItem(2);
                    break;
            }
        }

        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

         networkChecker = new NetworkChecker(this);

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
                                    .setIsSmartLockEnabled(false)
                                    .setLogo(R.drawable.logo)
                                    .build(),
                            rcSignIn);
                }
                else {
                    //logged in


                    setUserCredentials(user);



                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        preferences.registerOnSharedPreferenceChangeListener(this);
        Log.d(TAG," on resume");
        Log.d(TAG,PreferenceManager.getDefaultSharedPreferences(this).getString
                ("pref_expendable_threshold_title","none"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        //preferences.unregisterOnSharedPreferenceChangeListener(this);
        Log.d(TAG," onPause");
    }



    private void setUpFirebaseListeners() {

        accBalListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentAccountBalance = dataSnapshot.getValue(Double.class) == null ? 0 :
                        dataSnapshot.getValue(Double.class);
                addBal.setText(formatAmount(currentAccountBalance));

                PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit()
                        .putLong(Constants.KEY_ACC_BAL_AMT,(long)currentAccountBalance).apply();

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

                PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit()
                        .putLong(Constants.KEY_MONTH_EXPENSE,(long)previousTodayTotal).apply();
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
                PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit()
                        .putLong(Constants.KEY_MONTH_EXPENSE,(long)previousThisMonthTotal).apply();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        lowAmtListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lowAmt = dataSnapshot.getValue(Double.class) == null? 0 :
                        dataSnapshot.getValue(Double.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        expendableAmountListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                expendableAmtLeft = dataSnapshot.getValue(Double.class) == null ? 0 :
                        dataSnapshot.getValue(Double.class);

                PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit()
                        .putLong(Constants.KEY_AMOUNT_TO_SPEND,(long)expendableAmtLeft).apply();

                expendableAmtTextView.setText(formatAmount(expendableAmtLeft));
                addBal.setText(formatAmount(currentAccountBalance));
                if (expendableAmtLeft == 0 && !isNotificationSent){
                    //send notification
                    showNotification("You have exhausted your expendable amount", Constants.FRAG_OVERVIEW);
                    expendableAmtTextView.setTextColor(getResources().getColor(R.color.low_amount));

                }
                else if (expendableAmtLeft <= lowAmt){
                    expendableAmtTextView.setTextColor(getResources().getColor(R.color.low_amount));
                    if(!isNotificationSent){
                        showNotification(getString(R.string.notification_low_amt,
                                ""+expendableAmtLeft), Constants.FRAG_OVERVIEW);
                    }
                }
                else {
                    expendableAmtTextView.setTextColor(getResources().getColor(R.color.amount));
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };




    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG," onStop");
        unregisterReceiver(networkChecker);
        auth.removeAuthStateListener(authStateListener);
        detachFirebaseListeners();

    }

    public void attachFirebaseListeners(){
        accountBalanceRef.addValueEventListener(accBalListener);
        todayExpenseRef.addValueEventListener(todayExpenseListener);
        thisMonthExpenseRef.addValueEventListener(thisMonthExpenseListener);
        lowAmountRef.addValueEventListener(lowAmtListener);
        expendableAmtRef.addValueEventListener(expendableAmountListener);
    }
    public void detachFirebaseListeners(){
        accountBalanceRef.removeEventListener(accBalListener);
        todayExpenseRef.removeEventListener(todayExpenseListener);
        thisMonthExpenseRef.removeEventListener(thisMonthExpenseListener);
        lowAmountRef.removeEventListener(lowAmtListener);
        expendableAmtRef.removeEventListener(expendableAmountListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
        registerReceiver(networkChecker,filter);
        Log.d(TAG," onStart");

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
        if (itemThatWasClickedId == R.id.filter) {
            createFilterDialog();
            return true;
        }

        else if (itemThatWasClickedId == R.id.tobuy_add)
        {
            if (toBuyFragment != null && viewPager.getCurrentItem() == 2)
                toBuyFragment.addItem();
        }
        return super.onOptionsItemSelected(item);

    }


    @OnClick({R.id.homeStartBal, R.id.expendable_amt})
    final public void launchStartBalanceDialog(final View view){

        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        final View inflator = inflater.inflate(R.layout.add_startbalance_alert_dialog, null);
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setView(inflator);

        final EditText enteredAmount = (EditText) inflator.findViewById(R.id.add_start_bal);


        alert.setPositiveButton(R.string.addBalance, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id)
            {
                String balance = enteredAmount.getText().toString();
                final double balanceAmt = Double.valueOf(balance);
                if (view.getId() == R.id.homeStartBal){
                    accountBalanceRef.setValue(balanceAmt);
                }
                else if (view.getId() == R.id.expendable_amt){
                    if (balanceAmt > currentAccountBalance){
                        displayMessageToUser("Expendable " +
                                "amount cannot be greater than account balance");
                    }
                    else if (balanceAmt < 0 ){
                       displayMessageToUser("Expendable " +
                               "amount cannot be less than 0");
                    }
                    else {
                        if (balanceAmt < 0)
                            displayMessageToUser("No money to spend");
                        else
                            expendableAmtRef.setValue(balanceAmt);
                    }

                }


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


        final EditText transacDetails = (EditText)  dialogView.findViewById(R.id.transac_desc);


        alert.setPositiveButton(R.string.addBalance, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id)
            {
                resetDailyValues();
                String amount = amtText.getText().toString();
                String desc = transacDetails.getText().toString();
                boolean isIncome = view.getId() == R.id.fab_add;

                long now = System.currentTimeMillis();
                calendar.setTimeInMillis(now);
                String day = calendar.getDisplayName(Calendar.DAY_OF_WEEK,Calendar.LONG, Locale
                    .ENGLISH);
                String month = calendar.getDisplayName(Calendar.MONTH,Calendar.LONG, Locale
                        .ENGLISH);

                Log.e(TAG," amount to add = "+amount);
                if (!TextUtils.isDigitsOnly(amount)){
                    Log.e(TAG," not digits = "+amount);
                    amtText.setError("Only numbers are allowed");
                    return;
                }

                else if (TextUtils.isEmpty(desc)){
                    Log.e(TAG," empty = "+amount);
                    transacDetails.setError("Cannot be empty");
                    return;
                }

                else {
                    Double transacAmt = Double.valueOf(amount);

                    if (isIncome){
                        currentAccountBalance += transacAmt;
                    }
                    else{
                        if (expendableAmtLeft <= 0){
                            displayMessageToUser("Expendable amount is exhausted\n set an amount");
                            return;
                        }
                        else if (expendableAmtLeft < transacAmt){
                            displayMessageToUser("Entered amount "+transacAmt+" is more than the " +
                                    "expendable amount");
                            return;
                        }
                        else {

                            currentAccountBalance -= transacAmt;
                            weeklyTransactionRef.child(day).setValue(previousTodayTotal + transacAmt);
                            monthlyTransactionReference.child(month).setValue(previousThisMonthTotal +
                                    transacAmt);
                            todayExpenseRef.setValue(previousTodayTotal + transacAmt);
                            thisMonthExpenseRef.setValue(previousThisMonthTotal + transacAmt);
                            expendableAmtLeft -= transacAmt;
                            expendableAmtRef.setValue(expendableAmtLeft);
                        }

                    }



                    TransactionItem item = new TransactionItem(transacAmt,desc,day+"_"+month,
                            currentAccountBalance,isIncome,now);

                    accountBalanceRef.setValue(currentAccountBalance);
                    String key = transactionReference.push().getKey();

                    HashMap<String, Object> map= new HashMap<>();
                    map.put("/"+key, item);
                    transactionReference.updateChildren(map);
                }

            }
        });

        alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        alert.show();
    }
    private void setupViewPager() {

        adapter = new SectionPagerAdapter(getSupportFragmentManager());
        overviewFragment = new OverviewFragment();
        chartsFragment = new ChartsFragment();
        toBuyFragment = new ToBuyFragment();

        adapter.addFragment(overviewFragment);
        adapter.addFragment(chartsFragment);
        adapter.addFragment(toBuyFragment);
        viewPager.setAdapter(adapter);
    }

  public void setupBottomView(){

      final BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
      helper.removeShiftMode(bottomNavigationView);
      bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
          @Override
          public boolean onNavigationItemSelected(@NonNull MenuItem item) {

              switch (item.getItemId()) {

                  case R.id.overview: {
                      viewPager.setCurrentItem(0);
                      break;
                  }
                  case R.id.charts: {
                      viewPager.setCurrentItem(1);
                      break;
                  }
                  case R.id.tobuy:
                      viewPager.setCurrentItem(2);
                      break;


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
            viewPager.setCurrentItem(0);
        } else if (id == R.id.nav_chart) {
            viewPager.setCurrentItem(1);

        } else if (id == R.id.nav_reset) {
            clearUserData();

        } else if (id == R.id.nav_about) {
           Intent i = new Intent(getApplicationContext(),about_us.class);
            startActivity(i);

        } else if (id == R.id.nav_rate){

        }
        else if (id == R.id.settings){
            startActivity(new Intent(this, SettingsActivity.class));
        }
        else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_signout) {
            AuthUI.getInstance().signOut(this);
            Toast.makeText(getApplicationContext(), "Succesfully signed out", Toast.LENGTH_SHORT).show();
            manager.saveUserEmail(Constants.DEFAULT_EMAIL);
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

        manager.saveUserEmail(email);
        App.configureDatabaseForUser(this);
        setupViewPager();




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

    private void displayMessageToUser(String message){
        Snackbar.make(getWindow().getDecorView().getRootView(),message,Snackbar.LENGTH_SHORT)
                .show();
    }

    public void showNotification(String message, String whichFragment){

        final int notificationID = 102;
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        mainActivityIntent.putExtra(Constants.WHICH_FRAG,whichFragment);
        PendingIntent pendingIntent = PendingIntent
                .getActivity(this,0,mainActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setContentTitle("My Purse")
                .setContentText(message)
                .build();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(notificationID,notification);
        isNotificationSent = true;
    }

    public void resetDailyValues(){
        String day = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(Constants.TODAY,"noday");

        String actualDay = calendar.getDisplayName(Calendar.DAY_OF_WEEK,Calendar.LONG,Locale.ENGLISH);

        if (!day.equalsIgnoreCase(actualDay)){
            //reset all daily values
            previousTodayTotal = 0;
            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit().putString(Constants.TODAY,actualDay)
                    .apply();
        }
    }



    public void createFilterDialog(){

        final View view = LayoutInflater.from(this).inflate(R.layout.dialog_filter,null,false);
        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.rg_filter);
        RadioButton rbIncome = (RadioButton) view.findViewById(R.id.rb_income);
        RadioButton rbExpense = (RadioButton) view.findViewById(R.id.rb_expenditure);
        RadioButton rbBoth = (RadioButton) view.findViewById(R.id.rb_all);

        final AlertDialog filterDilaog = new AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(true)
                .show();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {

                switch (radioGroup.getCheckedRadioButtonId()){
                    case R.id.rb_all:
                        overviewFragment.filterTransactions(0);
                        break;
                    case R.id.rb_income:
                        overviewFragment.filterTransactions(1);
                        break;
                    case R.id.rb_expenditure:
                        overviewFragment.filterTransactions(2);
                        break;
                }
            }
        });
        int key = PreferenceManager.getDefaultSharedPreferences(this).getInt(Constants
                .KEY_FILTER,0);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filterDilaog.isShowing())
                filterDilaog.dismiss();
            }
        };

        rbBoth.setOnClickListener(listener);
        rbExpense.setOnClickListener(listener);
        rbIncome.setOnClickListener(listener);

        switch (key){
            case 0:
                rbBoth.setChecked(true);
                break;
            case 1:
                rbIncome.setChecked(true);
                break;
            case 2:
                rbExpense.setChecked(true);
                break;
        }

    }

    public void showNoConnectivityDialog(){
        networkDialog = new AlertDialog.Builder(this)
                .setMessage("You are offline.")
                .setPositiveButton("Enable network", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent settingsIntent = new Intent(Settings.ACTION_SETTINGS);
                        startActivity(settingsIntent);
                    }
                })
                .setCancelable(false)
                .create();
        networkDialog.setCanceledOnTouchOutside(false);
        networkDialog.show();
    }

    public void hideNoConnectivityDialog(){

        if (networkDialog != null && networkDialog.isShowing())
            networkDialog.dismiss();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Toast.makeText(this," changed: "+key,Toast.LENGTH_SHORT).show();
        Log.d(TAG," called pref");
        final String startBalKey = getString(R.string.pref_key_start_balance);
        final String expendableKey = getString(R.string.pref_key_expendable);
        final String expendableThresholdKey = getString(R.string.pref_key_expendable_threshold);

        if (key.equalsIgnoreCase(startBalKey)){
            double amt = 0;
            try{
                amt = Double.valueOf(sharedPreferences.getString(key,"0"));
            }
            catch (NumberFormatException e){
                Log.d(TAG," not a number");
            }
            finally {
                accountBalanceRef.setValue(amt);
            }


        }
        else if (key.equals(expendableKey)){
            double amt = 0;
            try{
                amt = Double.valueOf(sharedPreferences.getString(key,"0"));
            }
            catch (NumberFormatException e){
                Log.d(TAG," not a number");
            }
            finally {
                expendableAmtRef.setValue(amt);
            }


        }
        else if (key.equals(expendableThresholdKey)){
            double amt = 0;
            try{
                amt = Double.valueOf(sharedPreferences.getString(key,"0"));
            }
            catch (NumberFormatException e){
                Log.d(TAG," not a number");
            }
            finally {
                lowAmountRef.setValue(amt);
            }

        }
    }

}