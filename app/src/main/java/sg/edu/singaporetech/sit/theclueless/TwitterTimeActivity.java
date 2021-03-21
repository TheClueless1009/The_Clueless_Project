package sg.edu.singaporetech.sit.theclueless;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * This activity compares the different post timings and plots the results on a bar graph and returns the most popular timing for twitter.
 * @author The Clueless
 */
public class TwitterTimeActivity extends AppCompatActivity {

    TextView tvHighestValue;
    ArrayList<TwitterPost> alDataTwitter;
    int mornCount = 0;
    int aftCount = 0;
    int evenCount = 0;
    int nightCount = 0;
    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
    Time fiveam, twelvepm, fivepm, ninepm, fouram;
    Time timets;

    /**
     * When the screen first loads, the different time ranges are created and data is fetched from Firestore.
     * The Data will then be compared and plotted in a bar graph.
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timebargraph);

        tvHighestValue = findViewById(R.id.tvHighestValue);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        try {
            fiveam = convertTime("Mon Mar 11 05:00:00 AM GMT 2021");
            twelvepm = convertTime("Mon Mar 11 12:00:00 PM GMT 2021");
            fivepm = convertTime("Mon Mar 11 17:00:00 PM GMT 2021");
            ninepm = convertTime("Mon Mar 11 21:00:00 PM GMT 2021");
            fouram = convertTime("Mon Mar 11 04:00:00 AM GMT 2021");

        } catch (ParseException e) {
            e.printStackTrace();
        }
        db.collection("twitter")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            alDataTwitter = new ArrayList<TwitterPost>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                TwitterPost post = document.toObject(TwitterPost.class);
                                alDataTwitter.add(post);
                            }

                            for (int i = 0; i < alDataTwitter.size(); i++) {
                                try {
                                    timets = convertTimets(alDataTwitter.get(i).getCreated_date());

                                    if (timets.after(fiveam) && timets.before(twelvepm)) {
                                        mornCount += 1;
                                    } else if (timets.after(twelvepm) && timets.before(fivepm)) {
                                        aftCount++;
                                    } else  if (timets.after(fivepm) && timets.before(ninepm)){
                                        evenCount++;
                                    } else {
                                        nightCount++;
                                    }

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                            }
                            BarChart barChart = (BarChart) findViewById(R.id.barchart);

                            ArrayList<BarEntry> entries = new ArrayList<>();
                            entries.add(new BarEntry(mornCount, 0));
                            entries.add(new BarEntry(aftCount, 1));
                            entries.add(new BarEntry(evenCount, 2));
                            entries.add(new BarEntry(nightCount, 3));


                            BarDataSet bardataset = new BarDataSet(entries, "Cells");

                            ArrayList<String> labels = new ArrayList<String>();
                            labels.add("Morn");
                            labels.add("Aft");
                            labels.add("Evening");
                            labels.add("Night");

                            BarData data = new BarData(labels, bardataset);
                            barChart.setData(data); // set the data and list of labels into chart
                            barChart.setDescription("Comparison of post timings");  // set the description
                            bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
                            barChart.animateY(5000);

                            highestScore(mornCount, aftCount, evenCount, nightCount);

                        }
                    }
                });


    }

    /**
     * This method is used to convert time range into a format that is able to be used for comparisons
     * @param time Takes in the different time range in the format "EE MM dd HH:mm:ss ss aa zzz yyyy"
     * @return The converted time range into "HH:mm:ss"
     * @throws ParseException
     */

    public Time convertTime(String time) throws ParseException {
        String[] timeSplit = time.split(" ");
        Time timeStamp = new Time(formatter.parse(timeSplit[3]).getTime());
        return timeStamp;
    }

    /**
     * This method is used to convert the fetched String into Date format and split into "HH:mm:ss"
     * @param time Takes in the time the post was created.
     * @return The converted timing
     * @throws ParseException
     */

    public Time convertTimets(String time) throws ParseException {
        String[] timeSplit = time.split(" ");
        Time timeStamp = new Time(formatter.parse(timeSplit[1]).getTime());
        return timeStamp;
    }

    /**
     * This method is used to find the the time range with the highest count.
     * @param count1 Takes in the mornCount from the time range in the morning
     * @param count2 Takes in the aftCount from the time range in the afternoon
     * @param count3 Takes in the evenCount from the time range in the evening
     * @param count4 Takes in the nightCount from the time range at Night
     */

    public void highestScore (int count1, int count2, int count3, int count4){
        double highest1 = Math.max(count1, count2);
        double highest2 = Math.max(count3, count4);
        String highestValue;
        double highest = Math.max(highest1, highest2);
        if (highest == count1)
            highestValue = "Morning";
        else if (highest == count2)
            highestValue = "Afternoon";
        else if (highest == count3)
            highestValue = "Evening";
        else
            highestValue = "Night";

        tvHighestValue.setText("The most popular timing is in the " + highestValue);
    }

}