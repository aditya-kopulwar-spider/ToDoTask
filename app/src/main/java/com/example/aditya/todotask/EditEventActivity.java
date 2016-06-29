package com.example.aditya.todotask;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.aditya.todotask.Models.ToDoModel;

import java.util.Calendar;
import java.util.GregorianCalendar;

import io.realm.Realm;
import io.realm.RealmQuery;

public class EditEventActivity extends AppCompatActivity {

    String date,time;
    public int year, month, day, hour, minute, id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);

        id = (int) getIntent().getLongExtra("eventID",-1);

        Realm realm = Realm.getDefaultInstance();
        final RealmQuery<ToDoModel> query = realm.where(ToDoModel.class);
        ToDoModel eventData = query.equalTo("id", id).findFirst();


        /*ToDoModel eventData = (ToDoModel) getIntent().getParcelableExtra("eventData");*/

        TextView eventTitle = (TextView) findViewById(R.id.eventTitle);
        eventTitle.setText(eventData.getTitle());

        TextView eventDescription = (TextView)findViewById(R.id.eventDescription);
        eventDescription.setText(eventData.getDescription());

        ImageView eventImage = (ImageView)findViewById(R.id.eventImage);
        eventImage.setImageBitmap(BitmapFactory.decodeFile(eventData.getImageURL()));

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

    public void saveEvent(View v){
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
                realm.copyToRealmOrUpdate(toDoModel);
            }
        });
        cancelAlarmService(v);
        setAlarm(v);
        toDoListCaller(v);
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

    public void toDoListCaller(View view){
        Intent i = new Intent(this,ToDoListActivity.class);
        startActivity(i);
    }


}
