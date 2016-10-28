package innovations.tcs.com.mlcpuser.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandlerMySlot extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "mlcpMySlots";
    private static final String TABLE = "mySlots";

    private static final String KEY_FLOOR_NAME = "floor";
    private static final String KEY_SLOT_NAME = "slot";
    private static final String KEY_IN_TIME = "time";
    private static final String KEY_DURATION = "duration";
    private static final String KEY_CAR_NUMBER= "car";

    public DatabaseHandlerMySlot(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE "
                + TABLE
                + "("
                + KEY_FLOOR_NAME + " TEXT,"
                + KEY_SLOT_NAME + " TEXT,"
                + KEY_IN_TIME + " TEXT,"
                + KEY_DURATION + " TEXT,"
                + KEY_CAR_NUMBER + " TEXT"
                + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    public void addMySlotDetails(String floor, String slot, String time, String duration, String car) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_FLOOR_NAME, floor);
        values.put(KEY_SLOT_NAME, slot);
        values.put(KEY_IN_TIME, time);
        values.put(KEY_DURATION, duration);
        values.put(KEY_CAR_NUMBER, car);

        db.insert(TABLE, null, values);
        db.close();
    }

    public List<String> getMySlotDetails() {
        List<String> mySlotDetailsList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToLast()) {
            mySlotDetailsList.add(cursor.getString(0));
            mySlotDetailsList.add(cursor.getString(1));
            mySlotDetailsList.add(cursor.getString(2));
            mySlotDetailsList.add(cursor.getString(3));
            mySlotDetailsList.add(cursor.getString(4));
        }
        return mySlotDetailsList;
    }

    public int getMySlotDetailsCount() {
        String countQuery = "SELECT * FROM " + TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        return cursor.getCount();
    }
}