package sk.upjs.ics.android.jot;


import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import sk.upjs.ics.android.jot.provider.EmployeeDatabaseOpenHelper;
import sk.upjs.ics.android.jot.provider.Provider;

import static sk.upjs.ics.android.util.Defaults.ALL_COLUMNS;
import static sk.upjs.ics.android.util.Defaults.NO_GROUP_BY;
import static sk.upjs.ics.android.util.Defaults.NO_HAVING;
import static sk.upjs.ics.android.util.Defaults.NO_SELECTION_ARGS;
import static sk.upjs.ics.android.util.Defaults.NO_SORT_ORDER;


// http://coenraets.org/blog/androidtutorial/

public class EmployeeDetailsActivity extends ListActivity {

    protected TextView employeeNameText;
    protected TextView employeeSurnameText;
    protected TextView titleText;
    protected List<EmployeeAction> actions;
    protected EmployeeActionAdapter adapter;
    protected int employeeId;
    protected int managerId;


    private EmployeeDatabaseOpenHelper databaseHelper;

    private Cursor cursor;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.employee_details);

        employeeId = getIntent().getIntExtra("EMPLOYEE_ID", 1);

        databaseHelper = new EmployeeDatabaseOpenHelper(this);
        SQLiteDatabase dbs = databaseHelper.getReadableDatabase();

        String selection = Provider.Employee._ID + "=" + employeeId;
        Cursor cursor = dbs.query(Provider.Employee.TABLE_NAME, ALL_COLUMNS, selection, NO_SELECTION_ARGS, NO_GROUP_BY, NO_HAVING, NO_SORT_ORDER);

        if (cursor.getCount() == 1)
        {
            cursor.moveToFirst();

            employeeNameText = (TextView) findViewById(R.id.firstNameEmp);
            employeeSurnameText = (TextView) findViewById(R.id.lastNameEmp);
            String first = cursor.getString(cursor.getColumnIndex("first_name"));
            String last = cursor.getString(cursor.getColumnIndex("last_name"));
            employeeNameText.setText(first);
            employeeSurnameText.setText(last);

            titleText = (TextView) findViewById(R.id.titleEmp);
            titleText.setText(cursor.getString(cursor.getColumnIndex("title")));

            actions = new ArrayList<EmployeeAction>();

            String officePhone = cursor.getString(cursor.getColumnIndex("office"));
            if (officePhone != null) {
                actions.add(new EmployeeAction("Call office", officePhone, EmployeeAction.ACTION_CALL));
            }

            String cellPhone = cursor.getString(cursor.getColumnIndex("cell"));
            if (cellPhone != null) {
                actions.add(new EmployeeAction("Call mobile", cellPhone, EmployeeAction.ACTION_CALL));
                actions.add(new EmployeeAction("SMS", cellPhone, EmployeeAction.ACTION_SMS));
            }

            String email = cursor.getString(cursor.getColumnIndex("email"));
            if (email != null) {
                actions.add(new EmployeeAction("Email", email, EmployeeAction.ACTION_EMAIL));
            }

            managerId = cursor.getInt(cursor.getColumnIndex("manager_id"));

            if (managerId>0) {
                actions.add(new EmployeeAction("View manager", cursor.getString(cursor.getColumnIndex("first_name")) + " " + cursor.getString(cursor.getColumnIndex("last_name")), EmployeeAction.ACTION_VIEW));
            }


//            String selection2 = Provider.Employee._ID + "=" + managerId;
//            cursor = dbs.query(Provider.Employee.TABLE_NAME, ALL_COLUMNS, selection2, NO_SELECTION_ARGS, NO_GROUP_BY, NO_HAVING, NO_SORT_ORDER);
//
//
//            cursor.moveToFirst();
//
//            if (cursor.getCount() >0) {
//                actions.add(new EmployeeAction("View direct reports", "(" + cursor.getCount() + ")", EmployeeAction.ACTION_REPORTS));
//            }

            adapter = new EmployeeActionAdapter();
            setListAdapter(adapter);
        }

    }


    public void onListItemClick(ListView parent, View view, int position, long id) {

        EmployeeAction action = actions.get(position);

        Intent intent;
        switch (action.getType()) {

            case EmployeeAction.ACTION_CALL:
                Uri callUri = Uri.parse("tel:" + action.getData());
                intent = new Intent(Intent.ACTION_DIAL, callUri);
                startActivity(intent);
                break;

            case EmployeeAction.ACTION_EMAIL:
                intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{action.getData()});
                startActivity(intent);
                break;

            case EmployeeAction.ACTION_SMS:
                Uri smsUri = Uri.parse("sms:" + action.getData());
                intent = new Intent(Intent.ACTION_VIEW, smsUri);
                startActivity(intent);
                break;

//            case EmployeeAction.ACTION_REPORTS:
//                intent = new Intent(this, DirectReports.class);
//                intent.putExtra("EMPLOYEE_ID", employeeId);
//                startActivity(intent);
//                break;

            case EmployeeAction.ACTION_VIEW:
                intent = new Intent(this, EmployeeDetailsActivity.class);
                intent.putExtra("EMPLOYEE_ID", managerId);
                startActivity(intent);
                break;
        }
    }

    class EmployeeActionAdapter extends ArrayAdapter<EmployeeAction> {

        EmployeeActionAdapter() {
            super(EmployeeDetailsActivity.this, R.layout.action_list_item, actions);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            EmployeeAction action = actions.get(position);
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.action_list_item, parent, false);
            TextView label = (TextView) view.findViewById(R.id.label);
            label.setText(action.getLabel());
            TextView data = (TextView) view.findViewById(R.id.data);
            data.setText(action.getData());
            return view;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement

        if (id == R.id.action_home) {
            home();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void home() {
        onBackPressed();
    }

}