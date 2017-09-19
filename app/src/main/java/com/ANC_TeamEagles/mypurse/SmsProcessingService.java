package com.ANC_TeamEagles.mypurse;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ANC_TeamEagles.mypurse.pojo.TransactionItem;
import com.ANC_TeamEagles.mypurse.utils.Constants;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import static com.ANC_TeamEagles.mypurse.App.accountBalanceRef;
import static com.ANC_TeamEagles.mypurse.App.expendableAmtRef;
import static com.ANC_TeamEagles.mypurse.App.monthlyTransactionReference;
import static com.ANC_TeamEagles.mypurse.App.thisMonthExpenseRef;
import static com.ANC_TeamEagles.mypurse.App.todayExpenseRef;
import static com.ANC_TeamEagles.mypurse.App.transactionReference;
import static com.ANC_TeamEagles.mypurse.App.weeklyTransactionRef;

/**
 * Created by nezspencer on 9/8/17.
 */

public class SmsProcessingService extends IntentService {

    private static final String EXTRA_MSG_BODY = "msg body";
    private static final String EXTRA_IS_INCOME ="transaction type";

    private static final String [] TRANSAC_TYPE = {"cr","credit","credited","dr","debit","debited"};

    private static final String START_TOKEN = "NGN";
    private boolean isIncome;

    public SmsProcessingService() {
        super(SmsProcessingService.class.getName());
    }

    public static void startSmsProcessingIntent(String msgBody, Context context){

        Intent processIntent = new Intent(context,SmsProcessingService.class);
        processIntent.putExtra(EXTRA_MSG_BODY,msgBody);
        context.startService(processIntent);

    }



    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String msgBody = null;
        if (intent.hasExtra(EXTRA_MSG_BODY))
             msgBody = intent.getStringExtra(EXTRA_MSG_BODY);

        if (msgBody != null)
            processMsgBody(msgBody);
    }

    public void processMsgBody(String body){
        String amtInWrds = "";
        int start = body.indexOf(START_TOKEN);
        int stop = body.indexOf(".");

        amtInWrds = body.substring(start+START_TOKEN.length(),stop);
        amtInWrds = amtInWrds.replace(",","");

        String [] wordsInMessage = body.split(" ");

        for (int i = 0; i < TRANSAC_TYPE.length; i++){
            boolean matchFound = false;

            for (String word : wordsInMessage){
                if (word.equalsIgnoreCase(TRANSAC_TYPE[i])){
                    isIncome = i <= 2;
                    matchFound = true;
                    break;
                }

            }
            if (matchFound)
                break;
        }

        saveToFirebase(amtInWrds,isIncome);
    }

    public void saveToFirebase(String amount, boolean isIncome) {

        try{
            Double transacAmt = Double.valueOf(amount);
            double previousThisMonthTotal = PreferenceManager.getDefaultSharedPreferences(this).getLong(Constants
                    .KEY_MONTH_EXPENSE, 0);
            double previousTodayTotal = PreferenceManager.getDefaultSharedPreferences(this).
                    getLong(Constants.KEY_TODAY_EXPENSE, 0);
            double expendableAmt = PreferenceManager.getDefaultSharedPreferences(this)
                    .getLong(Constants.KEY_AMOUNT_TO_SPEND, 0);

            double currentAccountBalance = PreferenceManager.getDefaultSharedPreferences(this)
                    .getLong(Constants.KEY_ACC_BAL_AMT, 0);

            Calendar calendar = Calendar.getInstance();

            long now = System.currentTimeMillis();
            calendar.setTimeInMillis(now);
            String day = calendar.getDisplayName(Calendar.DAY_OF_WEEK,Calendar.LONG, Locale
                    .ENGLISH);
            String month = calendar.getDisplayName(Calendar.MONTH,Calendar.LONG, Locale
                    .ENGLISH);

            if (isIncome) {
                currentAccountBalance += transacAmt;
            } else {
                currentAccountBalance -= transacAmt;
                weeklyTransactionRef.child(day).setValue(previousTodayTotal + transacAmt);
                monthlyTransactionReference.child(month).setValue(previousThisMonthTotal +
                        transacAmt);
                todayExpenseRef.setValue(previousTodayTotal + transacAmt);
                thisMonthExpenseRef.setValue(previousThisMonthTotal + transacAmt);
                expendableAmt -= transacAmt;
                expendableAmtRef.setValue(expendableAmt);
            }

            TransactionItem item = new TransactionItem(transacAmt,"bank transaction",day+"_"+month,
                    currentAccountBalance,isIncome,now);

            accountBalanceRef.setValue(currentAccountBalance);
            String key = transactionReference.push().getKey();

            HashMap<String, Object> map= new HashMap<>();
            map.put("/"+key, item);
            transactionReference.updateChildren(map);
        }

        catch(NumberFormatException e){
            Log.d(MainActivity.TAG,amount+" is not a number");
        }
    }

}
