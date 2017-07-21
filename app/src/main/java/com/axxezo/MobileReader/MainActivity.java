package com.axxezo.MobileReader;

/*
                      _..--''`-..._
                    -'             `-.
                 .-'                  `-.
                /   .-'''''''''--.       \
                |  /__........_   `-.    |
                 \ / -. .--._  ``-._ |   /
                  / /\ V //  `--._ -`-  /
                 / / || | |       \\ \`/
                  |  || |\|        \\ \
                 /| // /-._   _... ||  \
                //\ \ / <o>   <o>  ||\\|   TO PROTECT
                /  \//     /      // |\\    AND SERVE
                \| \\_\    ._/   // /|//
                 |\ //|\  .__.   / / |//
                  \|/// \ `=='  / / ///
                   \//  |\    .' / // -.
                       /-.`--' -'.' |   `------.
                _..---' \ `-..-'   /            \            .
             .-'         \  /@ \  /              \         . \\
            |     .$.     \/.--.\/_               \       /|#||
            /     $$$  _-'         `-.       /     .    .'  #||
           .   \  `$' /               \      |     |   :   _#/
           |    |    /           /    /      \     |   |  / #
           |    /    `.     \   /    /        \    |   /  : 6
           |   /       `-.      .   /          |   |  /   ' 9
           |  |           `-/.___.-'           |.==\.'   /.-6-.
           |  |            /      \            |   /    /((   ))
           |`= \        ..-    @   \          /   /    :  `-.-'
          |     `..___--            `--..__.-'  .'     :
          '      |\            @            /| /      /
         /       :|___________...._________/ \       /
        .        /|                       /   \     /
        :       / /                      /     `._.'
        |     .'./ \                    /
        |     :/    `.                 |
        |    /'       `-.              |
        :   /            `-.  c     _.'\
       /   /                `-..--''    \
      /   /       .                     |
     /   /`-.       .       @           |
    .-- \    `-._    .             .    |
  _/    \\_______\______________________/____
((_\\\\\|u((_________________________________)
    `---|             \       __..---''
        \              \ _.--'        |
                        /             /
         \             /
                      |              /
          \           |
           \          |             /
            \         |    :F_P:   /
             \        |           /*/

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.device.ScanManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.axxezo.MobileReader.R.id.status;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private static String token_navieraAustral = "860a2e8f6b125e4c7b9bc83709a0ac1ddac9d40f";
    private static String token_transportesAustral = "49f89ee1b7c45dcca61a598efecf0b891c2b7ac5";
    private TextView TextViewFullname;
    private EditText TextViewRut;
    private TextView TextViewStatus;
    private TextView TextViewManifestUpdate;
    private ImageView imageview;
    private final static String SCAN_ACTION = "urovo.rcv.message";//action
    private Vibrator mVibrator;
    private ScanManager mScanManager;
    private String barcodeStr;
    private boolean isScaning = false;
    MediaPlayer mp3Dennied;
    MediaPlayer mp3Permitted;
    MediaPlayer mp3Error;
    private static String AxxezoAPI;
    private boolean is_input = true;
    private Switch mySwitch;
    private Spinner comboLanded;
    private String selectedSpinnerLanded;
    private int selectedIntSpinnerLanded;
    private log_app log;
    private String updateTimePeople;
    private String TextViewTimePeople;
    public ArrayAdapter<String> adapter;

    /********
     * Timers Asyntask
     ****/
    private int timer_asyncUpdateManifest;
    private int timer_sendRecordsAPI;
    private int timer_asyncUpdatePeopleState;
    private int timer_asyncDeletePeopleManifest;

    /*****
     * Asyntask declarations.....
     */
    private RegisterTask Asynctask_sendRecord; //asyntask that send data to api axxezo
    private asyncTask_updatePeopleManifest AsyncTask_updatePeopleManifest;//asyntask to update in realtime new people inserts in manifest
    private AsyncUpdateStateManifest AsynTask_UpdateStateManifest;// asyntask to update states of people insert in manifest table;
    // private AsyncUpdateStateManifest AsynTask_UpdateDeletePerson;// asyntask to update states of people insert in manifest table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        TextViewFullname = (TextView) findViewById(R.id.fullname);
        TextViewRut = (EditText) findViewById(R.id.rut);
        TextViewStatus = (TextView) findViewById(status);
        TextViewManifestUpdate = (TextView) findViewById(R.id.textView_lastManifestUpdate);
        comboLanded = (Spinner) findViewById(R.id.spinner_setLanded);
        imageview = (ImageView) findViewById(R.id.imageView);
        mp3Dennied = MediaPlayer.create(MainActivity.this, R.raw.bad);
        mp3Permitted = MediaPlayer.create(MainActivity.this, R.raw.good);
        mp3Error = MediaPlayer.create(MainActivity.this, R.raw.error);
        mySwitch = (Switch) findViewById(R.id.mySwitch);
        selectedIntSpinnerLanded = -1;
        log = new log_app();
        selectedSpinnerLanded = "";
        TextViewTimePeople = "";


        //asign timers to Asyntask
        timer_sendRecordsAPI = 30000;                           //30 sec=30.000
        timer_asyncUpdateManifest = 120000;                     //2 min =120.000
        timer_asyncUpdatePeopleState = 15000;                   //15 sec=15.000
        timer_asyncDeletePeopleManifest = 420000;               //7 min =420000
        //asign url api axxezo
        //AxxezoAPI = "http://axxezo-test.brazilsouth.cloudapp.azure.com:5002/api";
        // AxxezoAPI = "http://192.168.1.102:9001/api";
        //AxxezoAPI = "http://bm03.bluemonster.cl:9001/api";
        AxxezoAPI = "http://axxezocloud.brazilsouth.cloudapp.azure.com:5002/api";


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mVibrator.vibrate(100);
                    if (TextViewRut.getText().toString().isEmpty()) {
                        TextViewRut.setHint("Ingrese Rut");
                        TextViewRut.setHintTextColor(Color.RED);
                        TextViewRut.requestFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(TextViewRut, InputMethodManager.SHOW_IMPLICIT);
                    } else {
                        PeopleValidator(TextViewRut.getText().toString().trim(), "", "", 17);
                    }
                }
            });
        }
        //second fab, to show information about travel
        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        if (fab2 != null)
            fab2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatabaseHelper db = DatabaseHelper.getInstance(getApplicationContext());
                    String switchState = mySwitch.isChecked() ? "Embarque" : "Desembarque";
                    String text = "Ruta: " + db.selectFirst("select route_name from config") +
                            "\nItinerario: " + db.selectFirst("select route_id from config") +
                            "\nPasajeros Puerto: " + selectedSpinnerLanded +
                            "\nPasajeros Embarcados:" + db.selectFirst("select count(id) from manifest where is_inside=1 and origin=(select id_mongo from ports where name='" + selectedSpinnerLanded + "')") +
                            "\nPasajeros Pendientes:" + db.selectFirst("select count(id) from manifest where is_inside=0 and origin =(select id_mongo from ports where name='" + selectedSpinnerLanded + "')") +
                            "\nUltima Actualizacion Manifiesto: " + TextViewTimePeople;
                    Snackbar snack = Snackbar.make(view, text, Snackbar.LENGTH_LONG)
                            .setAction("Action", null);
                    View view1 = snack.getView();
                    TextView information = (TextView) view1.findViewById(android.support.design.R.id.snackbar_text);
                    information.setTextColor(Color.WHITE);
                    information.setMaxLines(7);
                    snack.show();
                }
            });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (drawer != null) {
            drawer.addDrawerListener(toggle);
        }
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }

        // set by default
        //is_input = true;
        mySwitch.setChecked(true);
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    is_input = true;
                } else {
                    is_input = false;
                }
            }
        });
        fillSpinner();
        //call in oncreate asyntask
        sendRecordstoAPI();
        asyncUpdateManifestinTime();
        asyncUpdateManifestState(); //pending change values from string to integer
        getWindow().getDecorView().findViewById(R.id.content_main).invalidate();
       /* DatabaseHelper db=DatabaseHelper.getInstance(this);
        db.insert("update config set date_last_update='" + getCurrentDateTime("yyyy-MM-dd'T'HH:mm:ss") + "' where id=1");
        updateTimePeople = db.selectFirst("select date_last_update from config");
        if (!updateTimePeople.isEmpty() && updateTimePeople != null && !updateTimePeople.equals("null")) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date newDate = null;
            try {
                newDate = format.parse(updateTimePeople);
                format = new SimpleDateFormat("HH:mm");
                String date = format.format(newDate);
                if (!date.isEmpty() || !date.equals("null") || date != null)
                    TextViewTimePeople = date;
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }*/


    }

    public void fillSpinner() {
        //enable WAL mode in DB
        DatabaseHelper db = DatabaseHelper.getInstance(this);
        //db.setWriteAheadLoggingEnabled(true);
        //fill information in combobox
        Cursor getOriginandDestination = db.select("select name from ports order by name desc");
        ArrayList<String> listOriginDestination = new ArrayList<String>();
        if (getOriginandDestination != null && getOriginandDestination.getCount() > 0) {
            while (!getOriginandDestination.isAfterLast()) {
                if (getOriginandDestination.getString(0) != null)
                    listOriginDestination.add(getOriginandDestination.getString(0));
                getOriginandDestination.moveToNext();
            }
        }
        if (getOriginandDestination != null)
            getOriginandDestination.close();
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, listOriginDestination);
        comboLanded.setAdapter(adapter);
        comboLanded.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSpinnerLanded = comboLanded.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    @Override
    public void onBackPressed() {
       /* DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }*/
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_MainWindow) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        if (id == R.id.action_records) {
            Intent intent = new Intent(this, lastRecordsList.class);
            startActivity(intent);
        }
        if (id == R.id.action_find) {
            Intent intent = new Intent(this, find_people_in_manifest.class);
            startActivity(intent);
        }
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, Configuration.class);
            startActivityForResult(intent, 0);
        }
        if (id == R.id.action_manual_registration) {
            Intent intent = new Intent(this, manual_registration.class);
            startActivity(intent);
        }
        if (id == R.id.action_exit) {
            exitApp();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_mainWindow) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, Configuration.class);
            startActivityForResult(intent, 0);
        }
        if (id == R.id.nav_records) {
            Intent intent = new Intent(this, lastRecordsList.class);
            startActivity(intent);
        }
        if (id == R.id.nav_find) {
            Intent intent = new Intent(this, find_people_in_manifest.class);
            startActivity(intent);
        }
        if (id == R.id.nav_manual_registration) {
            Intent intent = new Intent(this, manual_registration.class);
            startActivity(intent);
        }
        if (id == R.id.nav_exit) {
            exitApp();
        }

        // then close the drawer Layout
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    /**
     * receive the information of barcod read and proccess that, once that extract dni of qr or barcode, send this to validate
     * in method PeopleValidator
     */
    private BroadcastReceiver mScanReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            // TODO Auto-generated method stub
            try {
                new LoadSound(4).execute();
                isScaning = false;
                mVibrator.vibrate(100);
                reset();

                byte[] barcode = intent.getByteArrayExtra("barocode");
                int barocodelen = intent.getIntExtra("length", 0);
                byte barcodeType = intent.getByteExtra("barcodeType", (byte) 0);
                barcodeStr = new String(barcode, 0, barocodelen);
                String rawCode = barcodeStr;

                int flag = 0; // 0 for end without k, 1 with k
                People person = new People();

                if (barcodeType == 28) { // QR code
                    if (barcodeStr.contains("client_code") && barcodeStr.contains("id_itinerary")) {
                        try { // Its a ticket
                            JSONObject json = new JSONObject(barcodeStr);
                            String doc = json.getString("client_code");

                            if (doc.contains("-")) {
                                doc = doc.substring(0, doc.indexOf("-"));
                            }
                            person.setDocument(doc);
                            barcodeStr = doc;
                            PeopleValidator(doc, json.getString("id_itinerary"), json.getString("port"), barcodeType);
                        } catch (JSONException e) {
                            log.writeLog(getApplicationContext(), "Main:line 368", "ERROR", e.getMessage());
                        }
                    } else if (rawCode.equals("CONFIG-AXX-6rVLydzn651RsZZ3dqWk")) {//configuration QR
                        Intent loadLog = new Intent(getApplicationContext(), log_show.class);
                        startActivity(loadLog);
                    } else if (rawCode.equals("close trip now")) {
                        if (comboLanded.getCount() > 0) {
                            Intent closeTrip = new Intent(getApplicationContext(), CloseTrip.class);
                            startActivity(closeTrip);
                        } else {
                            makeToast("No hay viaje configurado para cerrar");
                        }
                    } else if (barcodeStr.startsWith("https://")) { // Its a new DNI Cards.
                        barcodeStr = barcodeStr.substring(
                                barcodeStr.indexOf("RUN=") + 4,
                                barcodeStr.indexOf("&type"));
                        // Remove DV.
                        barcodeStr = barcodeStr.substring(0, barcodeStr.indexOf("-"));
                        PeopleValidator(barcodeStr, "", "", barcodeType);
                    } else if (!barcodeStr.contains("id_itinerary")) {
                        new LoadSound(1).execute();
                        TextViewFullname.setText("");
                        TextViewStatus.setText("QR INVALIDO");
                        imageview.setImageResource(R.drawable.img_false);
                    }
                }
                if (barcodeType == 17) { // PDF417->old dni
                    // 1.- validate if the rut is > 10 millions
                    String rutValidator = barcodeStr.substring(0, 8);
                    rutValidator = rutValidator.replace(" ", "");
                    rutValidator = rutValidator.endsWith("K") ? rutValidator.replace("K", "0") : rutValidator;
                    char dv = barcodeStr.substring(8, 9).charAt(0);
                    boolean isvalid = ValidarRut(Integer.parseInt(rutValidator), dv);
                    if (isvalid)
                        barcodeStr = rutValidator;
                    else { //try validate rut size below 10.000.000
                        rutValidator = barcodeStr.substring(0, 7);
                        rutValidator = rutValidator.replace(" ", "");
                        rutValidator = rutValidator.endsWith("K") ? rutValidator.replace("K", "0") : rutValidator;
                        dv = barcodeStr.substring(7, 8).charAt(0);
                        isvalid = ValidarRut(Integer.parseInt(rutValidator), dv);
                        if (isvalid)
                            barcodeStr = rutValidator;
                        else {
                            log.writeLog(getApplicationContext(), "Main:line 412", "ERROR", "rut invalido " + barcodeStr);
                            barcodeStr = "";
                            TextViewStatus.setText("RUT INVALIDO");
                        }
                    }
                    // Get name from DNI.
                    String[] array = rawCode.split("\\s+"); // Split by whitespace.
                    try {
                        TextViewFullname.setText(array[1].substring(0, array[1].indexOf("CHL")));
                    } catch (ArrayIndexOutOfBoundsException e) {
                        TextViewFullname.setText(array[2].substring(0, array[2].indexOf("CHL")));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        TextViewFullname.setText("");
                    }
                    PeopleValidator(barcodeStr, "", "", barcodeType);
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
                log.writeLog(getApplicationContext(), "Main:line 408", "ERROR", e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                log.writeLog(getApplicationContext(), "Main:line 411", "ERROR", e.getMessage());
            }
        }
    };

    /**
     * Return current local datetime in PDA, in format that specifies in string format
     *
     * @param format= how you want to receive the datetime,Ex:"dd-MM-yyyy HH:mm:SS"
     * @return return String with the current datetime
     */
    public String getCurrentDateTime(String format) {
        Calendar cal = Calendar.getInstance();
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat(format);
        return date.format(currentLocalTime);
    }

    public String getDeltasCurrentDateTime(String format) {
        Calendar cal = Calendar.getInstance();
        Date currentLocalTime = cal.getTime();
        SimpleDateFormat date = new SimpleDateFormat(format);
        date.setTimeZone(TimeZone.getTimeZone("UTC"));
        return date.format(currentLocalTime);
    }

    /**
     * method that validate old and new chilean national identity card
     *
     * @param rut=number without check digit
     * @param dv=        only check digit
     * @return true if the dni number is correct or false if dni number doesn´t match with check digit
     */
    public boolean ValidarRut(int rut, char dv) {
        dv = dv == 'k' ? dv = 'K' : dv;
        int m = 0, s = 1;
        for (; rut != 0; rut /= 10) {
            s = (s + rut % 10 * (9 - m++ % 6)) % 11;
        }
        return dv == (char) (s != 0 ? s + 47 : 75);
    }

    private void initScan() {
        // TODO Auto-generated method stub
        mScanManager = new ScanManager();
        mScanManager.openScanner();
        mScanManager.switchOutputMode(0);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        //  s.ServerKill();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        //fillSpinner();
        if (mScanManager != null) {
            mScanManager.stopDecode();
            isScaning = false;
        }
        unregisterReceiver(mScanReceiver);
        Log.d("estoy en ", "onPause");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("spinnerSelection", comboLanded.getSelectedItemPosition());
        editor.apply();
        //  s.ServerStop();//Remove if it needs to work with the screen off. Good practice: Server must stop.
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        initScan();
        IntentFilter filter = new IntentFilter();
        filter.addAction(SCAN_ACTION);
        registerReceiver(mScanReceiver, filter);
        Log.d("estoy en ", "onResume");
        //load spinner selected in sharedPreference method

        SharedPreferences sharedPref = getSharedPreferences("userPreference", MODE_PRIVATE);
        int spinnerValue = sharedPref.getInt("spinnerSelection", -1);
        if (spinnerValue != -1) {// set the value of the spinner
            //  Log.e("estoy aqui", "estoy cargando sharedPreference");
            comboLanded.setSelection(spinnerValue);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("estoy en", "onRestart");

    }

    private void asyncUpdateManifestinTime() {
        final Handler handler = new Handler();
        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            AsyncTask_updatePeopleManifest = new asyncTask_updatePeopleManifest();
                            AsyncTask_updatePeopleManifest.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        } catch (Exception e) {
                            e.printStackTrace();
                            log.writeLog(getApplicationContext(), "MainActivity", "ERROR", "asyncUpdateManifestState() " + e.getMessage());
                        }
                    }
                });
            }
        };

        timer.schedule(task, 0, timer_asyncUpdateManifest);  // 5 min=300000 // 6 min =360000
    }

    /*   private void asyncDeletePeopleinTime() {
           final Handler handler = new Handler();
           Timer timer = new Timer();

           TimerTask task = new TimerTask() {
               @Override
               public void run() {
                   handler.post(new Runnable() {
                       public void run() {
                           try {
                               AsyncTask_updatePeopleManifest = new asyncTask_updatePeopleManifest();
                               AsyncTask_updatePeopleManifest.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                           } catch (Exception e) {
                               e.printStackTrace();
                               log.writeLog(getApplicationContext(), "MainActivity", "ERROR", "asyncUpdateManifestState() " + e.getMessage());
                           }
                       }
                   });
               }
           };

           timer.schedule(task, 0, timer_asyncDeletePeopleManifest);  // 5 min=300000 // 6 min =360000
       }
   */
    private void asyncUpdateManifestState() {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            AsynTask_UpdateStateManifest = new AsyncUpdateStateManifest();
                            AsynTask_UpdateStateManifest.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        } catch (Exception e) {
                            log.writeLog(getApplicationContext(), "MainActivity", "ERROR", "asyncUpdateManifestState() " + e.getMessage());
                        }
                    }
                });
            }
        };
        timer.schedule(task, 0, timer_asyncUpdatePeopleState);  // 3min =180000 //4 min = 240000;
    }

    /**
     * call method getUpdatePeopleManifest per each port save in DB, in each port, get new person and insert that
     *
     * @return integer with difference between old manifest and new manifest with people download
     */
    private int Asyntask_insertNewPeopleManifest() {
        //update manifest
        DatabaseHelper db = DatabaseHelper.getInstance(this);
        log_app log = new log_app();
        int count_before = Integer.parseInt(db.selectFirst("select count(id) from manifest"));
        int total_temp = 0;
        try {
            String id_route = db.selectFirst("select route_id from config");
            if (id_route != null && !id_route.isEmpty() && !id_route.equals("null"))
                db.insertJSON(new getAPIInformation(AxxezoAPI, Integer.parseInt(id_route), 0).execute().get(), "manifest");
            else
                log.writeLog(getApplicationContext(), "MainActivity", "ERROR", "Asyntask_insertNewPeopleManifest, route_id esta nulo o vacio, no se pudo ejecutar proceso asyncrono");
            int count_after = Integer.parseInt(db.selectFirst("select count(id) from manifest"));
            if (count_before != count_after) {
                total_temp = count_after - count_before;
            }
            if (total_temp > 0) {
                db.insert("update config set date_last_update='" + getDeltasCurrentDateTime("yyyy-MM-dd'T'HH:mm:ss") + "' where id=1");
                updateTimePeople = db.selectFirst("select date_last_update from config");
                if (!updateTimePeople.isEmpty() && updateTimePeople != null && !updateTimePeople.equals("null")) {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    Date newDate = null;
                    try {
                        newDate = format.parse(updateTimePeople);
                        format = new SimpleDateFormat("HH:mm");
                        String date = format.format(newDate);
                        if (!date.isEmpty() || !date.equals("null") || date != null)
                            TextViewTimePeople = date;
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
            }
        } catch (android.database.SQLException | JSONException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
            log.writeLog(getApplicationContext(), "MainActivity", "ERROR", "Asyntask_insertNewPeopleManifest" + e.getMessage());
        }
        Log.e("asynctask", "insertnewpeoplemanifest");
        return total_temp;
    }

    public void reset() {
        try {
            initScan();
            IntentFilter filter = new IntentFilter();
            filter.addAction(SCAN_ACTION);
            registerReceiver(mScanReceiver, filter);
            TextViewFullname.setText("Nombre");
            TextViewRut.setText("Nº Documento");
            TextViewStatus.setText("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendRecordstoAPI() { //send records to api
        final Handler handler = new Handler();
        Timer timer = new Timer();
        final DatabaseHelper db = DatabaseHelper.getInstance(this);
        final log_app log = new log_app();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            if (Asynctask_sendRecord == null) {
                                OfflineRecordsSynchronizer();
                            }
                            if (db.record_desync_count() > 0 && Asynctask_sendRecord.getStatus() != AsyncTask.Status.RUNNING)
                                OfflineRecordsSynchronizer();
                        } catch (android.database.SQLException e) {
                            log.writeLog(getApplicationContext(), "MainActivity", "ERROR", "updateDB" + e.getMessage());
                        }
                    }
                });
            }
        };
        timer.schedule(task, 0, timer_sendRecordsAPI);  // 360000= 6 minutes, 7 minutes=420000
    }

    /**
     * PeopleValidator, contains all validations of qr and pdf417 code, receive
     *
     * @param rut=          dni number extract in method broadcastReceivere
     * @param id_itinerary= travel id, use to validate person in table manifest
     * @param port=         embark person port
     * @param type=         indicate the type of code read
     */
    public void PeopleValidator(String rut, String id_itinerary, String port, int type) {
        boolean valid = false;
        Cursor person = null;
        Record record = new Record(); // Object to be sended to API Axxezo.
        DatabaseHelper db = DatabaseHelper.getInstance(this);
        Cursor validation = null;
        rut = rut.trim().toUpperCase();
        if (comboLanded.getChildCount() == 0) { //1.-validations
            TextViewStatus.setText("PORFAVOR CONFIGURE EL MANIFIESTO PRIMERO");
            new LoadSound(4).execute();
            record.setReason(5);
        } else {
            TextViewRut.setText(rut);
            if (type == 28 && !id_itinerary.isEmpty()) {//pure QR code
                if (id_itinerary.trim().equals(db.selectFirst("select route_id from config where route_id='" + id_itinerary + "'").trim())) {
                    validation = db.select("select p.name,m.origin from ports as p left join manifest as m on p.id_mongo=m.origin where m.id_people='" + rut + "'");
                    if (validation.getCount() != 0) {
                        if (mySwitch.isChecked()) {
                            if ((validation.getString(0)).trim().equals(selectedSpinnerLanded.trim()))
                                valid = true;
                            else {
                                TextViewStatus.setText("PUERTO EMBARQUE ES " + validation.getString(0));
                                record.setReason(1);
                            }
                        } else {
                            validation = db.select("select p.name from ports as p left join manifest as m on p.id_mongo=m.destination where m.id_people='" + rut + "'");
                            if (validation.getString(0).trim().equals(selectedSpinnerLanded.trim()))
                                valid = true;
                            else {
                                TextViewStatus.setText("PUERTO DESEMBARQUE ES " + validation.getString(0));
                                record.setReason(2);
                            }
                        }
                    } else {
                        TextViewStatus.setText("PERSONA NO SE ENCUENTRA EN MANIFIESTO");
                        record.setReason(3);
                    }
                } else {
                    TextViewStatus.setText("VIAJE NO CORRESPONDE");
                    record.setReason(4);
                }
            } else if (type == 28 && id_itinerary == "" || type == 17) { //old dni and new dni validations
                validation = db.select("select p.name,m.id_people from ports as p left join manifest as m on p.id_mongo=m.origin where m.id_people='" + rut + "'");
                if (validation.getCount() != 0) {
                    if (mySwitch.isChecked()) {
                        if (!selectedSpinnerLanded.equals(validation.getString(0))) {
                            TextViewStatus.setText("PUERTO EMBARQUE ES " + validation.getString(0));
                            record.setReason(1);
                        } else
                            valid = true;
                    } else {
                        validation = db.select("select p.name from ports as p left join manifest as m on p.id_mongo=m.destination where m.id_people='" + rut + "'");
                        if (!selectedSpinnerLanded.equals(validation.getString(0))) {
                            TextViewStatus.setText("PUERTO DESEMBARQUE ES " + validation.getString(0));
                            record.setReason(2);
                        } else
                            valid = true;
                    }
                } else {
                    TextViewStatus.setText("PERSONA NO SE ENCUENTRA EN MANIFIESTO");
                    record.setReason(3);
                }
            }
            if (validation != null)
                validation.close();
        }
        //2.-fill person information in cursor person, order in cursor rut,name,origin,destination,boletus
        if (valid) {
            person = db.validatePerson(rut);
            record.setPerson_document(rut);
            if (person.getCount() > 0 && person.getString(1) != null) {
                TextViewFullname.setText(person.getString(1));
                record.setPerson_name(person.getString(1));
            } else {
                record.setPerson_name("");
                TextViewFullname.setText("");
            }
            record.setSync(0);
            record.setDatetime(getCurrentDateTime("yyyy-MM-dd HH:mm:ss"));
            record.setPort_registry(db.selectFirst("select id_mongo from ports where name = '" + comboLanded.getSelectedItem().toString() + "'"));
            TextViewRut.setText(rut);

            new LoadSound(2).execute();
            imageview.setImageResource(R.drawable.img_true);
            TextViewStatus.setText("");

            //fill record
            record.setPermitted(1);

            if (is_input) {
                record.setInput(1);
                db.updatePeopleManifest(rut, person.getString(2), person.getString(3), 1);
            } else {
                record.setInput(2);
                db.updatePeopleManifest(rut, person.getString(2), person.getString(3), 2);
            }

            //record.setPort_registry(comboLanded.getSelectedItem().toString());

            record.setOrigin(person.getString(2));
            record.setDestination(person.getString(3));
            record.setMongo_id_person(person.getString(5));
            record.setMongo_id_manifest(db.selectFirst("select manifest_id from config"));
            record.setMongo_id_register(person.getString(6));
        } else {
            record.setPerson_document(rut);
            TextViewFullname.setText("");
            new LoadSound(3).execute();
            imageview.setImageResource(R.drawable.img_false);

            if (is_input) record.setInput(1);
            else record.setInput(2);
            record.setPermitted(-1);
            record.setDatetime(getCurrentDateTime("yyyy-MM-dd HH:mm:ss"));
            record.setPort_registry(db.selectFirst("select id_mongo from ports where name = '" + comboLanded.getSelectedItem().toString() + "'"));
        }
        db.add_record(record);
        if (person != null)
            person.close();
    }

    public void makeToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    /**
     * get all register in table records where sync=0 (not synchronized) and put this in a List<Records>
     * once done, send list to asyncronous sendRecords
     */
    public void OfflineRecordsSynchronizer() {
        DatabaseHelper db = DatabaseHelper.getInstance(this);
        List<Record> records = db.get_desynchronized_records();
        Asynctask_sendRecord = new RegisterTask(records, AxxezoAPI);
        Asynctask_sendRecord.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * PUT method that send to api the data contains in local database
     *
     * @param record= contains all information of the register, like dni, name, origin, destination,etc
     * @param url=    addres of endpoint to send data
     * @param client= receive a client okhttp to send registers, the reason of that, is avoid to create per each record a object okhttp, and only usage one instance of this
     * @return
     */
    public String PUT(Record record, String url, OkHttpClient client) {
        String result = "";
        String json = "";
        JSONObject jsonObject = new JSONObject();
        DatabaseHelper db = DatabaseHelper.getInstance(this);
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");
        try {
            url = url + "/registers/" + record.getMongo_id_register();
            Log.d("put", url);
            jsonObject.accumulate("person", record.getMongo_id_person());
            jsonObject.accumulate("seaport", record.getPort_registry());
            //jsonObject.accumulate("manifest", record.getMongo_id_manifest()); //falta
            jsonObject.accumulate("state", record.getInput() + "");
            jsonObject.accumulate("date", record.getDatetime()); //falta formatear 2017-01-01 00:00:00

            Log.e("debug", url.toString());

            json = jsonObject.toString();
            Log.d(url, json);

            RequestBody body = RequestBody.create(JSON, json);

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Content-type", "application/json")
                    .put(body)
                    .build();

            //PUT using okhttp
            Response response = client.newCall(request).execute();

            String tmp = response.body().string(); //Response{protocol=http/1.1, code=401, message=Unauthorized, url=http://axxezo-test.brazilsouth.cloudapp.azure.com:9001/api/registers}
            // 10. convert inputstream to string
            Log.d("---Response---", tmp);
            if (tmp != null) {
                if (response.isSuccessful()) {
                    // if has sync=0 its becouse its an offline record to be will synchronized.
                    if (record.getSync() == 0) {
                        db.update_record(record.getId());
                    }
                }
            } else {
                result = String.valueOf(response.code());
            }
            //result its the json to sent
            if (result.startsWith("http://"))
                result = "204"; //no content
        } catch (JSONException | IOException e) {
            e.printStackTrace();
            log.writeLog(getApplicationContext(), "Main: PUT method", "ERROR", e.getMessage());
        }

        // 11. return result
        return result;
    }

    public String POST(Record record, String url, OkHttpClient client) {
        String result = "";
        String json = "";
        boolean isManualSell=false;
        JSONObject jsonObject = new JSONObject();
        DatabaseHelper db = DatabaseHelper.getInstance(this);
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");
        try {
            jsonObject.accumulate("documentId", record.getPerson_document());
            jsonObject.accumulate("itinerary", db.selectFirst("select id_mongo from routes where id=(select route_id from config order by id desc limit 1)"));
            jsonObject.accumulate("date", record.getDatetime());
            if (record.getTicket() != 0) {
                url = url + "/registers/manualSell/";//manual registers
                Log.e("URL", url);
                // jsonObject.accumulate("ticket", record.getTicket());
                jsonObject.accumulate("name", record.getPerson_name());
                jsonObject.accumulate("origin", record.getOrigin());
                jsonObject.accumulate("destination", record.getDestination());
                jsonObject.accumulate("ticketId", record.getTicket());
                isManualSell=true;
            } else if (record.getPermitted() == -1) {//denied registers
                url = url + "/registers/deniedRegister";
                jsonObject.accumulate("deniedReason", record.getReason());
                jsonObject.accumulate("origin", record.getPort_registry());
            }

            json = jsonObject.toString();
            Log.e(url, json);
            RequestBody body = RequestBody.create(JSON, json);

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Content-type", "application/json")
                    .post(body)
                    .build();

            //POST using okhttp
            Response response = client.newCall(request).execute();

            String tmp = response.body().string(); //Response{protocol=http/1.1, code=401, message=Unauthorized, url=http://axxezo-test.brazilsouth.cloudapp.azure.com:9001/api/registers}
            // 10. convert inputstream to string
            //aqui tmp obtiene post

            Log.d("----POST response---", tmp);
            if (tmp != null) {
                if (response.isSuccessful()) {
                    // if has sync=0 its becouse its an offline record to be will synchronized.
                    if (record.getSync() == 0) {
                        db.update_record(record.getId());
                    }
                    if(isManualSell){
                        JSONObject jsonManualSell=new JSONObject(tmp);
                        String ObjectId=jsonManualSell.getString("registerId").trim();
                        String personId=jsonManualSell.getString("personId").trim();
                        if(!ObjectId.isEmpty()&&!personId.isEmpty())
                        db.insert("update people set id_register='"+ObjectId+"',id_mongo='"+ObjectId+"' where document='"+record.getPerson_document()+"'");


                    }
                }
            } else {
                result = String.valueOf(response.code());
            }
            //result its the json to sent
            if (result.startsWith("http://"))
                result = "204"; //no content
        } catch (JSONException | IOException e) {
            e.printStackTrace();
            log.writeLog(getApplicationContext(), "Main: POST method", "ERROR", e.getMessage());
        }

        // 11. return result
        return result;
    }

    public class RegisterTask extends AsyncTask<Void, Void, String> {
        private String url;
        private List<Record> newRecord;

        RegisterTask(List<Record> newRecord, String url) {
            this.newRecord = newRecord;
            this.url = url;
        }

        @Override
        protected String doInBackground(Void... params) {
            String postReturn = "";
            final OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(1, TimeUnit.SECONDS)
                    .writeTimeout(0, TimeUnit.SECONDS)
                    .readTimeout(0, TimeUnit.SECONDS)
                    .build();
            for (int i = 0; i < newRecord.size(); i++) {
                Record record = newRecord.get(i);
                if (record.getPermitted() == 1)
                    PUT(record, url, client);
                else if (record.getPermitted() == -1 || record.getTicket() != 0)//for denied registers
                    POST(record, url, client);
            }
            return postReturn;
        }
    }

    public class asyncTask_updatePeopleManifest extends AsyncTask<Void, Void, Integer> {
        private int update_manifest_count;

        @Override
        protected Integer doInBackground(Void... params) {
            return update_manifest_count = Asyntask_insertNewPeopleManifest();

        }

        @Override
        protected void onPostExecute(Integer integer) {
           /* if (integer > 0) {
                TextViewManifestUpdate.setTextColor(Color.WHITE);
                TextViewManifestUpdate.setText("Manifiesto actualizado: " + TextViewTimePeople + " hrs.");
            }*/
        }
    }

    public class AsyncUpdateStateManifest extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            //set in 1000 miliseconds to timeout connection
            final OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(2, TimeUnit.SECONDS)
                    .writeTimeout(0, TimeUnit.SECONDS)
                    .readTimeout(0, TimeUnit.SECONDS)
                    .build();

            getUpdateStates(client);
            return null;
        }
    }


    /**
     * Add new People in manifest, according to datetime in PDA
     * the difference between this and the getmanifest from configuration is that only update the manifest, as you can see
     * the endpoint is different
     *
     * @param Url
     * @param ID_route
     * @return content JSON to insert
     * @throws IOException
     */
    public String Asyntask_insertNewPeopleManifest(String Url, int ID_route) throws IOException {
        //http://axxezo-test.brazilsouth.cloudapp.azure.com:9001/api/manifests?itinerary=1824&date=2017-04-14T10:44:00
        DatabaseHelper db = DatabaseHelper.getInstance(this);
        updateTimePeople = db.selectFirst("select date_last_update from config");
        URL url = new URL(Url + "/manifests?itinerary=" + ID_route + "&date=" + updateTimePeople);
        String content = "";
        HttpURLConnection conn = null;
        try {
            Log.e("URL async_new People", url.toString());
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("TOKEN", token_navieraAustral);
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(2000);
            conn.connect();

            int connStatus = conn.getResponseCode();
            InputStream getData = conn.getInputStream();
            if (connStatus != 200) {
                content = String.valueOf(getData);
            } else {
                content = convertInputStreamToString(getData);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        if (conn != null) {
            conn.disconnect();
        }
        if (content.length() <= 2) { //[]
            content = "204"; // No content
        }
        //finally updating
        return content;
    }


 /*
    public String Asyntask_deletePeopleManifest(String Url, int ID_route) throws IOException {
        //http://axxezo-test.brazilsouth.cloudapp.azure.com:9001/api/manifests?itinerary=1824&date=2017-04-14T10:44:00
        DatabaseHelper db = DatabaseHelper.getInstance(this);
        updateTimePeople = db.selectFirst("select date_last_update from config");
        URL url = new URL(Url + "/manifests?itinerary=" + ID_route + "&date=" + updateTimePeople);
        String content = "";
        HttpURLConnection conn = null;
        try {
            Log.e("URL async_new People", url.toString());
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("TOKEN", token_navieraAustral);
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(2000);
            conn.connect();

            int connStatus = conn.getResponseCode();
            InputStream getData = conn.getInputStream();
            if (connStatus != 200) {
                content = String.valueOf(getData);
            } else {
                content = convertInputStreamToString(getData);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        if (conn != null) {
            conn.disconnect();
        }
        if (content.length() <= 2) { //[]
            content = "204"; // No content
        }
        //finally updating
        return content;
    }
*/

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;
        inputStream.close();
        return result;
    }

    private void exitApp() {
        this.finishAffinity();
    }

    /**
     * return value count of DB, corresponding to embark,landed and pending
     *
     * @param position= 1(manifestcount),2(pendingCount),3(embarkedCount),4(landedCount)
     * @return count=return count of selected position
     */

    public int getCountEDP(int position) {
        DatabaseHelper db = DatabaseHelper.getInstance(this);
        int manifestCount = -1;
        int PendingCount = -1;
        int EmbarkedCount = -1;
        int LandedCount = -1;
        Cursor getCountsofDB = db.select("select (select count(*) from manifest)," +
                "(select count(*) from manifest where is_inside=0),(select count(*) from manifest where is_inside=1)," +
                "(select count(*) from manifest where is_inside=2)");
        int count = 0;
        if (getCountsofDB != null && getCountsofDB.getCount() > 0) {
            manifestCount = getCountsofDB.getInt(0);
            PendingCount = getCountsofDB.getInt(1);
            EmbarkedCount = getCountsofDB.getInt(2);
            LandedCount = getCountsofDB.getInt(3);
        }
        switch (position) {
            case 1:
                count = manifestCount;
                break;
            case 2:
                count = PendingCount;
                break;
            case 3:
                count = EmbarkedCount;
                break;
            case 4:
                count = LandedCount;
                break;
        }
        if (getCountsofDB != null)
            getCountsofDB.close();
        return count;
    }

    public class getAPIInformation extends AsyncTask<String, Void, String> {
        private String URL;
        private String getInformation;
        private int flag;
        private int route;
        private int port;

        getAPIInformation(String URL, int route, int flag) {//manifest
            this.URL = URL;
            this.route = route;
            getInformation = "";
            this.flag = flag;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                switch (flag) {
                    case 0:
                        getInformation = Asyntask_insertNewPeopleManifest(URL, route);
                        break;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return getInformation;
        }

        @Override
        public String toString() {
            return getInformation + "";
        }

        protected void onPostExecute(String result) {
            Log.d("getAPIInformation Res", result);
        }

    }

    /**
     * get list from api, then, take each documentId and find this in manifest table, if found it, compare state that is entering with state in db
     */
    public void getUpdateStates(OkHttpClient client) {
        DatabaseHelper db = DatabaseHelper.getInstance(this);
        Cursor itinerary = db.select("select route_id from config");
        if (itinerary.getCount() > 0) {
            String url = AxxezoAPI + "/registers/status?itinerary=" + itinerary.getInt(0);
            log_app log = new log_app();
            String result = "";
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
            Response response = null;
            JSONArray jsonArray = null;

            if (itinerary != null)
                itinerary.close();
            // Log.e("timeout get",client.readTimeoutMillis()+"");
            //1.- obtaining json array with states from endpoint
            try {
                response = client.newCall(request).execute();
                if (response != null) {
                    try {
                        result = response.body().string();
                        jsonArray = new JSONArray(result);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    result = response.code() + "";
                }
                if (response != null)
                    response.close();
            } catch (IOException e) {
                log.writeLog(getApplicationContext(), "Main line:1172", "ERROR", e.getMessage());
                AsynTask_UpdateStateManifest.cancel(true);
                Log.e("status", "OFFLINE");
            }

            //2.- process JSONarray, ask each jsonobject if exist in manifest table and compare states
            if (jsonArray != null && jsonArray.length() > 0) {
                JSONObject person_information;
                String getInside;
                String dni_json;
                String origin;
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        person_information = jsonArray.getJSONObject(i);
                        dni_json = person_information.getString("documentId");
                        origin = person_information.getString("origin");
                        if (dni_json.contains("-"))
                            dni_json = dni_json.substring(0, dni_json.indexOf("-"));
                        getInside = db.selectFirst("select is_inside from manifest where id_people='" + dni_json + "'");
                        //Log.e(dni_json, person_information.getString("state"));

                        if (!getInside.isEmpty() && (!getInside.equals(person_information.getString("state"))) && !origin.isEmpty()) {
                            //Log.d(dni_json + " " + getInside, person_information.getString("state"));
                            db.insert("update manifest set is_inside='" + person_information.getString("state") +
                                    "' where id_people='" + dni_json.trim().toUpperCase() + "' and origin='" + origin + "'");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        log.writeLog(getApplicationContext(), "Main line:1083", "ERROR", e.getMessage());
                    }
                }
            }
        }

    }

    /**
     * Asyntask to play sounds in background
     * 1 Error
     * 2 Permitted
     * 3 Denied
     * 4 stop all
     */
    private class LoadSound extends AsyncTask<Void, Void, Void> {
        private int typeSound = -1;

        public LoadSound(int typeSound) {
            this.typeSound = typeSound;
        }

        @Override
        protected Void doInBackground(Void... params) {
            switch (typeSound) {
                case 1:
                    if (mp3Error.isPlaying()) mp3Error.pause();
                    mp3Error.seekTo(0);
                    mp3Error.start();
                    break;
                case 2:
                    if (mp3Permitted.isPlaying()) mp3Permitted.pause();
                    mp3Permitted.seekTo(0);
                    mp3Permitted.start();
                    break;
                case 3:
                    if (mp3Dennied.isPlaying()) mp3Dennied.pause();
                    mp3Dennied.seekTo(0);
                    mp3Dennied.start();
                    break;
                case 4:
                    if (mp3Error.isPlaying()) mp3Error.pause();
                    mp3Error.seekTo(0);
                    if (mp3Dennied.isPlaying()) mp3Dennied.pause();
                    mp3Dennied.seekTo(0);
                    if (mp3Permitted.isPlaying()) mp3Permitted.pause();
                    mp3Permitted.seekTo(0);
                    break;
            }
            return null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /* protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (comboLanded != null && comboLanded.getCount() > 0)

            outState.putInt("combolanded", selectedIntSpinnerLanded);
        Log.e("state spinner", "" + selectedIntSpinnerLanded);
    }*/
    private void killAyntask(boolean state) {
        Asynctask_sendRecord.cancel(state); //asyntask that send data to api axxezo
        AsyncTask_updatePeopleManifest.cancel(state);//asyntask to update in realtime new people inserts in manifest
        AsynTask_UpdateStateManifest.cancel(state);// asyntask to update states of people insert in manifest table;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            fillSpinner();
            Log.e("estoy", "estoy en onactivityResult");
        }
    }
    /*
    public void deletePeopleManifest(){
        DatabaseHelper db=DatabaseHelper.getInstance(this);
        String id_route = db.selectFirst("select route_id from config");
        try {
            db.insertJSON(new getAPIInformation(AxxezoAPI, Integer.parseInt(id_route),1).execute().get(), "manifest_update");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    private class deletePeopleManifest extends AsyncTask<Void, Void, Void>{



        @Override
        protected Void doInBackground(Void... params) {
            deletePeopleManifest();
            return null;
        }
    }
*/

}
