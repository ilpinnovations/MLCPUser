package innovations.tcs.com.mlcpuser.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import innovations.tcs.com.mlcpuser.Beans.Info;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "mlcpEmployee";
    private static final String TABLE_INFO = "employee";

    private static final String KEY_VEHICLE_NUMBER = "id";
    private static final String KEY_EMPLOYEE_NAME = "name";
    private static final String KEY_LOCATION = "loc";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE "
                + TABLE_INFO
                + "("
                + KEY_VEHICLE_NUMBER + " TEXT,"
                + KEY_EMPLOYEE_NAME + " TEXT,"
                + KEY_LOCATION + " TEXT"
                + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INFO);
        onCreate(db);
    }

    public void dropTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS info");
        this.onCreate(db);
    }

    public void addContact(Info info) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_VEHICLE_NUMBER, info.getVehicleNumber());
        values.put(KEY_EMPLOYEE_NAME, info.getName());
        values.put(KEY_LOCATION, info.getLocation());

        db.insert(TABLE_INFO, null, values);
        db.close();
    }

    Info getContact(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_INFO,
                new String[]
                        {
                            KEY_VEHICLE_NUMBER,
                            KEY_EMPLOYEE_NAME,
                            KEY_LOCATION
                        },
                KEY_VEHICLE_NUMBER
                        + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Info info = new Info(cursor.getString(0), cursor.getString(1), cursor.getString(2));
        return info;
    }

    public List<Info> getAllContacts() {
        List<Info> contactList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_INFO;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Info info = new Info();
                info.setVehicleNumber(cursor.getString(0));
                info.setName(cursor.getString(1));
                info.setLocation(cursor.getString(2));
                contactList.add(info);
            } while (cursor.moveToNext());
        }
        return contactList;
    }

    public int updateContact(Info info) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_EMPLOYEE_NAME, info.getName());
        values.put(KEY_LOCATION, info.getLocation());

        return db.update(TABLE_INFO, values, KEY_VEHICLE_NUMBER + " = ?",
                new String[]{String.valueOf(info.getVehicleNumber())});
    }

    public void deleteContact(Info info) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_INFO, KEY_VEHICLE_NUMBER + " = ?",
                new String[]{String.valueOf(info.getVehicleNumber())});
        db.close();
    }

    public boolean truncateTable() {
        String truncateQuery = "DELETE FROM " + TABLE_INFO;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(truncateQuery, null);
        if (cursor.moveToFirst()){
            Log.i("DATABASE HANDLER", "Unsuccessful truncation!");
            cursor.close();
            db.close();
            return false;
        }else {
            Log.i("DATABASE HANDLER", "Truncate successful!");
            cursor.close();
            db.close();
            return true;
        }

    }

    public int getContactsCount() {
        String countQuery = "SELECT * FROM " + TABLE_INFO;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();

        cursor.close();
        return count;
    }
}