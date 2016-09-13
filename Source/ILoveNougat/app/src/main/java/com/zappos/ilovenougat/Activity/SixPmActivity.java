package com.zappos.ilovenougat.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zappos.ilovenougat.Model.SixPmproduct;
import com.zappos.ilovenougat.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SixPmActivity extends AppCompatActivity {
    TextView productText;
    TextView brandText;
    TextView originalSixPrice, offSix, priceSix, priceZappos,notification,originalText,offSixText,priceSixText, priceZText;
    ImageView v;
    SixPmproduct z = null;
    String product, zappoprice;
    ArrayList<SixPmproduct> sixPmproducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_six_pm);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Intent i = getIntent();
        product = i.getStringExtra("product");
        zappoprice = i.getStringExtra("price");
        sixPmproducts = new ArrayList<>();
        productText = (TextView) findViewById(R.id.productText);
        brandText = (TextView) findViewById(R.id.brandText);
        originalSixPrice = (TextView) findViewById(R.id.originalSixPrice);
        offSix = (TextView) findViewById(R.id.offSix);
        priceSix = (TextView) findViewById(R.id.priceSix);
        priceZappos = (TextView) findViewById(R.id.priceZappos);
        notification = (TextView) findViewById(R.id.notification);
        originalText = (TextView) findViewById(R.id.originalSixPriceText);
        priceSixText = (TextView) findViewById(R.id.priceSixText);
        offSixText = (TextView) findViewById(R.id.offSixText);
        priceZText = (TextView) findViewById(R.id.priceZapposText);
        v = (ImageView) findViewById(R.id.imageView2);
        brandText.setText(product);
        findProduct(product);

    }

    public void findProduct(String s) {
        new AsyncHttpTask().execute(s);
//        updateList();

    }

    public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPreExecute() {
            setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected Integer doInBackground(String... params) {
            InputStream inputStream = null;
            Integer result = 0;
            HttpURLConnection urlConnection = null;

            try {
                /* forming th java.net.URL object */
                String search = params[0];
                URL url = new URL("https://api.6pm.com/Search?term=" + search + "&key=524f01b7e2906210f7bb61dcbe1bfea26eb722eb");

                urlConnection = (HttpURLConnection) url.openConnection();

                /* for Get request */
                urlConnection.setRequestMethod("GET");

                int statusCode = urlConnection.getResponseCode();

                /* 200 represents HTTP OK */
                if (statusCode == 200) {

                    BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        response.append(line);
                    }

                    parseResult(response.toString());
                    result = 1; // Successful
                } else {
                    result = 0; //"Failed to fetch data!";
                }

            } catch (Exception e) {
                Log.d("hiiii", e.getLocalizedMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            setProgressBarIndeterminateVisibility(false);

            /* Download complete. Lets update UI */
            if (result == 1) {
               updateList();
            } else {
                Log.e("hiiii", "Failed to fetch data!");
            }
        }
    }

    private void parseResult(String result) {

        final JSONObject jsonResult;
        try {
            jsonResult = new JSONObject(result);
            JSONArray itemsJson = jsonResult.getJSONArray("results");
            if (itemsJson.length() != 0) {
                for (int i = 0; i < itemsJson.length(); i++) {
                    JSONObject j = (JSONObject) itemsJson.get(i);
                    z = new SixPmproduct();
                    z.setBrandName(j.getString("brandName"));
                    z.setImageURL(j.getString("thumbnailImageUrl"));
                    z.setProductName(j.getString("productName"));
                    z.setOff(j.getString("percentOff"));
                    z.setOriginalPrice(j.getString("originalPrice"));
                    z.setPrice(j.getString("price"));
                    // sixPmproducts.add(z);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void updateList() {
        if(z != null && (z.getProductName().equals(product))) {
            productText.setText(z.getProductName());
            brandText.setText(z.getBrandName());
            Picasso.with(getApplicationContext()).load(z.getImageURL()).resize(200, 300).into(v);
            originalSixPrice.setText(z.getOriginalPrice());
            offSix.setText(z.getOff());
            priceSix.setText(z.getPrice());
            priceZappos.setText(zappoprice);
            String sixPriceString = z.getPrice().toString();
            float sixp = Float.parseFloat(sixPriceString.substring(1));
            float zappos = Float.parseFloat(zappoprice.substring(1));
            if (sixp <= zappos) {
                notification.setText("You are at right place. You will save " + (zappos - sixp) + "$");
            } else
                notification.setText("It is good to save money by buying this from Zappos");
        }else
        {
            productText.setText("No Matching products available");
            brandText.setVisibility(View.INVISIBLE);
            v.setVisibility(View.INVISIBLE);
            originalSixPrice.setVisibility(View.INVISIBLE);
            offSix.setVisibility(View.INVISIBLE);
            priceSix.setVisibility(View.INVISIBLE);
            priceZappos.setVisibility(View.INVISIBLE);
            originalText.setVisibility(View.INVISIBLE);
            priceSixText.setVisibility(View.INVISIBLE);
            offSixText.setVisibility(View.INVISIBLE);
            priceZText.setVisibility(View.INVISIBLE);
            notification.setText("Zappos is always best");
        }
    }

}
