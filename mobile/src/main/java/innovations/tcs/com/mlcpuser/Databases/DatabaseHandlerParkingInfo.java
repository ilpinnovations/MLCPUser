package innovations.tcs.com.mlcpuser.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandlerParkingInfo extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "mlcpParkingInformation";
    private static final String TABLE = "parkingInformation";

    private static final String KEY_YESTERDAY = "yesterday";
    private static final String KEY_TODAY = "today";

    public DatabaseHandlerParkingInfo(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE "
                + TABLE
                + "("
                + KEY_YESTERDAY + " TEXT,"
                + KEY_TODAY + " TEXT"
                + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    public void addParkingInfoDetails(String yesterday_hrs, String today_hrs) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_YESTERDAY, yesterday_hrs);
        values.put(KEY_TODAY, today_hrs);

        db.insert(TABLE, null, values);
        db.close();
    }

    public List<String> getParkingInfoDetails() {
        List<String> parkingInfoDetailsList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToLast()) {
            parkingInfoDetailsList.add(cursor.getString(0));
            parkingInfoDetailsList.add(cursor.getString(1));
        }
        return parkingInfoDetailsList;
    }

    public int getParkingInfoDetailsCount() {
        String countQuery = "SELECT * FROM " + TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        return cursor.getCount();
    }
}