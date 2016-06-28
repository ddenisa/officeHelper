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

public class EmployeeDatabaseOpenHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "employees";
    public static final int DATABASE_VERSION = 1;

    public EmployeeDatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, DEFAULT_CURSOR_FACTORY, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createTableSql());
        insertSampleEntry(db, "Ryan", "Howard", "Vice President, North East", "Management", 0, "Scranton", "570-999-8888", "570-999-8887", "ryan@dundermifflin.com", "howard.jpg");
        insertSampleEntry(db, "Michael", "Scott", "Regional Manager", "Management", 1, "Scranton", "570-888-9999", "570-222-3333", "michael@dundermifflin.com", "scott.jpg");
        insertSampleEntry(db, "Dwight", "Schrute", "Assistant Regional Manager", "Management", 2, "Scranton", "570-444-4444", "570-333-3333", "dwight@dundermifflin.com", "schrute.jpg");
        insertSampleEntry(db, "Jim", "Halpert", "Assistant Regional Manager", "Manage", 2, "Scranton", "570-222-2121", "570-999-1212", "jim@dundermifflin.com", "halpert.jpg");
        insertSampleEntry(db, "Pamela", "Beesly", "Receptionist", "", 2, "Scranton", "570-999-5555", "570-999-7474", "pam@dundermifflin.com", "beesly.jpg");
        insertSampleEntry(db, "Angela", "Martin", "Senior Accountant", "Accounting", 2, "Scranton", "570-555-9696", "570-999-3232", "angela@dundermifflin.com", "martin.jpg");
        insertSampleEntry(db, "Kevin","Malone","Accountant","Accounting",6,"Scranton","570-777-9696","570-111-2525","kmalone@dundermifflin.com","malone.jpg");
        insertSampleEntry(db, "Oscar","Martinez","Accountant","Accounting",6,"Scranton","570-321-9999","570-585-3333","oscar@dundermifflin.com","martinez.jpg");
        insertSampleEntry(db, "Creed","Bratton","Quality Assurance","Customer Services",2,"Scranton","570-222-6666","333-8585","creed@dundermifflin.com","bratton.jpg");
        insertSampleEntry(db, "Andy","Bernard","Sales Director","Sales",2,"Scranton","570-555-0000","570-546-9999","andy@dundermifflin.com","bernard.jpg");
        insertSampleEntry(db, "Phyllis","Lapin","Sales Representative","Sales",10,"Scranton","570-141-3333","570-888-6666","phyllis@dundermifflin.com","lapin.jpg");
        insertSampleEntry(db, "Stanley","Hudson","Sales Representative","Sales",10,"Scranton","570-700-6666","570-777-6666","shudson@dundermifflin.com","hudson.jpg");
    }

    private void insertSampleEntry(SQLiteDatabase db, String first, String last, String title, String dep, int mid, String city, String office, String cell, String mail, String pic) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Provider.Employee.FIRST_NAME, first);
        contentValues.put(Provider.Employee.LAST_NAME, last);
        contentValues.put(Provider.Employee.TITLE, title);
        contentValues.put(Provider.Employee.DPT, dep);
        contentValues.put(Provider.Employee.MANAGER_ID, mid);
        contentValues.put(Provider.Employee.CITY, city);
        contentValues.put(Provider.Employee.O_PHONE, office);
        contentValues.put(Provider.Employee.C_PHONE, cell);
        contentValues.put(Provider.Employee.EMAIL, mail);
        contentValues.put(Provider.Employee.PIC, pic);
        db.insert(Provider.Employee.TABLE_NAME, Defaults.NO_NULL_COLUMN_HACK, contentValues);
    }

    private String createTableSql() {
        String sqlTemplate = "CREATE TABLE %s ("
                + "%s INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "%s TEXT,"
                + "%s TEXT,"
                + "%s TEXT,"
                + "%s TEXT,"
                + "%s INTEGER,"
                + "%s TEXT,"
                + "%s TEXT,"
                + "%s TEXT,"
                + "%s TEXT,"
                + "%s TEXT"
                + ")";
        return String.format(sqlTemplate, TABLE_NAME, _ID, FIRST_NAME, LAST_NAME, TITLE, DPT, MANAGER_ID, CITY, O_PHONE, C_PHONE, EMAIL, PIC);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // do nothing
    }
}
