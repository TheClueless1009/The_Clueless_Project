package sg.edu.singaporetech.sit.theclueless;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

/**
 * SearchActivity is Crawl post and tweets by using keywords. It will display WordCloud img after the data is done crawling.
 * @author The Clueless
 */
public class SearchActivity extends AppCompatActivity {
    EditText etKeyword;
    Button btnSearch;
    ImageView ivImg;

    /**
     * When the screen first loads, the Firestore data is received and loaded into the python file where it will produce the image and store into firestore.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        etKeyword = findViewById(R.id.etKeyword);
        btnSearch = findViewById(R.id.btnSearch);
        ivImg = findViewById(R.id.ivImg);
        if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }

        Python py = Python.getInstance();
        final PyObject pyobj = py.getModule("assignment");

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pyobj.callAttr("main", etKeyword.getText().toString());
                Toast.makeText(SearchActivity.this, "THIS WORKS!", Toast.LENGTH_SHORT).show();

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("wordcloud")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){
                                    for (QueryDocumentSnapshot document : task.getResult()){
                                        Map<String, Object> imgMap = (Map) document.getData();
                                        byte[] decodedString = Base64.decode(imgMap.get("image").toString(), Base64.DEFAULT);
                                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                        ivImg.setImageBitmap(decodedByte);

                                    }
                                }else{
                                    Log.d("Reddit Activity", "Error getting documents: ",task.getException());
                                }
                            }
                        });

            }
        });

    }
}