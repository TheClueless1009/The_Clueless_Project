package sg.edu.singaporetech.sit.theclueless;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * The Twitter Adapter is used to display data into customListView to improve readability
 * @author The Clueless
 */
public class TwitterAdapter extends ArrayAdapter {
    Context parent_context;
    int layout_id;
    ArrayList<TwitterPost> twitterList;

    /**
     * Defining context, resource and object
     * @param context Parent_context will hold the current environment.
     * @param resource layout_id gets the id of layout that the data will be loaded into.
     * @param objects twitterList is the array that holds all the data.
     */
    public TwitterAdapter(@NonNull Context context, int resource, @NonNull ArrayList<TwitterPost> objects){
        super(context, resource, objects);
        parent_context = context;
        layout_id = resource;
        twitterList = objects;
    }

    /**
     * Loading of data into the customListView
     * @param position Get the row of the item in ArrayList.
     * @param convertView Inflate the view based on the Layout customized for each row.
     * @param parent Loads the current environment into rowView.
     * @return The combined ArrayList into ListView.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) parent_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(layout_id, parent,false);
        TextView tvTweet = rowView.findViewById(R.id.tvTweet);
        TwitterPost currentItem = twitterList.get(position);
        tvTweet.setText(currentItem.getTweet());


        return rowView;
    }
}
