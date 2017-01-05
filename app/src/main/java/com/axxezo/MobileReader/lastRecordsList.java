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
    private DatabaseHelper db;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ArrayList<Cards> users;
    private customViewPeople adapter;
    private int manifestCount;
    private int PendingCount;
    private int EmbarkedCount;
    private int LandedCount;

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
        db=new DatabaseHelper(this);
        addPersonCards();
        getStatusFromManifest();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_lastRecords);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = "Total Manifiesto: " + manifestCount +
                        " Embarcados: "+EmbarkedCount +
                        " Desembarcados: "+LandedCount+
                        " Pendientes : "+PendingCount;
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
            manifestCount = -1;
            DatabaseHelper dbHelper = new DatabaseHelper(this.getApplicationContext());
            newDB = dbHelper.getReadableDatabase();
            Cursor c = newDB.rawQuery("select m.id_people,p.name,p.nationality,m.origin,m.destination,m.is_inside from manifest as m left join people as p on m.id_people=p.document", null);
            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        String DNI = c.getString(c.getColumnIndex("id_people"));
                        String Name = c.getString(c.getColumnIndex("name"));
                        String nationality = c.getString(c.getColumnIndex("nationality"));
                        int isInput = c.getInt(c.getColumnIndex("is_inside"));
                        // String origin = c.getString(c.getColumnIndex("origin"));
                        // String destination = c.getString(c.getColumnIndex("destination"));
                        users.add(new Cards(DNI, Name, isInput, nationality));
                        manifestCount++;
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
    }*/
    public void getStatusFromManifest(){
        ArrayList<String> select_counts= db.selectFromDB("select (select count(*) from manifest),(select count(*) from people)," +
                "(select count(*) from manifest where is_inside=0),(select count(*) from manifest where is_inside=1)," +
                "(select count(*) from manifest where is_inside=2)","|");
        if(select_counts.size()>0){
            String[] binnacle_param_id = select_counts.get(0).split("\\|");
            manifestCount=Integer.parseInt(binnacle_param_id[0]);
            PendingCount=Integer.parseInt(binnacle_param_id[2]);
            EmbarkedCount=Integer.parseInt(binnacle_param_id[3]);
            LandedCount=Integer.parseInt(binnacle_param_id[4]);
            /*if(binnacle_param_id[0].equals(binnacle_param_id[1])){
                Toast.makeText(this,"La tabla Manifest y la tabla People poseen el mismo tamaño",Toast.LENGTH_LONG).show();
            }else
                Toast.makeText(this,"La tabla Manifest y la tabla People NO poseen el mismo tamaño",Toast.LENGTH_LONG).show();
                */
        }

    }

}
