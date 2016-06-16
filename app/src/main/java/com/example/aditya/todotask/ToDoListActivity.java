package com.example.aditya.todotask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.aditya.todotask.Models.ToDoModel;

import io.realm.Realm;

public class ToDoListActivity extends Activity {



    private CustomAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.to_do_list_view);

        final Intent intent = new Intent(this,EditEventActivity.class);

        listAdapter = new CustomAdapter();

        ListView listView = (ListView) findViewById(R.id.EventsList);

        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(
                  new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        long eventId = listAdapter.getItemId(position);
                        intent.putExtra("eventID", eventId);
                        startActivity(intent);
                    }
                }
        );

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                showDeleteConfirmationAlert(position);
                return true;
            }
        });
    }

    private void showDeleteConfirmationAlert(final int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        final Realm realm = Realm.getDefaultInstance();
        alertDialogBuilder.setMessage("Do you really want to delete it ?");

        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final ToDoModel item = listAdapter.getItem(position);
                int id = (int) item.getId();
                realm.executeTransaction(new Realm.Transaction(){

                    @Override
                    public void execute(Realm realm) {
                        item.deleteFromRealm();
                    }
                });
                NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                nm.cancel(id);

                listAdapter.reloadData();
            }
        });

        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void onAddEventClick(View view){
        Intent i = new Intent(this,AddEventActivity.class);

        startActivity(i);
    }

}