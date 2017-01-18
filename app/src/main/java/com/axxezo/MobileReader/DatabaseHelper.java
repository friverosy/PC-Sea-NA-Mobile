package com.axxezo.MobileReader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

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
    private static final String PORT_ID_API = "id_api";
    private static final String PORT_NAME = "name";
    private static final String PORT_IS_IN_MANIFEST = "is_in_manifest";

    //transports
    private static final String SHIP_ID = "id";
    private static final String SHIP_NAME = "name";

    //records
    private static final String RECORD_ID = "id";
    private static final String RECORD_DATETIME = "datetime";
    private static final String RECORD_PERSON_DOC = "person_document";
    private static final String RECORD_PERSON_NAME = "person_name";
    private static final String RECORD_ORIGIN = "origin";
    private static final String RECORD_DESTINATION = "destination";
    private static final String RECORD_PORT_ID = "port_id";
    private static final String RECORD_SHIP_ID = "ship_id";
    private static final String RECORD_SAILING_HOUR = "sailing_hour";
    private static final String RECORD_IS_INPUT = "input";
    private static final String RECORD_SYNC = "sync";
    private static final String RECORD_IS_PERMITTED = "permitted";
    private static final String RECORD_COUNT_TOTAL = "count_total";
    private static final String RECORD_COUNT_EMBARKED = "count_embarked";
    private static final String RECORD_COUNT_LANDED = "count_landed";
    private static final String RECORD_COUNT_PENDING = "count_pending";
    private static final String RECORD_TICKET = "ticket";

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
    private static final String[] RECORDS_COLUMNS = {RECORD_ID, RECORD_DATETIME, RECORD_PERSON_DOC, RECORD_PERSON_NAME, RECORD_ORIGIN, RECORD_DESTINATION, RECORD_PORT_ID, RECORD_SHIP_ID, RECORD_SAILING_HOUR, RECORD_IS_INPUT, RECORD_SYNC, RECORD_IS_PERMITTED, RECORD_COUNT_TOTAL, RECORD_COUNT_EMBARKED, RECORD_COUNT_LANDED, RECORD_COUNT_PENDING, RECORD_TICKET};
    private static final String[] MANIFEST_COLUMNS = {MANIFEST_ID, MANIFEST_PEOPLE_ID, MANIFEST_ORIGIN, MANIFEST_DESTINATION, MANIFEST_ISINSIDE};
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
            PERSON_DOCUMENT + " TEXT NOT NULL UNIQUE, " +
            PERSON_NAME + " TEXT, " + PERSON_NATIONALITY + " TEXT, " +
            PERSON_AGE + " INTEGER);";

    // "CONSTRAINT "+PERSON_DOCUMENT+" UNIQUE ("+PERSON_DOCUMENT+")); ";

    String CREATE_MANIFEST_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_MANIFEST + " ( " +
            MANIFEST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            MANIFEST_PEOPLE_ID + " TEXT NOT NULL UNIQUE, " +
            MANIFEST_ORIGIN + " TEXT, " +
            MANIFEST_DESTINATION + " TEXT," +
            MANIFEST_ISINSIDE + " INTEGER DEFAULT 0); ";

    String CREATE_ROUTES_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_ROUTES + " ( " +
            ROUTE_ID + " INTEGER PRIMARY KEY, " +
            ROUTE_NAME + " TEXT);";

    String CREATE_PORTS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_PORTS + " ( " +
            PORT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            PORT_ID_API + " INTEGER, " +
            PORT_NAME + " TEXT, " +
            PORT_IS_IN_MANIFEST + " TEXT DEFAULT 'FALSE');";

    String CREATE_TRANSPORTS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_SHIPS + " ( " +
            SHIP_ID + " INTEGER PRIMARY KEY, " +
            SHIP_NAME + " TEXT);";

    String CREATE_RECORDS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_RECORDS + " ( " +
            RECORD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            RECORD_DATETIME + " TEXT, " +
            RECORD_PERSON_DOC + " INTEGER, " +
            RECORD_PERSON_NAME + " TEXT, " +
            RECORD_ORIGIN + " INTEGER, " +
            RECORD_DESTINATION + " INTEGER, " +
            RECORD_PORT_ID + " INTEGER, " +
            RECORD_SHIP_ID + " INTEGER, " +
            RECORD_SAILING_HOUR + " TEXT, " +
            RECORD_IS_INPUT + " INTEGER, " +
            RECORD_SYNC + " INTEGER, " +
            RECORD_IS_PERMITTED + " INTEGER," +
            RECORD_COUNT_TOTAL + " INTEGER," +
            RECORD_COUNT_EMBARKED + " INTEGER," +
            RECORD_COUNT_LANDED + " INTEGER," +
            RECORD_COUNT_PENDING + " INTEGER," +
            RECORD_TICKET + " TEXT);";

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

    public void insertJSON(String json, String table) throws JSONException {
        SQLiteDatabase db = this.getWritableDatabase();
        JSONObject objectJson;
        JSONArray jsonArray;
        switch (table) {
            case "routes":
                if (!json.isEmpty() && json.length() > 3) {
                    objectJson = new JSONObject(json);
                    jsonArray = objectJson.getJSONArray("list_routes");
                    try {
                        db.beginTransaction();
                        Log.d("--add route", String.valueOf(db.isOpen()));
                        db.delete(TABLE_ROUTES, null, null);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            ContentValues values = new ContentValues();
                            Routes routes = new Routes(jsonArray.getJSONObject(i).getInt("id_ruta"), jsonArray.getJSONObject(i).getString("nombre_ruta"));
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
                    Log.i("json content", json.toString());
                break;
            case "ports":
                if (!json.isEmpty()) {
                    objectJson = new JSONObject(json);
                    jsonArray = objectJson.getJSONArray("list_sections_route");
                    try {
                        db.beginTransaction();
                        db.delete(TABLE_PORTS, null, null);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            ContentValues values = new ContentValues();
                            Ports port = new Ports(jsonArray.getJSONObject(i).getInt("id_ubicacion"), jsonArray.getJSONObject(i).getString("nombre_ubicacion"));
                            values.put(PORT_ID_API, port.getId());
                            values.put(PORT_NAME, port.getName().trim());
                            Log.d("Ports content :", port.toString());
                            db.insert(TABLE_PORTS, // table
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
                break;
            case "ships":
                if (!json.isEmpty()) {
                    objectJson = new JSONObject(json);
                    jsonArray = objectJson.getJSONArray("list_transport");
                    try {
                        db.beginTransaction();
                        Log.d("--add transports", String.valueOf(db.isOpen()));

                        db.delete(TABLE_SHIPS, null, null);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            ContentValues values = new ContentValues();
                            Ships port = new Ships(jsonArray.getJSONObject(i).getInt("id_transporte"), jsonArray.getJSONObject(i).getString("nombre_transporte"));
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
                break;
            case "hours":
                if (!json.isEmpty()) {
                    objectJson = new JSONObject(json);
                    jsonArray = objectJson.getJSONArray("list_hours");
                    try {
                        db.beginTransaction();
                        Log.d("--add Hours", String.valueOf(db.isOpen()));

                        db.delete(TABLE_HOURS, null, null);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            ContentValues values = new ContentValues();
                            Hours hours = new Hours(jsonArray.getJSONObject(i).getString("horas"));
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
                break;
            case "manifest":
                if (!json.isEmpty()) {
                    objectJson = new JSONObject(json);
                    jsonArray = objectJson.getJSONArray("manifiesto_pasajero");
                    try {
                        //db.delete(TABLE_MANIFEST, null, null);
                        //db.execSQL("delete from sqlite_sequence where name='MANIFEST'");
                        db.beginTransaction();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Log.d("for", String.valueOf(i));
                            ContentValues valuesPerson = new ContentValues();
                            ContentValues valuesManifest = new ContentValues();

                            People people = new People(jsonArray.getJSONObject(i).getString("codigo_pasajero"), jsonArray.getJSONObject(i).getString("nombre_pasajero"), jsonArray.getJSONObject(i).getString("nacionalidad"), 0);
                            navieraManifest manifest = new navieraManifest(jsonArray.getJSONObject(i).getString("codigo_pasajero"), jsonArray.getJSONObject(i).getString("origen"), jsonArray.getJSONObject(i).getString("destino"), 0);

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

                            db.execSQL("insert or ignore into people(" + PERSON_DOCUMENT + "," + PERSON_NAME + "," + PERSON_NATIONALITY + "," + PERSON_AGE + ") VALUES('" +
                                    doc + "','" + people.getName() + "','" + people.getNationality() + "'," + people.getAge() + ")");
                            db.execSQL("insert or ignore into manifest(" + MANIFEST_PEOPLE_ID + "," + MANIFEST_ORIGIN + "," + MANIFEST_DESTINATION + "," + MANIFEST_ISINSIDE + ") VALUES('" +
                                    doc + "','" + manifest.getOrigin() + "','" + manifest.getDestination() + "','" + manifest.getIsInside() + "')");
                        }
                        db.setTransactionSuccessful();
                        Log.d("total manifiesto", selectFirst("select count(id) from manfiest"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (SQLException sqle) {
                        sqle.printStackTrace();
                    } finally {
                        db.endTransaction();
                    }
                } else
                    Log.i("error", "Json empty!");
                break;
        }
    }
    public String selectFirst(String Query) {
        String firstElement = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(Query, null);
        cursor.moveToFirst();
        firstElement = cursor.getString(0);
        Log.i("first element", "-----" + firstElement);
        //db.close();
        return firstElement;
    }
    //cris
    public String validatePerson(String rut) {
        //return the person data if this person is in manifest table
        ArrayList<String> list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select m.id_people,p.name,m.origin,m.destination,(select name from ports where id_api=(select port_id from config))," +
                "(select name from ships where id=(select ship_id from config)) from manifest as m left join people as p on m.id_people=p.document where m.id_people='" + rut + "'", null);
        cursor.moveToFirst();
        String row = "";
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            int i = 0;
            while (i < cursor.getColumnCount()) {
                row = row + cursor.getString(i) + ",";
                i++;
            }
            list.add(row);
        }
        //db.close();
        return row;
    }

    public void add_record(Record record) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();

        //values.put(RECORD_ID, record.getId());
        values.put(RECORD_PERSON_DOC, record.getPerson_document());
        values.put(RECORD_PERSON_NAME, record.getPerson_name());
        values.put(RECORD_ORIGIN, record.getOrigin());
        values.put(RECORD_DESTINATION, record.getDestination());
        values.put(RECORD_PORT_ID, record.getPort_id());
        values.put(RECORD_SHIP_ID, record.getShip_id());
        values.put(RECORD_SAILING_HOUR, record.getSailing_hour());
        values.put(RECORD_IS_INPUT, record.getInput());
        values.put(RECORD_SYNC, record.getSync());
        values.put(RECORD_DATETIME, record.getDatetime());
        values.put(RECORD_IS_PERMITTED, record.getPermitted());
        values.put(RECORD_COUNT_TOTAL, record.getManifest_total());
        values.put(RECORD_COUNT_EMBARKED, record.getManifest_embarked());
        values.put(RECORD_COUNT_LANDED, record.getManifest_landed());
        values.put(RECORD_COUNT_PENDING, record.getManifest_pending());
        values.put(RECORD_TICKET, record.getManifest_pending());

        // 3. insert
        try {
            db.insert(TABLE_RECORDS, null, values);
        } catch (SQLException e) {
            Log.e("DataBase Error", "Error to insert record: " + values);
            e.printStackTrace();
        }

        // 4. close
        // db.close();
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
                            cursor.getString(4) + ";" + //ORIGIN
                            cursor.getString(5) + ";" + //DESTINATION
                            cursor.getString(6) + ";" + //PORT
                            cursor.getString(7) + ";" + //SHIP
                            cursor.getString(8) + ";" + //SAILING_HOUR
                            cursor.getInt(9) + ";" +    //INPUT
                            cursor.getInt(10) + ";" +   //SYNC
                            cursor.getInt(11) + ";" +   //PERMITTED
                            cursor.getInt(12) + ";" +   //MANIFEST TOTAL
                            cursor.getInt(13) + ";" +   //MANIFEST EMBARKED
                            cursor.getInt(14) + ";" +   //MANIFEST LANDED
                            cursor.getInt(15) + ";" +   //MANIFEST PENDING
                            cursor.getString(16) + ";"  //MANIFEST TICKET(ONLY IN MANUAL REGISTRATION)
            );
            cursor.moveToNext();
        }

        cursor.close();
        //db.close();
        // 5. return
        return records;
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
        //db.close();
        if (i > 0) Log.d("Local Record updated", String.valueOf(id));
        else Log.e("Error updating record", String.valueOf(id));
    }
    public void updatePeopleManifest(String rut, int input) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues valores = new ContentValues();
            valores.put("is_inside", input);
            db.update(TABLE_MANIFEST, valores, "id_people=" + rut, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //db.close();
    }

    public ArrayList<String> select(String select, String split) {
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
        //db.close();
        return list;
    }

    public boolean insert(String insert) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            db.execSQL(insert);
            db.setTransactionSuccessful();
            return true;
        } catch (android.database.SQLException s) {
            return false;
        } finally {
            db.endTransaction();
        }
    }

    public ArrayList<String> getComboboxList(String table) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> list = new ArrayList<String>();
        Cursor cursor = db.rawQuery("SELECT * from " + table + ";", null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            if (cursor.isFirst()) {
                switch(table){
                    case "routes":
                        list.add("< Elija una ruta >");
                        break;
                    case "ships":
                        list.add("< Elija una nave >");
                        break;
                    case "hours":
                        list.add("< Elija una hora >");
                        break;
                }
            }
            list.add(cursor.getString(1));
        }
        Log.i("list String", "List: " + list.toString());
        //db.close();
        return list;
    }


}