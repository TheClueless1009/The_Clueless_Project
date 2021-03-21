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
 * This is Twitter which will View all the tweets that is being crawled.
 * @author The Clueless
 */
public class TwitterActivity extends AppCompatActivity {
    ListView lvTwitter;
    ArrayList<TwitterPost> alTwitterPost;
    TwitterAdapter caTwitter;

    /**
     * When the screen first loads, twitters data is being loaded into the ListView.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter);

        lvTwitter = findViewById(R.id.listViewTwitter);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("twitter")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                TwitterPost post = document.toObject(TwitterPost.class);
                                alTwitterPost.add(post);
                            }
                            caTwitter = new TwitterAdapter(TwitterActivity.this, R.layout.twitter_post, alTwitterPost);
                            lvTwitter.setAdapter(caTwitter);
                        }else{
                            Log.d("Twitter Activity", "Error getting documents: ",task.getException());
                        }
                    }
                });

        alTwitterPost = new ArrayList<>();
    }
}