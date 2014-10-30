package com.intervencije;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.intervencije.com.intervencije.greenList.FragmentGreen;
import com.intervencije.com.intervencije.greenList.GreenListAdapter;
import com.intervencije.com.intervencije.greenList.GreenListItems;
import com.intervencije.com.intervencije.redList.FragmentRed;
import com.intervencije.com.intervencije.redList.RedListAdapter;
import com.intervencije.com.intervencije.redList.RedListItems;
import com.intervencije.com.intervencije.sms.FragmentSms;
import com.intervencije.com.intervencije.sms.SmsDataSource;
import com.intervencije.com.intervencije.sms.SmsListAdapter;
import com.intervencije.com.intervencije.sms.SmsListItems;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    public static GreenListAdapter greenListAdapter;
    public static RedListAdapter redListAdapter;
    public static SmsListAdapter smsListAdapter;
    public static boolean downloadThreadRunning = false;

    public FragmentGreen fragmentGreen;
    public FragmentRed fragmentRed;
    public FragmentSms fragmentSms;

    public SharedPreferences settings;

    public SmsDataSource smsDataSource;


    private Object mActionMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);


        //greenListAdapter = new GreenListAdapter(this,getTestData());
        //redListAdapter = new RedListAdapter(this,getTestDataRed());

        greenListAdapter = new GreenListAdapter(this, new ArrayList());
        redListAdapter = new RedListAdapter(this, new ArrayList());


        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentRed = new FragmentRed();

        fragmentGreen = new FragmentGreen();
        //fragmentTransaction.add(R.id.pager,fragmentGreen);
        // fragmentTransaction.commit();

        fragmentSms = new FragmentSms();

        settings = PreferenceManager.getDefaultSharedPreferences(this);

        smsDataSource = new SmsDataSource(this);

        try {
            smsDataSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        smsListAdapter = new SmsListAdapter(this, smsDataSource.getAllSms());

        smsDataSource.close();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        if (!downloadThreadRunning)
            new Thread(new DownloadThread()).start();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingActivity.class);
                this.startActivity(intent);
                break;
            case R.id.action_refresh:
                if (!downloadThreadRunning)
                    new Thread(new DownloadThread()).start();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if (position == 0)
                return fragmentSms;
            if (position == 1)
                return fragmentGreen;
            else
                return fragmentRed;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.tab1).toUpperCase(l);
                case 1:
                    return getString(R.string.tab2).toUpperCase(l);
                case 2:
                    return getString(R.string.tab3).toUpperCase(l);
            }
            return null;
        }


    }

    private ArrayList getTestData() {
        ArrayList result = new ArrayList();

        GreenListItems item = new GreenListItems();
        item.setName("Nejc");
        item.setSurname("Šimnic");
        item.setDriverLicence("C");
        item.setSpecialThings("IDA, TRE; Bolnicar");
        item.setTimeOfcall("20:22");
        result.add(item);

        item = new GreenListItems();
        item.setName("Matic");
        item.setSurname("Šimnic");
        item.setDriverLicence("C");
        item.setSpecialThings("IDA, TRE");
        item.setTimeOfcall("20:23");
        result.add(item);

        item = new GreenListItems();
        item.setName("Gašper");
        item.setSurname("Finžgar");
        item.setDriverLicence("C, B");
        item.setSpecialThings("TRE");
        item.setTimeOfcall("18:22");
        result.add(item);

        item = new GreenListItems();
        item.setName("Nejc");
        item.setSurname("Šimnic");
        item.setDriverLicence("C");
        item.setSpecialThings("IDA, TRE; Bolnicar");
        item.setTimeOfcall("20:22");
        result.add(item);

        return result;
    }

    private ArrayList getTestDataRed() {
        ArrayList result = new ArrayList();

        RedListItems item = new RedListItems();
        item.setName("Nejc");
        item.setSurname("Šimnic");
        result.add(item);

        item = new RedListItems();
        item.setName("Matic");
        item.setSurname("Šimnic");
        result.add(item);


        return result;
    }


    private ArrayList getTestDataSms() {
        ArrayList result = new ArrayList();

        SmsListItems item = new SmsListItems();
        item.setBody("to je sporočilo");
        //item.setDateAndTime("15.11.2013 16:46");
        result.add(item);

        item = new SmsListItems();
        item.setBody("to je sporočilo2");
        //item.setDateAndTime("18.1.2012 03:23");
        result.add(item);


        return result;
    }

    public class DownloadThread implements Runnable {
        Document doc;
        ArrayList greenList = new ArrayList();
        GreenListItems greenListItem;

        ArrayList redList = new ArrayList();
        RedListItems redListItem;

        @Override
        public void run() {
            try {
                downloadThreadRunning = true;

                runOnUiThread(new Runnable() {
                    public void run() {
                        setMessageGreenList("");
                        setMessageRedList("");
                        Toast.makeText(getApplicationContext(), "Osveževanje...", Toast.LENGTH_SHORT).show();
                    }
                });

                Thread.sleep(1000);

                boolean settingsOK = true;
                String connectionString = settings.getString("intervencijska_lista", "");
                String verify = getResources().getString(R.string.alarm_list_verify_string);
                if(connectionString.contains(verify))
                {
                    connectionString = connectionString.replace(verify,"");
                    if(connectionString.equals(""))
                        settingsOK = false;
                }else{
                    settingsOK = false;
                }


                if (settingsOK) {
                    //look if internet is on
                    ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
                    if (cd.isConnectingToInternet()) {
                        doc = Jsoup.connect(settings.getString("intervencijska_lista", "")).get();

                        boolean personYellow = false;
                        Elements table1 = doc.getElementsByClass("intervlisttable");
                        for (Element row : table1.select("tr")) {
                            Elements style = row.select("tr[style]");
                            if (style.size() >= 1)
                                personYellow = true;
                            Elements data = row.select("td");
                            if (data.size() >= 3) {
                                String person = data.get(1).text() + " " + data.get(2).text() + " " + data.get(3).text() + " " + data.get(4).text() + " " + data.get(5).text() + " " + data.get(6).text();

                                Log.w("LIST", person);

                                greenListItem = new GreenListItems();
                                greenListItem.setName(data.get(1).text());
                                greenListItem.setSurname(data.get(2).text());
                                greenListItem.setDriverLicence(data.get(3).text());
                                greenListItem.setSpecialThings(data.get(4).text());
                                greenListItem.setTimeOfcall(data.get(5).text());
                                greenListItem.setPersonYellow(personYellow);
                                greenList.add(greenListItem);
                            }
                            personYellow = false;
                        }

                        Elements table2 = doc.getElementsByClass("intervlisttable2");
                        for (Element row : table2.select("tr")) {
                            Elements data = row.select("td");
                            if (data.size() >= 3) {
                                String person = data.get(1).text() + " " + data.get(2).text();

                                Log.w("LISTRed", person);

                                redListItem = new RedListItems();
                                redListItem.setName(data.get(1).text());
                                redListItem.setSurname(data.get(2).text());
                                redList.add(redListItem);
                            }
                        }
                        runOnUiThread(new Runnable() {
                            public void run() {
                                greenListAdapter.updateGreenList(greenList);
                                redListAdapter.updateRedList(redList);

                                if (greenList.size() == 0)
                                    setMessageGreenList(getResources().getString(R.string.alarm_list_empty));
                                if (redList.size() == 0)
                                    setMessageRedList(getResources().getString(R.string.alarm_list_empty));
                            }
                        });
                    } else {
                        //no internet connection

                        runOnUiThread(new Runnable() {
                            public void run() {
                                greenListAdapter.clear();
                                redListAdapter.clear();

                                setMessageGreenList(getResources().getString(R.string.alarm_list_no_connection));
                                setMessageRedList(getResources().getString(R.string.alarm_list_no_connection));

                            }
                        });

                        Log.w("DowloadThred", "NoInternetConnection");
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            greenListAdapter.clear();
                            redListAdapter.clear();

                            setMessageGreenList(getResources().getString(R.string.alarm_list_setting_problems));
                            setMessageRedList(getResources().getString(R.string.alarm_list_setting_problems));

                        }
                    });
                }

            } catch (Exception ex) {
                Log.e("Napaka", ex.getMessage());
                runOnUiThread(new Runnable() {
                    public void run() {
                        setMessageGreenList(getResources().getString(R.string.alarm_list_error));
                        setMessageRedList(getString(R.string.alarm_list_error));
                    }
                });
            } finally {
                downloadThreadRunning = false;

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Konec...", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void setMessageGreenList(String text) {
        TextView textView = (TextView) findViewById(R.id.textViewMessagesGreen);
        if (textView != null)
            textView.setText(text);
    }

    private void setMessageRedList(String text) {
        TextView textView = (TextView) findViewById(R.id.textViewMessagesRed);
        if (textView != null)
            textView.setText(text);
    }
}
