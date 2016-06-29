package com.example.aditya.todotask;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.aditya.todotask.Models.ToDoModel;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;


//Use Base adapter and overrides method by yourself
public class CustomAdapter extends BaseAdapter {
    Realm realm = Realm.getDefaultInstance();
    private List<ToDoModel> toDoModels;

    public CustomAdapter() {
        super();
        updateData();
    }

    @Override
    public int getCount() {
        return toDoModels.size();
    }

    //Returns the todomodel object by passing the position number of click.
    @Override
    public ToDoModel getItem(int position) {
        return toDoModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View customView = layoutInflater.inflate(R.layout.custom_event_row, parent, false);

        ToDoModel m = new ToDoModel();
        ToDoModel m1 = m;

        ToDoModel singleEvent = getItem(position);

        TextView title = (TextView) customView.findViewById(R.id.Title);
        TextView date = (TextView) customView.findViewById(R.id.Date);
        TextView time = (TextView) customView.findViewById(R.id.Time);

        title.setText(singleEvent.getTitle());
        date.setText(singleEvent.getDate());
        time.setText(singleEvent.getTime());

        return customView;
    }

    public void reloadData(){
        updateData();
        notifyDataSetChanged();
    }

    private void updateData() {
        final RealmQuery<ToDoModel> query = realm.where(ToDoModel.class);
        RealmResults<ToDoModel> result = query.findAll();
        toDoModels = result.subList(0,result.size());
    }
}
