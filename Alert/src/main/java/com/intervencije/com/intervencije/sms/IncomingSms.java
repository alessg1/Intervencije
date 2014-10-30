package com.intervencije.com.intervencije.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;
import android.view.WindowManager;


import com.intervencije.alertActivity;

/**
 * Created by Nejc on 16.11.2013.
 */
public class IncomingSms extends BroadcastReceiver {

    private static final String SMS_RECIVED = "android.provider.Telephony.SMS_RECEIVED";

    SharedPreferences settings;

    @Override
    public void onReceive(Context context, Intent intent) {
        //SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
        //String trigger = SP.getString("triger", "test");

        settings = PreferenceManager.getDefaultSharedPreferences(context);

        String ric1 = "", ric2 = "", ric3 = "", ric4 = "";
        String triger = "";

        boolean useRic = false;

        boolean handleSMS = false;

        if (settings.getBoolean("checkboxRICtriger", false)) {
            ric1 = settings.getString("ric1", "");
            ric2 = settings.getString("ric2", "");
            ric3 = settings.getString("ric3", "");
            ric4 = settings.getString("ric4", "");

            useRic = true;
        } else {
            triger = settings.getString("triger_text", "ReCO");
        }

        if (intent.getAction().equals(SMS_RECIVED)) {
            Bundle bundle = intent.getExtras();
            Object[] pdus = (Object[]) bundle.get("pdus");
            SmsMessage smsMsg = SmsMessage.createFromPdu((byte[]) pdus[0]);

            if (useRic) {
                //filter by RIC number
                String messageBody = smsMsg.getMessageBody();
                if (ric1 != "")
                    if (messageBody.contains(ric1))
                        handleSMS = true;
                if (ric2 != "")
                    if (messageBody.contains(ric2))
                        handleSMS = true;
                if (ric3 != "")
                    if (messageBody.contains(ric3))
                        handleSMS = true;
                if (ric4 != "")
                    if (messageBody.contains(ric4))
                        handleSMS = true;

            } else {
                if (smsMsg.getMessageBody().contains(triger))
                    handleSMS = true;
            }

            if (handleSMS) {
                abortBroadcast();
                Intent startAlertIntent = new Intent(context, alertActivity.class);
                startAlertIntent.putExtra("SMS", smsMsg.getMessageBody().toString());
                startAlertIntent.putExtra("ADRESS", smsMsg.getOriginatingAddress().toString());
                //startAlertIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startAlertIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(startAlertIntent);
            }
        }

    }
}

