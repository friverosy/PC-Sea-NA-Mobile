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
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class lastRecordsList extends ListActivity implements AdapterView.OnItemSelectedListener {
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
    private Spinner combo_destination;
    private cardsSpinnerAdapter spinner_adapter;


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
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        //adapter.getFilter().filter();
        combo_destination = (Spinner) findViewById(R.id.spinner_destination);
        //combo_destination.setOnItemSelectedListener(this);
        db = new DatabaseHelper(this);

        ArrayList<String> select_from_manifest = db.selectFromDB("select distinct origin from manifest", "");
        String[] manifest_is_inside = null;
        ArrayList<String> listDestination = new ArrayList();
        listDestination.addAll(select_from_manifest);
        spinner_adapter = new cardsSpinnerAdapter(this, android.R.layout.simple_spinner_dropdown_item, listDestination);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        combo_destination.setAdapter(spinner_adapter);
        combo_destination.setOnItemSelectedListener(this);


        addPersonCards();
        getStatusFromManifest(0);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_lastRecords);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = "Total Manifiesto: " + getStatusFromManifest(1) +
                        " Embarcados: " + getStatusFromManifest(3) +
                        " Desembarcados: " + getStatusFromManifest(4) +
                        " Pendientes : " + getStatusFromManifest(2);
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
            DatabaseHelper dbHelper = new DatabaseHelper(this.getApplicationContext());
            newDB = dbHelper.getReadableDatabase();
            Cursor c = newDB.rawQuery("select m.id_people,p.name,p.nationality,m.origin,m.destination,m.is_inside from manifest as m left join people as p on m.id_people=p.document", null);
            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        String DNI = c.getString(c.getColumnIndex("id_people"));
                        String Name = c.getString(c.getColumnIndex("name"));
                        // String nationality = c.getString(c.getColumnIndex("nationality"));
                        int isInput = c.getInt(c.getColumnIndex("is_inside"));
                        String origin = c.getString(c.getColumnIndex("origin"));
                        String destination = c.getString(c.getColumnIndex("destination"));
                        users.add(new Cards(DNI, Name, isInput, origin, destination));
                    } while (c.moveToNext());
                }
            }
            c.close();
        } catch (SQLiteException se) {
            Log.e(getClass().getSimpleName(), "Could not create or Open the database");
        }
    }

    public int getStatusFromManifest(int position) {
        ArrayList<String> select_counts = db.selectFromDB("select (select count(*) from manifest)," +
                "(select count(*) from manifest where is_inside=0),(select count(*) from manifest where is_inside=1)," +
                "(select count(*) from manifest where is_inside=2)", "|");
        int count = 0;
        if (select_counts.size() > 0) {
            String[] binnacle_param_id = select_counts.get(0).split("\\|");
            manifestCount = Integer.parseInt(binnacle_param_id[0]);
            PendingCount = Integer.parseInt(binnacle_param_id[1]);
            EmbarkedCount = Integer.parseInt(binnacle_param_id[2]);
            LandedCount = Integer.parseInt(binnacle_param_id[3]);
        }
        switch (position) {
            case 1:
                count = manifestCount;
                break;
            case 2:
                count = PendingCount;
                break;
            case 3:
                count = EmbarkedCount;
                break;
            case 4:
                count = LandedCount;
                break;
        }

        return count;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Cards cards = new Cards();
        //Here we use the Filtering Feature which we implemented in our Adapter class.
        adapter.getFilter().filter((CharSequence) parent.getItemAtPosition(position), new Filter.FilterListener() {
            @Override
            public void onFilterComplete(int count) {
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);
            }
        });

    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
