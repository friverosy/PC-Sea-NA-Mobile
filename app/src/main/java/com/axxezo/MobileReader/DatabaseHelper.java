package com.axxezo.MobileReader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
    private static final String TABLE_MANIFEST = "MANIFEST";

    //table columns
    private static final String MANIFEST_ID = "id";
    private static final String MANIFEST_PEOPLE_ID = "id_people";
    private static final String MANIFEST_ORIGIN = "origin";
    private static final String MANIFEST_DESTINATION = "destination";
    private static final String MANIFEST_ISINSIDE = "is_inside";


    //people
    private static final String PERSON_ID = "id";
    private static final String PERSON_DOCUMENT = "document";
    private static final String PERSON_NAME = "name";
    private static final String PERSON_NATIONALITY = "nationality";
    private static final String PERSON_AGE = "age";
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
    private static final String RECORD_DATETIME = "datetime";
    private static final String RECORD_PERSON_DOC = "person_document";
    private static final String RECORD_PERSON_NAME = "person_name";
    private static final String RECORD_ROUTE_ID = "route_id";
    private static final String RECORD_PORT_ID = "port_id";
    private static final String RECORD_SHIP_ID = "ship_id";
    private static final String RECORD_SAILING_HOUR = "sailing_hour";
    private static final String RECORD_IS_INPUT = "input";
    private static final String RECORD_SYNC = "sync";
    private static final String RECORD_IS_PERMITTED = "permitted";

    //hours
    private static final String HOUR_ID = "id";
    private static final String HOUR_NAME = "name";

    //Config
    private static final String CONFIG_ROUTE_ID = "route_id";
    private static final String CONFIG_PORT_ID = "port_id";
    private static final String CONFIG_SHIP_ID = "ship_id";
    private static final String CONFIG_HOUR = "hour";

    //set table colums
    private static final String[] PEOPLE_COLUMS = {PERSON_ID, PERSON_DOCUMENT, PERSON_NAME, PERSON_NATIONALITY, PERSON_AGE};
    private static final String[] RECORDS_COLUMNS = {RECORD_ID, RECORD_DATETIME, RECORD_PERSON_DOC, RECORD_PERSON_NAME, RECORD_ROUTE_ID, RECORD_PORT_ID, RECORD_SHIP_ID, RECORD_SAILING_HOUR, RECORD_IS_INPUT, RECORD_SYNC, RECORD_IS_PERMITTED};
    private static final String[] ROUTES_COLUMNS = {ROUTE_ID, ROUTE_NAME};
    private static final String[] PORTS_COLUMNS = {PORT_ID, PORT_NAME};
    private static final String[] TRANSPORTS_COLUMNS = {SHIP_ID, SHIP_NAME};
    private static final String[] HOURS_COLUMNS = {HOUR_ID, HOUR_NAME};
    private static final String[] CONFIG_COLUMNS = {CONFIG_ROUTE_ID, CONFIG_PORT_ID, CONFIG_SHIP_ID, CONFIG_HOUR};

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "NavieraAustral";

    // SQL statement to create the differents tables
    String CREATE_PEOPLE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_PEOPLE + " ( " +
            PERSON_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            PERSON_DOCUMENT + " TEXT, " +
            PERSON_NAME + " TEXT, " + PERSON_NATIONALITY + " TEXT, " +
            PERSON_AGE + " INTEGER," +
            "CONSTRAINT "+PERSON_DOCUMENT+" UNIQUE ("+PERSON_DOCUMENT+")); ";

    String CREATE_MANIFEST_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_MANIFEST + " ( " +
            MANIFEST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            MANIFEST_PEOPLE_ID + " TEXT, " +
            MANIFEST_ORIGIN + " TEXT, " +
            MANIFEST_DESTINATION + " TEXT," +
            MANIFEST_ISINSIDE + " INTEGER DEFAULT 0); ";

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
            RECORD_PERSON_DOC + " INTEGER, " +
            RECORD_PERSON_NAME + " TEXT, " +
            RECORD_ROUTE_ID + " INTEGER, " +
            RECORD_PORT_ID + " INTEGER, " +
            RECORD_SHIP_ID + " INTEGER, " +
            RECORD_SAILING_HOUR + " TEXT, " +
            RECORD_IS_INPUT + " INTEGER, " +
            RECORD_SYNC + " INTEGER, " +
            RECORD_IS_PERMITTED + " INTEGER);";

    String CREATE_HOURS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_HOURS + " ( " +
            HOUR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            HOUR_NAME + " TEXT);";

    String CREATE_CONFIG_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_CONFIG + " ( " +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            CONFIG_ROUTE_ID + " INTEGER, " +
            CONFIG_PORT_ID + " INTEGER, " +
            CONFIG_SHIP_ID + " INTEGER, " +
            CONFIG_HOUR + " TEXT);";

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
        db.execSQL(CREATE_MANIFEST_TABLE);

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
        SQLiteDatabase db = this.getWritableDatabase();
        JSONObject objectJson;
        JSONArray jsonRoutes;
        if (!json.isEmpty()) {
            objectJson = new JSONObject(json);
            jsonRoutes = objectJson.getJSONArray("list_routes");
            try {
                db.beginTransaction();
                Log.d("--add route", String.valueOf(db.isOpen()));

                db.delete(TABLE_ROUTES,null,null);

                for (int i = 0; i < jsonRoutes.length(); i++) {
                    ContentValues values = new ContentValues();
                    Routes routes = new Routes(jsonRoutes.getJSONObject(i).getInt("id_ruta"), jsonRoutes.getJSONObject(i).getString("nombre_ruta"));
                    values.put(ROUTE_ID, routes.getID());
                    values.put(ROUTE_NAME, routes.getName().trim());
                    Log.d("Routes content :", routes.toString());
                    db.insert(TABLE_ROUTES, // table
                            null, //nullColumnHack
                            values); // key/value -> keys = column names/ values = column values
                }
                db.setTransactionSuccessful();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                db.endTransaction();
            }
        } else
            Log.i("error", "Json empty!");
        db.close();
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

                db1.delete(TABLE_PORTS,null,null);

                for (int i = 0; i < jsonRoutes.length(); i++) {
                    ContentValues values = new ContentValues();
                    Ports port = new Ports(jsonRoutes.getJSONObject(i).getInt("id_ubicacion"), jsonRoutes.getJSONObject(i).getString("nombre_ubicacion"));
                    values.put(PORT_ID, port.getId());
                    values.put(PORT_NAME, port.getName().trim());
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
        db1.close();
    }

    public void insertShipsDB(String json) throws JSONException {
        SQLiteDatabase db = this.getWritableDatabase();
        JSONObject objectJson;
        JSONArray jsonRoutes;
        if (!json.isEmpty()) {
            objectJson = new JSONObject(json);
            jsonRoutes = objectJson.getJSONArray("list_transport");
            try {
                db.beginTransaction();
                Log.d("--add transports", String.valueOf(db.isOpen()));

                db.delete(TABLE_SHIPS,null,null);

                for (int i = 0; i < jsonRoutes.length(); i++) {
                    ContentValues values = new ContentValues();
                    Ships port = new Ships(jsonRoutes.getJSONObject(i).getInt("id_transporte"), jsonRoutes.getJSONObject(i).getString("nombre_transporte"));
                    values.put(SHIP_ID, port.getID());
                    values.put(SHIP_NAME, port.getName().trim());
                    Log.d("Ships content :", port.toString());
                    db.insert(TABLE_SHIPS, // table
                            null, //nullColumnHack
                            values); // key/value -> keys = column names/ values = column values
                }
                db.setTransactionSuccessful();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                db.endTransaction();
            }
        } else
            Log.i("error", "Json empty!");
        db.close();
    }

    public void insertHoursDB(String json) throws JSONException {
        SQLiteDatabase db = this.getWritableDatabase();
        JSONObject objectJson;
        JSONArray jsonRoutes;
        if (!json.isEmpty()) {
            objectJson = new JSONObject(json);
            jsonRoutes = objectJson.getJSONArray("list_hours");
            try {
                db.beginTransaction();
                Log.d("--add Hours", String.valueOf(db.isOpen()));

                db.delete(TABLE_HOURS,null,null);

                for (int i = 0; i < jsonRoutes.length(); i++) {
                    ContentValues values = new ContentValues();
                    Hours hours = new Hours(jsonRoutes.getJSONObject(i).getString("horas"));
                    values.put(HOUR_NAME, hours.getName().trim());
                    Log.d("Hours content :", hours.toString());
                    db.insert(TABLE_HOURS, // table
                            null, //nullColumnHack
                            values); // key/value -> keys = column names/ values = column values
                }
                db.setTransactionSuccessful();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                db.endTransaction();
            }
        } else
            Log.i("error", "Json empty!");
        db.close();
    }

    public int insertManifestDB(String json) throws JSONException {
        int cantPeople = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        JSONObject objectJson;
        JSONArray jsonManifest;
        if (!json.isEmpty()) {
            objectJson = new JSONObject(json);
            jsonManifest = objectJson.getJSONArray("manifiesto_pasajero");
            try {
                db.delete(TABLE_MANIFEST,null,null);
                //db.beginTransaction();
                for (int i = 0; i < jsonManifest.length(); i++) {
                    Log.d("for", String.valueOf(i));
                    cantPeople++;
                    ContentValues valuesPerson = new ContentValues();
                    ContentValues valuesManifest = new ContentValues();

                    People people = new People(jsonManifest.getJSONObject(i).getString("codigo_pasajero"), jsonManifest.getJSONObject(i).getString("nombre_pasajero"), jsonManifest.getJSONObject(i).getString("nacionalidad"), 0);
                    navieraManifest manifest = new navieraManifest(jsonManifest.getJSONObject(i).getString("codigo_pasajero"), jsonManifest.getJSONObject(i).getString("origen"), jsonManifest.getJSONObject(i).getString("destino"), 0);

                    String doc;
                    doc = people.getDocument();
                    if (people.getDocument().contains("-"))
                        doc = doc.substring(0, doc.length() - 2);

                    valuesPerson.put(PERSON_DOCUMENT, doc);
                    valuesPerson.put(PERSON_NAME, people.getName().trim());
                    valuesPerson.put(PERSON_NATIONALITY, people.getNationality());
                    valuesPerson.put(PERSON_AGE, people.getAge());

                    valuesManifest.put(MANIFEST_PEOPLE_ID, doc);
                    valuesManifest.put(MANIFEST_ORIGIN, manifest.getOrigin());
                    valuesManifest.put(MANIFEST_DESTINATION, manifest.getDestination());
                    valuesManifest.put(MANIFEST_ISINSIDE, manifest.getIsInside());

                    db.insert(TABLE_PEOPLE, // table
                            null, //nullColumnHack
                            valuesPerson); // key/value -> keys = column names/ values = column values
                    //db1.update(TABLE_MANIFEST,valuesManifest, "id_people="+people.getDocument().trim(),null);
                    db.insert(TABLE_MANIFEST, // table
                            null, //nullColumnHack
                            valuesManifest); // key/value -> keys = column names/ values = column values
                    cantPeople = i;
                }

//                db.setTransactionSuccessful();
                Log.d("total manifiesto", String.valueOf(manifest_count()));
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (SQLException sqle){
                sqle.printStackTrace();
            } finally {
               // db.endTransaction();
            }
        } else
            Log.i("error", "Json empty!");
        db.close();
        return cantPeople;

    }

    public int manifest_count(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + MANIFEST_ID + " FROM "+TABLE_MANIFEST+";", null);
        return cursor.getCount();
    }

    public ArrayList<String> getListFromDB(String table) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> list = new ArrayList<String>();
        Cursor cursor = db.rawQuery("SELECT * from " + table + ";", null);
        Log.i("Tmp", "SELECT * from " + table + ";");
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
        db.close();
        return firstElement;
    }

    public void insertID(int i, String table) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
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
                    values.put(CONFIG_HOUR, i);
                    break;
            }
            Log.d("ID route content :", i + "");
            db.insert(TABLE_CONFIG, // table
                    null, //nullColumnHack
                    values); // key/value -> keys = column names/ values = column values
        } finally {
            db.setTransactionSuccessful();
            db.endTransaction();
        }
        db.close();
    }

    public void insertSettingsValues(int route, int port, int transport, String hour) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL("DROP TABLE IF EXISTS config");
            db.execSQL(CREATE_CONFIG_TABLE);
            db.beginTransaction();
            ContentValues values = new ContentValues();
            values.put(CONFIG_ROUTE_ID, route);
            values.put(CONFIG_PORT_ID, port);
            values.put(CONFIG_SHIP_ID, transport);
            values.put(CONFIG_HOUR, hour);
            db.insert(TABLE_CONFIG, // table
                    null, //nullColumnHack
                    values); // key/value -> keys = column names/ values = column values
            Log.i("VALUE SETTINGS", route + "," + port + "," + transport + "," + hour);
        } finally {
            db.setTransactionSuccessful();
            db.endTransaction();
        }
        db.close();
    }

    //cris
    public String validatePerson(String rut) {
        SQLiteDatabase db = this.getReadableDatabase();
        String out = "";
        try {
            Cursor cursor =
                    db.query(TABLE_PEOPLE, // a. table
                            PEOPLE_COLUMS, // b. column names
                            PERSON_DOCUMENT + " = ?", // c. selections
                            new String[]{String.valueOf(rut)}, // d. selections args
                            null, // e. group by
                            null, // f. having
                            null, // g. order by
                            null); // h. limit
            if (cursor != null) {
                if (!(cursor.moveToFirst()) || cursor.getCount() == 0) {
                    //cursor is empty
                } else {
                    // 3. if we got results get the first one
                    cursor.moveToFirst();

                    Log.d("<<<", cursor.getString(1));

                    // 4. build String
                    out = cursor.getString(1) + "," +
                            cursor.getString(2) + "," +
                            cursor.getString(3) + "," +
                            cursor.getInt(4);
                }
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        db.close();
        return out;
    }

    public void add_record(Record record) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();

        //values.put(RECORD_ID, record.getId());
        values.put(RECORD_PERSON_DOC, record.getPerson_document());
        values.put(RECORD_PERSON_NAME, record.getPerson_name());
        values.put(RECORD_ROUTE_ID, record.getRoute_id());
        values.put(RECORD_PORT_ID, record.getPort_id());
        values.put(RECORD_SHIP_ID, record.getShip_id());
        values.put(RECORD_SAILING_HOUR, record.getSailing_hour());
        values.put(RECORD_IS_INPUT, record.getInput());
        values.put(RECORD_SYNC, record.getSync());
        values.put(RECORD_DATETIME, record.getDatetime());
        values.put(RECORD_IS_PERMITTED, record.getPermitted());

        // 3. insert
        try {
            db.insert(TABLE_RECORDS, null, values);
        } catch (SQLException e) {
            Log.e("DataBase Error", "Error to insert record: " + values);
            e.printStackTrace();
        }

        // 4. close
        db.close();
    }

    public List get_desynchronized_records() {

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_RECORDS, // a. table
                        RECORDS_COLUMNS, // b. column names
                        RECORD_SYNC + "=0", // c. selections
                        null, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. get all
        cursor.moveToFirst();
        List<String> records = new ArrayList<>();

        while (cursor.isAfterLast() == false) {
            records.add(
                    cursor.getInt(0) + ";" + //ID
                            cursor.getString(1) + ";" + //DATETIME
                            cursor.getString(2) + ";" + //PERSON_DOCUMENT
                            cursor.getString(3) + ";" + //PERSON_NAME
                            cursor.getInt(4) + ";" + //ROUTE_ID
                            cursor.getInt(5) + ";" + //PORT_ID
                            cursor.getInt(6) + ";" + //SHIP_ID
                            cursor.getString(7) + ";" + //SAILING_HOUR
                            cursor.getInt(8) + ";" + //INPUT
                            cursor.getInt(9) + ";" + //SYNC
                            cursor.getInt(10) //PERMITTED
            );
            cursor.moveToNext();
        }

        cursor.close();
        db.close();

        // 5. return
        return records;
    }

    public int record_desync_count() {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT " + RECORD_ID + " FROM " +
                    TABLE_RECORDS + " WHERE " + RECORD_SYNC + "=0;", null);
            db.close();
            return cursor.getCount();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void update_record(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        int i = 0;
        try {
            values.put(RECORD_SYNC, 1);

            // 3. updating row
            i = db.update(TABLE_RECORDS, //table
                    values, // column/value
                    RECORD_ID + "=" + id, // where
                    null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // 4. close
        db.close();
        if (i > 0) Log.d("Local Record updated", String.valueOf(id));
        else Log.e("Error updating record", String.valueOf(id));
    }

    public String getHourSelected() {
        SQLiteDatabase db = this.getWritableDatabase();
        String hora = "";
        String selectQuery = "SELECT " + TABLE_HOURS + "." + HOUR_NAME + " FROM " + TABLE_HOURS +
                " INNER JOIN " + TABLE_CONFIG + " ON " +
                TABLE_HOURS + "." + HOUR_NAME + " = " + TABLE_CONFIG + "." + CONFIG_HOUR + ";";
        try {
            Cursor c = db.rawQuery(selectQuery, new String[]{});
            if (c.moveToFirst()) {
                hora = c.getString(c.getColumnIndex(ROUTE_NAME));
            }
            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.close();
        return hora;
    }

    public String getShipSelected() {
        SQLiteDatabase db = this.getWritableDatabase();
        String ship = "";
        String selectQuery = "SELECT " + TABLE_SHIPS + "." + SHIP_ID + " FROM " + TABLE_SHIPS +
                " INNER JOIN " + TABLE_CONFIG + " ON " +
                TABLE_SHIPS + "." + SHIP_ID + " = " + TABLE_CONFIG + "." + CONFIG_SHIP_ID + ";";
        try {
            Cursor c = db.rawQuery(selectQuery, new String[]{});
            if (c.moveToFirst()) {
                ship = c.getString(c.getColumnIndex(SHIP_ID));
            }
            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.close();
        return ship;
    }

    public String getNameShipSelected() {
        SQLiteDatabase db = this.getWritableDatabase();
        String ship = "";
        String selectQuery = "SELECT " + TABLE_SHIPS + "." + SHIP_NAME + " FROM " + TABLE_SHIPS +
                " INNER JOIN " + TABLE_CONFIG + " ON " +
                TABLE_SHIPS + "." + SHIP_ID + " = " + TABLE_CONFIG + "." + CONFIG_SHIP_ID + ";";
        try {
            Cursor c = db.rawQuery(selectQuery, new String[]{});
            if (c.moveToFirst()) {
                ship = c.getString(c.getColumnIndex(SHIP_NAME));
            }
            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.close();
        return ship;
    }

    public String getRouteSelected() {
        SQLiteDatabase db = this.getWritableDatabase();
        String route = "";
        String selectQuery = "SELECT " + TABLE_ROUTES + "." + ROUTE_ID + " FROM " + TABLE_ROUTES +
                " INNER JOIN " + TABLE_CONFIG + " ON " +
                TABLE_ROUTES + "." + ROUTE_ID + " = " + TABLE_CONFIG + "." + CONFIG_ROUTE_ID + ";";
        try {
            Cursor c = db.rawQuery(selectQuery, new String[]{});
            if (c.moveToFirst()) {
                route = c.getString(c.getColumnIndex(ROUTE_ID));
            }
            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.close();
        return route;
    }

    public String getNameRouteSelected() {
        SQLiteDatabase db = this.getWritableDatabase();
        String route = "";
        String selectQuery = "SELECT " + TABLE_ROUTES + "." + ROUTE_NAME + " FROM " + TABLE_ROUTES +
                " INNER JOIN " + TABLE_CONFIG + " ON " +
                TABLE_ROUTES + "." + ROUTE_ID + " = " + TABLE_CONFIG + "." + CONFIG_ROUTE_ID + ";";
        try {
            Cursor c = db.rawQuery(selectQuery, new String[]{});
            if (c.moveToFirst()) {
                route = c.getString(c.getColumnIndex(ROUTE_NAME));
            }
            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.close();
        return route;
    }

    public String getPortSelected() {
        SQLiteDatabase db = this.getWritableDatabase();
        String port = "";
        String selectQuery = "SELECT " + TABLE_PORTS + "." + PORT_ID + " FROM " + TABLE_PORTS +
                " INNER JOIN " + TABLE_CONFIG + " ON " +
                TABLE_PORTS + "." + PORT_ID + " = " + TABLE_CONFIG + "." + CONFIG_PORT_ID + ";";
        try {
            Cursor c = db.rawQuery(selectQuery, new String[]{});
            if (c.moveToFirst()) {
                port = c.getString(c.getColumnIndex(PORT_ID));
            }
            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.close();
        return port;
    }

    public String getNamePortSelected() {
        SQLiteDatabase db = this.getWritableDatabase();
        String port = "";
        String selectQuery = "SELECT " + TABLE_PORTS + "." + PORT_NAME + " FROM " + TABLE_PORTS +
                " INNER JOIN " + TABLE_CONFIG + " ON " +
                TABLE_PORTS + "." + PORT_ID + " = " + TABLE_CONFIG + "." + CONFIG_PORT_ID + ";";
        try {
            Cursor c = db.rawQuery(selectQuery, new String[]{});
            if (c.moveToFirst()) {
                port = c.getString(c.getColumnIndex(PORT_NAME));
            }
            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.close();
        return port;
    }

    public void updatePeopleManifest(String rut, int input) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues valores = new ContentValues();
            valores.put("is_inside", input);
            db.update(TABLE_MANIFEST, valores, "id_people="+rut, null);
        } catch (SQLException e){
            e.printStackTrace();
        }
        db.close();
    }
    public ArrayList<String> selectFromDB(String select, String split) {
        ArrayList<String> list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(select, null);
        cursor.moveToFirst();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            int i = 0;
            String row = "";
            while (i < cursor.getColumnCount()) {
                row = row + cursor.getString(i) + split;
                i++;
            }
            list.add(row);
        }
        db.close();
        return list;

    }

}