package com.jtv.sample.jtvlogin;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import android.util.Log;
import android.widget.HorizontalScrollView;
import android.widget.ListView;

public class HomeActivity extends AppCompatActivity {
    // final String api="http://192.168.1.163/JTV/upload/api/";
    final String api = config.Videodetails_api;
    private RecyclerView mRVbigimage;
    private RecyclerView mRVvideolist;
    private AdapterFish mAdapter;
    private AdapterBanner mAdapter2;
    public int lastvitem=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_fish);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        new AsyncFetch().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks
        int id = item.getItemId();

        switch (id) {
            case R.id.search_item:
            {
                Intent j = new Intent(this, searchActivity.class);
                this.startActivity(j);
                break;
            }

            case R.id.account:
            {
                Intent j=new Intent(this,ProfileActivity.class);
                this.startActivity(j);
                break;
            }
            case R.id.logout:
            {
                Intent i=new Intent(this,LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                this.finish();

            }
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private class AsyncFetch extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(HomeActivity.this);
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... params) {
            JSONParser jp =new JSONParser();
            String result = jp.getJSONArrayfromURL(api);
            Log.e("result", result);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {

            //this method will be running on UI thread
            pdLoading.dismiss();
            final List<DataFish> data=new ArrayList<>();

            try {

                JSONArray jArray = new JSONArray(result);

                // Extract data from json and store into ArrayList as class objects
                for(int i=0;i<jArray.length();i++){
                    JSONObject json_data = jArray.getJSONObject(i);
                    DataFish fishData = new DataFish();
                    fishData.fishImage= json_data.getString("imgurl");
                    fishData.fishName= json_data.getString("mname");
                    fishData.catName= json_data.getString("assetid");
                    fishData.videourl= json_data.getString("videourl");
                    data.add(fishData);
                }

                // Setup and Handover data to recyclerview
                mRVbigimage = (RecyclerView)findViewById(R.id.bigimage);
                mRVvideolist = (RecyclerView)findViewById(R.id.videolist);
                mAdapter = new AdapterFish(HomeActivity.this, data);
                mAdapter2 = new AdapterBanner(HomeActivity.this, data);
                mRVbigimage.setAdapter(mAdapter2);
                mRVvideolist.setAdapter(mAdapter);

                //Layoutmanager for video list
                LinearLayoutManager MyLayoutManager = new LinearLayoutManager(HomeActivity.this);
                MyLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

                //Layoutmanager for the banner
                LinearLayoutManager MyLayoutManager2 = new LinearLayoutManager(HomeActivity.this);
                MyLayoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);

                mRVvideolist.setLayoutManager(MyLayoutManager);
                mRVbigimage.setLayoutManager(MyLayoutManager2);

            } catch (JSONException e) {
                Toast.makeText(HomeActivity.this, e.toString(), Toast.LENGTH_LONG).show();
            }

            final Handler handler = new Handler();
            final LinearLayoutManager layoutManager = ((LinearLayoutManager) mRVbigimage.getLayoutManager());

            mRVbigimage
                    .addOnScrollListener(new RecyclerView.OnScrollListener() {

                        @Override
                        public void onScrolled(RecyclerView recyclerView,
                                               int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);
                            int lastVisibleItem = layoutManager
                                    .findLastVisibleItemPosition();
                            int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                            if(dx>0)
                                mRVbigimage.smoothScrollToPosition(lastVisibleItem);
                            else if(dx<0)
                                mRVbigimage.smoothScrollToPosition(firstVisibleItem);
                            lastvitem=lastVisibleItem;
                        }

                    });

            //programmatically scroll the item of the banner after every 6 seconds
            final Runnable runnable = new Runnable() {
                public void run() {
                    int newpos= lastvitem;

                    // need to do tasks on the UI thread
                    mRVbigimage.smoothScrollToPosition(newpos +1);
                    if (lastvitem++ < mAdapter2.getItemCount())
                        handler.postDelayed(this, 6000);
                    else {
                        lastvitem=0;
                        mRVbigimage.scrollToPosition(lastvitem);
                        run();
                    }
                }
            };
            handler.post(runnable);
        }
    }

}