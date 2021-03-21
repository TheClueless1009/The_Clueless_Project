package sg.edu.singaporetech.sit.theclueless;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * MainActivity is where all the button and linking to all the different activity.
 * @author The Clueless
 */
public class MainActivity extends AppCompatActivity {

    Button btnTwitter, btnReddit, btnPieChart, btnSearch, btnTweetTime, btnRedTime, btnChart;

    /**
     * The first screen that is loaded with all the buttons for the user to select
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnTwitter = findViewById(R.id.btnTwitter);
        btnReddit = findViewById(R.id.btnReddit);
        btnPieChart = findViewById(R.id.btnPieChart);
        btnSearch = findViewById(R.id.btnSearchK);
        btnRedTime = findViewById(R.id.btnRedditTime);
        btnTweetTime = findViewById(R.id.btnTwitterTime);
        btnChart = findViewById(R.id.btnChart);

        btnTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), TwitterActivity.class);
                startActivity(intent);

            }
        });

        btnReddit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), RedditActivity.class);
                startActivity(intent);
            }
        });

        btnPieChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), PieChartActivity.class);
                startActivity(intent);
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), SearchActivity.class);
                startActivity(intent);
            }
        });

        btnTweetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), TwitterTimeActivity.class);
                startActivity(intent);
            }
        });

        btnRedTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), RedditTimeActivity.class);
                startActivity(intent);
            }
        });

        btnChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), ChartActivity.class);
                startActivity(intent);
            }
        });

    }
}