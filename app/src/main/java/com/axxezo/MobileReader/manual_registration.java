package com.axxezo.MobileReader;

import android.content.Context;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
        final DatabaseHelper db = new DatabaseHelper(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_registration);
        ticket_no = (EditText) findViewById(R.id.registration_input_ticket);
        dni = (EditText) findViewById(R.id.registration_input_DNI);
        name = (EditText) findViewById(R.id.registration_input_name);
        origin = (Spinner) findViewById(R.id.registration_spinner_origin);
        destination = (Spinner) findViewById(R.id.registration_spinner_destination);
        save_manual_registration = (Button) findViewById(R.id.button_manual_registration_commit);
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        //adapter spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, db.select("select distinct origin from manifest union select distinct destination from manifest", ""));
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
                if (ticket_no.getText() == null) {
                    ticket_no.setError("falta ingresar folio Boleta");
                }
                if (dni.getText() == null) {
                    dni.setError("falta ingresar DNI");
                }
                if (name.getText() == null) {
                    name.setError("falta ingresar Nombre");
                }
                if (ticket_no.getText() != null && dni.getText() != null && name.getText() != null) {
                    db.insert("insert into people(document,name) values('" + dni.getText().toString().toUpperCase() + "','" + name.getText().toString().toUpperCase() + "')");
                    db.insert("insert into manifest(id_people,origin,destination) values('" + dni.getText().toString().toUpperCase() + "','" + origin.getSelectedItem().toString().trim() + "','" + destination.getSelectedItem().toString().trim() + "')");
                   // db.insert("insert into records(datetime,person_document,origin,destination,ticket,sync) values('" + getCurrentDateTime() + "','" + dni.getText().toString().toUpperCase() + "','" + origin.getSelectedItem().toString().trim() + "','" + destination.getSelectedItem().toString().trim() + "','" + ticket_no.getText().toString().toUpperCase() +"','"+0+"')");
                    Toast.makeText(getApplicationContext(),"Persona Ingresada Exitosamente",Toast.LENGTH_LONG).show();
                    Record record=new Record();
                    record.setDatetime(getCurrentDateTime("yyy-MM-dd HH:mm:ss.S"));
                    record.setPerson_document(dni.getText().toString().toUpperCase());
                    record.setOrigin(origin.getSelectedItem().toString().trim());
                    record.setDestination(destination.getSelectedItem().toString().trim());
                    record.setTicket(Integer.parseInt(ticket_no.getText().toString()));
                    record.setPermitted(0);
                    db.add_record(record);
                    db.close();
                }
            }
        });


    }
    //dd-MM-yyyy hh:MM:ss
    public String getCurrentDateTime(String format) {
        Calendar cal = Calendar.getInstance();
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("dd-MM-yyyy hh:MM:ss");
        String localTime = date.format(currentLocalTime);
        return localTime;
    }


}
