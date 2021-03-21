package sg.edu.singaporetech.sit.theclueless;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * This is RedditActivity which will View all the Reddit post that is being crawled.
 * @author The Clueless
 */

public class RedditActivity extends AppCompatActivity {
    ListView lvReddit;
    ArrayList<RedditPost> alRedditPost;
    RedditAdapter caReddit;

    /**
     *  When the screen first loads, reddits data is being loaded into the ListView.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reddit);

        lvReddit = findViewById(R.id.listViewReddit);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("reddit")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                RedditPost post = document.toObject(RedditPost.class);
                                alRedditPost.add(post);
                            }
                            caReddit = new RedditAdapter(RedditActivity.this, R.layout.reddit_post, alRedditPost);
                            lvReddit.setAdapter(caReddit);
                        }else{
                            Log.d("Reddit Activity", "Error getting documents: ",task.getException());
                        }
                    }
                });

        alRedditPost = new ArrayList<>();

    }
}