package com.axxezo.MobileReader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.device.ScanManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{


    private TextView TextViewFullname;
    private TextView TextViewRut;
    private ImageView imageview;
    DatabaseHelper db = new DatabaseHelper(this);
    private final static String SCAN_ACTION = "urovo.rcv.message";//扫描结束action
    private Vibrator mVibrator;
    private ScanManager mScanManager;
    private SoundPool soundpool = null;
    private int soundid;
    private String barcodeStr;
    private boolean isScaning = false;
    private Spinner combobox;
    MediaPlayer mp3Dennied;
    MediaPlayer mp3Permitted;
    MediaPlayer mp3Error;
    private static MainActivity mInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mInstance=this;
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        TextViewFullname = (TextView) findViewById(R.id.fullname);
        TextViewRut = (TextView) findViewById(R.id.rut);
        imageview = (ImageView) findViewById(R.id.imageView);
        mp3Dennied = MediaPlayer.create(MainActivity.this, R.raw.bad);
        mp3Permitted = MediaPlayer.create(MainActivity.this, R.raw.good);
        mp3Error = MediaPlayer.create(MainActivity.this, R.raw.error);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //example API functionality
        //Log.i("getApiRoutes", new getAPIroutes().execute().toString());
        // Log.i("getApiPorts", new getAPIPorts(2).execute().toString());
        //Log.i("getApiTransports", new getAPITransports(2, 5, "2016-11-12").execute().toString());
        //Log.i("getApiHours", new getAPIHours(2, 5, 3, "2016-11-12").execute().toString());
        //Log.i("getApiManifest", new getAPIManifest(2, 5, 3, "2016-11-12", "23:30").execute().toString());
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.nav_settings){
            Intent intent = new Intent(this, Configuration.class);
            startActivity(intent);
        }
        if(id==R.id.nav_records){
            Intent intent = new Intent(this, lastRecordsList.class);
            startActivity(intent);
        }


        // then close the drawer Layout
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private BroadcastReceiver mScanReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            // TODO Auto-generated method stub
            if (mp3Error.isPlaying()) mp3Error.stop();
            if (mp3Dennied.isPlaying()) mp3Dennied.stop();
            if (mp3Permitted.isPlaying()) mp3Permitted.stop();

            isScaning = false;
            //soundpool.play(soundid, 1, 1, 0, 0, 1);

            mVibrator.vibrate(100);
            //reset();

            byte[] barcode = intent.getByteArrayExtra("barocode");
            int barocodelen = intent.getIntExtra("length", 0);
            byte barcodeType = intent.getByteExtra("barcodeType", (byte) 0);
            Log.i("codetype", String.valueOf(barcodeType));
            barcodeStr = new String(barcode, 0, barocodelen);
            String rawCode = barcodeStr;
            Log.i("Raw Barcode", rawCode);

            int flag = 0; // 0 for end without k, 1 with k

            if (barcodeType == 28) { // QR code
                Log.i("Debugger", "QR");
                // get just run
                barcodeStr = barcodeStr.substring(
                        barcodeStr.indexOf("RUN=") + 4,
                        barcodeStr.indexOf("&type"));
                // remove dv.
                barcodeStr = barcodeStr.substring(0, barcodeStr.indexOf("-"));
            } else if (barcodeType == 17) { // PDF417
                Log.i("Debugger", "PDF417");
                barcodeStr = barcodeStr.substring(0, 9);
                barcodeStr = barcodeStr.replace(" ", "");
                if (barcodeStr.endsWith("K")) {
                    flag = 1;
                }

                // Define length of character.
                if (Integer.parseInt(barcodeStr) > 400000000 && flag == 0) {
                    barcodeStr = barcodeStr.substring(0, barcodeStr.length() - 2);
                } else if (flag == 0) {
                    barcodeStr = barcodeStr.substring(0, barcodeStr.length() - 1);
                }

                // Get name from DNI.
                String[] array = rawCode.split("\\s+"); // Split by whitespace.
                try {
                    TextViewFullname.setText(array[1].substring(0, array[1].indexOf("CHL")));
                } catch (ArrayIndexOutOfBoundsException e) {
                    TextViewFullname.setText(array[2].substring(0, array[2].indexOf("CHL")));
                } catch (Exception ex) {
                    TextViewFullname.setText("");
                }
            }

            barcodeStr = barcodeStr.replace("k", "");
            barcodeStr = barcodeStr.replace("K", "");

            Log.i("Cooked Barcode", barcodeStr);
            writeLog("Cooked Barcode", barcodeStr);

            try {
                TextViewRut.setText(barcodeStr);
                // Buil record object

                //Send to AccessControl API
                Record record = new Record();
                if (!TextViewFullname.getText().toString().isEmpty())
                    record.setPerson_fullname(TextViewFullname.getText().toString());
                record.setPerson_run(barcodeStr);
            } catch (NullPointerException e) {
                e.printStackTrace();
                writeLog("ERROR", e.toString());
                //new GetPeopleTask().execute(server + "/employee/" + barcodeStr);
            }
        }
    };

    public String getCurrentDateTime() {
        Calendar cal = Calendar.getInstance();
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("dd-MM-yyy HH:mm:ss");
        String localTime = date.format(currentLocalTime);
        return localTime;
    }

    public void writeLog(String LogType, String content) {
        String filename = "AccessControl.log";
        String message = getCurrentDateTime() + " [" + LogType + "]" + ": " + content + "\n";
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput(filename, Context.MODE_APPEND);
            outputStream.write(message.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            writeLog("ERROR", e.toString());
        }
    }

    private void initScan() {
        // TODO Auto-generated method stub
        mScanManager = new ScanManager();
        mScanManager.openScanner();

        mScanManager.switchOutputMode(0);
        soundpool = new SoundPool(1, AudioManager.STREAM_NOTIFICATION, 100); // MODE_RINGTONE
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if (mScanManager != null) {
            mScanManager.stopDecode();
            isScaning = false;
        }
        unregisterReceiver(mScanReceiver);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        initScan();
        IntentFilter filter = new IntentFilter();
        filter.addAction(SCAN_ACTION);
        registerReceiver(mScanReceiver, filter);
    }

    public void reset() {
        try {
            initScan();
            IntentFilter filter = new IntentFilter();
            filter.addAction(SCAN_ACTION);
            registerReceiver(mScanReceiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clean() {
        barcodeStr = "";
    }






}
