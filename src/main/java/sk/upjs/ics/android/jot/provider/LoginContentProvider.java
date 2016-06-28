package sk.upjs.ics.android.jot.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import sk.upjs.ics.android.util.Defaults;

import static android.content.ContentResolver.SCHEME_CONTENT;
import static sk.upjs.ics.android.util.Defaults.ALL_COLUMNS;
import static sk.upjs.ics.android.util.Defaults.AUTOGENERATED_ID;
import static sk.upjs.ics.android.util.Defaults.NO_CONTENT_OBSERVER;
import static sk.upjs.ics.android.util.Defaults.NO_GROUP_BY;
import static sk.upjs.ics.android.util.Defaults.NO_HAVING;
import static sk.upjs.ics.android.util.Defaults.NO_NULL_COLUMN_HACK;
import static sk.upjs.ics.android.util.Defaults.NO_SELECTION_ARGS;
import static sk.upjs.ics.android.util.Defaults.NO_SORT_ORDER;

/**
 * Created by Deniska on 18.6.2016.
 */
public class LoginContentProvider extends ContentProvider {

    public static final String AUTHORITY = "sk.upjs.ics.android.jot.provider.LoginContentProvider";

    public static final Uri CONTENT_URI = new Uri.Builder()
            .scheme(SCHEME_CONTENT)
            .authority(AUTHORITY)
            .appendPath(Provider.Login.TABLE_NAME)
            .build();



    private static final int URI_MATCH_LOGIN_BY_ID = 1;

//    private static final String MIME_TYPE_EMPLOYEES = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd." + AUTHORITY + "." + Provider.Employee.TABLE_NAME;
//    private static final String MIME_TYPE_SINGLE_EMPLOYEE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + AUTHORITY + "." + Provider.Employee.TABLE_NAME;

    private UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);


    private LoginDatabaseOpenHelper databaseHelper;

    @Override
    public boolean onCreate() {
        databaseHelper = new LoginDatabaseOpenHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        long id = ContentUris.parseId(uri);
        Cursor cursor = findById(id);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    private Cursor findById(long id) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String selection = Provider.Login._ID + "=" + id;
        return db.query(Provider.Login.TABLE_NAME, ALL_COLUMNS, selection, NO_SELECTION_ARGS, NO_GROUP_BY, NO_HAVING, NO_SORT_ORDER);
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri newItemUri = saveLogin(values);
        getContext().getContentResolver().notifyChange(CONTENT_URI, NO_CONTENT_OBSERVER);
        return newItemUri;
    }

    private Uri saveLogin(ContentValues values) {
        ContentValues login = new ContentValues();
        login.put(Provider.Login._ID, AUTOGENERATED_ID);
        login.put(Provider.Login.USER_NAME, values.getAsString(Provider.Login.USER_NAME));
        login.put(Provider.Login.PASSWORD, values.getAsString(Provider.Login.PASSWORD));

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        long newId = db.insert(Provider.Login.TABLE_NAME, NO_NULL_COLUMN_HACK, login);
        return ContentUris.withAppendedId(CONTENT_URI, newId);
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        long id = ContentUris.parseId(uri);
        int affectedRows = databaseHelper.getWritableDatabase()
                .delete(Provider.Login.TABLE_NAME, Provider.Login._ID + " = " + id, Defaults.NO_SELECTION_ARGS);
        getContext().getContentResolver().notifyChange(CONTENT_URI, NO_CONTENT_OBSERVER);
        return affectedRows;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        long id = ContentUris.parseId(uri);
        int affectedRows = databaseHelper.getWritableDatabase()
                .update(Provider.Login.TABLE_NAME,contentValues,Provider.Login._ID + " = " + id, Defaults.NO_SELECTION_ARGS);
        getContext().getContentResolver().notifyChange(CONTENT_URI, NO_CONTENT_OBSERVER);
        return affectedRows;
    }
}