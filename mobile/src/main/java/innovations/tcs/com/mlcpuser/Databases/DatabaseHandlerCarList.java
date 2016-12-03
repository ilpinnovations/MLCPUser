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

public class DatabaseHandlerCarList extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "mlcpCars";
    private static final String TABLE_INFO = "cars";

    private static final String KEY_VEHICLE_NUMBER = "vehicle";

    public DatabaseHandlerCarList(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE "
                + TABLE_INFO
                + "("
                + KEY_VEHICLE_NUMBER + " TEXT"
                + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INFO);
        onCreate(db);
    }

    public void addCar(Info info) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String car = info.getVehicleNumber();
        values.put(KEY_VEHICLE_NUMBER, car);
        db.insert(TABLE_INFO, null, values);
        db.close();
    }

    public void addCarList(Info info) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        for (String car : info.getVehicleNumberList()) {
            values.put(KEY_VEHICLE_NUMBER, car);
            db.insert(TABLE_INFO, null, values);
        }
        db.close();
    }

    public List<String> getCarList() {
        List<String> carList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_INFO;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                carList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return carList;
    }

    public int getCarListCount() {
        String countQuery = "SELECT * FROM " + TABLE_INFO;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
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

}