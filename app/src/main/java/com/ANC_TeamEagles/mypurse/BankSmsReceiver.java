package com.ANC_TeamEagles.mypurse;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by nezspencer on 9/8/17.
 */

public class BankSmsReceiver extends BroadcastReceiver {

    private static final String QUERY_STRING = "bank";


    public BankSmsReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Sender"," message received");
        showToast(context,"message received! ");

        String sender = "";
        String body ="";

        for (SmsMessage message: Telephony.Sms.Intents.getMessagesFromIntent(intent)){
            sender = message.getDisplayOriginatingAddress().toLowerCase();
            Log.e("Sender",sender);
            showToast(context,"sender: "+sender);
            if (sender.contains(QUERY_STRING))
            {
                body = message.getMessageBody();
                Log.e("Sender",body);
                Toast.makeText(context,body,Toast.LENGTH_LONG).show();

                SmsProcessingService.startSmsProcessingIntent(body, context);

            }
        }
    }

    public void showToast(Context context, String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }

}
