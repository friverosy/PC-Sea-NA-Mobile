package com.axxezo.MobileReader;

import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class lastRecordsList extends ListActivity {
    private ArrayList<String> results = new ArrayList<String>();
    private SQLiteDatabase newDB;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ArrayList<People> users;
    private customViewPeople adapter;

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
                String text = "Total Manifiesto: " + "\n" +
                        "Embarcados: " + "Desembarcados: ";
                Snackbar.make(view, text, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


    }
    private void addPersonCards() {
        try {
            DatabaseHelper dbHelper = new DatabaseHelper(this.getApplicationContext());
            newDB = dbHelper.getReadableDatabase();
            Cursor c = newDB.rawQuery("select document,name,nationality,origin,destination from people", null);
            if (c != null ) {
                if  (c.moveToFirst()) {
                    do {
                        String DNI = c.getString(c.getColumnIndex("document"));
                        String Name = c.getString(c.getColumnIndex("name"));
                        String nationality = c.getString(c.getColumnIndex("nationality"));
                        String origin = c.getString(c.getColumnIndex("origin"));
                        String destination = c.getString(c.getColumnIndex("destination"));

                        users.add(new People(DNI,Name,nationality));

                    }while (c.moveToNext());
                }
            }
            c.close();
        } catch (SQLiteException se ) {
            Log.e(getClass().getSimpleName(), "Could not create or Open the database");
        }
    }
}
