package com.axxezo.MobileReader;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.dd.CircularProgressButton;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class Configuration extends AppCompatActivity {

    private Spinner combobox_route;
    private String selectionSpinnerRoute;
    String hour;
    private Vibrator mVibrator;
    private String URL;
    private String token_navieraAustral;
    private String token_transportesAustral;
    private CircularProgressButton loadButton;
    private String AxxezoAPI;
    private int manifest_load_ports;
    private String status;
    private String id_api_route;
    private boolean onclick = false;

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
        status = "";

        token_navieraAustral = "860a2e8f6b125e4c7b9bc83709a0ac1ddac9d40f";
        token_transportesAustral = "49f89ee1b7c45dcca61a598efecf0b891c2b7ac5";
        //URL = "http://ticket.bsale.cl/control_api";
        URL = "http://axxezo-test.brazilsouth.cloudapp.azure.com:9001/api";
        //URL = "http://192.168.1.102:9000/api";
        //AxxezoAPI = "http://192.168.1.102:9000/api";
        AxxezoAPI = "http://axxezo-test.brazilsouth.cloudapp.azure.com:9001/api/";

        //inserts in db
        try {
            DatabaseHelper db = DatabaseHelper.getInstance(this);
            db.insertJSON(new getAPIInformation().execute().get(), "routes");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        loadComboboxRoutes();
        final resetEndPoint reset = new resetEndPoint();
        //button
        loadButton.setIndeterminateProgressMode(true);
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //simulateSuccessProgress(loadButton);
                onclick = true;
                // if ((combobox_route != null && combobox_route.getSelectedItem() != null) && !combobox_route.getSelectedItem().equals("")) {
                mVibrator.vibrate(100);
                loadManifest();
                loadButton.setClickable(false);
                reset.execute();
                if (status.equals("200"))
                    Toast.makeText(getApplicationContext(), "se ha reiniciado la sincronizacion exitosamente", Toast.LENGTH_SHORT);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                //wifiState(true);
                //} else {
                //    Toast.makeText(getApplication(), "Faltan campos por completar, verifique", Toast.LENGTH_SHORT).show();
                //    loadButton.setProgress(-1);

                //}
            }
        });
    }

    /**
     * fill combobox, obtaining information content in table "routes"
     */
    public void loadComboboxRoutes() {
        final DatabaseHelper db = DatabaseHelper.getInstance(this);
        //create adapter from combobox_route
        combobox_route.setClickable(true);
        ArrayList<String> routes = db.selectAsList("select name from routes", 0);
        if (routes != null)
            routes.add(0, "<ELIJA UNA RUTA>");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, routes);
        //set adapter to spinner
        combobox_route.setAdapter(adapter);
        //set listener from spinner
        combobox_route.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                loadButton.setClickable(true);
                if (combobox_route.getSelectedItemPosition() != 0) {
                    String nameElement = combobox_route.getSelectedItem().toString();
                    Cursor idElementSelected =db.select("SELECT id_mongo,id from ROUTES where name=" + "'" + nameElement + "'");
                    if (idElementSelected!=null&&idElementSelected.getCount()>0) {
                        selectionSpinnerRoute = idElementSelected.getString(1);
                        id_api_route=idElementSelected.getString(0);
                        Log.i("id LogApp Routes", "----" + selectionSpinnerRoute);
                    }
                    if(idElementSelected!=null)
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

        try {

            db.insertJSON(new getAPIInformation(URL, token_navieraAustral, selectionSpinnerRoute).execute().get(), "manifest");
            db.insert("insert into config (route_id) values ("+selectionSpinnerRoute+")");
            db.insertJSON(new getAPIInformation(URL, id_api_route).execute().get(), "ports"); //insert ports of route selected

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        //load size of manifest
        String select_counts = db.selectFirst("select count(id) from manifest");
        if (!select_counts.isEmpty()) {
            Toast.makeText(Configuration.this, "se han cargado " + Integer.parseInt(select_counts) + " personas a la base de datos", Toast.LENGTH_LONG).show();
        }
    }

    public class getAPIInformation extends AsyncTask<String, Void, String> {
        private String URL;
        private String getInformation;
        private String token;
        private int flag = -1;
        private String route;
        private String id_api_bsale;
        private

        getAPIInformation() {//routes
            getInformation = "";
            flag = 0;
        }

        getAPIInformation(String URL, String token, String id_mongo_route) {//manifest
            this.URL = URL;
            this.token = token;
            this.route = id_mongo_route;
            getInformation = "";
            flag = 1;
        }

        getAPIInformation(String URL, String id_api_bsale) {//ports
            this.URL = URL;
            this.route = id_api_bsale;
            getInformation = "";
            flag = 2;
        }


        @Override
        protected String doInBackground(String... strings) {
            try {
                switch (flag) {
                    case 0:
                        getInformation = getRoutes();
                        break;
                    case 1:
                        getInformation = getManifest(URL, token, route);
                        break;
                    case 2:
                        getInformation = getPorts(URL, route);
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
        }
    }

    /**
     * Give the avalaible routes in the System obtain the routes from endpoint http://ticket.bsale.cl/control_api/itinerarios?date="insert date here"
     *
     * @return content in string, but it really is json array
     * @throws IOException
     */
    public String getRoutes() throws IOException {
        URL url = new URL(URL + "/itineraries/");
        Log.d("get routes", url.toString());
        String content = null;
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("TOKEN",token_navieraAustral);
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

    public String getManifest(String Url, String Token, String id_mongo_route) throws IOException {
        //"http://axxezo-test.brazilsouth.cloudapp.azure.com:9001/api/manifests?itinerary="
        URL url = new URL(Url + "/manifests?itinerary=" + id_mongo_route);
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

    public String getPorts(String Url, String  id_mongo_route) throws IOException {
        URL url = new URL(Url+"/itineraries/"+id_mongo_route+"/seaports");
        Log.d("get ports", url.toString());
        String content = "";
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("TOKEN", token_navieraAustral);
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(2000);
            conn.connect();

            int connStatus = conn.getResponseCode();
            InputStream getData = conn.getInputStream();
            if (connStatus != 200 && connStatus != 201) {
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


    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }

    /**
     * reset endpoint states, in old version was neccesary because we didn`t have a id_itinerary
     *
     * @return int that contains http status of this operation (when status 200 is OK)
     */
    public String GETReset() {
        String url = AxxezoAPI + "/states/removeAll";
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

    private class resetEndPoint extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            return status = GETReset();
        }

        @Override
        protected void onPostExecute(String s) {
            status = s;
        }

    }

    public String getCurrentDateTime(String format) {
        Calendar cal = Calendar.getInstance();
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat(format);
        String localTime = date.format(currentLocalTime);
        return localTime;
    }

}
