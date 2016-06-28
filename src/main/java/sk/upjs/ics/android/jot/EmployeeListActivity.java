package sk.upjs.ics.android.jot;

// from http://coenraets.org/blog/androidtutorial/

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.AsyncQueryHandler;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import sk.upjs.ics.android.jot.provider.EmployeeContentProvider;
import sk.upjs.ics.android.jot.provider.EmployeeDatabaseOpenHelper;
import sk.upjs.ics.android.jot.provider.Provider;

import static sk.upjs.ics.android.util.Defaults.ALL_COLUMNS;
import static sk.upjs.ics.android.util.Defaults.DISMISS_ACTION;
import static sk.upjs.ics.android.util.Defaults.NO_COOKIE;
import static sk.upjs.ics.android.util.Defaults.NO_CURSOR;
import static sk.upjs.ics.android.util.Defaults.NO_FLAGS;
import static sk.upjs.ics.android.util.Defaults.NO_GROUP_BY;
import static sk.upjs.ics.android.util.Defaults.NO_HAVING;
import static sk.upjs.ics.android.util.Defaults.NO_SELECTION;
import static sk.upjs.ics.android.util.Defaults.NO_SELECTION_ARGS;
import static sk.upjs.ics.android.util.Defaults.NO_SORT_ORDER;


public class EmployeeListActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {

    private static final int EMPLOYEES_LOADER_ID = 0;
    private static final int INSERT_EMPLOYEE_TOKEN = 0;
    private static final int DELETE_EMPLOYEE_TOKEN = 0;

    private GridView employeesGridView;
    private SimpleCursorAdapter adapter;

    private EmployeeDatabaseOpenHelper databaseHelper;

    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getLoaderManager().initLoader(EMPLOYEES_LOADER_ID, Bundle.EMPTY, this);

        employeesGridView = (GridView) findViewById(R.id.employeesGridView);
        employeesGridView.setAdapter(initializeAdapter());
        employeesGridView.setOnItemClickListener(this);

        databaseHelper = new EmployeeDatabaseOpenHelper(this);
            }


    private void insertIntoContentProvider(String first, String last) {
        Uri uri = EmployeeContentProvider.CONTENT_URI;
        ContentValues values = new ContentValues();
        values.put(Provider.Employee.FIRST_NAME, first);
        values.put(Provider.Employee.LAST_NAME, last);

        AsyncQueryHandler insertHandler = new AsyncQueryHandler(getContentResolver()) {
            @Override
            protected void onInsertComplete(int token, Object cookie, Uri uri) {
                Toast.makeText(EmployeeListActivity.this, "Employee was saved", Toast.LENGTH_SHORT)
                        .show();
            }
        };

        insertHandler.startInsert(INSERT_EMPLOYEE_TOKEN, NO_COOKIE, uri, values);
    }

    private void searchEmployee() {
        final EditText firstEditText = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle("Search employee")
                .setView(firstEditText)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = firstEditText.getText().toString();
                        search(name);
                    }
                })
                .setNegativeButton("Cancel",DISMISS_ACTION)
                .show();
    }

    private void search(String name) {
        SQLiteDatabase dbs = databaseHelper.getReadableDatabase();
        String[] un = {name, name};
        String selection = Provider.Employee.FIRST_NAME + " = ? " + " OR " + Provider.Employee.LAST_NAME + " =  ?  ";
        //String selection2 = Provider.Employee.LAST_NAME + "=%?%";
        cursor = dbs.query(Provider.Employee.TABLE_NAME, ALL_COLUMNS, selection, un, NO_GROUP_BY, NO_HAVING, NO_SORT_ORDER);
        //cursor2 = dbs.query(Provider.Employee.TABLE_NAME, ALL_COLUMNS, selection2, un, NO_GROUP_BY, NO_HAVING, NO_SORT_ORDER);
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();

        }
        String[] from = {Provider.Employee.FIRST_NAME, Provider.Employee.LAST_NAME, Provider.Employee.TITLE};
        int[] to = {R.id.firstName, R.id.lastName, R.id.title};
        this.adapter = new SimpleCursorAdapter(this, R.layout.employee_list_view, cursor, from, to, NO_FLAGS);
        employeesGridView.setAdapter(adapter);

    }

    private ListAdapter initializeAdapter() {
        String[] from = {Provider.Employee.FIRST_NAME, Provider.Employee.LAST_NAME, Provider.Employee.TITLE};
        int[] to = {R.id.firstName, R.id.lastName, R.id.title};
        this.adapter = new SimpleCursorAdapter(this, R.layout.employee_list_view, NO_CURSOR, from, to, NO_FLAGS);
        return this.adapter;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = new CursorLoader(this);
        loader.setUri(EmployeeContentProvider.CONTENT_URI);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        this.adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        this.adapter.swapCursor(NO_CURSOR);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, final long id) {
        Intent intent = new Intent(this, EmployeeDetailsActivity.class);
        Cursor cursor = (Cursor) parent.getItemAtPosition(position);
        int employeeIdIndex = cursor.getColumnIndex(Provider.Employee._ID);
        int employeeId = cursor.getInt(employeeIdIndex);
        intent.putExtra("EMPLOYEE_ID", employeeId);
        startActivity(intent);

//        Cursor selectedNoteCursor = (Cursor) parent.getItemAtPosition(position);
//        int descriptionColumnIndex = selectedNoteCursor.getColumnIndex(Provider.Employee.FIRST_NAME);
//        String noteDescription = selectedNoteCursor.getString(descriptionColumnIndex);
//
//        new AlertDialog.Builder(this)
//                .setMessage(noteDescription)
//                .setTitle("Employee")
//                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        deleteNote(id);
//                    }
//                })
//                .setNegativeButton("Close", DISMISS_ACTION)
//                .show();
    }

    private void deleteNote(long id) {
        Uri selectedNoteUri = ContentUris.withAppendedId(EmployeeContentProvider.CONTENT_URI, id);
        AsyncQueryHandler deleteHandler = new AsyncQueryHandler(getContentResolver()) {
            @Override
            protected void onDeleteComplete(int token, Object cookie, int result) {
                Toast.makeText(EmployeeListActivity.this, "Employee was deleted", Toast.LENGTH_SHORT)
                        .show();
            }
        };
        deleteHandler.startDelete(DELETE_EMPLOYEE_TOKEN, NO_COOKIE, selectedNoteUri,
                NO_SELECTION, NO_SELECTION_ARGS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            searchEmployee();
            return true;
        }
        if (id == R.id.action_home) {
            home();
            return true;
        }
        if (id == R.id.action_login){
            login();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void login() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void home() {
        Intent intent = new Intent(this, EmployeeListActivity.class);
        startActivity(intent);
    }
}
