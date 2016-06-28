package sk.upjs.ics.android.jot;

//code ffrom http://android-emotions.com/create-a-login-and-registration-form-in-android-using-sqlite-database/


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import sk.upjs.ics.android.jot.provider.LoginDatabaseOpenHelper;
import sk.upjs.ics.android.jot.provider.Provider;

import static sk.upjs.ics.android.util.Defaults.ALL_COLUMNS;
import static sk.upjs.ics.android.util.Defaults.NO_GROUP_BY;
import static sk.upjs.ics.android.util.Defaults.NO_HAVING;
import static sk.upjs.ics.android.util.Defaults.NO_SELECTION_ARGS;
import static sk.upjs.ics.android.util.Defaults.NO_SORT_ORDER;

public class LoginActivity extends Activity {

    Button btnSignIn,btnSignUp;

    private LoginDatabaseOpenHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        databaseHelper = new LoginDatabaseOpenHelper(this);

        // Get The Refference Of Buttons
        btnSignIn=(Button)findViewById(R.id.buttonSignIN);
        btnSignUp=(Button)findViewById(R.id.buttonSignUP);

        // Set OnClick Listener on SignUp button
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub

                /// Create Intent for SignUpActivity  abd Start The Activity
                Intent intentSignUP = new Intent(getApplicationContext(), SignUPActivity.class);
                startActivity(intentSignUP);
            }
        });
    }

    public void signIn(View V)
    {
        final Dialog dialog = new Dialog(LoginActivity.this);
        dialog.setContentView(R.layout.login);
        dialog.setTitle("Login");

        // get the Refferences of views
        final EditText editTextUserName=(EditText)dialog.findViewById(R.id.editTextUserNameToLogin);
        final  EditText editTextPassword=(EditText)dialog.findViewById(R.id.editTextPasswordToLogin);

        Button btnSignIn=(Button)dialog.findViewById(R.id.buttonSignIn);

        // Set On ClickListener
        btnSignIn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // get The User name and Password
                String[] userName={editTextUserName.getText().toString()};
                String password=editTextPassword.getText().toString();

                // fetch the Password form database for respective user name
                SQLiteDatabase dbs = databaseHelper.getReadableDatabase();
                String selection = Provider.Login.USER_NAME + "=?";

                Cursor cursor = dbs.query(Provider.Login.TABLE_NAME, ALL_COLUMNS, selection, userName, NO_GROUP_BY, NO_HAVING, NO_SORT_ORDER);

                String storedPass;

                if (cursor.getCount() != 0) {
                    cursor.moveToFirst();

                    storedPass = cursor.getString(cursor.getColumnIndex("password"));

                    if (password.equals(storedPass)) {
                        Toast.makeText(LoginActivity.this, "Congrats: Login Successfull", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                        Intent intent = new Intent(getApplicationContext(), EmployeeListAfterLoginActivity.class);
                        int userNameIndex = cursor.getColumnIndex(Provider.Login.USER_NAME);
                        String userNameText = cursor.getString(userNameIndex);
                        intent.putExtra("USER_NAME", userNameText);
                        startActivity(intent);
                    } else {
                        Toast.makeText(LoginActivity.this, "Password does not match", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "User Name does not exist", Toast.LENGTH_LONG).show();
                }
            }
        });

        dialog.show();
    }



}
