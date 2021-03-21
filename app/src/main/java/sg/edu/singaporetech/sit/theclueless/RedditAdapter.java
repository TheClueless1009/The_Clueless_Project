package sg.edu.singaporetech.sit.theclueless;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
/**
 * The Reddit Adapter is used to display data into customListView to improve readability
 * @author The Clueless
 */
public class RedditAdapter extends ArrayAdapter {
    Context parent_context;
    int layout_id;
    ArrayList<RedditPost> redditList;

    /**
     * Defining context, resource and object
     * @param context Parent_context will hold the current environment.
     * @param resource layout_id gets the id of layout that the data will be loaded into.
     * @param objects twitterList is the array that holds all the data.
     */
    public RedditAdapter(@NonNull Context context, int resource, @NonNull ArrayList<RedditPost> objects){
        super(context, resource, objects);
        parent_context = context;
        layout_id = resource;
        redditList = objects;
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
        TextView tvTitle = rowView.findViewById(R.id.tvTitle);
        TextView tvContent = rowView.findViewById(R.id.tvContent);
        LinearLayout linearLayoutContent = rowView.findViewById(R.id.linearLayoutContent);
        RedditPost currentItem = redditList.get(position);
        tvTitle.setText(currentItem.getTitle());
        if (currentItem.getContent().isEmpty()){
            linearLayoutContent.setVisibility(View.INVISIBLE);
        }else{
            tvContent.setText(currentItem.getContent());
        }
        return rowView;
    }
}
