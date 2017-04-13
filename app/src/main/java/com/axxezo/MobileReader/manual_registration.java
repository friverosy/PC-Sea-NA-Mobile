package com.axxezo.MobileReader;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class manual_registration extends AppCompatActivity {
    private EditText ticket_no;
    private EditText dni;
    private EditText name;
    private Spinner origin;
    private Spinner destination;
    private Button save_manual_registration;
    private String selected_origin;
    private String selected_destination;
    private Vibrator mVibrator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final DatabaseHelper db = DatabaseHelper.getInstance(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_registration);
        ticket_no = (EditText) findViewById(R.id.registration_input_ticket);
        dni = (EditText) findViewById(R.id.registration_input_DNI);
        name = (EditText) findViewById(R.id.registration_input_name);
        origin = (Spinner) findViewById(R.id.registration_spinner_origin);
        destination = (Spinner) findViewById(R.id.registration_spinner_destination);
        save_manual_registration = (Button) findViewById(R.id.button_manual_registration_commit);
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        //fill information in combobox
        Cursor getOriginandDestination = db.select("select distinct m.origin,p.name from manifest as m left join ports as p on m.origin=p.id_mongo union select distinct m.destination,p.name from manifest as m left join ports as p on m.destination=p.id_mongo");
        ArrayList<String> listOriginDestination = new ArrayList<String>();
        if (getOriginandDestination != null)
            while (!getOriginandDestination.isAfterLast()) {
                listOriginDestination.add(getOriginandDestination.getString(1));
                getOriginandDestination.moveToNext();
            }
        if (listOriginDestination == null || listOriginDestination.isEmpty())
            listOriginDestination.add("");


        //adapter spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, listOriginDestination);
        //set adapter to spinner
        origin.setAdapter(adapter);
        destination.setAdapter(adapter);


        //listeners of combobox

        origin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_origin = origin.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        destination.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_destination = destination.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //finally listener of button
        save_manual_registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVibrator.vibrate(100);
                if (ticket_no.getText().toString().isEmpty()) {
                    ticket_no.setError("Falta ingresar Número de Boleta");
                    ticket_no.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(ticket_no, InputMethodManager.SHOW_IMPLICIT);
                } else if (dni.getText().toString().isEmpty()) {
                    dni.setError("Falta ingresar DNI");
                    dni.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(dni, InputMethodManager.SHOW_IMPLICIT);
                } else if (name.getText().toString().isEmpty()){
                    name.setError("Falta ingresar Nombre");
                    name.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(name, InputMethodManager.SHOW_IMPLICIT);
                } else {
                    // String origin =selected_origin;
                    String port = db.selectFirst("select id_api from ports where name='" + selected_origin + "'");
                    db.insert("insert into people(document,name) values('" + dni.getText().toString().toUpperCase() + "','" + name.getText().toString().toUpperCase() + "')");
                    db.insert("insert into manifest(id_people,origin,destination,port,boletus) values('" + dni.getText().toString().toUpperCase() + "','" + origin.getSelectedItem().toString().trim() + "','" + destination.getSelectedItem().toString().trim() + "','" + port + "','" + Integer.parseInt(ticket_no.getText().toString()) + "')");
                    Record record = new Record();
                    record.setDatetime(getCurrentDateTime("yyy-MM-dd HH:mm:ss.S"));
                    record.setPerson_document(dni.getText().toString().toUpperCase());
                    record.setPerson_name(name.getText().toString().toUpperCase());
                    record.setOrigin(origin.getSelectedItem().toString().trim());
                    record.setDestination(destination.getSelectedItem().toString().trim());
                    record.setTicket(Integer.parseInt(ticket_no.getText().toString()));
                    record.setPermitted(0);
                    //db.add_record(record);
                    new RegisterTask(record).execute();
                }
            }
        });
        if (getOriginandDestination != null)
            getOriginandDestination.close();

    }

    //dd-MM-yyyy hh:MM:ss
    public String getCurrentDateTime(String format) {
        Calendar cal = Calendar.getInstance();
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("dd-MM-yyyy hh:MM:ss");
        String localTime = date.format(currentLocalTime);
        return localTime;
    }

    public class RegisterTask extends AsyncTask<Void, Void, String> {
        private Record newRecord;

        RegisterTask(Record newRecord) {
            this.newRecord = newRecord;
        }

        @Override
        protected String doInBackground(Void... params) {
            String postReturn = "";
            final OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(1, TimeUnit.SECONDS)
                    .writeTimeout(0, TimeUnit.SECONDS)
                    .readTimeout(0, TimeUnit.SECONDS)
                    .build();
            POST(newRecord, client);
            return postReturn;
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(getApplicationContext(), "Persona Ingresada Exitosamente", Toast.LENGTH_LONG).show();
        }
    }


    public String POST(Record record, OkHttpClient client) {
        String url = "http://axxezo-test.brazilsouth.cloudapp.azure.com:9001/api";
        log_app log = new log_app();
        String result = "";
        String json = "";
        JSONObject jsonObject = new JSONObject();
        DatabaseHelper db = DatabaseHelper.getInstance(this);
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");
        try {
            jsonObject.accumulate("person", record.getMongo_id_person());
            jsonObject.accumulate("seaport", record.getPort_registry());
            jsonObject.accumulate("manifest", db.selectFirst("select id_mongo from routes where id=(select route_id from config)")); //falta
            jsonObject.accumulate("state", record.getInput()+"");
            jsonObject.accumulate("date", record.getDatetime()); //falta formatear 2017-01-01 00:00:00
            jsonObject.accumulate("ticket", record.getTicket());
            jsonObject.accumulate("reason", record.getReason());

            json = jsonObject.toString();
            Log.d("json posting", json);

            RequestBody body = RequestBody.create(JSON, json);

            // create object okhttp
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Content-type", "application/json")
                    .post(body)
                    .build();

            //POST using okhttp
            Response response = client.newCall(request).execute();
            String tmp = response.body().string(); //Response{protocol=http/1.1, code=401, message=Unauthorized, url=http://axxezo-test.brazilsouth.cloudapp.azure.com:9001/api/registers}
            Log.d("-----response",tmp);
            //log.writeLog(getApplicationContext(), "Main:line 1037", "DEBUG", "response " + response.code() + " name " + record.getPerson_name());

            // 10. convert inputstream to string
            if (tmp != null) {
                if (response.isSuccessful()) {
                    Log.d("json POSTED", json);
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
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            log.writeLog(getApplicationContext(), "Main: POST method", "ERROR", e.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
            log.writeLog(getApplicationContext(), "Main: POST method", "ERROR", e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            log.writeLog(getApplicationContext(), "Main: POST method", "ERROR", e.getMessage());
        }

        // 11. return result
        return result;
    }


}
