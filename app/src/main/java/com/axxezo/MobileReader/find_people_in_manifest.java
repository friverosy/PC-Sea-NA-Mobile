package com.axxezo.MobileReader;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class find_people_in_manifest extends AppCompatActivity {

    private Vibrator mVibrator;
    private SoundPool soundpool = null;
    private String barcodeStr;
    private boolean isScaning = false;
    private EditText find_people;
    private TextView show_dni;
    private TextView show_name;
    private TextView show_origin;
    private TextView show_destination;
    private ImageView image_authorized;
    private Button button_find_in_manifest;
    MediaPlayer mp3Dennied;
    MediaPlayer mp3Permitted;
    MediaPlayer mp3Error;
    private DatabaseHelper db;
    log_app log = new log_app();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_people_in_manifest);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        find_people = (EditText) findViewById(R.id.editText_find_people);
        show_dni = (TextView) findViewById(R.id.textView_show_DNI);
        show_name = (TextView) findViewById(R.id.textView_show_name);
        show_origin = (TextView) findViewById(R.id.textView_show_origin);
        show_destination = (TextView) findViewById(R.id.textView_show_destination);
        image_authorized = (ImageView) findViewById(R.id.imageView_is_in_manifest);

        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mp3Dennied = MediaPlayer.create(this, R.raw.bad);
        mp3Permitted = MediaPlayer.create(this, R.raw.good);
        mp3Error = MediaPlayer.create(this, R.raw.error);
        db = DatabaseHelper.getInstance(this);
        button_find_in_manifest = (Button) findViewById(R.id.button_manual_search);
        button_find_in_manifest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVibrator.vibrate(100);
                findInManifest(find_people.getText().toString().toUpperCase().trim());
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                mVibrator.vibrate(100);
                reset("");
                show_dni.setText("");
                find_people.setText("");
            }
        });


    }
    public boolean rutValidator(int rut, char dv) {
        dv = dv == 'k' ? dv = 'K' : dv;
        int m = 0, s = 1;
        for (; rut != 0; rut /= 10) {
            s = (s + rut % 10 * (9 - m++ % 6)) % 11;
        }
        return dv == (char) (s != 0 ? s + 47 : 75);
    }

    public void reset(String content) {
        try {
            //show_dni.setText("");
            show_name.setText(content);
            show_origin.setText(content);
            show_destination.setText(content);
            //find_people.setText("");
            image_authorized.setImageResource(0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void findInManifest(String document) {
        Cursor get_selected_dni = db.select("select ma.id_people,(select name from ports where id_mongo=ma.origin),(select name from ports where id_mongo=ma.destination), p.name  from manifest as ma left join people as p on ma.id_people=p.document where ma.id_people='" + document + "'");
        if (get_selected_dni!=null&&get_selected_dni.getCount() > 0) {
            mp3Permitted.start();
            show_dni.setText(document);
            show_origin.setText(get_selected_dni.getString(1));
            show_destination.setText(get_selected_dni.getString(2));
            show_name.setText(get_selected_dni.getString(3));
            image_authorized.setImageResource(R.drawable.icon_tick_true_manifest);
        } else {
            mp3Dennied.start();
            show_dni.setText(document);
            reset("< No Encontrado >");
            image_authorized.setImageResource(R.drawable.icon_tick_false_manifest);
        }
        if(get_selected_dni!=null)
            get_selected_dni.close();
    }


}
