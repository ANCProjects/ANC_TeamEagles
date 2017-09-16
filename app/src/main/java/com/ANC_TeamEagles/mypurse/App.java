package com.ANC_TeamEagles.mypurse;

import android.app.Application;

import com.ANC_TeamEagles.mypurse.utils.Constants;
import com.ANC_TeamEagles.mypurse.utils.PrefManager;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

/**
 * Created by nezspencer on 8/8/17.
 */

public class App extends Application {

    private static FirebaseDatabase appDatabase;
    public static DatabaseReference userRef;
    public static DatabaseReference monthlyTransactionReference;
    public static DatabaseReference weeklyTransactionRef;
    public static DatabaseReference accountBalanceRef;
    public static DatabaseReference todayExpenseRef;
    public static DatabaseReference thisMonthExpenseRef;
    public static DatabaseReference transactionReference;
    public static DatabaseReference toBuyRef;
    public static DatabaseReference lowAmountRef;

    public static DatabaseReference expendableAmtRef;

    public static boolean isToBuyNotificationClicked;
    private static PrefManager manager;

    private static Calendar calendar = Calendar.getInstance();

    @Override
    public void onCreate() {
        super.onCreate();
        appDatabase = FirebaseDatabase.getInstance();
        appDatabase.setPersistenceEnabled(true);
        manager = new PrefManager(this);
        userRef = appDatabase.getReference().child(manager.getUserEmail());
        setUpDbReferences();
        isToBuyNotificationClicked =false;
    }

    private static void setUpDbReferences(){
        transactionReference = userRef.child(Constants.NODE_TRANSACTION);
        monthlyTransactionReference = userRef.child(Constants.NODE_MONTHLY);
        String week_year = "Week"+calendar.get(Calendar.WEEK_OF_YEAR)+"_Year"+calendar.get(Calendar
                .YEAR);
        weeklyTransactionRef = userRef.child(Constants.NODE_THIS_WEEK).child(week_year);
        accountBalanceRef = userRef.child(Constants.ACCOUNT_TOTAL);
        todayExpenseRef = userRef.child(Constants.NODE_EXPENDITURE_TODAY);
        thisMonthExpenseRef = userRef.child(Constants.NODE_EXPENDITURE_THIS_MONTH);
        expendableAmtRef = userRef.child(Constants.NODE_EXPENDABLE);
        lowAmountRef = userRef.child(Constants.NODE_LOW_AMT);
        toBuyRef = userRef.child(Constants.NODE_TO_BUY);
    }

    public static void configureDatabaseForUser(MainActivity activity){
        userRef = appDatabase.getReference().child(manager.getUserEmail());
        setUpDbReferences();

        activity.detachFirebaseListeners();
        activity.attachFirebaseListeners();
    }


}
