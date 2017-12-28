package com.jtv.sample.jtvlogin;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class searchActivity extends AppCompatActivity {

    String api = config.Search_api;
  //  String api="http://192.168.1.163/JTV/web/search.php?";
    EditText ed1;
    private RecyclerView mRVFishPrice;
    private AdapterSearch mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //to change the color of the actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) findViewById(R.id.search);
        searchView.setQueryHint("Search");
        Log.e("sv", searchView.toString());
        if (null != searchView) {
            searchView.setSearchableInfo(searchManager
                    .getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(false);
        }

        //on typing any query in the search area
        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String newText) {
                if(newText.length()==0){
                    new AsyncFetch().execute("\"\"");
                }
                else
                new AsyncFetch().execute(newText);
                return true;
            }

            public boolean onQueryTextSubmit(String query) {
                new AsyncFetch().execute(query);
                searchView.clearFocus();
                //Here u can get the value "query" which is entered in the search box.
                return true;
            }

        };
        searchView.setOnQueryTextListener(queryTextListener);
    }

    //for the menu items
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks
        int id = item.getItemId();

        switch (id) {
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
                break;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }


    private class AsyncFetch extends AsyncTask<String, String, String> {

        URL url = null;

        @Override
        protected String doInBackground(String... params) {
            try {
                String searchValue;
                searchValue = URLEncoder.encode(params[0], "UTF-8");
                String passURL = api+"v="+searchValue;
                Log.e("sv", searchValue);
                JSONParser jp= new JSONParser();
                String result = jp.getJSONArrayfromURL(passURL);
                return result;
            } catch (IOException e) {
                e.printStackTrace();
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String result) {

            //this method will be running on UI thread
            List<DataFish> data=new ArrayList<>();
            try {
                Log.e("finalresult", result);
                if(result.equals("[]")){
                  //  Toast.makeText(getApplicationContext(), "No such video.", Toast.LENGTH_SHORT).show();
                }
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
                mRVFishPrice = (RecyclerView)findViewById(R.id.fishPriceList);
                mAdapter = new AdapterSearch(searchActivity.this, data);
                mRVFishPrice.setAdapter(mAdapter);
                mRVFishPrice.setLayoutManager(new LinearLayoutManager(searchActivity.this));
            } catch (JSONException e) {
                Log.e("sa", e.toString());
                Toast.makeText(searchActivity.this, e.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }
}