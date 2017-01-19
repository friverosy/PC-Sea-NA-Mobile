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
import android.device.ScanManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private String URL = "http://ticket.bsale.cl/control_api";
    private static String token_navieraAustral = "860a2e8f6b125e4c7b9bc83709a0ac1ddac9d40f";
    private static String token_transportesAustral = "49f89ee1b7c45dcca61a598efecf0b891c2b7ac5";
    private TextView TextViewFullname;
    private TextView TextViewRut;
    private TextView TextViewStatus;
    private TextView TextViewManifestUpdate;
    private ImageView imageview;
    private final static String SCAN_ACTION = "urovo.rcv.message";//扫描结束action
    private Vibrator mVibrator;
    private ScanManager mScanManager;
    private SoundPool soundpool = null;
    private int soundid;
    private String barcodeStr;
    private boolean isScaning = false;
    MediaPlayer mp3Dennied;
    MediaPlayer mp3Permitted;
    MediaPlayer mp3Error;
    private static String AxxezoAPI;
    private static String ImaginexAPI;
    private boolean is_input = true;
    private Switch mySwitch;
    private static MainActivity mInstance;


    String SERVERIP = "192.168.1.50";

    Server s = new Server(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mInstance = this;
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        TextViewFullname = (TextView) findViewById(R.id.fullname);
        TextViewRut = (TextView) findViewById(R.id.rut);
        TextViewStatus = (TextView) findViewById(R.id.status);
        TextViewManifestUpdate = (TextView) findViewById(R.id.textView_lastManifestUpdate);
        imageview = (ImageView) findViewById(R.id.imageView);
        mp3Dennied = MediaPlayer.create(MainActivity.this, R.raw.bad);
        mp3Permitted = MediaPlayer.create(MainActivity.this, R.raw.good);
        mp3Error = MediaPlayer.create(MainActivity.this, R.raw.error);
        mySwitch = (Switch) findViewById(R.id.mySwitch);

        writeLog("DEBUG", "Application has started Correctly");
        //AxxezoAPI = "http://axxezocloud.brazilsouth.cloudapp.azure.com:3001";
        AxxezoAPI = "http://192.168.1.126:3000/api";
        ImaginexAPI = "http://ticket.bsale.cl/control_api";

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHelper db = new DatabaseHelper(getApplicationContext());
                String text = "Ruta: " + db.selectFirst("select routes.name from routes inner join config on routes.id=config.route_id");
                       // ", Puerto: " + db.selectFirst("select ports.name from ports inner join config on ports.id_api=config.port_id") + "\n" +
                       // "  Nave: " + db.selectFirst("select ships.name from ships inner join config on ships.id=config.ship_id") +
                       // ", Hora: " + db.selectFirst("select hours.name from hours inner join config on hours.name=config.hour");
                Snackbar.make(view, text, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                db.close();
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
        is_input = true;
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
            if (mp3Error.isPlaying()) mp3Error.stop();
            if (mp3Dennied.isPlaying()) mp3Dennied.stop();
            if (mp3Permitted.isPlaying()) mp3Permitted.stop();

            isScaning = false;
            //soundpool.play(soundid, 1, 1, 0, 0, 1);

            mVibrator.vibrate(100);
            reset();

            byte[] barcode = intent.getByteArrayExtra("barocode");
            int barocodelen = intent.getIntExtra("length", 0);
            byte barcodeType = intent.getByteExtra("barcodeType", (byte) 0);
            Log.i("codetype", String.valueOf(barcodeType));
            barcodeStr = new String(barcode, 0, barocodelen);
            String rawCode = barcodeStr;
            writeLog("Raw Code:", rawCode);
            Log.d("---", rawCode);

            int flag = 0; // 0 for end without k, 1 with k
            Person person = new Person();

            if (barcodeType == 28) { // QR code
                if (barcodeStr.contains("client_code")) {
                    // Its a ticket
                    try {
                        Log.d("barcode", barcodeStr);
                        JSONObject json = new JSONObject(barcodeStr);
                        String doc = json.getString("client_code");
                        doc = doc.substring(0, doc.length() - 2);
                        person.setDocument(doc);
                        barcodeStr = doc;
                        ticketValidator(doc, json.getString("route"), json.getString("port"), json.getString("date"), json.getString("hour"), json.getString("transport"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Its a DNI Card.
                    barcodeStr = barcodeStr.substring(
                            barcodeStr.indexOf("RUN=") + 4,
                            barcodeStr.indexOf("&type"));
                    // Remove DV.
                    barcodeStr = barcodeStr.substring(0, barcodeStr.indexOf("-"));
                    documentValidator(barcodeStr);
                }

            } else if (barcodeType == 17) { // PDF417
                barcodeStr = barcodeStr.substring(0, 9);
                barcodeStr = barcodeStr.replace(" ", "");
                if (barcodeStr.endsWith("K")) {
                    barcodeStr = barcodeStr.replace("K", "0");

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
                    ex.printStackTrace();
                    TextViewFullname.setText("");
                }
                barcodeStr = barcodeStr.replace("k", "");
                barcodeStr = barcodeStr.replace("K", "");
                documentValidator(barcodeStr);
            }

            Log.i("Cooked Barcode", barcodeStr);
            writeLog("Cooked Barcode", barcodeStr);
        }
    };

    public String getCurrentDateTime() {
        Calendar cal = Calendar.getInstance();
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("dd-MM-yyy HH:mm:ss");
        String localTime = date.format(currentLocalTime);
        return localTime;
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
        s.ServerKill();
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
        UpdateDb();
        updateManifest();
        IntentFilter filter = new IntentFilter();
        filter.addAction(SCAN_ACTION);
        registerReceiver(mScanReceiver, filter);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                s.run();

            }
        });
        t.start();
    }

    private void updateManifest() {
       Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    DatabaseHelper db = new DatabaseHelper(getApplicationContext());
                    try {
                        ArrayList<String> select_from_manifest = db.select("select * from config", "|");
                        String[] manifest_config = null;
                        int count_after=Integer.parseInt(db.selectFirst("select count(id) from manifest"));
                        for(int i=0;i<select_from_manifest.size();i++) {
                            if (select_from_manifest.size() > 0) {
                                manifest_config = select_from_manifest.get(0).split("\\|");
                            }
                            db.insertJSON(new getAPIInformation(URL, token_navieraAustral,Integer.parseInt(manifest_config[1]),Integer.parseInt(manifest_config[2]),Integer.parseInt(manifest_config[3]), getCurrentDate(), manifest_config[4]).execute().get(), "manifest");
                        }
                        int count_before=Integer.parseInt(db.selectFirst("select count(id) from manifest"));
                        if(count_before!=count_after){
                            int total=count_before-count_after;
                            Toast.makeText(getApplication(),"se han actualizado "+total+" en el manifiesto",Toast.LENGTH_SHORT).show();
                            TextViewManifestUpdate.setTextColor(Color.WHITE);
                            TextViewManifestUpdate.setText("Ultima Actualizacion "+getCurrentDateTime());
                        }
                        db.close();
                        Thread.sleep(300000); // 5 Min = 300000
                    } catch (Exception e) {
                        writeLog("ERROR", e.toString());
                    }
                    db.close();
                }
            }
        };
        new Thread(runnable).start();
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

    public void UpdateDb() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    DatabaseHelper db = new DatabaseHelper(getApplicationContext());
                    try {
                        if (Integer.parseInt(db.selectFirst("select count(id) from records where sync=0").trim()) >= 1)
                            OfflineRecordsSynchronizer();
                        db.close();
                        Thread.sleep(300000); // 5 Min = 300000
                    } catch (Exception e) {
                        writeLog("ERROR", e.toString());
                    }
                    db.close();
                }
            }
        };
        new Thread(runnable).start();
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
        final Record record_send = new Record();
        DatabaseHelper db = new DatabaseHelper(this);

        if (date.equals(getCurrentDate()))
            if (hour.equals(db.selectFirst("select hours.name from hours inner join config on hours.name=config.hour")))
                if (route.equals(db.selectFirst("select routes.id from routes inner join config on routes.id=config.route_id")))
                    if (port.equals(db.selectFirst("select ports.id_api from ports inner join config on ports.id_api=config.port_id")))
                        if (ship.equals(db.selectFirst("select ships.id from ships inner join config on ships.id=config.ship_id"))) {
                            person = db.validatePerson(rut);
                            if (!person.isEmpty())
                                valid = true;
                            else TextViewStatus.setText("PERSONA NO ENCONTRADA EN EL MANIFIESTO");
                        } else TextViewStatus.setText("NAVE NO CORRESPONDE");
                    else TextViewStatus.setText("PUERTO NO CORRESPONDE");
                else TextViewStatus.setText("RUTA NO CORRESPONDE");
            else TextViewStatus.setText("HORARIO NO CORRESPONDE");
        else TextViewStatus.setText("FECHA NO CORRESPONDE");

        String[] array = new String[20];
        if (valid) {
            mp3Permitted.start();
            imageview.setImageResource(R.drawable.img_true);
            array = person.split(",");
            TextViewFullname.setText(array[1]);
            record.setPermitted(1);
        } else {
            mp3Dennied.start();
            imageview.setImageResource(R.drawable.img_false);
            TextViewRut.setText(rut);
            TextViewFullname.setText("");
            record.setPermitted(0);
        }

        record.setPerson_document(rut);
        record.setPerson_name(TextViewFullname.getText().toString());
        if (is_input) record.setInput(1);
        else record.setInput(2);
        record.setDatetime(getCurrentDateTime());
        record.setSync(0);
        record.setPort_id(array[4]);
        record.setShip_id(array[5]);
        record.setSailing_hour(hour);
        db.add_record(record);
        db.updatePeopleManifest(rut, record.getInput());
        db.close();
        /****************************Client**************************************************/
        JSONObject json_to_send = new JSONObject();

        try {
            json_to_send.put("document", record.getPerson_document());
            json_to_send.put("input", record.getInput());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String temp_string_1 = json_to_send.toString();
        Log.d("Aca1", "AQUI");


        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Client f = new Client(temp_string_1, SERVERIP, 8080);
                f.run();
            }
        });
        t.start();
        /*****************************************************************************/
        new RegisterTask(record, AxxezoAPI + "/records").execute();
    }

    public void documentValidator(String rut) {
        String person;
        DatabaseHelper db = new DatabaseHelper(this);
        //schema validate person string=m.id_people,p.name where m is manifest and p is people
        person = db.validatePerson(rut);
        String[] array = new String[20];
        Record record = new Record(); // Object to be sended to API Axxezo.
        if (!person.isEmpty()) {
            mp3Permitted.start();
            imageview.setImageResource(R.drawable.img_true);
            array = person.split(",");
            TextViewFullname.setText(array[1]);
            TextViewRut.setText(array[0]);
            record.setPermitted(1);
        } else {
            mp3Dennied.start();
            TextViewRut.setText(rut);
            TextViewStatus.setText("NO ESTA EN EL MANIFIESTO");
            imageview.setImageResource(R.drawable.img_false);
            record.setPermitted(0);
        }

        record.setPerson_document(rut);
        Log.i("manifest", TextViewFullname.getText().toString());
        if (TextViewFullname.getText().equals("NO ESTA EN EL MANIFIESTO"))
            record.setPerson_name("");
        else record.setPerson_name(TextViewFullname.getText().toString());

        if (is_input) record.setInput(1);
        else record.setInput(2);
        record.setDatetime(getCurrentDateTime());
        record.setSync(0);
        record.setOrigin(array[2]);
        record.setDestination(array[3]);
        record.setPort_id(array[4]);
        record.setShip_id(array[5]);
        record.setManifest_total(getStatusFromManifest(1));
        record.setManifest_embarked(getStatusFromManifest(3));
        record.setManifest_landed(getStatusFromManifest(4));
        record.setManifest_pending(getStatusFromManifest(2));
        db.add_record(record);
        db.updatePeopleManifest(rut, record.getInput());
        db.close();


/*****************Client******************************************************************/
        JSONObject json_to_send = new JSONObject();

        try {
            json_to_send.put("document", record.getPerson_document());
            json_to_send.put("input", record.getInput());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String temp_string_2 = json_to_send.toString();
        Log.d("Aca2", "AQUI");


        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Client f = new Client(temp_string_2, SERVERIP, 8080);
                f.run();
            }
        });
        t.start();
        /**************************************************************************************************/
        new RegisterTask(record, AxxezoAPI + "/records").execute();
    }

    public void makeToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
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

    public void OfflineRecordsSynchronizer() {
        DatabaseHelper db = new DatabaseHelper(this);
        List records = db.get_desynchronized_records();
        db.close();

        String[] arr;
        for (int i = 0; i <= records.size() - 1; i++) {
            Record record = new Record();
            Log.d("Missing Sync", records.get(i).toString());
            arr = records.get(i).toString().split(";");
            record.setId(Integer.parseInt(arr[0]));
            record.setDatetime(arr[1]);
            record.setPerson_document(arr[2]);
            record.setPerson_name(arr[3]);
            record.setOrigin(arr[4]);
            record.setDestination(arr[5]);
            record.setPort_id(arr[6]);
            record.setShip_id(arr[7]);
            record.setSailing_hour(arr[8]);
            record.setInput(Integer.parseInt(arr[9]));
            record.setSync(Integer.parseInt(arr[10]));
            record.setPermitted(Integer.parseInt(arr[11]));
            record.setManifest_total(getStatusFromManifest(1));
            record.setManifest_embarked(getStatusFromManifest(3));
            record.setManifest_landed(getStatusFromManifest(4));
            record.setManifest_pending(getStatusFromManifest(2));

            new RegisterTask(record, AxxezoAPI + "/records").execute();
        }
    }

    public String POST(Record record, String url) {
        InputStream inputStream;
        String result = "";
        String json = "";
        String jsonCount = "";
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObjectCount = new JSONObject();
        try {

            jsonObject.accumulate("datetime", record.getDatetime());
            jsonObject.accumulate("doc", record.getPerson_document());
            jsonObject.accumulate("name", record.getPerson_name());
            jsonObject.accumulate("origen", record.getOrigin());
            jsonObject.accumulate("destination", record.getDestination());
            jsonObject.accumulate("port", record.getPort_id());//puerto de registro
            jsonObject.accumulate("ship", record.getShip_id());
            jsonObject.accumulate("sailing_hour", record.getSailing_hour());
            jsonObject.accumulate("state", record.getInput());
            jsonObject.accumulate("permitted", record.getPermitted());

            jsonObjectCount.accumulate("total", record.getManifest_total());
            jsonObjectCount.accumulate("embarkeds", record.getManifest_embarked());
            jsonObjectCount.accumulate("landed", record.getManifest_landed());
            //jsonObjectCount.accumulate("manifest_pending", record.getManifest_pending());
            jsonObjectCount.accumulate("boletus", record.getTicket());

            ArrayList<JSONObject> temp = new ArrayList<>();
            temp.add(jsonObject);
            temp.add(jsonObjectCount);

            // 4. convert JSONObject to JSON to String
            for (int i = 0; i < temp.size(); i++) {
                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                if (i == 1) {
                    url = AxxezoAPI + "/manifests/update";
                }
                HttpPost httpPost = new HttpPost(url);
                if (temp.get(i).length() <= 14 && record.getId() != 0) { // 9 element on json
                    json = temp.get(i).toString();

                    // 5. set json to StringEntity
                    StringEntity se = new StringEntity(json);

                    // 6. set httpPost Entity
                    httpPost.setEntity(se);

                    // 7. Set some headers to inform server about the type of the content
                    httpPost.setHeader("Accept", "application/json");
                    httpPost.setHeader("Content-type", "application/json");

                    // 8. Execute POST request to the given URL

                    if (!AxxezoAPI.equals("http://:0")) {
                        HttpResponse httpResponse = httpclient.execute(httpPost);
                        //Log.e("status code",httpResponse.getStatusLine().getStatusCode()+"");
                        // 9. receive response as inputStream
                        inputStream = httpResponse.getEntity().getContent();

                        // 10. convert inputstream to string
                        if (inputStream != null) {
                            result = convertInputStreamToString(inputStream);
                            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                                Log.d("json POSTED", json);
                                // if has sync=0 its becouse its an offline record to be will synchronized.
                                if (record.getSync() == 0) {
                                    Log.d("---", "going into update record");
                                    DatabaseHelper db = new DatabaseHelper(this);
                                    db.update_record(record.getId());
                                    db.close();
                                }
                            }
                        } else {
                            result = String.valueOf(httpResponse.getStatusLine().getStatusCode());
                        }
                        //result its the json to sent
                        if (result.startsWith("http://"))
                            result = "204"; //no content
                    } else {
                        mp3Error.start();
                        //Toast.makeText(MainActivity.this, "Configure datos del servidor primero", Toast.LENGTH_LONG).show();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                makeToast("Configure datos del servidor primero");
                            }
                        });
                    }
                }
            }
        } catch (Exception e) {
            Log.d("---", "offline " + e.getMessage().toString());
        }

        // 11. return result
        return result;
    }

    public class RegisterTask extends AsyncTask<Void, Void, String> {

        private Record newRecord;
        private String url;

        RegisterTask(Record newRecord, String url) {
            this.newRecord = newRecord;
            this.url = url;
        }

        @Override
        protected String doInBackground(Void... params) {
            return POST(newRecord, url);
        }
    }

    public class getAPIManifest extends AsyncTask<String, Void, String> {
        public String getInformation = "";
        private int route;
        private int port;
        private int transports;
        String Date;
        String Hour;

        getAPIManifest(int id_route, int id_port, int id_transpot, String date, String hour) {
            route = id_route;
            port = id_port;
            transports = id_transpot;
            Date = date;
            Hour = hour;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                getInformation = getManifest(URL, token_navieraAustral, route, port, Date, transports, Hour);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return getInformation;
        }

        @Override
        public String toString() {
            return getInformation + "";
        }

    }

    public String getRoutes(String Url, String Token) throws IOException {
        URL url = new URL(Url + "/routes");
        String content = null;
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
        if (content == null || content.length() <= 2) { //[]
            content = "204"; // No content
        }
        Log.d("Routes Server response", content);
        return content;
    }

    public String getPorts(String Url, String Token, int ID_route) throws IOException {
        URL url = new URL(Url + "/ports?route=" + ID_route);
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
        Log.d("Ports Server response", content);
        return content;
    }

    public String getTransports(String Url, String Token, int ID_route, int ID_port, String date) throws IOException {
        //String date must be in format yyyy-MM-dd
        URL url = new URL(Url + "/transports?route=" + ID_route + "&date=" + date + "&port=" + ID_port);
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
        Log.d("trans Server response", content);
        return content;
    }

    public String getHours(String Url, String Token, int ID_route, int ID_port, String date, int ID_transport) throws IOException {
        //String date must be in format yyyy-MM-dd
        URL url = new URL(Url + "/hours?route=" + ID_route + "&date=" + date + "&port=" + ID_port + "&transport=" + ID_transport);
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
        Log.d(" Hour Server response", content);
        return content;
    }

    public String getManifest(String Url, String Token, int ID_route, int ID_port, String date, int ID_transport, String hour) throws IOException {
        //String date must be in format yyyy-MM-dd
        //String hour must be in format HH-dd
        URL url = new URL(Url + "/hours?route=" + ID_route + "&date=" + date + "&port=" + ID_port + "&transport=" + ID_transport + "&hour=" + hour);
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

    public int getStatusFromManifest(int position) {
        DatabaseHelper db = new DatabaseHelper(this);
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
        db.close();
        return count;
    }
    public class getAPIInformation extends AsyncTask<String, Void, String> {
        private String URL;
        private String getInformation;
        private String token;
        private String date;
        private String hour;
        private int flag=-1;
        private int route;
        private int port;
        private int transport;

        getAPIInformation(String URL,String token,int route, int port, int transport, String date, String hour) {//manifest
            this.URL=URL;
            this.token=token;
            this.route = route;
            this.port = port;
            this.transport = transport;
            this.date = date;
            this.hour = hour;
            getInformation="";
            flag=4;
        }



        @Override
        protected String doInBackground(String... strings) {
            try {
                switch (flag){
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


}
