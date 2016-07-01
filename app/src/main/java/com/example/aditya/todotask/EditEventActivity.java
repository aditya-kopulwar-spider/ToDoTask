package com.example.aditya.todotask;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.aditya.todotask.Models.ToDoModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;

import io.realm.Realm;
import io.realm.RealmQuery;

public class EditEventActivity extends AppCompatActivity {

    String date,time;
    public int year, month, day, hour, minute, id;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    String imageURL;
    static String tempImageURL=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);

        id = (int) getIntent().getLongExtra("eventID",-1);

        Realm realm = Realm.getDefaultInstance();
        final RealmQuery<ToDoModel> query = realm.where(ToDoModel.class);
        ToDoModel eventData = query.equalTo("id", id).findFirst();


        /*ToDoModel eventData = (ToDoModel) getIntent().getParcelableExtra("eventData");*/

        EditText eventTitle = (EditText) findViewById(R.id.eventTitle);
        eventTitle.setText(eventData.getTitle());



        EditText eventDescription = (EditText)findViewById(R.id.eventDescription);
        eventDescription.setScroller(new Scroller(this));
        eventDescription.setMaxLines(2);
        eventDescription.setVerticalScrollBarEnabled(true);
        //eventDescription.setMovementMethod(new ScrollingMovementMethod());
        eventDescription.setText(eventData.getDescription());
        ImageView eventImage = (ImageView) findViewById(R.id.eventImage);

        if (tempImageURL == null) {
            eventImage.setImageBitmap(BitmapFactory.decodeFile(eventData.getImageURL()));
            imageURL = eventData.getImageURL();
            tempImageURL = imageURL;
        }
        else {
            eventImage.setImageBitmap(BitmapFactory.decodeFile(tempImageURL));
        }

        TextView eventTime = (TextView)findViewById(R.id.eventTime);
        time = eventData.getTime();
        eventTime.setText(time);

        TextView eventDate = (TextView)findViewById(R.id.eventDate);
        date = eventData.getDate();
        eventDate.setText(date);

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        Button button = (Button)findViewById(R.id.CameraButton);
        if(!hasCamera())
            button.setEnabled(false);
    }

    private boolean hasCamera(){
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }


    public void relaunchCamera(View view){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_IMAGE_CAPTURE && resultCode==RESULT_OK){
            Bitmap photo = (Bitmap) data.getExtras().get("data");


            // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
            Uri tempUri = getImageUri(getApplicationContext(), photo);
            tempImageURL = tempUri.toString();

            // CALL THIS METHOD TO GET THE ACTUAL PATH
            File finalFile = new File(getRealPathFromURI(tempUri));
            tempImageURL = finalFile.toString();

            ImageView eventImage = (ImageView)findViewById(R.id.eventImage);
            eventImage.setImageBitmap(BitmapFactory.decodeFile(tempImageURL));
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

            TextView eventDate = (TextView)findViewById(R.id.eventDate);
            eventDate.setText(date);
        }
    };

    private TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener(){
        @Override
        public void onTimeSet(TimePicker view, int hourPicked, int minutePicked) {
            hour = hourPicked;
            minute = minutePicked;
            if (minutePicked<10)
                time = (String.valueOf(hourPicked) + ':'+ '0' + (String.valueOf(minutePicked)));
            else
                time = (String.valueOf(hourPicked) + ':' + (String.valueOf(minutePicked)));

            TextView eventTime = (TextView)findViewById(R.id.eventTime);
            eventTime.setText(time);
        }
    };

    public void showTimePickerDialog(View v) {
        showDialog(123);
    }

    public void showDatePickerDialog(View v) {
        showDialog(999);
    }

    public void saveEvent(View v){
        if(((EditText) findViewById(R.id.eventTitle)).getText().toString().trim().isEmpty() ||
                (((EditText) findViewById(R.id.eventDescription)).getText().toString()).trim().isEmpty()){
            Toast.makeText(getApplicationContext(), "One of the field is empty", Toast.LENGTH_LONG).show();
        }
        else {
        Realm realm = Realm.getDefaultInstance();
        final ToDoModel toDoModel = new ToDoModel();

        realm.executeTransaction(new Realm.Transaction(){

            @Override
            public void execute(Realm realm) {
                toDoModel.setId(id);
                toDoModel.setTime(time);
                toDoModel.setDate(date);
                toDoModel.setTitle(((EditText) findViewById(R.id.eventTitle)).getText().toString());
                toDoModel.setDescription(((EditText) findViewById(R.id.eventDescription)).getText().toString());
                toDoModel.setImageURL(tempImageURL);
                realm.copyToRealmOrUpdate(toDoModel);
            }
        });
        cancelAlarmService(v);
        setAlarm(v);
        toDoListCaller();
    }
    }

    public void setAlarm(View view){

        Long alarmTime = new GregorianCalendar(year,month,day,hour,minute).getTimeInMillis();

        Intent alarmIntent = new Intent(this, AlertReceiver.class);

        alarmIntent.putExtra("alarmId",id);

        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP,alarmTime, PendingIntent.getBroadcast(this,1,alarmIntent,PendingIntent.FLAG_UPDATE_CURRENT));
    }

    public void cancelAlarmService(View v){
        NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        nm.cancel((int) getIntent().getExtras().getLong("alarmId",id));
    }

    public void cancelEventEditing(View view){
        toDoListCaller();
    }

    @Override
    public void onBackPressed() {
        toDoListCaller();
    }

    public void toDoListCaller(){
        Intent i = new Intent(this,ToDoListActivity.class);
        startActivity(i);
    }

}
