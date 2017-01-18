package com.axxezo.MobileReader;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;


import com.dd.CircularProgressButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
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
import java.util.concurrent.ExecutionException;

public class Configuration extends AppCompatActivity {

    DatabaseHelper db = new DatabaseHelper(this);
    private Spinner combobox;
    private Spinner combobox_ports;
    private Spinner combobox_transports;
    private Spinner combobox_hours;
    private Integer selectionSpinnerRoute;
    private Integer selectionSpinnerPorts;
    private Integer selectionSpinnerTransports;
    private Integer selectionSpinnerHour;
    String hour;
    private Vibrator mVibrator;
    private String URL = "http://ticket.bsale.cl/control_api";
    private String token_navieraAustral = "860a2e8f6b125e4c7b9bc83709a0ac1ddac9d40f";
    private String token_transportesAustral = "49f89ee1b7c45dcca61a598efecf0b891c2b7ac5";
    private CircularProgressButton loadButton;
    private int manifest_load_ports;
    Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        combobox = (Spinner) findViewById(R.id.spinner);
        // combobox_ports = (Spinner) findViewById(R.id.spinner_ports);
        combobox_transports = (Spinner) findViewById(R.id.spinner_ship);
        combobox_transports.setClickable(false);
        combobox_hours = (Spinner) findViewById(R.id.spinner_hours);
        combobox_hours.setClickable(false);
        combobox.setClickable(false);
        loadButton = (CircularProgressButton) findViewById(R.id.button_loadManifest);
        manifest_load_ports = -1;
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
       /* fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        //inserts in db
        try {
            db.insertJSON(new getAPIroutes().execute().get().toString(), "routes");
            db.close();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        loadComboboxRoutes();

        //button
        loadButton.setIndeterminateProgressMode(false);
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //simulateSuccessProgress(loadButton);
                if ((combobox_transports != null && combobox_transports.getSelectedItem() != null && !combobox_transports.getSelectedItem().equals("")) && (combobox_hours != null &&
                        combobox_hours.getSelectedItem() != null && !combobox_hours.getSelectedItem().equals("")) &&
                        (combobox != null && combobox.getSelectedItem() != null) && !combobox.getSelectedItem().equals("")) {
                    loadButton.setProgress(10);
                    mVibrator.vibrate(100);
                    db.insert("delete from config");
                    db.insert("insert into config(route_id,port_id,ship_id,hour) values ('" + selectionSpinnerRoute + "','" + selectionSpinnerPorts + "','" +
                            selectionSpinnerTransports + "','" + hour + "')");
                    loadManifest();
                    loadButton.setProgress(100);
                    loadButton.setClickable(false);
                } else {
                    Toast.makeText(getApplication(), "Faltan campos por completar, verifique", Toast.LENGTH_SHORT).show();
                    loadButton.setProgress(-1);
                }
            }
        });
    }

    public void loadComboboxRoutes() {
        //create adapter from combobox
        combobox.setClickable(true);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, db.getComboboxList("routes"));
        //set adapter to spinner
        combobox.setAdapter(adapter);
        //set listener from spinner
        combobox.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                loadButton.setClickable(true);
                loadButton.setProgress(0);
                if (combobox.getSelectedItemPosition() != 0) {
                    String nameElement = combobox.getSelectedItem().toString();
                    int idElementSelected = Integer.parseInt(db.selectFirst("SELECT id from ROUTES where name=" + "'" + nameElement + "'"));
                    if (idElementSelected != 0) {
                        selectionSpinnerRoute = idElementSelected;
                        Log.i("id Log Routes", "----" + selectionSpinnerRoute);
                        try {
                            db.insertJSON(new getAPIPorts(selectionSpinnerRoute).execute().get().toString(), "ports");
                            loadComboboxPorts();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    public void loadComboboxPorts() {
        //int idElementSelected = Integer.parseInt(db.selectFirst("SELECT id from ports where name=" + "'" + nameElement + "'"));
        ArrayList<String> select_from_manifest = db.select("SELECT id_api from ports limit 1", "|");
        String[] manifest_is_inside = null;
        if (select_from_manifest.size() > 0) {
            manifest_is_inside = select_from_manifest.get(0).split("\\|");
            selectionSpinnerPorts = Integer.parseInt(manifest_is_inside[0]);
            try {
                db.insertJSON(new getAPITransports(selectionSpinnerRoute, selectionSpinnerPorts, getCurrentDate(0)).execute().get().toString(), "ships");
                loadComboboxShips();

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }


        } else
            Toast.makeText(this, "No se encuentra una conexion a internet disponible, verifique", Toast.LENGTH_LONG).show();
    }


    public void loadComboboxShips() {
        //create adapter from comboboX
        combobox_transports.setClickable(true);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, db.getComboboxList("ships"));
        //set adapter to spinner
        combobox_transports.setAdapter(adapter);
        //set listener from spinner
        combobox_transports.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                loadButton.setProgress(0);
                if (combobox_transports.getSelectedItemPosition() != 0) {
                    String nameElement = combobox_transports.getSelectedItem().toString();
                    int idElementSelected = Integer.parseInt(db.selectFirst("SELECT id from ships where name=" + "'" + nameElement + "'"));
                    if (idElementSelected != 0) {
                        selectionSpinnerTransports = idElementSelected;
                        Log.i("id Log Ships", "----" + selectionSpinnerTransports);
                        try {
                            db.insertJSON(new getAPIHours(selectionSpinnerRoute, selectionSpinnerPorts, selectionSpinnerTransports, getCurrentDate(0)).execute().get().toString(), "hours");
                            loadComboboxHours();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }

                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    public void loadComboboxHours() {
        //create adapter from combobox
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, db.getComboboxList("hours"));
        //set adapter to spinner
        combobox_hours.setAdapter(adapter);
        combobox_hours.setClickable(true);
        //set listener from spinner
        combobox_hours.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                loadButton.setProgress(0);
                if (combobox_hours.getSelectedItemPosition() != 0) {
                    String nameElement = combobox_hours.getSelectedItem().toString();
                    int idElementSelected = Integer.parseInt(db.selectFirst("SELECT id from Hours  where name=" + "'" + nameElement + "'"));
                    if (idElementSelected != 0) {
                        selectionSpinnerHour = idElementSelected;
                        Log.i("id Log Hours", "----" + selectionSpinnerHour);
                        hour = nameElement;
                        //db.insertHoursDB(new getAPIHours(selectionSpinnerRoute, selectionSpinnerPorts, selectionSpinnerTransports, getCurrentDate(0)).execute().get().toString());
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    public void loadManifest() {
        //first delete the manifest table
        db.insert("delete from manifest");
        db.insert("delete from sqlite_sequence where name='MANIFEST'");

        //charge the manifest per each port in people table
        try {
            ArrayList<String> select_from_manifest = db.select("SELECT id_api from ports where is_in_manifest='FALSE'", "");
            ArrayList<String> select_hour_config = db.select("SELECT hour from config", "");
            String[] manifest_is_inside = null;
            String[] hour_setting = select_hour_config.get(0).split(":");
            String hours = "";
            if (select_from_manifest.size() > 0) {
                int i = 0;
                while (!select_from_manifest.isEmpty()) {
                    String currentDatetime = getCurrentDate(0);
                    manifest_is_inside = select_from_manifest.get(0).split("\\|");
                    selectionSpinnerPorts = Integer.parseInt(manifest_is_inside[0]);
                    hour = new getAPIHours(selectionSpinnerRoute, selectionSpinnerPorts, selectionSpinnerTransports, currentDatetime).execute().get().toString();
                    //load manifest of the next day if the hour is
                    if (i > 0 && Integer.parseInt(hour_setting[0]) > 20) {
                        currentDatetime = getCurrentDate(1);
                        hour = new getAPIHours(selectionSpinnerRoute, selectionSpinnerPorts, selectionSpinnerTransports, currentDatetime).execute().get().toString();
                    }
                    //obtain the json hour information
                    JSONObject objectJson;
                    JSONArray jsonManifest;
                    if (!hour.isEmpty()) {
                        objectJson = new JSONObject(hour);
                        jsonManifest = objectJson.getJSONArray("list_hours");
                        try {
                            for (int j = 0; j < jsonManifest.length(); j++) {
                                hours = (jsonManifest.getJSONObject(0).getString("horas"));
                            }
                        } catch (JSONException e) {
                            Log.e("error loadManifest hour", e.getMessage().toString());
                        }
                    }
                    db.insertJSON(new getAPIManifest(selectionSpinnerRoute, selectionSpinnerPorts, selectionSpinnerTransports, currentDatetime, hours).execute().get().toString(),"manifest");
                    //finally, delete from arraylist, the port
                    select_from_manifest.remove(selectionSpinnerPorts.toString());
                    Log.d("select_from_manifest", select_from_manifest.size() + "");
                    db.insert("update ports set is_in_manifest='TRUE' where id_api='" + selectionSpinnerPorts + "'");
                    i++;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        //load size of manifest

        ArrayList<String> select_counts = db.select("select count(*) from manifest", "|");
        if (select_counts.size() > 0) {
            String[] binnacle_param_id = select_counts.get(0).split("\\|");
            Toast.makeText(Configuration.this, "se han cargado " + Integer.parseInt(binnacle_param_id[0]) + " personas a la base de datos", Toast.LENGTH_LONG).show();
        }
    }

    public class getAPIroutes extends AsyncTask<String, Void, String> {
        private String getInformation = "";

        @Override
        protected String doInBackground(String... strings) {
            try {
                getInformation = getRoutes(URL, token_navieraAustral);
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

    public class getAPIPorts extends AsyncTask<String, Void, String> {
        private String getInformation = "";
        private int route;

        getAPIPorts(int id_route) {
            route = id_route;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                getInformation = getPorts(URL, token_navieraAustral, route);
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

    public class getAPITransports extends AsyncTask<String, Void, String> {
        public String getInformation = "";
        private int route;
        private int port;
        String Date;

        getAPITransports(int id_route, int id_port, String date) {
            route = id_route;
            port = id_port;
            Date = date;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                getInformation = getTransports(URL, token_navieraAustral, route, port, Date);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return getInformation;
        }

        protected void onPostExecute(String result) {
        }

        @Override
        public String toString() {
            return getInformation + "";
        }

    }

    public class getAPIHours extends AsyncTask<String, Void, String> {
        public String getInformation = "";
        private int route;
        private int port;
        private int transports;
        String Date;

        getAPIHours(int route_id, int port_id, int transports_id, String date) {
            route = route_id;
            port = port_id;
            transports = transports_id;
            Date = date;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                getInformation = getHours(URL, token_navieraAustral, route, port, Date, transports);
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

    /*
           Give the avalaible routes in the System
           obtain the routes from api http://ticket.bsale.cl/control_api/routes
    */
    public String getRoutes(String Url, String Token) throws IOException {
        URL url = new URL(Url + "/routes");
        Log.d("get routes", url.toString());
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
            Log.e("class config, line 443", ioe.getMessage().toString());
        }
        if (conn != null) {
            conn.disconnect();
        }
        if (content == null || content.length() <= 2) { //[]
            content = "204"; // No content
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Existe un problema con la conexion de internet, verifique e intente nuevamente", Toast.LENGTH_LONG).show();
                }
            });
        }
        Log.d("Routes Server response", content);
        return content;
    }

    public String getPorts(String Url, String Token, int ID_route) throws IOException {
        URL url = new URL(Url + "/ports?route=" + ID_route);
        Log.d("get ports", url.toString());
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
        Log.d("get ships", url.toString());
        String content = "";
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("TOKEN", Token);
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(4000);
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
        Log.d("get hours", url.toString());
        String content = "";
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("TOKEN", Token);
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(4000);
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
        Log.d(" Hour Server response", content);
        return content;
    }

    public String getManifest(String Url, String Token, int ID_route, int ID_port, String date, int ID_transport, String hour) throws IOException {
        //String date must be in format yyyy-MM-dd
        //String hour must be in format HH-dd
        URL url = new URL(Url + "/manifests?route=" + ID_route + "&date=" + date + "&port=" + ID_port + "&transport=" + ID_transport + "&hour=" + hour);
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

    public String getCurrentDate(int days) {
        Calendar cal = Calendar.getInstance();
        Date currentLocalTime;
        DateFormat date = new SimpleDateFormat("yyyy-MM-dd");
        String returntime = "";
        if (days == 0) {
            currentLocalTime = cal.getTime();
            returntime = date.format(currentLocalTime);
        } else if (days > 0) {
            cal.add(Calendar.DAY_OF_MONTH, days); //Adds a day
            returntime = date.format(cal.getTime());
        }

        return returntime;
    }

    public String getCurrentTime() {
        Calendar cal = Calendar.getInstance();
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("HH:mm");
        String localTime = date.format(currentLocalTime);
        return localTime;
    }

    public String getToken_navieraAustral() {
        return token_navieraAustral;
    }

    public String getURl() {
        return URL;
    }

}
