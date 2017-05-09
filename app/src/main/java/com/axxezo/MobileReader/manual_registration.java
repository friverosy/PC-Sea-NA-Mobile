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
        Cursor getOriginandDestination = db.select("select name from ports");
        ArrayList<String> listOriginDestination = new ArrayList<String>();
        if (getOriginandDestination != null)
            while (!getOriginandDestination.isAfterLast()) {
                listOriginDestination.add(getOriginandDestination.getString(0));
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
                    String origin_mongo_id=db.selectFirst("select id_mongo from ports where name='"+origin.getSelectedItem().toString().trim()+"'");
                    String destination_mongo_id=db.selectFirst("select id_mongo from ports where name='"+destination.getSelectedItem().toString().trim()+"'");
                    String port = db.selectFirst("select id_api from ports where name='" + selected_origin + "'");
                    String dniStr = dni.getText().toString().toUpperCase();

                    /*String rutValidator = barcodeStr.substring(0, 8);
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
                    }*/

                    db.insert("insert into people(document,name) values('" + dniStr + "','" + name.getText().toString().toUpperCase() + "')");
                    db.insert("insert into manifest(id_people,origin,destination,port,boletus,is_inside) values('" + dni.getText().toString().toUpperCase() + "','" + origin_mongo_id + "','" + destination_mongo_id + "','" + port + "','" + Integer.parseInt(ticket_no.getText().toString())+ "','" +1+ "')");
                    Record record = new Record();
                    record.setDatetime(getCurrentDateTime("yyy-MM-dd HH:mm:ss.S"));
                    record.setPerson_document(dni.getText().toString().toUpperCase());
                    record.setPerson_name(name.getText().toString().toUpperCase());
                    record.setOrigin(origin_mongo_id);
                    record.setDestination(destination_mongo_id);
                    record.setTicket(Integer.parseInt(ticket_no.getText().toString()));
                    record.setPermitted(0);
                    record.setMongo_id_manifest(db.selectFirst("select id_mongo from routes where id=(select route_id from config)"));
                    db.add_record(record);
                    Toast.makeText(getApplicationContext(),"Persona Registrada Correctamente",Toast.LENGTH_SHORT).show();
                    finish();
                    //new RegisterTask(record).execute();
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
}
