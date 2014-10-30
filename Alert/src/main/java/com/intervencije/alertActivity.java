package com.intervencije;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.intervencije.com.intervencije.sms.SmsDataSource;
import com.intervencije.com.intervencije.sms.SqlLiteHelper;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Nejc on 16.11.2013.
 */
public class alertActivity extends Activity {

    private Vibrator vibrator;
    private Dialog dialog;
    private Ringtone ringtone;
    private SharedPreferences settings;
    private KeyguardManager keyguardManager;
    private KeyguardManager.KeyguardLock keyguardLock;
    private PowerManager.WakeLock mWakeLock;
    private AlertDialog.Builder dialogBack;

    private boolean oldOSversion = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //android.os.Debug.waitForDebugger();

        if (Build.VERSION.SDK_INT <= 8)
            oldOSversion = true;


        settings = PreferenceManager.getDefaultSharedPreferences(this);

        //set vibrator
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        //get content of sms
        String bodyOfMessage = getIntent().getExtras().getString("SMS").toString();
        String number = getIntent().getExtras().getString("ADRESS").toString();

        saveSmsToInbox(bodyOfMessage, number);

        //if phone is locked and has turned off screen turn ON and ulock key
        // turnScreenOnAndKeyGuardOFF();
        //save sms to phone database
        saveToDatabase(number,bodyOfMessage);

        //pager sound
        Uri alert = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.de900loop);

        ringtone = RingtoneManager.getRingtone(getApplicationContext(), alert);

        if (settings.getBoolean("checkboxRingIFsilent", true))
            ringtone.setStreamType(AudioManager.STREAM_ALARM);
        else
            ringtone.setStreamType(AudioManager.STREAM_RING);
        //ringtone.play();


        //Alert Dialog
        dialog = new Dialog(this, R.style.AlertDialogTheme);
        dialog.setContentView(R.layout.alert_dialog);

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);

        //dialog.setTitle("Poziv...");

        // set the custom dialog components - text, image and button
        TextView text = (TextView) dialog.findViewById(R.id.textViewBody);
        text.setText(bodyOfMessage);


        Button dialogButtonYes = (Button) dialog.findViewById(R.id.buttonYes);
        // if button is clicked, close the custom dialog
        dialogButtonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stopVibrate();
                ringtone.stop();
                dialog.dismiss();

                Log.e("KLICNA ŠTEVILKA YES", settings.getString("alarm_yes", ""));
                String alarmYes = settings.getString("alarm_yes", "");

                if(alarmYes !="")
                    callNumber(alarmYes);
                else
                    Toast.makeText(getApplicationContext(), "Nastavi, klicne številke!", Toast.LENGTH_SHORT).show();

                if (oldOSversion) {
                    keyguardLock.reenableKeyguard();
                    if (mWakeLock.isHeld()) {
                        mWakeLock.release();
                    }
                }

                finish();
            }
        });

        Button dialogButtonNo = (Button) dialog.findViewById(R.id.buttonNo);
        // if button is clicked, close the custom dialog
        dialogButtonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stopVibrate();
                ringtone.stop();
                dialog.dismiss();

                String alarmNo = settings.getString("alarm_no", "");

                if(alarmNo !="")
                    callNumber(alarmNo);
                else
                    Toast.makeText(getApplicationContext(), "Nastavi, klicne številke!", Toast.LENGTH_SHORT).show();

                if (oldOSversion) {
                    keyguardLock.reenableKeyguard();
                    if (mWakeLock.isHeld()) {
                        mWakeLock.release();
                    }
                }

                finish();
            }
        });

        Button dialobButtonLater = (Button) dialog.findViewById(R.id.buttonLater);
        // if button is clicked, close the custom dialog
        dialobButtonLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stopVibrate();
                ringtone.stop();
                dialog.dismiss();

                String alarmLater = settings.getString("alarm_later", "");

                if(alarmLater !="")
                    callNumber(alarmLater);
                else
                    Toast.makeText(getApplicationContext(), "Nastavi, klicne številke!", Toast.LENGTH_SHORT).show();

                if (oldOSversion) {
                    keyguardLock.reenableKeyguard();
                    if (mWakeLock.isHeld()) {
                        mWakeLock.release();
                    }
                }

                finish();
            }
        });


        dialogBack =  new AlertDialog.Builder(this);
        dialogBack.setTitle("Brez odziva!");
        dialogBack.setMessage("Ste prepričani, da se ne želite odzvati na poziv?");
        dialogBack.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                stopVibrate();
                ringtone.stop();
                dialog.dismiss();

                if (oldOSversion) {
                    keyguardLock.reenableKeyguard();
                    if (mWakeLock.isHeld()) {
                        mWakeLock.release();
                    }
                }

                finish();
            }
        });
        dialogBack.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogI, int which) {
                // do nothing
                dialog.show();
            }
        });

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialogBack.show();
            }
        });

        dialog.show();
    }


    public void turnScreenOnAndKeyGuardOFF() {
        if (!oldOSversion) {
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        } else{
            PowerManager mPowerManager = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
            mWakeLock = mPowerManager.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
                                                                         PowerManager.FULL_WAKE_LOCK |
                                                                         PowerManager.ACQUIRE_CAUSES_WAKEUP), "intervencije");
            if (mWakeLock.isHeld()) {
                mWakeLock.release();
            }
            mWakeLock.acquire();

            keyguardManager = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
            keyguardLock = keyguardManager.newKeyguardLock("intervencije");
            keyguardLock.disableKeyguard();
        }
    }

    public void saveToDatabase(String number,String content) {
        SmsDataSource smsDataSource = new SmsDataSource(this);

        try {
            smsDataSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        smsDataSource.createSMS(number,content, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        if (MainActivity.smsListAdapter != null)
            MainActivity.smsListAdapter.update(smsDataSource);

        smsDataSource.close();


    }

    public void vibrate() {
        long[] pattern = {0, 1000, 1000};

        // The '-1' here means to vibrate once
        // '0' would make the pattern vibrate indefinitely
        vibrator.vibrate(pattern, 0);
    }

    public void stopVibrate() {
        vibrator.cancel();
    }

    private void callNumber(String number) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + number));
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(callIntent);
    }

    private void saveSmsToInbox(String body, String adress) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("address", adress);
        contentValues.put("body", body);
        contentValues.put("date", java.lang.System.currentTimeMillis());

        getContentResolver().insert(Uri.parse("content://sms/inbox"), contentValues);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopVibrate();
        ringtone.stop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        vibrate();
        ringtone.play();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        turnScreenOnAndKeyGuardOFF();
    }
}
