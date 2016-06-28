package sk.upjs.ics.android.jot.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import sk.upjs.ics.android.util.Defaults;

import static android.provider.BaseColumns._ID;
import static sk.upjs.ics.android.jot.provider.Provider.Employee.CITY;
import static sk.upjs.ics.android.jot.provider.Provider.Employee.C_PHONE;
import static sk.upjs.ics.android.jot.provider.Provider.Employee.DPT;
import static sk.upjs.ics.android.jot.provider.Provider.Employee.EMAIL;
import static sk.upjs.ics.android.jot.provider.Provider.Employee.FIRST_NAME;
import static sk.upjs.ics.android.jot.provider.Provider.Employee.MANAGER_ID;
import static sk.upjs.ics.android.jot.provider.Provider.Employee.O_PHONE;
import static sk.upjs.ics.android.jot.provider.Provider.Employee.PIC;
import static sk.upjs.ics.android.jot.provider.Provider.Employee.TABLE_NAME;
import static sk.upjs.ics.android.jot.provider.Provider.Employee.LAST_NAME;
import static sk.upjs.ics.android.jot.provider.Provider.Employee.TITLE;
import static sk.upjs.ics.android.util.Defaults.DEFAULT_CURSOR_FACTORY;

public class EventDatabaseOpenHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "events";
    public static final int DATABASE_VERSION = 1;

    public EventDatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, DEFAULT_CURSOR_FACTORY, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createTableSql());
        insertSampleEntry(db, "Do something","March" , "21", 18, "March", "21", 19);

    }

    private void insertSampleEntry(SQLiteDatabase db, String description, String s_month, String s_day , int s_hour, String e_month, String e_day , int e_hour) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Provider.Event.DESCRIPTION, description);
        contentValues.put(Provider.Event.START_MONTH, s_month);
        contentValues.put(Provider.Event.START_DAY, s_day);
        contentValues.put(Provider.Event.START_HOUR, s_hour);
        contentValues.put(Provider.Event.END_MONTH, e_month);
        contentValues.put(Provider.Event.END_DAY, e_day);
        contentValues.put(Provider.Event.END_HOUR, e_hour);
        db.insert(Provider.Event.TABLE_NAME, Defaults.NO_NULL_COLUMN_HACK, contentValues);
    }

    private String createTableSql() {
        String sqlTemplate = "CREATE TABLE %s ("
                + "%s INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "%s TEXT,"
                + "%s TEXT,"
                + "%s TEXT,"
                + "%s TEXT,"
                + "%s TEXT,"
                + "%s TEXT,"
                + "%s TEXT"
                + ")";
        return String.format(sqlTemplate, Provider.Event.TABLE_NAME, Provider.Event._ID, Provider.Event.DESCRIPTION, Provider.Event.START_MONTH, Provider.Event.START_DAY, Provider.Event.START_HOUR, Provider.Event.END_MONTH, Provider.Event.END_DAY, Provider.Event.END_HOUR);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // do nothing
    }
}
