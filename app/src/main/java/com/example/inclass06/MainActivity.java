//Assignment Inclass 06
//File Name: Group12_InClass06
//Sanika Pol
//Snehal Kekan

package com.example.inclass06;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "demo";
    String baseURL = "http://newsapi.org/v2/top-headlines";
    Button btn_Select;
    TextView tv_category;
    ImageView iv_prev,iv_next;
    int curr = 0;
    int size = 0;
    ArrayList<News> news_global = new ArrayList<>();
    TextView tv_title;
    TextView tv_publishedAt;
    TextView tv_content;
    TextView tv_scroll;
    ImageView iv_image;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Main Activity");
        final String[] category = {"Business","Entertainment","General","Health","Science","Sports","Technology"};

        btn_Select   = findViewById(R.id.btnSelect);
        tv_category = findViewById(R.id.tv_selectKeyword);

        iv_prev = findViewById(R.id.iv_prev);
        iv_next = findViewById(R.id.iv_next);
        tv_title = findViewById(R.id.tv_title);
        tv_publishedAt = findViewById(R.id.tv_publishedAt);
        tv_content = findViewById(R.id.tv_content);
        tv_scroll = findViewById(R.id.tv_scrollText);
        iv_next.setVisibility(ImageView.INVISIBLE);
        iv_prev.setVisibility(ImageView.INVISIBLE);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(ProgressBar.INVISIBLE);

        if (isConnected()) {
            Log.d(TAG,"Connected");

            btn_Select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG,"On click on select");
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Select a Category!")
                            .setItems(category, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.d(TAG, "Clicked on: " + category[which]);
                                    tv_category.setText(category[which]);
                                    String keyword = category[which];
                                    progressBar.setVisibility(ProgressBar.VISIBLE);
                                    new GetJSONData().execute(category[which]);
                                    iv_next.setVisibility(ImageView.VISIBLE);
                                    iv_prev.setVisibility(ImageView.VISIBLE);
                                }
                            });
                    builder.create().show();

                }
            });

            iv_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressBar.setVisibility(ProgressBar.VISIBLE);
                    if(curr==size-1){
                        curr = 0;
                    }
                    else {
                        curr = curr +1;
                    }
                    Log.d(TAG,"Next for no:  " + curr);
                    Log.d(TAG,"Article" + news_global.get(curr).toString());

                    String title = news_global.get(curr).getTitle();
                    if(title.equals("null")){
                        tv_title.setText("");
                    }else {
                        tv_title.setText(title);
                    }

                    String publishedAt = news_global.get(curr).getPublishedAt();

                    if(publishedAt.equals("null")){
                        tv_publishedAt.setText("");
                    }else {
                        tv_publishedAt.setText(publishedAt);
                    }
                    //Log.d(TAG,"content: " + news_global.get(curr).getDescription());

                    String content = news_global.get(curr).getDescription();
                    if(content.equals("null")){
                        tv_content.setText("");
                        Toast.makeText(MainActivity.this, "No  Content Found", Toast.LENGTH_SHORT).show();
                    }else {
                        tv_content.setText(content.toString());
                    }

                    int scroll = curr+1;
                    tv_scroll.setText(Integer.toString(scroll) + " out of " + size);

                    String urlToImage = news_global.get(curr).getImageURL();
                    Log.d(TAG,"URL = " + urlToImage);
                    iv_image = findViewById(R.id.iv_image);
                    if(urlToImage.equals("null")){
                        iv_image.setImageDrawable(null);
                        Toast.makeText(MainActivity.this, "No Images Found", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Picasso.get().load(urlToImage).into(iv_image);
                    }

                    if(title.equals("null") && publishedAt.equals("null") && urlToImage.equals("null") && content.equals("null")){
                        Toast.makeText(MainActivity.this, "No News Found", Toast.LENGTH_SHORT).show();
                    }


                    progressBar.setVisibility(ProgressBar.INVISIBLE);

                }
            });

            iv_prev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    progressBar.setVisibility(ProgressBar.VISIBLE);
                    if(curr==0){

                        curr = size-1;
                    }
                    else {

                        curr = curr - 1;
                    }
                    Log.d(TAG,"Next for no:  " + curr);
                    Log.d(TAG,"Article" + news_global.get(curr).toString());
                    String title = news_global.get(curr).getTitle();
                    if(title.equals("null")){
                        tv_title.setText("");
                    }else {
                        tv_title.setText(title);
                    }

                    String publishedAt = news_global.get(curr).getPublishedAt();

                    if(publishedAt.equals("null")){
                        tv_publishedAt.setText("");
                    }else {
                        tv_publishedAt.setText(publishedAt);
                    }
                    String content = news_global.get(curr).getDescription();
                    if(content.equals("null")){
                        tv_content.setText("");
                        Toast.makeText(MainActivity.this, "No  Content Found", Toast.LENGTH_SHORT).show();
                    }else {
                        tv_content.setText(content.toString());
                    }

                    int scroll = curr+1;
                    tv_scroll.setText(Integer.toString(scroll) + " out of " + size);

                    String urlToImage = news_global.get(curr).getImageURL();
                    Log.d(TAG,"URL = " + urlToImage);
                    iv_image = findViewById(R.id.iv_image);
                    if(urlToImage.equals("null")){
                        iv_image.setImageDrawable(null);
                        Toast.makeText(MainActivity.this, "No Images Found", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Picasso.get().load(urlToImage).into(iv_image);
                    }
                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                }
            });


        }
        else{
            Log.d(TAG,"Not connected");
            Toast.makeText(MainActivity.this, "Not Connected", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !((NetworkInfo) networkInfo).isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }

    class GetJSONData extends AsyncTask<String,Void, ArrayList<News>>{
        @Override
        protected ArrayList<News> doInBackground(String... strings) {

            HttpURLConnection connection = null;
            ArrayList<News> news = new ArrayList<>();
            String category = strings[0];
            try {
                String url = baseURL + "?"
                        + "apiKey=" + getResources().getString(R.string.news_key)
                        + "&country=us&" + "category=" + URLEncoder.encode(category,"UTF-8");

                URL urlB = new URL(url);

                connection = (HttpURLConnection) urlB.openConnection();
                connection.connect();


                if(connection.getResponseCode()==HttpURLConnection.HTTP_OK) {
                    String json = IOUtils.toString(connection.getInputStream(), "UTF8");

                    JSONObject root = new JSONObject(json);
                    JSONArray articles = root.getJSONArray("articles");
                    for (int i = 0; i < articles.length(); i++) {
                        JSONObject articleJson = articles.getJSONObject(i);
                        News newsItem = new News();
                        newsItem.title = articleJson.getString("title");
                        newsItem.imageURL = articleJson.getString("urlToImage");
                        newsItem.description = articleJson.getString("description");
                        newsItem.publishedAt = articleJson.getString("publishedAt");
                        news.add(newsItem);
                    }
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return news;
        }

        @Override
        protected void onPostExecute(ArrayList<News> news) {
            TextView tv_title = findViewById(R.id.tv_title);
            TextView tv_publishedAt = findViewById(R.id.tv_publishedAt);
            TextView tv_content = findViewById(R.id.tv_content);
            TextView tv_scroll = findViewById(R.id.tv_scrollText);
            curr = 0;
            size = news.size();
            news_global = news;
            if(news.size()>0){
                //for(int i=0;i<size;i++) {
                    curr = 0;
                    Log.d(TAG,"Article" + news.get(0).toString());
                    String title = news.get(0).getTitle();
                    tv_title.setText(title.toString());
                    String publishedAt = news.get(0).getPublishedAt();
                    tv_publishedAt.setText(publishedAt.toString());
                    String content = news.get(0).getDescription();
                    tv_content.setText(content.toString());
                    int scroll = 0+1;
                    tv_scroll.setText(Integer.toString(scroll) + " out of " + size);

                    String urlToImage = news.get(0).getImageURL();
                    ImageView iv_image = findViewById(R.id.iv_image);
                    Picasso.get().load(urlToImage).into(iv_image);
                    iv_next.setVisibility(ImageView.VISIBLE);
                    iv_prev.setVisibility(ImageView.VISIBLE);
                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                //}
            }


            super.onPostExecute(news);
        }
    }
}
