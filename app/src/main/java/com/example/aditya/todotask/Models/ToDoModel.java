package com.example.aditya.todotask.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.sql.Time;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class ToDoModel extends RealmObject /*implements Parcelable*/{

    @PrimaryKey
    private long id;
    private String title;
    private String description;
    private String date;
    private String time;
    private String imageURL;

    /*public ToDoModel() {
    }*/

    /*protected ToDoModel(Parcel in) {
        id = in.readLong();
        title = in.readString();
        description = in.readString();
        date = in.readString();
        time = in.readString();
        imageURL = in.readString();
    }*/

    /*public static final Creator<ToDoModel> CREATOR = new Creator<ToDoModel>() {
        @Override
        public ToDoModel createFromParcel(Parcel in) {
            return new ToDoModel(in);
        }

        @Override
        public ToDoModel[] newArray(int size) {
            return new ToDoModel[size];
        }
    };*/

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    /*@Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(date);
        dest.writeString(time);
        dest.writeString(imageURL);
    }*/
}
