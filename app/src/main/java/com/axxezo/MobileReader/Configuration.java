package com.axxezo.MobileReader;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Config;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.CircularProgressButton;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Configuration extends AppCompatActivity {

    private Spinner combobox_route;
    private String selectionSpinnerRoute;
    String hour;
    private Vibrator mVibrator;
    private String AxxezoAPI;
    private String token_navieraAustral;
    private String token_transportesAustral;
    private CircularProgressButton loadButton;
    //private String AxxezoAPI;
    private int manifest_load_ports;
    private String status;
    private String id_api_route;
    private boolean onclick = false;
    private TextView route;
    private TextView dataPicker;
    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //wifiState(false);

        combobox_route = (Spinner) findViewById(R.id.spinner);
        combobox_route.setClickable(false);
        loadButton = (CircularProgressButton) findViewById(R.id.button_loadManifest);
        manifest_load_ports = -1;
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        route = (TextView) findViewById(R.id.textView);
        dataPicker = (TextView) findViewById(R.id.datetime_picker);
        status = "";

        token_navieraAustral = "860a2e8f6b125e4c7b9bc83709a0ac1ddac9d40f";
        token_transportesAustral = "49f89ee1b7c45dcca61a598efecf0b891c2b7ac5";
        //AxxezoAPI = "http://axxezo-test.brazilsouth.cloudapp.azure.com:9001/api";
        //AxxezoAPI = "http://192.168.1.102:9000/api";

        //test tzu 18/05/2017
        //AxxezoAPI ="http://bm03.bluemonster.cl:9001/api";
        AxxezoAPI = "http://axxezocloud.brazilsouth.cloudapp.azure.com:5002/api";

        //button
        loadButton.setIndeterminateProgressMode(true);
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //simulateSuccessProgress(loadButton);
                onclick = true;
                mVibrator.vibrate(100);
                loadManifest();
                loadButton.setClickable(false);
                if (status.equals("200"))
                    Toast.makeText(getApplicationContext(), "se ha reiniciado la sincronizacion exitosamente", Toast.LENGTH_SHORT).show();

                Intent refresh = getIntent();
                refresh.putExtra("spinner","reload");
                setResult(RESULT_OK,refresh);
                finish();



            }
        });
        //clear cache of sistem before fill routes spinner
        deleteCache(this);

        try {
            DatabaseHelper db = DatabaseHelper.getInstance(getApplicationContext());
            db.insertJSON(new getAPIInformation(updateLabel()).execute().get(), "routes");
        } catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        loadComboboxRoutes();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
                try {
                    DatabaseHelper db = DatabaseHelper.getInstance(getApplicationContext());
                    db.insertJSON(new getAPIInformation(updateLabel()).execute().get(), "routes");
                    int countRoutes = Integer.parseInt(db.selectFirst("select count(id) from routes"));
                    if (countRoutes == 0) {
                        Toast.makeText(getApplicationContext(), "No existen rutas para el dia seleccionado o no se han cargado. Reintente", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException | InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                loadComboboxRoutes();
            }

        };

        dataPicker.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(Configuration.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }
    private String updateLabel() {
        String myFormat = "dd/MM/yyyy";
        String dateTimeDBformat="yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        SimpleDateFormat DBformat = new SimpleDateFormat(dateTimeDBformat, Locale.getDefault());

        dataPicker.setText(sdf.format(myCalendar.getTime()));
        return DBformat.format(myCalendar.getTime());
    }
    /**
     * fill combobox, obtaining information content in table "routes"
     */
    public void loadComboboxRoutes() {
        final DatabaseHelper db = DatabaseHelper.getInstance(this);
        //create adapter from combobox_route
        combobox_route.setClickable(true);
        final ArrayList<String> routes = db.selectAsList("select name from routes", 0);
        if (routes != null)
            routes.add(0, "<ELIJA UNA RUTA>");
        ArrayAdapter<String> adapter = null;
        if (routes != null) {
            adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, routes);
        }
        //combobox_route.setTag();
        //set adapter to spinner
        combobox_route.setAdapter(adapter);
        //set listener from spinner
        combobox_route.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                loadButton.setClickable(true);
                if (combobox_route.getSelectedItemPosition() != 0) {
                    String nameElement = combobox_route.getSelectedItem().toString();
                    Cursor idElementSelected = db.select("SELECT id_mongo,id from ROUTES where name=" + "'" + nameElement + "'");
                    if (idElementSelected.getCount() > 0) {
                        selectionSpinnerRoute = idElementSelected.getString(1);
                        id_api_route = idElementSelected.getString(0);
                    }
                    route.setText("Viaje " + selectionSpinnerRoute + " Seleccionado");
                    idElementSelected.close();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    /**
     * obtain manifest of endpoint, need the user select a route in combobox, insert the data in db local, fill two tables: manifest and  people
     */
    public void loadManifest() {
        DatabaseHelper db = DatabaseHelper.getInstance(this);
        //first delete the manifest table
        db.insert("delete from manifest");
        db.insert("delete from sqlite_sequence where name='MANIFEST'");
        db.insert("delete from config");
        db.insert("delete from sqlite_sequence where name='CONFIG'");
        db.insert("delete from sqlite_sequence where name='PEOPLE'");
        db.insert("delete from ports");
        db.insert("delete from sqlite_sequence where name='PORTS'");

        try {

            db.insertJSON(new getAPIInformation(AxxezoAPI, token_navieraAustral, selectionSpinnerRoute,updateLabel()).execute().get(), "manifest");
            db.insert("insert or replace into config (route_id,manifest_id,date_last_update,route_name) values ('" + selectionSpinnerRoute + "','" + id_api_route + "','" + getDeltasCurrentDateTime("yyyy-MM-dd'T'HH:mm:ss") + "',(select name from routes where id='"+selectionSpinnerRoute+"'))");//jhy
            // cambiar insert pot update
            //db.updateConfig(selectionSpinnerRoute);
            //db.insert("insert into config (route_id) values ("+selectionSpinnerRoute+")");
            db.insertJSON(new getAPIInformation(AxxezoAPI, id_api_route).execute().get(), "ports"); //insert ports of route selected

        } catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        //load size of manifest
        String select_counts = db.selectFirst("select count(id) from manifest");
        if (!select_counts.isEmpty()) {
            Toast.makeText(Configuration.this, "se han cargado " + Integer.parseInt(select_counts) + " personas en el manifiesto", Toast.LENGTH_LONG).show();
        }
    }

    public class getAPIInformation extends AsyncTask<String, Void, String> {
        private String URL;
        private String getInformation;
        private String token;
        private int flag = -1;
        private String route;
        private String datetime;

        private getAPIInformation(String datetime) {//routes
            getInformation = "";
            flag = 0;
            this.datetime=datetime;
        }

        getAPIInformation(String URL, String token, String id_mongo_route,String datetime) {//manifest
            this.URL = URL;
            this.token = token;
            this.route = id_mongo_route;
            getInformation = "";
            flag = 1;
            this.datetime=datetime;
        }

        getAPIInformation(String URL, String id_api_bsale) {//ports
            this.URL = URL;
            this.route = id_api_bsale;
            getInformation = "";
            flag = 2;
        }


        @Override
        protected String doInBackground(String... strings) {
            final OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(2, TimeUnit.SECONDS)
                    .writeTimeout(0, TimeUnit.SECONDS)
                    .readTimeout(0, TimeUnit.SECONDS)
                    .build();
            try {
                switch (flag) {
                    case 0:
                        getInformation = getRoutes(datetime,client);
                        break;
                    case 1:
                        getInformation = getManifest(URL, token, route,client);
                        break;
                    case 2:
                        getInformation = getPorts(URL, route,client);
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
            if (onclick)
                loadButton.setProgress(100);
            else
                loadButton.setProgress(0);
        }
    }

    /**
     * Give the avalaible routes in the System obtain the routes from endpoint http://ticket.bsale.cl/control_api/itinerarios?date="insert date here"
     *
     * @return content in string, but it really is json array
     * @throws IOException
     */
    public String getRoutes(String format, OkHttpClient client) throws IOException {
        URL url = new URL(AxxezoAPI + "/itineraries?date=" + format);
        Log.d("routes url", url.toString());
        String content = "";
        log_app log = new log_app();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response != null) {
                content = response.body().string();
                Log.e("content", content);
            } else
                content = response.code() + "";

        } catch (IOException e) {
            final String error = e.getMessage();
            log.writeLog(getApplicationContext(), "Configuration:line 333", "ERROR", e.getMessage());
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Error de conexion al servidor " + error, Toast.LENGTH_LONG).show();
                }
            });
        }
        if (response != null)
            response.close();
        return content;
    }

    public String getManifest(String Url, String Token, String id_mongo_route, OkHttpClient client) throws IOException {
        //"http://axxezo-test.brazilsouth.cloudapp.azure.com:9001/api/manifests?itinerary="
        URL url = new URL(Url + "/manifests?itinerary=" + id_mongo_route);
        String content = "";
        log_app log = new log_app();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response != null) {
                content = response.body().string();
            } else
                content = response.code() + "";
        } catch (IOException e) {
            final String error = e.getMessage();
            log.writeLog(getApplicationContext(), "Configuration:line 366", "ERROR", e.getMessage());
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Error de conexion al servidor " + error, Toast.LENGTH_LONG).show();
                }
            });
        }
        if (response != null)
            response.close();
        return content;
    }

    public String getPorts(String Url, String id_mongo_route, OkHttpClient client) throws IOException {
        URL url = new URL(Url + "/itineraries/" + id_mongo_route + "/seaports");
        String content = "";
        log_app log = new log_app();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response != null) {
                content = response.body().string();
            } else
                content = response.code() + "";
            Log.e("ports content", content);
        } catch (IOException e) {
            final String error = e.getMessage();
            log.writeLog(getApplicationContext(), "Configuration:line 333", "ERROR", e.getMessage());
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Error de conexion al servidor " + error, Toast.LENGTH_LONG).show();
                }
            });
        }
        if (response != null)
            response.close();
        return content;
    }

    /**
     * reset endpoint states, in old version was neccesary because we didn`t have a id_itinerary
     *
     * @return int that contains http status of this operation (when status 200 is OK)
     */
    public String GETReset() {
        String url = AxxezoAPI + "states/removeAll";
        String result = "";
        InputStream inputStream;
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        HttpResponse httpResponse;
        try {
            httpResponse = httpclient.execute(httpGet);
            result = httpResponse.getStatusLine().toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    public String getCurrentDateTime(String format) {
        Calendar cal = Calendar.getInstance();
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat(format);
        return date.format(currentLocalTime);
    }
    public String getDeltasCurrentDateTime(String format) {
        //cambiar horas a UTC
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat(format);
        date.setTimeZone(TimeZone.getTimeZone("UTC"));
        return date.format(currentLocalTime);
    }
    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

}
