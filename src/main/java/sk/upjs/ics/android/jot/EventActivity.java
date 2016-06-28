package sk.upjs.ics.android.jot;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.LoaderManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AsyncQueryHandler;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.SpannableStringBuilder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import sk.upjs.ics.android.jot.provider.EmployeeContentProvider;
import sk.upjs.ics.android.jot.provider.EmployeeDatabaseOpenHelper;
import sk.upjs.ics.android.jot.provider.EventContentProvider;
import sk.upjs.ics.android.jot.provider.EventDatabaseOpenHelper;
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

/**
 * Created by Deniska on 22.6.2016.
 */
public class EventActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {

    private static final int NOTES_LOADER_ID = 0;
    private static final int INSERT_NOTE_TOKEN = 0;
    private static final int DELETE_NOTE_TOKEN = 0;

    private GridView notesGridView;
    private SimpleCursorAdapter adapter;
    private EditText newNoteEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar);

        getLoaderManager().initLoader(NOTES_LOADER_ID, Bundle.EMPTY, this);

        notesGridView = (GridView) findViewById(R.id.eventsGridView);
        notesGridView.setAdapter(initializeAdapter());
        notesGridView.setOnItemClickListener(this);
    }


    private void insertIntoContentProvider(String noteDescription, String s, String d, String p, String f) {
        Uri uri = EventContentProvider.CONTENT_URI;
        ContentValues values = new ContentValues();
        values.put(Provider.Event.DESCRIPTION, noteDescription);
        values.put(Provider.Event.START_MONTH, s);
        values.put(Provider.Event.START_DAY, d);
        values.put(Provider.Event.END_MONTH, p);
        values.put(Provider.Event.END_DAY, f);

        AsyncQueryHandler insertHandler = new AsyncQueryHandler(getContentResolver()) {
            @Override
            protected void onInsertComplete(int token, Object cookie, Uri uri) {
                Toast.makeText(EventActivity.this, "Event was saved", Toast.LENGTH_SHORT)
                        .show();
            }
        };

        insertHandler.startInsert(INSERT_NOTE_TOKEN, NO_COOKIE, uri, values);
    }

    private void createNewNote() {

        final Dialog dialog = new Dialog(EventActivity.this);
        dialog.setContentView(R.layout.new_event);
        dialog.setTitle("Create new event");

        // get the Refferences of views
        final EditText editDe=(EditText)dialog.findViewById(R.id.editDescription);
        final  EditText editSM=(EditText)dialog.findViewById(R.id.editStartMonth);
        final  EditText editSD=(EditText)dialog.findViewById(R.id.editStartDay);
        final  EditText editEM=(EditText)dialog.findViewById(R.id.editEndMonth);
        final  EditText editED=(EditText)dialog.findViewById(R.id.editEndDay);


        Button btnSignIn=(Button)dialog.findViewById(R.id.buttonCreate);

        // Set On ClickListener
        btnSignIn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                String description = editDe.getText().toString();
                String sM = editSM.getText().toString();
                String sD = editSD.getText().toString();
                String eM = editEM.getText().toString();
                String eD = editED.getText().toString();

                insertIntoContentProvider(description, sM, sD, eM, eD);
                dialog.dismiss();
                triggerNotification();
            }
        });
        dialog.show();


    }

    private ListAdapter initializeAdapter() {
        String[] from = {Provider.Event.DESCRIPTION , Provider.Event.START_MONTH, Provider.Event.START_DAY, Provider.Event.END_MONTH, Provider.Event.END_DAY};
        int[] to = {R.id.description, R.id.sMonth, R.id.sDay, R.id.eMonth, R.id.eDay};
        this.adapter = new SimpleCursorAdapter(this, R.layout.event_grid_view, NO_CURSOR, from, to, NO_FLAGS);
        return this.adapter;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = new CursorLoader(this);
        loader.setUri(EventContentProvider.CONTENT_URI);
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
        Cursor selectedNoteCursor = (Cursor) parent.getItemAtPosition(position);
        int descriptionColumnIndex = selectedNoteCursor.getColumnIndex(Provider.Event.DESCRIPTION);
        String noteDescription = selectedNoteCursor.getString(descriptionColumnIndex);

        new AlertDialog.Builder(this)
                .setMessage(noteDescription)
                .setTitle("Event")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteNote(id);
                    }
                })
                .setNegativeButton("Close", DISMISS_ACTION)
                .show();
    }

    private void deleteNote(long id) {
        Uri selectedNoteUri = ContentUris.withAppendedId(EventContentProvider.CONTENT_URI, id);
        AsyncQueryHandler deleteHandler = new AsyncQueryHandler(getContentResolver()) {
            @Override
            protected void onDeleteComplete(int token, Object cookie, int result) {
                Toast.makeText(EventActivity.this, "Event was deleted", Toast.LENGTH_SHORT)
                        .show();
            }
        };
        deleteHandler.startDelete(DELETE_NOTE_TOKEN, NO_COOKIE, selectedNoteUri,
                NO_SELECTION, NO_SELECTION_ARGS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.event_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            createNewNote();
            return true;
        }
        if (id == R.id.action_home){
            home();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void home() {
        onBackPressed();
    }

    private void triggerNotification() {


        Notification notification = new Notification.Builder(this)
                .setContentTitle("OfficeHelper")
                .setContentText("New event was added")
                .setContentIntent(getEmptyNotificationContentIntent())
                .setTicker("Office Helper")
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .getNotification();

        NotificationManager notificationManager
                = (NotificationManager) getSystemService(
                Context.NOTIFICATION_SERVICE);
        notificationManager.notify("Office Helper", 0, notification);
    }

    public PendingIntent getEmptyNotificationContentIntent() {
        int REQUEST_CODE = 0;
        int NO_FLAGS = 0;
        Intent intent = new Intent(getApplicationContext(), EventActivity.class);

        PendingIntent contentIntent = PendingIntent.getActivity(this, REQUEST_CODE, intent, NO_FLAGS);
        return contentIntent;
    }
}