package sg.edu.singaporetech.sit.theclueless;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.util.ArrayList;

/**
 * This is a PieChart Activity that will display Pie Chart in relation to the number of likes for Twitter and upvotes for Reddit.
 * @author The Clueless
 */

public class PieChartActivity extends AppCompatActivity {

    TextView tvReddit, tvTwitter;
    PieChart doughnut;

    ArrayList<RedditPost> pcRedditList;
    ArrayList<TwitterPost> pcTwitterList;

    int totalScore = 0;
    RedditPost currentScore;

    int totalLikes = 0;
    TwitterPost currentLikes;

    /**
     * When the screen first loads, both reddit and twitter's collection was used to get the necessary data, and then inserted into the PieChart.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piechart);

        tvReddit = findViewById(R.id.tvReddit);
        tvTwitter = findViewById(R.id.tvTwitter);
        doughnut = findViewById(R.id.doughnut);

        FirebaseFirestore dbReddit = FirebaseFirestore.getInstance();
        dbReddit.collection("reddit")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                RedditPost postR = document.toObject(RedditPost.class);
                                pcRedditList.add(postR);
                            }
                            for(int i=0; i < pcRedditList.size(); i++) {
                                currentScore = pcRedditList.get(i);
                                totalScore += currentScore.getScore();
                            }

                            tvReddit.setText(String.valueOf(totalScore));
                            doughnut.addPieSlice(
                                    new PieModel(
                                            "Reddit",
                                            totalScore,
                                            Color.parseColor("#FFA726")));


                        }else{
                            Log.d("Reddit Activity", "Error getting documents: ",task.getException());
                        }
                    }
                });

        FirebaseFirestore dbTwitter = FirebaseFirestore.getInstance();
        dbTwitter.collection("twitter")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                TwitterPost postT = document.toObject(TwitterPost.class);
                                pcTwitterList.add(postT);
                            }
                            for(int i=0; i < pcTwitterList.size(); i++) {
                                currentLikes = pcTwitterList.get(i);
                                totalLikes += currentLikes.getLikes();
                            }

                            tvTwitter.setText(String.valueOf(totalLikes));
                            String twitterDoughnut = tvTwitter.getText().toString();
                            doughnut.addPieSlice(
                                    new PieModel(
                                            "Twitter",
                                            totalLikes,
                                            Color.parseColor("#66BB6A")));

                        }else{
                            Log.d("Twitter Activity", "Error getting documents: ", task.getException());
                        }
                    }
                });

        pcRedditList = new ArrayList<>();
        pcTwitterList = new ArrayList<>();

        doughnut.startAnimation();
    }
}