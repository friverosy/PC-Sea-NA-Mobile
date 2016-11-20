package com.axxezo.MobileReader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by axxezo on 14/11/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    //Table names
    private static final String TABLE_PEOPLE = "PEOPLE";
    private static final String TABLE_RECORDS = "RECORDS";
    private static final String TABLE_ROUTES = "ROUTES";
    private static final String TABLE_PORTS = "PORTS";
    private static final String TABLE_SHIPS = "SHIPS";
    private static final String TABLE_HOURS = "HOURS";
    private static final String TABLE_CONFIG = "CONFIG";

    //table columns

    //people
    private static final String PERSON_ID = "id";
    private static final String PERSON_DOCUMENT = "document";
    private static final String PERSON_NAME = "name";
    private static final String PERSON_NATIONALITY = "nationality";
    private static final String PERSON_AGE = "age";
    private static final String PERSON_ORIGIN = "origin";
    private static final String PERSON_DESTINATION = "destination";

    //routes
    private static final String ROUTE_ID = "id";
    private static final String ROUTE_NAME = "name";

    //ports
    private static final String PORT_ID = "id";
    private static final String PORT_NAME = "name";

    //transports
    private static final String SHIP_ID = "id";
    private static final String SHIP_NAME = "name";

    //records
    private static final String RECORD_ID = "id";
    private static final String RECORD_DATETIME = "records_datetime";
    private static final String RECORD_PERSON_DOC = "records_peopleDoc";
    private static final String RECORD_SAILING_ID = "records_sailingID";
    private static final String RECORD_VEHICLE_ID = "records_vehicleID";
    private static final String RECORD_SHIP_ID = "records_shipID";
    private static final String RECORD_SYNC = "records_isSynchronized";

    //hours
    private static final String HOUR_ID = "id";
    private static final String HOUR_NAME = "name";

    //Config
    private static final String CONFIG_ROUTE_ID = "route_id";
    private static final String CONFIG_PORT_ID = "port_id";
    private static final String CONFIG_SHIP_ID = "ship_id";
    private static final String CONFIG_HOUR_ID = "hour_id";

    //set table colums
    private static final String[] PEOPLE_COLUMS = {PERSON_ID,PERSON_DOCUMENT, PERSON_NAME, PERSON_NATIONALITY, PERSON_AGE};
    private static final String[] RECORDS_COLUMNS = {RECORD_ID, RECORD_DATETIME, RECORD_PERSON_DOC, RECORD_SAILING_ID, RECORD_VEHICLE_ID, RECORD_SHIP_ID, RECORD_SYNC};
    private static final String[] ROUTES_COLUMNS = {ROUTE_ID, ROUTE_NAME};
    private static final String[] PORTS_COLUMNS = {PORT_ID, PORT_NAME};
    private static final String[] TRANSPORTS_COLUMNS = {SHIP_ID, SHIP_NAME};
    private static final String[] HOURS_COLUMNS = {HOUR_ID, HOUR_NAME};
    private static final String[] CONFIG_COLUMNS = {CONFIG_ROUTE_ID, CONFIG_PORT_ID, CONFIG_SHIP_ID, CONFIG_HOUR_ID};

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "NavieraAustral";

    // SQL statement to create the differents tables

    /*
 /hours
    private static final String HOURS_ID = "hours_id";
    private static final String HOURS_NAME = "hours_name";


     */
    String CREATE_PEOPLE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_PEOPLE + " ( " +
            PERSON_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            PERSON_DOCUMENT + " TEXT, " +
            PERSON_NAME + " TEXT, " + PERSON_NATIONALITY + " TEXT, " +
            PERSON_AGE + " INTEGER, "+
            PERSON_ORIGIN+" TEXT, "+
            PERSON_DESTINATION+" TEXT);";


    String CREATE_ROUTES_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_ROUTES + " ( " +
            ROUTE_ID + " INTEGER PRIMARY KEY, " +
            ROUTE_NAME + " TEXT);";

    String CREATE_PORTS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_PORTS + " ( " +
            PORT_ID + " INTEGER PRIMARY KEY, " +
            PORT_NAME + " TEXT);";

    String CREATE_TRANSPORTS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_SHIPS + " ( " +
            SHIP_ID + " INTEGER PRIMARY KEY, " +
            SHIP_NAME + " TEXT);";

    String CREATE_RECORDS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_RECORDS + " ( " +
            RECORD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            RECORD_DATETIME + " TEXT, " +
            RECORD_SAILING_ID + " INTEGER, " +
            RECORD_VEHICLE_ID + " INTEGER, " +
            RECORD_SHIP_ID + " INTEGER, " +
            RECORD_SYNC + " INTEGER);";

    String CREATE_HOURS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_HOURS + " ( " +
            HOUR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            HOUR_NAME + " TEXT);";

    String CREATE_CONFIG_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_CONFIG + " ( " +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            CONFIG_ROUTE_ID + " INTEGER, " +
            CONFIG_PORT_ID + " INTEGER, " +
            CONFIG_SHIP_ID + " INTEGER, " +
            CONFIG_HOUR_ID + " INTEGER);";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //first create the tables
        db.execSQL(CREATE_PEOPLE_TABLE);
        db.execSQL(CREATE_ROUTES_TABLE);
        db.execSQL(CREATE_PORTS_TABLE);
        db.execSQL(CREATE_TRANSPORTS_TABLE);
        db.execSQL(CREATE_RECORDS_TABLE);
        db.execSQL(CREATE_HOURS_TABLE);
        db.execSQL(CREATE_CONFIG_TABLE);

        //db.execSQL("PRAGMA foreign_keys=ON;");
        // first i must look the structure form of the get in api, and after create the table settings

      /*  String CREATE_SETTING_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_SETTING + " (" +
                "id INTEGER PRIMARY KEY, url TEXT, port INTEGET)";

        db.execSQL(CREATE_SETTING_TABLE);*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if it existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PEOPLE);
        //create fresh tables
        this.onCreate(db);
    }

    /**
     * CRUD operations (create "add", read "get", update, delete)
     */
    public void insertRoutesDB(String json) throws JSONException {
        SQLiteDatabase db1 = this.getWritableDatabase();
        JSONObject objectJson;
        JSONArray jsonRoutes;
        if (!json.isEmpty()) {
            objectJson = new JSONObject(json);
            jsonRoutes = objectJson.getJSONArray("list_routes");
            try {
                db1.beginTransaction();
                Log.d("--add route", String.valueOf(db1.isOpen()));
                db1.execSQL("DROP TABLE IF EXISTS routes");
                db1.execSQL(CREATE_ROUTES_TABLE);

                for (int i = 0; i < jsonRoutes.length(); i++) {
                    ContentValues values = new ContentValues();
                    Routes routes = new Routes(jsonRoutes.getJSONObject(i).getInt("id_ruta"), jsonRoutes.getJSONObject(i).getString("nombre_ruta"));
                    values.put(ROUTE_ID, routes.getID());
                    values.put(ROUTE_NAME, routes.getName());
                    Log.d("Routes content :", routes.toString());
                    db1.insert(TABLE_ROUTES, // table
                            null, //nullColumnHack
                            values); // key/value -> keys = column names/ values = column values
                }
                db1.setTransactionSuccessful();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                db1.endTransaction();
            }
        } else
            Log.i("error", "Json empty!");

    }

    public void insertPortsDB(String json) throws JSONException {
        SQLiteDatabase db1 = this.getWritableDatabase();
        JSONObject objectJson;
        JSONArray jsonRoutes;
        if (!json.isEmpty()) {
            objectJson = new JSONObject(json);
            jsonRoutes = objectJson.getJSONArray("list_sections_route");
            try {
                db1.beginTransaction();
                Log.d("--add ports", String.valueOf(db1.isOpen()));
                db1.execSQL("DROP TABLE IF EXISTS ports");
                db1.execSQL(CREATE_PORTS_TABLE);

                for (int i = 0; i < jsonRoutes.length(); i++) {
                    ContentValues values = new ContentValues();
                    Ports port = new Ports(jsonRoutes.getJSONObject(i).getInt("id_ubicacion"), jsonRoutes.getJSONObject(i).getString("nombre_ubicacion"));
                    values.put(PORT_ID, port.getId());
                    values.put(PORT_NAME, port.getName());
                    Log.d("Ports content :", port.toString());
                    db1.insert(TABLE_PORTS, // table
                            null, //nullColumnHack
                            values); // key/value -> keys = column names/ values = column values
                }
                db1.setTransactionSuccessful();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                db1.endTransaction();
            }
        } else
            Log.i("error", "Json empty!");

    }

    public void insertShipsDB(String json) throws JSONException {
        SQLiteDatabase db1 = this.getWritableDatabase();
        JSONObject objectJson;
        JSONArray jsonRoutes;
        if (!json.isEmpty()) {
            objectJson = new JSONObject(json);
            jsonRoutes = objectJson.getJSONArray("list_transport");
            try {
                db1.beginTransaction();
                Log.d("--add transports", String.valueOf(db1.isOpen()));
                db1.execSQL("DROP TABLE IF EXISTS ships");
                db1.execSQL(CREATE_TRANSPORTS_TABLE);

                for (int i = 0; i < jsonRoutes.length(); i++) {
                    ContentValues values = new ContentValues();
                    Ships port = new Ships(jsonRoutes.getJSONObject(i).getInt("id_transporte"), jsonRoutes.getJSONObject(i).getString("nombre_transporte"));
                    values.put(SHIP_ID, port.getID());
                    values.put(SHIP_NAME, port.getName());
                    Log.d("Ships content :", port.toString());
                    db1.insert(TABLE_SHIPS, // table
                            null, //nullColumnHack
                            values); // key/value -> keys = column names/ values = column values
                }
                db1.setTransactionSuccessful();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                db1.endTransaction();
            }
        } else
            Log.i("error", "Json empty!");

    }

    public void insertHoursDB(String json) throws JSONException {
        SQLiteDatabase db1 = this.getWritableDatabase();
        JSONObject objectJson;
        JSONArray jsonRoutes;
        if (!json.isEmpty()) {
            objectJson = new JSONObject(json);
            jsonRoutes = objectJson.getJSONArray("list_hours");
            try {
                db1.beginTransaction();
                Log.d("--add Hours", String.valueOf(db1.isOpen()));
                db1.execSQL("DROP TABLE IF EXISTS hours");
                db1.execSQL(CREATE_HOURS_TABLE);

                for (int i = 0; i < jsonRoutes.length(); i++) {
                    ContentValues values = new ContentValues();
                    Hours hours = new Hours(jsonRoutes.getJSONObject(i).getString("horas"));
                    values.put(HOUR_NAME, hours.getName());
                    Log.d("Hours content :", hours.toString());
                    db1.insert(TABLE_HOURS, // table
                            null, //nullColumnHack
                            values); // key/value -> keys = column names/ values = column values
                }
                db1.setTransactionSuccessful();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                db1.endTransaction();
            }
        } else
            Log.i("error", "Json empty!");

    }
    public int insertManifestDB(String json) throws JSONException {
        int cantPeople=0;
        SQLiteDatabase db1 = this.getWritableDatabase();
        JSONObject objectJson;
        JSONArray jsonRoutes;
        if (!json.isEmpty()) {
            objectJson = new JSONObject(json);
            jsonRoutes = objectJson.getJSONArray("manifiesto_pasajero");
            try {
                db1.beginTransaction();
                Log.d("--add Manifest", String.valueOf(db1.isOpen()));
                db1.execSQL("DROP TABLE IF EXISTS people");
                db1.execSQL(CREATE_PEOPLE_TABLE);
                for (int i = 0; i < jsonRoutes.length(); i++) {
                    Log.d("People content :", "= "+jsonRoutes.length());
                    cantPeople++;
                    ContentValues values = new ContentValues();
                    People people = new People(jsonRoutes.getJSONObject(i).getString("codigo_pasajero"),jsonRoutes.getJSONObject(i).getString("nombre_pasajero"),jsonRoutes.getJSONObject(i).getString("nacionalidad"),0
                            ,jsonRoutes.getJSONObject(i).getString("origen"),jsonRoutes.getJSONObject(i).getString("destino"));
                    values.put(PERSON_DOCUMENT, people.getDocument());
                    values.put(PERSON_NAME, people.getName());
                    values.put(PERSON_NATIONALITY, people.getNationality());
                    values.put(PERSON_AGE, people.getAge());
                    values.put(PERSON_ORIGIN, people.getOrigin());
                    values.put(PERSON_DESTINATION, people.getDestination());
                    Log.d("People content :", people.toString());
                    db1.insert(TABLE_PEOPLE, // table
                            null, //nullColumnHack
                            values); // key/value -> keys = column names/ values = column values
                    cantPeople=i;
                }

                db1.setTransactionSuccessful();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                db1.endTransaction();
            }
        } else
            Log.i("error", "Json empty!");
        return cantPeople;

    }

    public ArrayList<String> getListFromDB(String table) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> list = new ArrayList<String>();
        Cursor cursor = db.rawQuery("SELECT * from " + table, null);
        Log.i("Tmp", "SELECT * from " + table);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            if (cursor.isFirst()) {
                list.add("");
            }
            list.add(cursor.getString(1));
        }
        Log.i("list String", "List: " + list.toString());
        db.close();
        return list;
    }

    public String selectFirstFromDB(String Query) {
        String firstElement = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(Query, null);
        cursor.moveToFirst();
        firstElement = cursor.getString(0);
        Log.i("first element", "-----" + firstElement);
        return firstElement;
    }

    public void insertID(int i, String table) {
        SQLiteDatabase db1 = this.getWritableDatabase();
        try {
            db1.beginTransaction();
            ContentValues values = new ContentValues();
            switch (table) {
                case "routes":
                    values.put(CONFIG_ROUTE_ID, i);
                    break;
                case "ports":
                    values.put(CONFIG_PORT_ID, i);
                    break;
                case "ships":
                    values.put(CONFIG_SHIP_ID, i);
                    break;
                case "hours":
                    values.put(CONFIG_HOUR_ID, i);
                    break;
            }
            Log.d("ID route content :", i + "");
            db1.insert(TABLE_CONFIG, // table
                    null, //nullColumnHack
                    values); // key/value -> keys = column names/ values = column values
        } finally {
            db1.setTransactionSuccessful();
            db1.endTransaction();

        }
    }

    public void insertSettingsValues(int route, int port, int transport, String hour) {
        SQLiteDatabase db1 = this.getWritableDatabase();
        try {
            db1.execSQL("DROP TABLE IF EXISTS config");
            db1.execSQL(CREATE_CONFIG_TABLE);
            db1.beginTransaction();
            ContentValues values = new ContentValues();
            values.put(CONFIG_ROUTE_ID, route);
            values.put(CONFIG_PORT_ID, port);
            values.put(CONFIG_SHIP_ID, transport);
            values.put(CONFIG_HOUR_ID,hour);
            db1.insert(TABLE_CONFIG, // table
                    null, //nullColumnHack
                    values); // key/value -> keys = column names/ values = column values
            Log.i("VALUE SETTINGS",route+","+port+","+transport+","+hour);
        } finally {
            db1.setTransactionSuccessful();
            db1.endTransaction();

        }
    }
}