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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.device.ScanManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
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
    private final static String SCAN_ACTION = "urovo.rcv.message";//扫描结束action
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
    private RegisterTask Asynctask_sendRecord;
    private int timer_sendRecordsAPI;

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
        timer_sendRecordsAPI=420000;

        AxxezoAPI = "http://axxezocloud.brazilsouth.cloudapp.azure.com:3000/api";
        //AxxezoAPI = "http://192.168.1.126:3000/api";

        //enable WAL mode in DB
        DatabaseHelper db = DatabaseHelper.getInstance(getApplicationContext());
        db.setWriteAheadLoggingEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHelper db = DatabaseHelper.getInstance(getApplicationContext());
                String text = "Ruta: " + db.selectFirst("select routes.name from routes inner join config on routes.id=config.route_id");
                if (!TextViewRut.getText().toString().trim().isEmpty())
                    documentValidator(TextViewRut.getText().toString().trim());
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
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, db.select("select distinct origin from manifest union select distinct destination from manifest order by origin desc", ""));
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

        //sendRecordstoAPI();
        //updateManifest();
        //asyncUpdateManifestinTime();
        //asyncUpdateManifestState();

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
                Person person = new Person();

                if (barcodeType == 28) { // QR code
                    if (barcodeStr.contains("client_code")) {
                        try { // Its a ticket
                            JSONObject json = new JSONObject(barcodeStr);
                            String doc = json.getString("client_code");

                            if (doc.contains("-")) {
                                doc = doc.substring(0, doc.indexOf("-"));
                            }
                            person.setDocument(doc);
                            barcodeStr = doc;
                            ticketValidator(doc, json.getString("route"), json.getString("port"), json.getString("date"), json.getString("hour"), json.getString("transport"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (rawCode.equals("CONFIG-AXX-6rVLydzn651RsZZ3dqWk")) {//configuration QR
                        Intent loadLog = new Intent(getApplicationContext(), log_show.class);
                        startActivity(loadLog);
                    } else { // Its a DNI Card.
                        barcodeStr = barcodeStr.substring(
                                barcodeStr.indexOf("RUN=") + 4,
                                barcodeStr.indexOf("&type"));
                        // Remove DV.
                        barcodeStr = barcodeStr.substring(0, barcodeStr.indexOf("-"));
                        documentValidator(barcodeStr);
                    }

                } else if (barcodeType == 17) { // PDF417
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
                    documentValidator(barcodeStr);
                }
            } catch (NullPointerException e) {
                log.writeLog(getApplicationContext(), "Main:line 408", "ERROR", e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                log.writeLog(getApplicationContext(), "Main:line 411", "ERROR", e.getMessage());
            }
        }
    };

    public String getCurrentDateTime(String format) {
        Calendar cal = Calendar.getInstance();
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat(format);
        String localTime = date.format(currentLocalTime);
        return localTime;
    }

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
                            new UpdateManifest().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        } catch (Exception e) {
                            // error, do something
                        }
                    }
                });
            }
        };

        timer.schedule(task, 0, 360000);  // 5 min=300000 // 6 min =360000
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
                            // new AsyncUpdateStateManifest().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        } catch (Exception e) {
                            // error, do something
                        }
                    }
                });
            }
        };
        timer.schedule(task, 0, 240000);  // 3min =180000 //4 min = 240000;
    }

    private int updateManifest() {
        DatabaseHelper db = DatabaseHelper.getInstance(this);
        log_app log = new log_app();
        int count_before = Integer.parseInt(db.selectFirst("select count(id) from manifest"));
        int total_temp = 0;
        try {
            ArrayList<String> select_from_manifest = db.select("select * from config", "|");
            String[] manifest_config = null;

            for (int i = 0; i < select_from_manifest.size(); i++) {
                if (select_from_manifest.size() > 0) {
                    manifest_config = select_from_manifest.get(i).split("\\|");
                    db.insertJSON(new getAPIInformation(URL, token_navieraAustral, Integer.parseInt(manifest_config[1]), Integer.parseInt(manifest_config[2]), Integer.parseInt(manifest_config[3]), manifest_config[5], manifest_config[4]).execute().get(), "manifest", Integer.parseInt(manifest_config[2]));
                }
            }
            int count_after = Integer.parseInt(db.selectFirst("select count(id) from manifest"));
            //Log.e("count after", count_after + "");
            if (count_before != count_after) {
                int total = count_after - count_before;
                total_temp = total;
            }
            //Thread.sleep(3000);
        } catch (android.database.SQLException e) {
            log.writeLog(getApplicationContext(), "MainActivity", "ERROR", "updateManifest" + e.getMessage());
        } catch (JSONException e) {
            log.writeLog(getApplicationContext(), "MainActivity", "ERROR", "updateManifest" + e.getMessage());
        } catch (InterruptedException e) {
            log.writeLog(getApplicationContext(), "MainActivity", "ERROR", "updateManifest" + e.getMessage());
        } catch (ExecutionException e) {
            log.writeLog(getApplicationContext(), "MainActivity", "ERROR", "updateManifest" + e.getMessage());
        }
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

    public void sendRecordstoAPI() {
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
                            if (Asynctask_sendRecord == null ) {
                                OfflineRecordsSynchronizer();
                            }
                            if (db.record_desync_count() > 0&& Asynctask_sendRecord.getStatus()!= AsyncTask.Status.RUNNING)
                                OfflineRecordsSynchronizer();
                        } catch (android.database.SQLException e) {
                            log.writeLog(getApplicationContext(), "MainActivity", "ERROR", "updateDB" + e.getMessage());
                        }
                    }
                });
            }
        };
        timer.schedule(task, 0,timer_sendRecordsAPI);  // 360000= 6 minutes, 7 minutes=420000
    }

    public String getCurrentDate() {
        Calendar cal = Calendar.getInstance();
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("yyyy-MM-dd");
        String localTime = date.format(currentLocalTime);
        return localTime;
    }

    public void ticketValidator(String rut, String route, String port, String date, String hour, String ship) {
        boolean valid = false;
        String person = null;
        Record record = new Record(); // Object to be sended to API Axxezo.
        DatabaseHelper db = DatabaseHelper.getInstance(this);
        rut = rut.trim().toUpperCase();

        if (date.equals(getCurrentDate()))
            if (hour.equals(db.selectFirst("select hours.name from hours inner join config on hours.name=config.hour")))
                if (route.equals(db.selectFirst("select routes.id from routes inner join config on routes.id=config.route_id")))
                    if (port.equals(db.selectFirst("select id_api from ports where name='" + selectedSpinnerLanded + "'")))
                        if (ship.equals(db.selectFirst("select ships.id from ships inner join config on ships.id=config.ship_id"))) {
                            person = db.validatePerson(rut);
                            if (!person.isEmpty()) {
                                valid = true;
                            } else TextViewStatus.setText("PERSONA NO ENCONTRADA EN EL MANIFIESTO");
                            if (is_input) {
                                if (!selectedSpinnerLanded.equals(db.selectFirst("select origin from manifest where id_people='" + rut + "'"))) {
                                    TextViewStatus.setText("PUERTO EMBARQUE NO PERTENECE");
                                    valid = false;
                                } else
                                    valid = true;
                            }
                            if (!is_input) {
                                if (!selectedSpinnerLanded.equals(db.selectFirst("select destination from manifest where id_people='" + rut + "'"))) {
                                    TextViewStatus.setText("PUERTO DESEMBARQUE NO PERTENECE");
                                    valid = false;
                                } else
                                    valid = true;
                            }
                        } else TextViewStatus.setText("NAVE NO CORRESPONDE");
                    else TextViewStatus.setText("PUERTO NO CORRESPONDE");
                else TextViewStatus.setText("RUTA NO CORRESPONDE");
            else TextViewStatus.setText("HORARIO NO CORRESPONDE");
        else TextViewStatus.setText("FECHA NO CORRESPONDE");

        String[] array = new String[20];
        if (person != null)
            array = person.split(";");
        else
            array = db.validatePerson(rut).split(";");
        if (valid) {
            new LoadSound(2).execute();
            imageview.setImageResource(R.drawable.img_true);
            //array = person.split(";");
            TextViewFullname.setText(array[1]);
            TextViewRut.setText(rut);
            TextViewStatus.setText("");
            record.setPermitted(1);
        } else {
            new LoadSound(3).execute();
            imageview.setImageResource(R.drawable.img_false);
            TextViewRut.setText(rut);
            if (!array[0].equals(""))
                TextViewFullname.setText(array[1]);
            else
                TextViewFullname.setText("");

            record.setPermitted(0);
        }


        record.setPerson_document(rut);
        record.setPerson_name(TextViewFullname.getText().toString());

        if (!array[0].equals("")) record.setPerson_name(array[1]);
        else record.setPerson_name("");
        if (is_input && valid) record.setInput(1);
        else if (!is_input) {
            record.setInput(-1);
            TextViewStatus.setText("DESEMBARCO CON TICKET NO CORRESPONDE");
        }
        if (!valid)
            record.setInput(-1);
        record.setDatetime(getCurrentDateTime("yyyy-MM-dd'T'HH:mm:ss.S'Z'"));
        record.setSync(0);
        if (!array[0].equals("")) {
            record.setPort_registry(array[4]);
        }
        //add information that isn`t content in qr code
        ArrayList<String> select_from_manifest = db.select("select origin,destination from manifest where id_people='" + rut + "'", "|");
        String[] manifest_config = null;
        if (select_from_manifest.size() > 0) {
            manifest_config = select_from_manifest.get(0).split("\\|");
            record.setOrigin(manifest_config[0]);
            record.setDestination(manifest_config[1]);
        }
        if (!valid)
            if (!TextViewStatus.getText().toString().isEmpty())
                record.setReason(TextViewStatus.getText().toString());
        db.add_record(record);
        if (valid)
            if (is_input)
                db.updatePeopleManifest(rut, 1);
            else if (!is_input)
                db.updatePeopleManifest(rut, 2);
        String validator = db.selectFirst("select is_inside from manifest where id_people='" + rut + "'");
        if (!valid && !validator.isEmpty()) {
            if (Integer.parseInt(validator) > 0) {
                //db.updatePeopleManifest(rut, record.getInput());
            } else
                db.updatePeopleManifest(rut, 0);
        }
    }

    public void documentValidator(String rut) {
        String person;
        rut = rut.toUpperCase();
        DatabaseHelper db = DatabaseHelper.getInstance(this);
        //schema validate person string=m.id_people,p.name where m is manifest and p is people
        person = db.validatePerson(rut);
        String[] array = new String[20];
        Record record = new Record(); // Object to be sended to API Axxezo.
        boolean valid = false;
        if (!person.isEmpty()) {
            if (is_input) {
                if (!selectedSpinnerLanded.equals(db.selectFirst("select origin from manifest where id_people='" + rut + "'"))) {
                    TextViewStatus.setText("PUERTO EMBARQUE NO PERTENECE");
                    valid = false;
                } else
                    valid = true;
            }
            if (!is_input) {
                if (!selectedSpinnerLanded.equals(db.selectFirst("select destination from manifest where id_people='" + rut + "'"))) {
                    TextViewStatus.setText("PUERTO DESEMBARQUE NO PERTENECE");
                    //valid = false;
                } else
                    valid = true;
            }
        }
        if (valid) {
            new LoadSound(2).execute();
            imageview.setImageResource(R.drawable.img_true);
            array = person.split(";");
            TextViewFullname.setText(array[1]);
            TextViewRut.setText(array[0]);
            TextViewStatus.setText("");
            record.setPermitted(1);
        } else {
            new LoadSound(3).execute();
            TextViewRut.setText(rut);
            //is_input = false;
            if (db.selectFirst("select id from manifest where id_people='" + rut + "'").isEmpty()) {
                TextViewStatus.setText("NO ESTA EN EL MANIFIESTO");
            }
            imageview.setImageResource(R.drawable.img_false);
            record.setPermitted(0);
        }

        record.setPerson_document(rut);
        if (TextViewFullname.getText().equals("NO ESTA EN EL MANIFIESTO"))
            record.setPerson_name("");
        else record.setPerson_name(TextViewFullname.getText().toString());

        if (array[1] != null) record.setPerson_name(array[1]);
        else record.setPerson_name("");

        /*if (array[6]!=null&&!array[6].equals("null")) record.setTicket(Integer.parseInt(array[6]));
        else record.setTicket(0);*/

        String validator = db.selectFirst("select is_inside from manifest where id_people='" + rut + "'");
        if (valid) {
            if (is_input)
                db.updatePeopleManifest(rut, 1);
            else
                db.updatePeopleManifest(rut, 2);
            record.setInput(Integer.parseInt(db.selectFirst("select is_inside from manifest where id_people='" + rut + "'")));
        } else {
            record.setInput(-1);
        }
        if (person.isEmpty())
            record.setInput(-1);

        if (!valid)
            if (!TextViewStatus.getText().toString().isEmpty())
                record.setReason(TextViewStatus.getText().toString());

        record.setDatetime(getCurrentDateTime("yyyy-MM-dd'T'HH:mm:ss.S'Z'"));
        record.setSync(0);
        record.setOrigin(array[2]);
        record.setDestination(array[3]);
        record.setPort_registry(selectedSpinnerLanded);
        db.add_record(record);
    }

    public void makeToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    public void OfflineRecordsSynchronizer() {
        DatabaseHelper db = DatabaseHelper.getInstance(this);
        List<Record> records = db.get_desynchronized_records();
        Asynctask_sendRecord = new RegisterTask(records, AxxezoAPI + "/records");
        Asynctask_sendRecord.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

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

    public class UpdateManifest extends AsyncTask<Void, Void, Integer> {
        private int update_manifest_count;


        @Override
        protected Integer doInBackground(Void... params) {
            return update_manifest_count = updateManifest();
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
            GETandUpdateStateInManifest();
            return null;
        }
    }


    public String getManifest(String Url, String Token, int ID_route, int ID_port, String date, int ID_transport, String hour) throws IOException {
        //String date must be in format yyyy-MM-dd
        //String hour must be in format HH-dd
        URL url = new URL(Url + "/manifests?route=" + ID_route + "&date=" + date + "&port=" + ID_port + "&transport=" + ID_transport + "&hour=" + hour);
        //Log.d("url manifest", url.toString());
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
            me.printStackTrace();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        if (conn != null) {
            conn.disconnect();
        }
        if (content.length() <= 2) { //[]
            content = "204"; // No content
        }
        //LogApp.i("Manifest response", content);
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

    public int getStatusFromManifest(int position) {
        DatabaseHelper db = DatabaseHelper.getInstance(this);
        int manifestCount = -1;
        int PendingCount = -1;
        int EmbarkedCount = -1;
        int LandedCount = -1;
        ArrayList<String> select_counts = db.select("select (select count(*) from manifest)," +
                "(select count(*) from manifest where is_inside=0),(select count(*) from manifest where is_inside=1)," +
                "(select count(*) from manifest where is_inside=2)", "|");
        int count = 0;
        if (select_counts.size() > 0) {
            String[] binnacle_param_id = select_counts.get(0).split("\\|");
            manifestCount = Integer.parseInt(binnacle_param_id[0]);
            PendingCount = Integer.parseInt(binnacle_param_id[1]);
            EmbarkedCount = Integer.parseInt(binnacle_param_id[2]);
            LandedCount = Integer.parseInt(binnacle_param_id[3]);
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
        return count;
    }

    public class getAPIInformation extends AsyncTask<String, Void, String> {
        private String URL;
        private String getInformation;
        private String token;
        private String date;
        private String hour;
        private int flag = -1;
        private int route;
        private int port;
        private int transport;

        getAPIInformation(String URL, String token, int route, int port, int transport, String date, String hour) {//manifest
            this.URL = URL;
            this.token = token;
            this.route = route;
            this.port = port;
            this.transport = transport;
            this.date = date;
            this.hour = hour;
            getInformation = "";
            flag = 4;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                switch (flag) {
                    case 4:
                        getInformation = getManifest(URL, token, route, port, date, transport, hour);
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

    public void wifiState(boolean bool) {
        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(bool);
    }

    public void manualRegistration() {
        DatabaseHelper db = DatabaseHelper.getInstance(this);
        String rut = TextViewRut.getText().toString();
        //1.- is in manifest ?
        String contentManifest = db.selectFirst("select is_inside from manifest where id_people='" + rut + "'");
        if (!contentManifest.isEmpty()) {
            Toast.makeText(this, "Persona No se encuentra en Manifiesto", Toast.LENGTH_SHORT).show();
        } else if (Integer.parseInt(contentManifest) == 0) {
            dialogManualRegistration("ALERTA", "Persona no registra embarque ni desembarque \nSeleccione una opcion", "Embarcar", "Desembarcar");
        }

    }

    public AlertDialog dialogManualRegistration(String title, String message, String ButtonTrue, String ButtonCancel) {
        //  AlertDialog.Builder builder = new AlertDialog.Builder((new ContextThemeWrapper(this, R.style.myDialog)));
        AlertDialog.Builder builder = new AlertDialog.Builder((new ContextThemeWrapper(this, R.style.myDialog)));

        builder.setTitle("Titulo")
                .setMessage("El Mensaje para el usuario")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                .setNegativeButton("CANCELAR",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
        return builder.create();
    }

    public void GETandUpdateStateInManifest() {
        Log.e("updating state", "MANIFEST STATE");
        //String url="http://192.168.1.117:3000/api/states/getState?doc=15792726&route=2&port=PUERTO%20MONTT&ship=JACAF&date=2017-01-19&hour=23:00";
        String url = AxxezoAPI + "/states/getState?";
        DatabaseHelper db = DatabaseHelper.getInstance(this);
        log_app log = new log_app();

        // get list manifest id_people and port

        ArrayList<String> select_dni_from_manifest = db.select("select id_people from manifest", "|");
        if (!select_dni_from_manifest.isEmpty()) {
            String[] get_manifest_row = null;
            int i = 0;
            while (i < select_dni_from_manifest.size()) {
                //Log.e("for i=", i + "");
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
                                } catch (JSONException e) {
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
        }

    }

    private class LoadSound extends AsyncTask<Void, Void, Void> {
        private int typeSound = -1;

        /*  Asyntask to play sounds in background
         *  1 Error
         *  2 Permitted
         *  3 Denied
         *  4 stop all
         */
        private LoadSound(int typeSound) {
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
