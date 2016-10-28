package innovations.tcs.com.mlcpuser.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandlerParkingStats extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "mlcpParkingStatistics";
    private static final String TABLE = "parkingStatistics";

    private static final String KEY_AVAILABLE = "available";
    private static final String KEY_TOTAL = "total";

    public DatabaseHandlerParkingStats(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE "
                + TABLE
                + "("
                + KEY_AVAILABLE + " TEXT,"
                + KEY_TOTAL + " TEXT"
                + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    public void addParkingStatsDetails(String available_slots, String total_slots) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_AVAILABLE, available_slots);
        values.put(KEY_TOTAL, total_slots);

        db.insert(TABLE, null, values);
        db.close();
    }

    public List<String> getParkingStatsDetails() {
        List<String> parkingStatsDetailsList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToLast()) {
            parkingStatsDetailsList.add(cursor.getString(0));
            parkingStatsDetailsList.add(cursor.getString(1));
        }
        return parkingStatsDetailsList;
    }

    public int getParkingStatsDetailsCount() {
        String countQuery = "SELECT * FROM " + TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        return cursor.getCount();
    }
}