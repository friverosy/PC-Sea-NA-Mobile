package com.axxezo.MobileReader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.device.ScanManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

    private final static String SCAN_ACTION = "urovo.rcv.message";//扫描结束action
    private Vibrator mVibrator;
    private ScanManager mScanManager;
    private SoundPool soundpool = null;
    private String barcodeStr;
    private boolean isScaning = false;
    private EditText find_people;
    private TextView show_dni;
    private TextView show_name;
    private TextView show_origin;
    private TextView show_destination;
    private TextView show_hour;
    private ImageView image_authorized;
    private Button button_find_in_manifest;
    MediaPlayer mp3Dennied;
    MediaPlayer mp3Permitted;
    MediaPlayer mp3Error;
    private DatabaseHelper db;


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
        show_hour = (TextView) findViewById(R.id.textView_show_hour);
        image_authorized = (ImageView) findViewById(R.id.imageView_is_in_manifest);

        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mp3Dennied = MediaPlayer.create(this, R.raw.bad);
        mp3Permitted = MediaPlayer.create(this, R.raw.good);
        mp3Error = MediaPlayer.create(this, R.raw.error);
        db = new DatabaseHelper(this);
        button_find_in_manifest = (Button) findViewById(R.id.button_manual_search);
        button_find_in_manifest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //code down keyboard when press this button
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

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
            reset("");

            byte[] barcode = intent.getByteArrayExtra("barocode");
            int barocodelen = intent.getIntExtra("length", 0);
            byte barcodeType = intent.getByteExtra("barcodeType", (byte) 0);
            Log.i("codetype", String.valueOf(barcodeType));
            barcodeStr = new String(barcode, 0, barocodelen);
            String rawCode = barcodeStr;
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
                        //send the text to edittext
                        find_people.setText(barcodeStr);
                        findInManifest(barcodeStr);
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
                    find_people.setText(barcodeStr);
                    findInManifest(barcodeStr);
                }

            } else if (barcodeType == 17) { // PDF417
                barcodeStr = barcodeStr.substring(0, 9);
                barcodeStr = barcodeStr.replace(" ", "");
                if (barcodeStr.endsWith("K")) {
                    flag = 1;
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
                    find_people.setText(array[1].substring(0, array[1].indexOf("CHL")));
                } catch (ArrayIndexOutOfBoundsException e) {
                    find_people.setText(array[2].substring(0, array[2].indexOf("CHL")));
                } catch (Exception ex) {
                    ex.printStackTrace();
                    find_people.setText("");
                }
                barcodeStr = barcodeStr.replace("k", "");
                barcodeStr = barcodeStr.replace("K", "");
                find_people.setText(barcodeStr);
                findInManifest(barcodeStr);
            }

            Log.i("Cooked Barcode", barcodeStr);
        }
    };

    public void reset(String content) {
        try {
            initScan();
            IntentFilter filter = new IntentFilter();
            filter.addAction(SCAN_ACTION);
            registerReceiver(mScanReceiver, filter);
            //show_dni.setText("");
            show_name.setText(content);
            show_origin.setText(content);
            show_destination.setText(content);
            show_hour.setText(content);
            //find_people.setText("");
            image_authorized.setImageResource(0);

        } catch (Exception e) {
            e.printStackTrace();
        }
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
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        initScan();
        // UpdateDb();
        IntentFilter filter = new IntentFilter();
        filter.addAction(SCAN_ACTION);
        registerReceiver(mScanReceiver, filter);
    }

    public void findInManifest(String document) {
        ArrayList<String> select_from_manifest = db.selectFromDB("select ma.id_people,ma.origin,ma.destination, p.name,(select hour from config limit 1) from manifest as ma left join people as p on ma.id_people=p.document where ma.id_people='" + document + "'", "|");
        if (select_from_manifest.size() > 0) {
            mp3Permitted.start();
            String[] manifest_is_inside = select_from_manifest.get(0).split("\\|");
            show_dni.setText(document);
            show_name.setText(manifest_is_inside[3]);
            show_origin.setText(manifest_is_inside[1]);
            show_destination.setText(manifest_is_inside[2]);
            show_hour.setText(manifest_is_inside[4]);
            image_authorized.setImageResource(R.drawable.icon_tick_true_manifest);
        } else {
            mp3Dennied.start();
            show_dni.setText(document);
            reset("< No Encontrado >");
            image_authorized.setImageResource(R.drawable.icon_tick_false_manifest);
        }
    }


}
