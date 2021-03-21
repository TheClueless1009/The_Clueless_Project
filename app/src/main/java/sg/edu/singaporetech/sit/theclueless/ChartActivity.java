package sg.edu.singaporetech.sit.theclueless;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.Series;

import java.util.ArrayList;
import java.util.Map;

/**
 * This is a ChartActivity which will display Scatter Plot.
 * @author The Clueless
 */

public class ChartActivity extends AppCompatActivity {
    PointsGraphSeries<DataPoint> xySeries;
    PointsGraphSeries<DataPoint> onClickSeries;
    GraphView mScatterPlot;
    ArrayList<Object> posts;
    ArrayList<XYValue> xyValueArray;
    Map<String, Object> map;
    int i = 1;

    /**
     * When the screen first loaded. It will retrieve all the x and y axis from the Firebase.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        mScatterPlot = (GraphView) findViewById(R.id.scatterPlot);
        xySeries = new PointsGraphSeries<>();
        xyValueArray = new ArrayList<>();


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("sentimentAnalysis").document("first");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        map = (Map)document.getData();

                        for (int i = 1; i <= map.size(); i++) {
                            Map<String, Object> val = (Map)map.get(Integer.toString(i));
                            Object x_val = (Double) val.get("polarity");
                            Object y_val = (Double) val.get("subjectivity");

                            xyValueArray.add(new XYValue((Double) x_val, (Double) y_val));

                        }
                        xyValueArray = sortArray(xyValueArray);
                        //add the data to the series
                        for(int i = 0;i <xyValueArray.size(); i++){
                            double x = xyValueArray.get(i).getX();
                            double y = xyValueArray.get(i).getY();
                            xySeries.appendData(new DataPoint(x,y),true, 1000);
                        }
//                        Log.d("LOL2", "DocumentSnapshot data: " + map.get("22"));
                        createScatterPlot();

                    } else {
                        Log.d("Chart", "No such document");
                    }
                } else {
                    Log.d("Chart", "get failed with ", task.getException());
                }
            }
        });
    }

    /**
     * createScatterPlot() is to create the graph.
     */
    private void createScatterPlot() {
        xySeries.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                onClickSeries = new PointsGraphSeries<>();
                onClickSeries.appendData(new DataPoint(dataPoint.getX(), dataPoint.getY()), true, 100);
                onClickSeries.setShape(PointsGraphSeries.Shape.RECTANGLE);
                onClickSeries.setColor(Color.RED);
                onClickSeries.setSize(25f);
                mScatterPlot.removeAllSeries();
                mScatterPlot.addSeries(onClickSeries);
                createScatterPlot();

            }
        });

        //set some properties
        xySeries.setShape(PointsGraphSeries.Shape.TRIANGLE);
        xySeries.setColor(Color.BLUE);
        xySeries.setSize(20f);

        //set Scrollable and Scaleable
        mScatterPlot.getViewport().setScalable(true);
        mScatterPlot.getViewport().setScalableY(true);
        mScatterPlot.getViewport().setScrollable(true);
        mScatterPlot.getViewport().setScrollableY(true);

        //set manual x bounds
        mScatterPlot.getViewport().setYAxisBoundsManual(true);
        mScatterPlot.getViewport().setMaxY(1);
        mScatterPlot.getViewport().setMinY(0);

        //set manual y bounds
        mScatterPlot.getViewport().setXAxisBoundsManual(true);
        mScatterPlot.getViewport().setMaxX(1);
        mScatterPlot.getViewport().setMinX(-1);

        mScatterPlot.addSeries(xySeries);

        // use static labels for horizontal and vertical labels
        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(mScatterPlot);
        mScatterPlot.getGridLabelRenderer().setHorizontalAxisTitle("Polarity");
        mScatterPlot.getGridLabelRenderer().setVerticalAxisTitle("Subjectivity");

    }

    /**
     * This is to sort the array into Ascending order.
     * @param array ArrayList that is being input.
     * @return returns sorted array.
     */
    private ArrayList<XYValue> sortArray(ArrayList<XYValue> array){
        /*
        //Sorts the xyValues in Ascending order to prepare them for the PointsGraphSeries<DataSet>
         */
        int factor = Integer.parseInt(String.valueOf(Math.round(Math.pow(array.size(),2))));
        int m = array.size()-1;
        int count = 0;

        while(true){
            m--;
            if(m <= 0){
                m = array.size() - 1;
            }

            try{
                //print out the y entrys so we know what the order looks like

                double tempY = array.get(m-1).getY();
                double tempX = array.get(m-1).getX();
                if(tempX > array.get(m).getX() ){
                    array.get(m-1).setY(array.get(m).getY());
                    array.get(m).setY(tempY);
                    array.get(m-1).setX(array.get(m).getX());
                    array.get(m).setX(tempX);
                }
                else if(tempY == array.get(m).getY()){
                    count++;
                }

                else if(array.get(m).getX() > array.get(m-1).getX()){
                    count++;
                }

                //break when factorial is done
                if(count == factor ){
                    break;
                }
            }catch(ArrayIndexOutOfBoundsException e){
                break;
            }
        }
        return array;
    }


}

