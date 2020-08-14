package com.example.vhhhatis;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    //    set the array list
    ArrayList<String> arrivalAtis = new ArrayList<String>();
    ArrayList<String> departureAtis = new ArrayList<String>();

    //    set the text views
    TextView arrAtis;
    TextView depAtis;

    public void getAtis(View view) {

//        clear the array list for the refresh button also to load again
        arrivalAtis.clear();
        departureAtis.clear();

//        start the download task
        DownloadTask task = new DownloadTask();
        String result = null;

//        find the text views to input the atis data
        arrAtis = (TextView) findViewById(R.id.arrAtisTextView);
        depAtis = (TextView) findViewById(R.id.depAtisTextView);

//        use a string builder to input the data into the text view
        StringBuilder builder = new StringBuilder();

        try {

//            first we need to get tht string of HTML from where to obtain the atis
            result = task.execute("https://atis.cad.gov.hk/ATIS/ATISweb/atis.php").get();

            // reg ex with the html image
            Pattern p0 = Pattern.compile("<div class=\"data_name_arr\">(.*?)</div>");
            Matcher m0 = p0.matcher(result);

            while (m0.find()) {
                arrivalAtis.add(m0.group(1));
            }

            // reg ex with the html image
            Pattern p1 = Pattern.compile("<div class=\"data_name_dep\">(.*?)</div>");
            Matcher m1 = p1.matcher(result);

            while (m1.find()) {
                departureAtis.add(m1.group(1));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

//        use the builder to input into text view
        for (String details : arrivalAtis) {
            builder.append(details + "<br>");
        }
        arrAtis.setText(Html.fromHtml(String.valueOf(builder)));
        builder.setLength(0);

        for (String details : departureAtis) {
            builder.append(details + "\n");
        }
        depAtis.setText(builder.toString());
        builder.setLength(0);
    }


    //    gives life to the refresh button
    public void refreshAtis(View view) {
        getAtis(null);
    }

    //    download task to obtain html
    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        initializes the app with one atis, the most current one by running get atis
        getAtis(null);
    }
}