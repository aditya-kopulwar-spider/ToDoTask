package com.example.aditya.todotask;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import io.realm.Realm;

import com.example.aditya.todotask.Models.ToDoModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class AddEventActivity extends AppCompatActivity {

    String date,time;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    public int year, month, day, hour,minute;
    String imageURL;
    long nextID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_add);
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        Button cameraButton = (Button)findViewById(R.id.cameraButton);
        if(!hasCamera())
            cameraButton.setEnabled(false);
    }


    private boolean hasCamera(){
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }


    public void launchCamera(View view){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_IMAGE_CAPTURE && resultCode==RESULT_OK){
            Bitmap photo = (Bitmap) data.getExtras().get("data");


            // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
            Uri tempUri = getImageUri(getApplicationContext(), photo);
            imageURL = tempUri.toString();

            // CALL THIS METHOD TO GET THE ACTUAL PATH
            File finalFile = new File(getRealPathFromURI(tempUri));
            imageURL = finalFile.toString();

        }
    }



    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //This creates a dialog for date picker
    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 999) {
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        else if(id == 123){
            return new TimePickerDialog(this, myTimeListener, hour, minute,true);
        }
        else
        return null;
    }


    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int yearPicked, int monthPicked, int dayPicked) {
            year = yearPicked;
            month = monthPicked;
            day = dayPicked;
            date = (String.valueOf(yearPicked) + '/' + String.valueOf(monthPicked+1) + '/' + String.valueOf(dayPicked));
        }
    };



    private TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener(){
        @Override
        public void onTimeSet(TimePicker view, int hourPicked, int minutePicked) {
            hour = hourPicked;
            minute = minutePicked;
            time = (String.valueOf(hourPicked) + ':' + (String.valueOf(minutePicked)));
        }
    };




    public void showTimePickerDialog(View v) {
        showDialog(123);
    }

    public void showDatePickerDialog(View v) {
        showDialog(999);
    }

    /*public void dateSelector(int year, int month,int day){
        date = (String.valueOf(year) + '/' + String.valueOf(month) + '/' + String.valueOf(day));
    }*/

    public void addEvent(View v){
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction(){
            @Override
            public void execute(Realm realm) {
                ToDoModel toDoModel = realm.createObject(ToDoModel.class);
                toDoModel.setTitle( ((EditText) findViewById(R.id.eventTitle)).getText().toString());
                toDoModel.setDescription(((EditText) findViewById(R.id.eventDescription)).getText().toString());
                nextID = (long) (realm.where(ToDoModel.class).max("id")) + 1;

                // insert new value
                toDoModel.setId(nextID);
                toDoModel.setDate(date);
                toDoModel.setTime(time);
                toDoModel.setImageURL(imageURL);
            }
        });
        setAlarm(v);
        ToDoListActivityCaller(v);
    }


    public void setAlarm(View view){

        Long alarmTime = new GregorianCalendar(year,month,day,hour,minute).getTimeInMillis();

        Intent alarmIntent = new Intent(this, AlertReceiver.class);

        alarmIntent.putExtra("alarmId",nextID);

        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP,alarmTime, PendingIntent.getBroadcast(this,1,alarmIntent,PendingIntent.FLAG_UPDATE_CURRENT));
    }


    public void ToDoListActivityCaller(View view){
        Intent i = new Intent(this,ToDoListActivity.class);

        startActivity(i);
    }
}
