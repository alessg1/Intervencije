package com.intervencije;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;


public class SettingActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);


        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("checkboxRICtriger", false))
            findPreference("triger_text").setEnabled(false);
        else
            findPreference("triger_text").setEnabled(true);


        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("checkboxRICtriger")) {
            boolean isEnabled = sharedPreferences.getBoolean("checkboxRICtriger", false);
            if (isEnabled)
                findPreference("triger_text").setEnabled(false);
            else
                findPreference("triger_text").setEnabled(true);
        }
    }
}