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
import android.database.Cursor;
import android.device.ScanManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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


    private String URL = "http://ticket.bsale.cl/control_api";
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
    private log_app log;


    /********
     * Timers Asyntask
     ****/
    private int timer_asyncUpdateManifest;
    private int timer_sendRecordsAPI;
    private int timer_asyncUpdatePeopleState;


    /*****
     * Asyntask declarations.....
     */
    private RegisterTask Asynctask_sendRecord; //asyntask that send data to api axxezo
    private asyncTask_updatePeopleManifest AsyncTask_updatePeopleManifest;


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
        log = new log_app();
        selectedSpinnerLanded = "";

        //asign timers to Asyntask
        timer_sendRecordsAPI = 420000;
        timer_asyncUpdateManifest = 120000;
        timer_asyncUpdatePeopleState = 240000;

        //asign url api axxezo
        AxxezoAPI = "http://axxezocloud.brazilsouth.cloudapp.azure.com:3000/api";
        //AxxezoAPI = "http://192.168.1.126:3000/api";

        //enable WAL mode in DB
        DatabaseHelper db = DatabaseHelper.getInstance(this);
        db.setWriteAheadLoggingEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHelper db = DatabaseHelper.getInstance(getApplicationContext());
                mVibrator.vibrate(100);
                if (!TextViewRut.getText().toString().trim().isEmpty())
                    PeopleValidator(TextViewRut.getText().toString().trim(), "", "", 17);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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
        //fill information in combobox
        Cursor getOriginandDestination = db.select("select distinct origin from manifest union select distinct destination from manifest order by origin desc");
        ArrayList<String> listOriginDestination = new ArrayList<String>();
        if (getOriginandDestination != null)
            while (!getOriginandDestination.isAfterLast()) {
                listOriginDestination.add(getOriginandDestination.getString(0));
                getOriginandDestination.moveToNext();
            }
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, listOriginDestination);
        comboLanded.setAdapter(adapter);
        comboLanded.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSpinnerLanded = comboLanded.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //call in oncreate asyntask
        //sendRecordstoAPI();
        //Asyntask_insertNewPeopleManifest();
        //asyncUpdateManifestinTime();
        //asyncUpdateManifestState();
        // if (getOriginandDestination != null)
        //     getOriginandDestination.close();
        if(getOriginandDestination!=null)
            getOriginandDestination.close();
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
            startActivity(intent);
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
            startActivity(intent);
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
        drawer.closeDrawer(GravityCompat.START);
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
                    } else if (barcodeStr.startsWith("https://")) { // Its a new DNI Cards.
                        barcodeStr = barcodeStr.substring(
                                barcodeStr.indexOf("RUN=") + 4,
                                barcodeStr.indexOf("&type"));
                        // Remove DV.
                        barcodeStr = barcodeStr.substring(0, barcodeStr.indexOf("-"));
                        PeopleValidator(barcodeStr, "", "", barcodeType);
                    } else if (!barcodeStr.contains("id_itinerary")) {
                        new LoadSound(1).execute();
                        TextViewStatus.setText("QR INVALIDO");
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
     * @return= return String with the current datetime
     */
    public String getCurrentDateTime(String format) {
        Calendar cal = Calendar.getInstance();
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat(format);
        String localTime = date.format(currentLocalTime);
        return localTime;
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
        if (mScanManager != null) {
            mScanManager.stopDecode();
            isScaning = false;
        }
        unregisterReceiver(mScanReceiver);
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
                            log.writeLog(getApplicationContext(), "MainActivity", "ERROR", "asyncUpdateManifestState() " + e.getMessage());
                        }
                    }
                });
            }
        };

        timer.schedule(task, 0, timer_asyncUpdateManifest);  // 5 min=300000 // 6 min =360000
    }

    private void asyncUpdateManifestState() {
        final Handler handler = new Handler();
        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            new AsyncUpdateStateManifest().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
        Cursor ports = null;
        try {
            //1.- to actualize manifest, i need to use endpoint and travel each port to fill the update of persons
            ports = db.select("select id_api from ports)");
            String id_route = db.selectFirst("select route_id from config");
            while (!ports.isAfterLast()) {
                db.insertJSON(new getAPIInformation(URL, token_navieraAustral, Integer.parseInt(id_route), ports.getInt(0)).execute().get(), "manifest");
            }
            int count_after = Integer.parseInt(db.selectFirst("select count(id) from manifest"));
            if (count_before != count_after) {
                int total = count_after - count_before;
                total_temp = total;
            }
        } catch (android.database.SQLException e) {
            log.writeLog(getApplicationContext(), "MainActivity", "ERROR", "Asyntask_insertNewPeopleManifest" + e.getMessage());
        } catch (JSONException e) {
            log.writeLog(getApplicationContext(), "MainActivity", "ERROR", "Asyntask_insertNewPeopleManifest" + e.getMessage());
        } catch (InterruptedException e) {
            log.writeLog(getApplicationContext(), "MainActivity", "ERROR", "Asyntask_insertNewPeopleManifest" + e.getMessage());
        } catch (ExecutionException e) {
            log.writeLog(getApplicationContext(), "MainActivity", "ERROR", "Asyntask_insertNewPeopleManifest" + e.getMessage());
        }
        if (ports != null)
            ports.close();
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
        rut = rut.trim().toUpperCase();
        if (comboLanded.getChildCount() == 0) { //1.-validations
            TextViewStatus.setText("PORFAVOR CONFIGURE EL MANIFIESTO PRIMERO");
            new LoadSound(4).execute();
        } else {
            TextViewRut.setText(rut);
            if (type == 28 && !id_itinerary.isEmpty()) {//pure QR code
                if (id_itinerary.trim().equals(db.selectFirst("select route_id from config where route_id='" + id_itinerary + "'").trim())) {
                    if (mySwitch.isChecked()) {
                        if ((db.selectFirst("select origin from manifest where id_people='" + rut + "'")).trim().equals(selectedSpinnerLanded.trim()))
                            valid = true;
                        else
                            TextViewStatus.setText("PUERTO EMBARQUE NO PERTENECE");
                    } else if ((db.selectFirst("select destination from manifest where id_people='" + rut + "'")).trim().equals(selectedSpinnerLanded.trim()))
                        valid = true;
                    else
                        TextViewStatus.setText("PUERTO DESEMBARQUE NO PERTENECE");
                } else
                    TextViewStatus.setText("VIAJE NO CORRESPONDE");
            } else if (type == 28 && id_itinerary == "" || type == 17) { //old dni and new dni validations
                if (!db.selectFirst("select id_people from manifest where id_people='" + rut + "'").isEmpty()) {
                    if (mySwitch.isChecked()) {
                        if (!selectedSpinnerLanded.equals(db.selectFirst("select origin from manifest where id_people='" + rut + "'"))) {
                            TextViewStatus.setText("PUERTO EMBARQUE NO PERTENECE");
                        } else
                            valid = true;
                    } else if (!selectedSpinnerLanded.equals(db.selectFirst("select destination from manifest where id_people='" + rut + "'"))) {
                        TextViewStatus.setText("PUERTO DESEMBARQUE NO PERTENECE");
                    } else
                        valid = true;
                } else
                    TextViewStatus.setText("PERSONA NO SE ENCUENTRA EN MANIFIESTO");
            }
        }
        //2.-fill person information in cursor person, order in cursor rut,name,origin,destination,boletus
        person = db.validatePerson(rut);

        if (valid && person != null)

        {
            new LoadSound(2).execute();
            imageview.setImageResource(R.drawable.img_true);
            if (person.getString(1) != null) TextViewFullname.setText(person.getString(1));
            TextViewRut.setText(rut);
            TextViewStatus.setText("");

            //fill record
            record.setPermitted(1);
            if (person.getString(1) != null) record.setPerson_name(person.getString(1));
            else record.setPerson_name("");
            record.setPerson_document(rut);
            if (is_input) {
                record.setInput(1);
                db.updatePeopleManifest(rut, 1);
            } else {
                record.setInput(2);
                db.updatePeopleManifest(rut, 2);
            }
            record.setDatetime(getCurrentDateTime("yyyy-MM-dd'T'HH:mm:ss.S'Z'"));
            record.setSync(0);
            record.setPort_registry(comboLanded.getSelectedItem().toString());
            record.setOrigin(person.getString(2));
            record.setDestination(person.getString(3));

            //finally add record
            db.add_record(record);
        } else if (!valid)

        {
            new LoadSound(3).execute();
            if (!TextViewStatus.getText().toString().isEmpty())
                record.setReason(TextViewStatus.getText().toString());
            imageview.setImageResource(R.drawable.img_false);
            TextViewRut.setText(rut);
            if (is_input) record.setInput(1);
            else record.setInput(2);
            record.setPermitted(-1);
            record.setSync(0);
            record.setPort_registry(comboLanded.getSelectedItem().toString());
            record.setDatetime(getCurrentDateTime("yyyy-MM-dd'T'HH:mm:ss.S'Z'"));

            //finally add record
            db.add_record(record);
        }

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
        Asynctask_sendRecord = new RegisterTask(records, AxxezoAPI + "/records");
        Asynctask_sendRecord.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * POST method that send to api the data contains in local database
     *
     * @param record= contains all information of the register, like dni, name, origin, destination,etc
     * @param url=    addres of endpoint to send data
     * @param client= receive a client okhttp to send registers, the reason of that, is avoid to create per each record a object okhttp, and only usage one instance of this
     * @return
     */
    public String POST(Record record, String url, OkHttpClient client) {
        String result = "";
        String json = "";
        JSONObject jsonObject = new JSONObject();
        DatabaseHelper db = DatabaseHelper.getInstance(this);
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");
        try {
            if (record.getDatetime() != null)
                jsonObject.accumulate("datetime", record.getDatetime());
            if (record.getPerson_document() != null)
                jsonObject.accumulate("doc", record.getPerson_document());
            if (record.getPerson_name() != null)
                jsonObject.accumulate("name", record.getPerson_name());
            if (record.getOrigin() != null)
                jsonObject.accumulate("origen", record.getOrigin());
            if (record.getDestination() != null)
                jsonObject.accumulate("destination", record.getDestination());
            if (record.getPort_registry() != null)
                jsonObject.accumulate("port", record.getPort_registry());//puerto de registro
            jsonObject.accumulate("state", record.getInput());
            jsonObject.accumulate("permitted", record.getPermitted());
            if (record.getTicket() != 0)
                jsonObject.accumulate("boletus", record.getTicket());
            if (record.getReason() != null)
                jsonObject.accumulate("reason", record.getReason());


            if (jsonObject.length() <= 22) { // 9 element on json
                json = jsonObject.toString();

                RequestBody body = RequestBody.create(JSON, json);

                // create object okhttp
                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("Accept", "application/json")
                        .addHeader("Content-type", "application/json")
                        .post(body)
                        .build();

                if (!AxxezoAPI.equals("http://:0")) {

                    //POST using okhttp
                    Response response = client.newCall(request).execute();
                    String tmp = response.body().string();
                    log.writeLog(getApplicationContext(), "Main:line 1037", "DEBUG", "response " + response.code() + " name " + record.getPerson_name());

                    // 10. convert inputstream to string
                    if (tmp != null) {
                        if (response.isSuccessful()) {
                            Log.d("json POSTED", json);
                            // if has sync=0 its becouse its an offline record to be will synchronized.
                            if (record.getSync() == 0) {
                                db.update_record(record.getId());
                            }
                        } else if (response.code() == 422) {
                            //return 422 when the record is sync but his state in db isn`t change
                            db.update_record(record.getId());
                        }
                    } else {
                        result = String.valueOf(response.code());
                    }
                    //result its the json to sent
                    if (result.startsWith("http://"))
                        result = "204"; //no content
                } else {
                    new LoadSound(1).execute();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            makeToast("Configure datos del servidor primero");
                        }
                    });
                }
            }
        } catch (UnsupportedEncodingException e) {
            log.writeLog(getApplicationContext(), "Main: POST method", "ERROR", e.getMessage());
        } catch (JSONException e) {
            log.writeLog(getApplicationContext(), "Main: POST method", "ERROR", e.getMessage());
        } catch (IOException e) {
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
            if (integer > 0) {
                TextViewManifestUpdate.setTextColor(Color.WHITE);
                TextViewManifestUpdate.setText("Manifiesto: " + getCurrentDateTime("dd-MM-yyyy HH:mm"));
            }
        }
    }

    public class AsyncUpdateStateManifest extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            getUpdateStates();
            return null;
        }
    }


    /**
     * Add new People in manifest, according to datetime in PDA
     * the difference between this and the getmanifest from configuration is that only update the manifest, as you can see
     * the endpoint is different
     *
     * @param Url
     * @param Token
     * @param ID_route
     * @param port
     * @return content JSON to insert
     * @throws IOException
     */
    public String Asyntask_insertNewPeopleManifest(String Url, String Token, int ID_route, int port) throws IOException {
        //String date must be in format yyyy-MM-dd
        URL url = new URL(Url + "/itinerary_manifest?itinerary=" + ID_route + "&port=" + port + "&date=" + getCurrentDateTime("yyyy-MM-dd"));
        Log.d("get manifest", url.toString());
        String content = "";
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("TOKEN", Token);
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(2000);
            conn.connect();

            int connStatus = conn.getResponseCode();
            InputStream getData = conn.getInputStream();
            if (connStatus != 200) {
                content = String.valueOf(getData);
            } else
                content = convertInputStreamToString(getData);
        } catch (MalformedURLException me) {

        } catch (IOException ioe) {

        }
        if (conn != null) {
            conn.disconnect();
        }
        if (content.length() <= 2) { //[]
            content = "204"; // No content
        }
        Log.d("Manifes Server response", content);
        return content;
    }

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
        private String token;
        private int flag = -1;
        private int route;
        private int port;

        getAPIInformation(String URL, String token, int route, int port) {//manifest
            this.URL = URL;
            this.token = token;
            this.route = route;
            this.port = port;
            getInformation = "";
            flag = 0;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                switch (flag) {
                    case 0:
                        getInformation = Asyntask_insertNewPeopleManifest(URL, token, route, port);
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
        }

    }

    /**
     * download a jsonarray that contains people last state, then, compare this with the state in db local, this method sync the state of persons in PDAs
     */
    public void getUpdateStates() {
      /*  String url = AxxezoAPI + "/states/getState?";
        DatabaseHelper db = DatabaseHelper.getInstance(this);
        log_app log = new log_app();

        // get list manifest id_people and port
        Cursor peopleDocument = db.select("select id_people from manifest");
        if (peopleDocument!=null&&peopleDocument.getCount()>0) {
            int i = 0;
            while (!peopleDocument.isAfterLast()) {
                get_manifest_row = select_dni_from_manifest.get(i).split("\\|");
                if (get_manifest_row.length > 0) {// is not empty
                    ArrayList<String> get_config_per_rut = db.select("select (select route_id from config where port_registry=(select port from manifest where id_people='" + get_manifest_row[0] + "')),(select name from ports where id_api=(select port from manifest where id_people='" + get_manifest_row[0] + "')),(select name from ships where id=(select ship_id from config where port_registry=(select port from manifest where id_people='" + get_manifest_row[0] + "')))," +
                            "(select date from config where port_registry=(select port from manifest where id_people='" + get_manifest_row[0] + "')),(select hour from config where port_registry=(select port from manifest where id_people='" + get_manifest_row[0] + "'))", "|");
                    String[] get_config_per_row = get_config_per_rut.get(0).split("\\|");
                    if (get_config_per_row.length > 0) {
                        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                        nameValuePairs.add(new BasicNameValuePair("doc", get_manifest_row[0]));
                        nameValuePairs.add(new BasicNameValuePair("route", get_config_per_row[0]));
                        nameValuePairs.add(new BasicNameValuePair("port", get_config_per_row[1]));
                        nameValuePairs.add(new BasicNameValuePair("ship", get_config_per_row[2]));
                        nameValuePairs.add(new BasicNameValuePair("date", get_config_per_row[3]));
                        nameValuePairs.add(new BasicNameValuePair("hour", get_config_per_row[4]));

                        //finally send get request
                        String result = "";
                        InputStream inputStream;
                        HttpClient httpclient = new DefaultHttpClient();
                        String paramsString = URLEncodedUtils.format(nameValuePairs, "UTF-8");
                        HttpGet httpGet = new HttpGet(url + paramsString);
                        HttpResponse httpResponse = null;
                        try {
                            httpResponse = httpclient.execute(httpGet);
                            inputStream = httpResponse.getEntity().getContent();
                            if (inputStream != null) {
                                try {
                                    result = convertInputStreamToString(inputStream);
                                    JSONObject objectJson = new JSONObject(result);
                                    if (!objectJson.getString("doc").isEmpty() && objectJson.getInt("state") != -1)
                                        db.insert("update manifest set is_inside='" + objectJson.getInt("state") + "' where id_people='" + objectJson.getString("doc").trim().toUpperCase() + "'");


                                    //is_inside_from_manifest = Integer.parseInt(db.selectFirst("select is_inside from manifest where id_people='" + objectJson.getString("doc").trim() + "'"));
                                    /* seven combinations
                                    * 0 --- 0
                                    * 0 --- 1
                                    * 1 --- 0
                                    * 1 --- 1
                                    * 1 --- 2
                                    * 2 --- 1
                                    * 2 --- 2
                                     */
        //case  1,0 1,2 covered
        // if (is_inside_from_manifest < objectJson.getInt("state"))

        //else if (is_inside_from_manifest > objectJson.getInt("state"))
        //    db.insert("update manifest set is_inside='" + objectJson.getInt("state") + "' where id_people='" + objectJson.getString("doc").trim() + "'");
                                /*} catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                result = String.valueOf(httpResponse.getStatusLine().getStatusCode());
                            }
                            i++;
                            inputStream.close();
                        } catch (IOException e) {
                            log.writeLog(getApplicationContext(), "Main line:1373", "ERROR", e.getMessage());
                            Log.e("status", "OFFLINE");
                            i = select_dni_from_manifest.size();
                        }


                    }
                }
            }
        }*/

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


}
