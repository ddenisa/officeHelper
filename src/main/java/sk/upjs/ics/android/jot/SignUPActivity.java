package sk.upjs.ics.android.jot;

//code from http://android-emotions.com/create-a-login-and-registration-form-in-android-using-sqlite-database/

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import sk.upjs.ics.android.jot.provider.EmployeeContentProvider;
import sk.upjs.ics.android.jot.provider.LoginContentProvider;
import sk.upjs.ics.android.jot.provider.LoginDatabaseOpenHelper;
import sk.upjs.ics.android.jot.provider.Provider;
import sk.upjs.ics.android.util.Defaults;

import static sk.upjs.ics.android.util.Defaults.ALL_COLUMNS;
import static sk.upjs.ics.android.util.Defaults.NO_COOKIE;
import static sk.upjs.ics.android.util.Defaults.NO_GROUP_BY;
import static sk.upjs.ics.android.util.Defaults.NO_HAVING;
import static sk.upjs.ics.android.util.Defaults.NO_SORT_ORDER;

public class SignUPActivity extends Activity {
    EditText editTextUserName, editTextPassword, editTextConfirmPassword;
    Button btnCreateAccount;

    private static final int INSERT_EMPLOYEE_TOKEN = 0;

    private LoginDatabaseOpenHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);


        databaseHelper = new LoginDatabaseOpenHelper(this);

        // Get Refferences of Views
        editTextUserName = (EditText) findViewById(R.id.editTextUserName);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextConfirmPassword = (EditText) findViewById(R.id.editTextConfirmPassword);

        btnCreateAccount = (Button) findViewById(R.id.buttonCreateAccount);
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub

                String userName = editTextUserName.getText().toString();
                String[] un = {editTextUserName.getText().toString()};

                String password = editTextPassword.getText().toString();
                String confirmPassword = editTextConfirmPassword.getText().toString();

                // check if any of the fields are vaccant
                if (userName.equals("") || password.equals("") || confirmPassword.equals("")) {
                    Toast.makeText(getApplicationContext(), "Field Vaccant", Toast.LENGTH_LONG).show();
                    return;
                }
                // check if both password matches
                if (!password.equals(confirmPassword)) {
                    Toast.makeText(getApplicationContext(), "Password does not match", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    // Save the Data in Database


                    SQLiteDatabase dbs = databaseHelper.getWritableDatabase();

                    insertIntoContentProvider(userName,password);

                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);

                    startActivity(intent);

                }



            }

            private void insertIntoContentProvider(String userName, String password) {
                Uri uri = LoginContentProvider.CONTENT_URI;
                ContentValues login = new ContentValues();
                login.put(Provider.Login.USER_NAME, userName);
                login.put(Provider.Login.PASSWORD, password);

                AsyncQueryHandler insertHandler = new AsyncQueryHandler(getContentResolver()) {
                    @Override
                    protected void onInsertComplete(int token, Object cookie, Uri uri) {
                        Toast.makeText(getApplicationContext(), "Account Successfully Created ", Toast.LENGTH_LONG).show();
                    }
                };

                insertHandler.startInsert(INSERT_EMPLOYEE_TOKEN, NO_COOKIE, uri, login);
            }

        });
    }
}

