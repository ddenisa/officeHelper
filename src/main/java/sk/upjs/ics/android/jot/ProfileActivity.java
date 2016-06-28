package sk.upjs.ics.android.jot;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import sk.upjs.ics.android.jot.provider.EmployeeContentProvider;
import sk.upjs.ics.android.jot.provider.EmployeeDatabaseOpenHelper;
import sk.upjs.ics.android.jot.provider.LoginDatabaseOpenHelper;
import sk.upjs.ics.android.jot.provider.Provider;

import static sk.upjs.ics.android.util.Defaults.ALL_COLUMNS;
import static sk.upjs.ics.android.util.Defaults.NO_CURSOR;
import static sk.upjs.ics.android.util.Defaults.NO_FLAGS;
import static sk.upjs.ics.android.util.Defaults.NO_GROUP_BY;
import static sk.upjs.ics.android.util.Defaults.NO_HAVING;
import static sk.upjs.ics.android.util.Defaults.NO_SELECTION_ARGS;
import static sk.upjs.ics.android.util.Defaults.NO_SORT_ORDER;

public class ProfileActivity extends ActionBarActivity {

    protected TextView employeeNameText;
    protected TextView employeeSurnameText;
    protected TextView titleText;
    protected TextView officeText;
    protected TextView cellText;
    protected TextView emailText;
    protected TextView cityText;
    protected TextView departmentText;
    protected int employeeId;
    protected int managerId;
    Drawable drawable;

    private Uri uriFilePath;

    final int TAKE_PICTURE = 1;
    final int ACTIVITY_SELECT_IMAGE = 2;

    Button openCameraOrGalleryBtn;
    ImageView picture;

    private Bitmap mImageBitmap;
    private String mCurrentPhotoPath;

    protected String userName;

    private static final int EMPLOYEES_LOADER_ID = 0;

    private ListView profileView;
    private SimpleCursorAdapter adapter;

    private EmployeeDatabaseOpenHelper databaseHelper;
    private LoginDatabaseOpenHelper dh;

    private Cursor cursor;

    Bitmap bTemp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            if (mImageBitmap == null && savedInstanceState.getString("uri_file_path") != null) {
                try{
                    byte [] encodeByte=Base64.decode("uri_file_path",Base64.DEFAULT);
                    mImageBitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                }catch(Exception e) {
                    e.getMessage();
                }
            }
        }
        setContentView(R.layout.profile_list_view);


        databaseHelper = new EmployeeDatabaseOpenHelper(this);
        dh = new LoginDatabaseOpenHelper(this);

        userName = getIntent().getStringExtra("USER_NAME");

        profileView = (ListView) findViewById(R.id.profileListView);

        openCameraOrGalleryBtn=(Button)findViewById(R.id.btnSelectPhoto);

        picture = (ImageView)findViewById(R.id.profileImage);



        openCameraOrGalleryBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                selectImage();
            }
        });


        SQLiteDatabase dbs = databaseHelper.getReadableDatabase();
        String[] un = {userName};
        String selection = Provider.Employee.EMAIL + "=?";
        cursor = dbs.query(Provider.Employee.TABLE_NAME, ALL_COLUMNS, selection, un, NO_GROUP_BY, NO_HAVING, NO_SORT_ORDER);




        if (cursor.getCount() != 0)
        {
            cursor.moveToFirst();

            employeeNameText = (TextView) findViewById(R.id.firstName);
            employeeSurnameText = (TextView) findViewById(R.id.lastName);
            String first = cursor.getString(cursor.getColumnIndex("first_name"));
            String last = cursor.getString(cursor.getColumnIndex("last_name"));
            employeeNameText.setText(first);
            employeeSurnameText.setText(last);

            titleText = (TextView) findViewById(R.id.title);
            titleText.setText(cursor.getString(cursor.getColumnIndex("title")));

            departmentText = (TextView) findViewById(R.id.department);
            departmentText.setText(cursor.getString(cursor.getColumnIndex("department")));
            cityText = (TextView) findViewById(R.id.city);
            cityText.setText(cursor.getString(cursor.getColumnIndex("city")));
            officeText = (TextView) findViewById(R.id.office);
            officeText.setText(cursor.getString(cursor.getColumnIndex("office")));
            cellText = (TextView) findViewById(R.id.cell);
            cellText.setText(cursor.getString(cursor.getColumnIndex("cell")));
            emailText = (TextView) findViewById(R.id.email);
            emailText.setText(cursor.getString(cursor.getColumnIndex("email")));

        }
    }




    public void selectImage()
    {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options,new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                if(options[which].equals("Take Photo"))
                {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, TAKE_PICTURE);
                }
                else if(options[which].equals("Choose from Gallery"))
                {
                    Intent intent=new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, ACTIVITY_SELECT_IMAGE);
                }
                else if(options[which].equals("Cancel"))
                {
                    dialog.dismiss();
                }

            }
        });
        builder.show();
    }

    public void onActivityResult(int requestcode,int resultcode,Intent intent)
    {
        super.onActivityResult(requestcode, resultcode, intent);
        if(resultcode==RESULT_OK)
        {
            if(requestcode==TAKE_PICTURE)
            {
                mImageBitmap= (Bitmap) intent.getExtras().get("data");
                picture.setImageBitmap(mImageBitmap);

            }
            else if(requestcode==ACTIVITY_SELECT_IMAGE)
            {
                Uri selectedImage = intent.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                mImageBitmap = (BitmapFactory.decodeFile(picturePath));
                picture.setImageBitmap(mImageBitmap);
                //drawable=new BitmapDrawable(thumbnail);
                //picture.setImageDrawable(drawable);


            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }



    public void onBackPressed() {
        super.onBackPressed();

        if (mImageBitmap != null) {
            Bundle outState  = new Bundle();
            onSaveInstanceState(outState);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            mImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] b = baos.toByteArray();
            String temp = Base64.encodeToString(b, Base64.DEFAULT);

            outState.putString("uri_file_path", temp);
        }
     //   super.onBackPressed();

        //overridePendingTransition(R.anim.slide_down,0);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mImageBitmap != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            mImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] b = baos.toByteArray();
            String temp = Base64.encodeToString(b, Base64.DEFAULT);

            outState.putString("uri_file_path", temp);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_edit) {
            edit();
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

    private void edit() {

        editData();
    }

    private void editData() {

        final Dialog dialog = new Dialog(ProfileActivity.this);
        dialog.setContentView(R.layout.edit);
        dialog.setTitle("Edit");
        dialog.show();

        Button btnChnP=(Button)dialog.findViewById(R.id.buttonChangePass);
        Button brtCancel = (Button) dialog.findViewById(R.id.buttonCancel);

        brtCancel.setOnClickListener(new View.OnClickListener(){
            public void onClick(View w){
                dialog.dismiss();
            }
        });

        btnChnP.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                dialog.setContentView(R.layout.edit_password);
                dialog.setTitle("Change password");
                dialog.show();

                final EditText editTextOldPass=(EditText)dialog.findViewById(R.id.editTextPassword);
                final  EditText editTextNewPassword=(EditText)dialog.findViewById(R.id.editTextNewPassword);
                final  EditText editTextNewNewPassword=(EditText)dialog.findViewById(R.id.editTextNewNewPassword);

                Button btnChange=(Button)dialog.findViewById(R.id.buttonChange);


                btnChange.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {


                        SQLiteDatabase db = dh.getWritableDatabase();
                        String[] un = {userName};
                        String selection = Provider.Login.USER_NAME + "= ?";
                        Cursor cursor = db.query(Provider.Login.TABLE_NAME, ALL_COLUMNS, selection, un, NO_GROUP_BY, NO_HAVING, NO_SORT_ORDER);

                        String old = cursor.getString(cursor.getColumnIndex(Provider.Login.PASSWORD));
                        String[] o = {old};
                        String s = Provider.Login.PASSWORD + "=?";
                        if (editTextOldPass.getText().toString().equals(old)) {
                            if(editTextNewPassword.getText().toString().equals(editTextNewNewPassword.getText().toString())){
                                ContentValues val = new ContentValues();
                                val.put(Provider.Login.USER_NAME, userName);
                                val.put(Provider.Login.PASSWORD, editTextNewNewPassword.getText().toString());
                                db.update(Provider.Login.TABLE_NAME, val, s, o);

                            }
                        }
                    }





            });
    }


});
    }
}

