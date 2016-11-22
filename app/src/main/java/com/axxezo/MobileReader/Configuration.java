package com.axxezo.MobileReader;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;


import org.json.JSONException;

import java.io.BufferedReader;
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

public class Configuration extends AppCompatActivity {

    DatabaseHelper db = new DatabaseHelper(this);
    private Spinner combobox;
    private Spinner combobox_ports;
    private Spinner combobox_transports;
    private Spinner combobox_hours;
    int selectionSpinnerRoute;
    int selectionSpinnerPorts;
    int selectionSpinnerTransports;
    int selectionSpinnerHour;
    String hour;
    private String URL = "http://ticket.bsale.cl/control_api";
    private String token_navieraAustral = "860a2e8f6b125e4c7b9bc83709a0ac1ddac9d40f";
    private String token_transportesAustral = "49f89ee1b7c45dcca61a598efecf0b891c2b7ac5";
    private Button loadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        combobox = (Spinner) findViewById(R.id.spinner);
        combobox_ports = (Spinner) findViewById(R.id.spinner_ports);
        combobox_transports = (Spinner) findViewById(R.id.spinner_ship);
        combobox_hours = (Spinner) findViewById(R.id.spinner_hours);
        loadButton = (Button) findViewById(R.id.button_loadManifest);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        //inserts in db
        try {
            db.insertRoutesDB(new getAPIroutes().execute().get().toString());
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

        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.insertSettingsValues(selectionSpinnerRoute, selectionSpinnerPorts, selectionSpinnerTransports, hour);
                Toast.makeText(Configuration.this, "Settings Guardados Correctamente", Toast.LENGTH_SHORT).show();
                loadManifest();
            }
        });
    }

    public void loadComboboxRoutes() {
        //create adapter from combobox
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, db.getListFromDB("routes"));
        //set adapter to spinner
        combobox.setAdapter(adapter);
        //set listener from spinner
        combobox.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (combobox.getSelectedItemPosition() != 0) {
                    String nameElement = combobox.getSelectedItem().toString();
                    int idElementSelected = Integer.parseInt(db.selectFirstFromDB("SELECT id from ROUTES where name=" + "'" + nameElement + "'"));
                    if (idElementSelected != 0) {
                        selectionSpinnerRoute = idElementSelected;
                        Log.i("id Log Routes", "----" + selectionSpinnerRoute);
                        // / db.insertID(idElementSelected,"ROUTES");
                        try {
                            db.insertPortsDB(new getAPIPorts(selectionSpinnerRoute).execute().get().toString());
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
        //create adapter from combobox
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, db.getListFromDB("ports"));
        //set adapter to spinner
        combobox_ports.setAdapter(adapter);
        //set listener from spinner
        combobox_ports.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (combobox_ports.getSelectedItemPosition() != 0) {
                    String nameElement = combobox_ports.getSelectedItem().toString();
                    int idElementSelected = Integer.parseInt(db.selectFirstFromDB("SELECT id from ports where name=" + "'" + nameElement + "'"));
                    if (idElementSelected != 0) {
                        selectionSpinnerPorts = idElementSelected;
                        Log.i("id Log Routes", "----" + selectionSpinnerPorts);
                        try {
                            db.insertShipsDB(new getAPITransports(selectionSpinnerRoute, selectionSpinnerPorts, getCurrentDate()).execute().get().toString());
                            loadComboboxShips();
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

    public void loadComboboxShips() {
        //create adapter from combobox
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, db.getListFromDB("ships"));
        //set adapter to spinner
        combobox_transports.setAdapter(adapter);
        //set listener from spinner
        combobox_transports.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (combobox_transports.getSelectedItemPosition() != 0) {
                    String nameElement = combobox_transports.getSelectedItem().toString();
                    int idElementSelected = Integer.parseInt(db.selectFirstFromDB("SELECT id from ships where name=" + "'" + nameElement + "'"));
                    if (idElementSelected != 0) {
                        selectionSpinnerTransports = idElementSelected;
                        Log.i("id Log Ships", "----" + selectionSpinnerTransports);
                        try {
                            db.insertHoursDB(new getAPIHours(selectionSpinnerRoute, selectionSpinnerPorts, selectionSpinnerTransports, getCurrentDate()).execute().get().toString());
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
                android.R.layout.simple_spinner_item, db.getListFromDB("hours"));
        //set adapter to spinner
        combobox_hours.setAdapter(adapter);
        //set listener from spinner
        combobox_hours.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (combobox_hours.getSelectedItemPosition() != 0) {
                    String nameElement = combobox_hours.getSelectedItem().toString();
                    int idElementSelected = Integer.parseInt(db.selectFirstFromDB("SELECT id from Hours where name=" + "'" + nameElement + "'"));
                    if (idElementSelected != 0) {
                        selectionSpinnerHour = idElementSelected;
                        Log.i("id Log Hours", "----" + selectionSpinnerHour);
                        try {
                            hour = nameElement;
                            db.insertHoursDB(new getAPIHours(selectionSpinnerRoute, selectionSpinnerPorts, selectionSpinnerTransports, getCurrentDate()).execute().get().toString());
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

    public void loadManifest() {
        //finally charge the manifest in people table
        try {
            int load=db.insertManifestDB(new getAPIManifest(selectionSpinnerRoute, selectionSpinnerPorts, selectionSpinnerTransports,getCurrentDate(),hour).execute().get().toString());
            Toast.makeText(Configuration.this,"se han cargado "+ load+ " personas a la base de datos",Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
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

        protected void onPostExecute(String result) {
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
        if (content.length() <= 2) { //[]
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
        URL url = new URL(Url + "/manifests?route=" + ID_route + "&date=" + date + "&port=" + ID_port + "&transport=" + ID_transport + "&hour=" + hour);
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

    public String getCurrentDate() {
        Calendar cal = Calendar.getInstance();
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("yyyy-MM-dd");
        String localTime = date.format(currentLocalTime);
        return localTime;
    }

    public String getCurrentTime() {
        Calendar cal = Calendar.getInstance();
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("HH:mm");
        String localTime = date.format(currentLocalTime);
        return localTime;
    }


}
