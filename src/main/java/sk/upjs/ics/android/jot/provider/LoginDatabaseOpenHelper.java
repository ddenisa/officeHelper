package sk.upjs.ics.android.jot.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import sk.upjs.ics.android.util.Defaults;

import static sk.upjs.ics.android.util.Defaults.DEFAULT_CURSOR_FACTORY;



import static android.provider.BaseColumns._ID;

/**
 * Created by Deniska on 18.6.2016.
 */
public class LoginDatabaseOpenHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "login_table";
    public static final int DATABASE_VERSION = 1;

    public LoginDatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, DEFAULT_CURSOR_FACTORY, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createTableSql());
        insertSampleEntry(db, "login", "password");
    }

    private void insertSampleEntry(SQLiteDatabase db, String login, String pass) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Provider.Login.USER_NAME, login);
        contentValues.put(Provider.Login.PASSWORD, pass);
        db.insert(Provider.Login.TABLE_NAME, Defaults.NO_NULL_COLUMN_HACK, contentValues);
    }

    private String createTableSql() {
        String sqlTemplate = "CREATE TABLE %s ("
                + "%s INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "%s TEXT,"
                + "%s TEXT"
                + ")";
        return String.format(sqlTemplate, Provider.Login.TABLE_NAME, Provider.Login._ID, Provider.Login.USER_NAME, Provider.Login.PASSWORD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // do nothing
    }

}
