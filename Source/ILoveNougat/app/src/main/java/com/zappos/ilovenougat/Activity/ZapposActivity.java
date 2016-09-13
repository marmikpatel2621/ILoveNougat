package com.zappos.ilovenougat.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.zappos.ilovenougat.Adapter.ZapposAdapter;
import com.zappos.ilovenougat.Model.ZapposProduct;
import com.zappos.ilovenougat.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ZapposActivity extends AppCompatActivity {

    private static ZapposAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private ArrayList<ZapposProduct> productList;
    private static TextView searchText;
    public static View.OnClickListener myOnClickListener;
   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zappos);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        searchText =(TextView)findViewById(R.id.searchText);
        productList = new ArrayList<>();
        myOnClickListener = new MyOnClickListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
       adapter = new ZapposAdapter(getApplicationContext(),productList);
       recyclerView.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
   }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_zappos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void findItem(View v)
    {
        String search = searchText.getText().toString();
       new AsyncHttpTask().execute(search);
    }


    private static class MyOnClickListener implements View.OnClickListener {

        private final Context context;

        private MyOnClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            removeItem(v);
        }

        private void removeItem(View v) {
            int selectedItemPosition = recyclerView.getChildPosition(v);
            RecyclerView.ViewHolder viewHolder
                    = recyclerView.findViewHolderForPosition(selectedItemPosition);
            TextView textViewName
                    = (TextView) viewHolder.itemView.findViewById(R.id.textViewName);
            String selectedProductName = (String) textViewName.getText();
            TextView price = (TextView) viewHolder.itemView.findViewById(R.id.zappoprice);
            String p = (String)price.getText();
            Intent i = new Intent(v.getContext(),SixPmActivity.class);
            i.putExtra("product",selectedProductName);
            i.putExtra("price",p);
            v.getContext().startActivity(i);
        }
    }
    public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPreExecute() {
            setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected Integer doInBackground(String... params) {
            fetchList(params[0]);
            return 1; //"Failed to fetch data!";
        }

        @Override
        protected void onPostExecute(Integer result) {

            setProgressBarIndeterminateVisibility(false);

            /* Download complete. Lets update UI */
            if (result == 1) {
                adapter.notifyDataSetChanged();
            } else {
                Log.e("hiiii", "Failed to fetch data!");
            }
        }
    }
    private void fetchList(String search) {
        String url = "https://api.zappos.com/Search?term=" + search + "&key=b743e26728e16b81da139182bb2094357c31d331";
        OkHttpClient httpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        httpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final JSONObject jsonResult;
                final String result = response.body().string();
                productList.clear();
                try {
                    jsonResult = new JSONObject(result);
                    JSONArray itemsJson = jsonResult.getJSONArray("results");
                    if (itemsJson.length() != 0) {
                        for (int i = 0; i < itemsJson.length(); i++) {
                            JSONObject j = (JSONObject) itemsJson.get(i);
                            ZapposProduct z = new ZapposProduct();
                            z.setBrandName(j.getString("brandName"));
                            z.setImageURL(j.getString("thumbnailImageUrl"));
                            z.setProductName(j.getString("productName"));
                            z.setOff(j.getString("percentOff"));
                            z.setOriginalPrice(j.getString("originalPrice"));
                            z.setPrice(j.getString("price"));
                            Log.e("hiiii", z.getProductName());
                            productList.add(z);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
