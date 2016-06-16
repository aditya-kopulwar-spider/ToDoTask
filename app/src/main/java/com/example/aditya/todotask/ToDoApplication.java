package com.example.aditya.todotask;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class ToDoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Configure Realm for the application
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this)
                .name("todolist")
                .build();
        // Realm.deleteRealm(realmConfiguration); //Deletes the realms,
        // use when you want a clean slate for dev/etc

        // Make this Realm the default
        Realm.setDefaultConfiguration(realmConfiguration);
    }
}
