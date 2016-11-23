package com.axxezo.MobileReader;

import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class lastRecordsList extends ListActivity {
    private SQLiteDatabase newDB;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ArrayList<Cards> users;
    private customViewPeople adapter;
    private int peopleInManifest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_records_list);
        recyclerView = (RecyclerView) findViewById(R.id.peopleList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        users = new ArrayList<>();
        adapter = new customViewPeople(users);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        addPersonCards();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_lastRecords);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = "Total Manifiesto: " + peopleInManifest + "\n" +
                        "Embarcados: " + "Desembarcados: ";
                Snackbar snack = Snackbar.make(view, text, Snackbar.LENGTH_LONG)
                        .setAction("Action", null);
                View view1 = snack.getView();
                TextView information = (TextView) view1.findViewById(android.support.design.R.id.snackbar_text);
                information.setTextColor(Color.WHITE);
                snack.show();
            }
        });


    }

    private void addPersonCards() {
        try {
            peopleInManifest = 0;
            DatabaseHelper dbHelper = new DatabaseHelper(this.getApplicationContext());
            newDB = dbHelper.getReadableDatabase();
            Cursor c = newDB.rawQuery("select m.id_people,p.name,p.nationality,m.origin,m.destination,m.is_inside from manifest m left join people p on m.id_people=p.document", null);
            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        String DNI = c.getString(c.getColumnIndex("m.id_people"));
                        String Name = c.getString(c.getColumnIndex("p.name"));
                        String nationality = c.getString(c.getColumnIndex("p.nationality"));
                        int isInput = c.getInt(c.getColumnIndex("p.is_inside"));
                        // String origin = c.getString(c.getColumnIndex("origin"));
                        // String destination = c.getString(c.getColumnIndex("destination"));

                        users.add(new Cards(DNI, Name, isInput, nationality));
                        peopleInManifest++;


                    } while (c.moveToNext());
                }
            }
            c.close();
        } catch (SQLiteException se) {
            Log.e(getClass().getSimpleName(), "Could not create or Open the database");
        }
    }
/*
    private int validateImput(String person_document) {
        int isInput = 0;
        if (!person_document.isEmpty()) {
            try {
                DatabaseHelper dbHelper = new DatabaseHelper(this.getApplicationContext());
                newDB = dbHelper.getReadableDatabase();
                Cursor c = newDB.rawQuery("select input from records where person_document='" + person_document + "'" +
                        "and datetime between '"+getCurrentDate()+" 00:00"+"' and '"+getCurrentDate()+" 23:59"+"'order by datetime desc limit 1", null);
                Cursor c = newDB.rawQuery("select input from records where person_document='" + person_document + "'" +"'order by datetime desc limit 1", null);
                if (c != null) {
                    if (c.moveToFirst()) {
                        if (c.getCount() == 0)
                            isInput = 0;
                        isInput = c.getInt(c.getColumnIndex("input"));
                    }
                    c.close();
                }
            } catch (SQLiteException se) {
                Log.e(getClass().getSimpleName(), "Could not create or Open the database");
            }
        }
        return isInput;
    }
    public String getCurrentDate() {
        Calendar cal = Calendar.getInstance();
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("yyyy-MM-dd");
        String localTime = date.format(currentLocalTime);
        return localTime;
    }*/

}
