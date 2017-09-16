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

        if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)){

            String sender = "";
            String body ="";

            for (SmsMessage message: Telephony.Sms.Intents.getMessagesFromIntent(intent)){
                sender = message.getDisplayOriginatingAddress().toLowerCase();
                Log.e("Sender",sender);
                if (sender.contains(QUERY_STRING))
                {
                    body = message.getMessageBody();
                    Log.e("Sender",body);
                    Toast.makeText(context,body,Toast.LENGTH_LONG).show();

                    SmsProcessingService.startSmsProcessingIntent(body, context);

                }
            }
        }
    }
}
