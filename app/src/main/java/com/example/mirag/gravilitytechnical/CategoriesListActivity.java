package com.example.mirag.gravilitytechnical;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.NetworkOnMainThreadException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CategoriesListActivity extends AppCompatActivity {

    String urlService = "https://itunes.apple.com/us/rss/topfreeapplications/limit=20/json";

    ArrayList<String> categories = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories_list);

        final DatabaseManager appHelper = new DatabaseManager(getApplicationContext());
        final SQLiteDatabase db = appHelper.getWritableDatabase();

        boolean isConnected = NetworkReceiver.isConnected(getApplicationContext());
        if(isConnected){
            getData(db);
        } else {
            Toast.makeText(getApplicationContext(), "No internet connection, loading database categories", Toast.LENGTH_LONG).show();
            showCategories(db);
        }
    }

    private void showCategories(final SQLiteDatabase db) {

        final LinearLayout layout = (LinearLayout) findViewById(R.id.activity_categories_list);

        Cursor categories_cursor = db.rawQuery(
                String.format(
                        AppEntryManager.queryAllCategories,
                        AppEntryManager.AppEntry.COLUMN_NAME_CATEGORY
                ),
                null
        );

        int columnIndex = categories_cursor.getColumnIndex(AppEntryManager.AppEntry.COLUMN_NAME_CATEGORY);

        while(categories_cursor.moveToNext()) {
            String category = categories_cursor.getString(columnIndex);
            if(!categories.contains(category)){
                categories.add(category);
            }
        }

        categories_cursor.close();

        for(String object: categories) {
            Button categoryButton = new Button(getApplicationContext());
            categoryButton.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            categoryButton.setText(object);
            categoryButton.setTag(object);
            categoryButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view){
                    Intent categoryIntent = new Intent(CategoriesListActivity.this, AppListActivity.class);
                    categoryIntent.putExtra("EXTRA_CATEGORY_NAME", view.getTag().toString());
                    startActivity(categoryIntent);
                }
            });
            layout.addView(categoryButton);
        }
    }

    private void getData(final SQLiteDatabase db) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlService,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject reader = new JSONObject(response);
                            JSONObject feed = reader.getJSONObject("feed");
                            JSONArray apps = feed.getJSONArray("entry");

                            for(int a = 0; a < apps.length(); a++){

                                JSONObject currentEntry = apps.optJSONObject(a);

                                // Get all apps that have the same app id, it should only be one anyway
                                Cursor c = db.rawQuery(
                                        String.format(
                                                AppEntryManager.queryGeneralSingleValue,
                                                AppEntryManager.AppEntry.COLUMN_NAME_APP_ID,
                                                currentEntry.getJSONObject("id").getJSONObject("attributes").getInt("im:id")
                                        ),
                                        null);

                                // Avoid saving repeated apps
                                if (c.getCount() != 0) {
                                    c.close();
                                    continue;
                                }

                                c.close();

                                ContentValues values = new ContentValues();
                                values.put(AppEntryManager.AppEntry.COLUMN_NAME_APP_ID, currentEntry.getJSONObject("id").getJSONObject("attributes").getInt("im:id"));
                                values.put(AppEntryManager.AppEntry.COLUMN_NAME_NAME, currentEntry.getJSONObject("im:name").getString("label"));
                                values.put(AppEntryManager.AppEntry.COLUMN_NAME_SUMMARY, currentEntry.getJSONObject("summary").getString("label"));
                                values.put(AppEntryManager.AppEntry.COLUMN_NAME_PRICE_VALUE, currentEntry.getJSONObject("im:price").getJSONObject("attributes").getDouble("amount"));
                                values.put(AppEntryManager.AppEntry.COLUMN_NAME_PRICE_CURRENCY, currentEntry.getJSONObject("im:price").getJSONObject("attributes").getString("currency"));
                                values.put(AppEntryManager.AppEntry.COLUMN_NAME_TITLE, currentEntry.getJSONObject("title").getString("label"));
                                values.put(AppEntryManager.AppEntry.COLUMN_NAME_CATEGORY, currentEntry.getJSONObject("category").getJSONObject("attributes").getString("label"));
                                values.put(AppEntryManager.AppEntry.COLUMN_NAME_RELEASE_DATE, currentEntry.getJSONObject("im:releaseDate").getString("label"));
                                values.put(AppEntryManager.AppEntry.COLUMN_NAME_IMAGE_1, currentEntry.getJSONArray("im:image").optJSONObject(0).getString("label"));
                                values.put(AppEntryManager.AppEntry.COLUMN_NAME_IMAGE_2, currentEntry.getJSONArray("im:image").optJSONObject(1).getString("label"));
                                values.put(AppEntryManager.AppEntry.COLUMN_NAME_IMAGE_3, currentEntry.getJSONArray("im:image").optJSONObject(2).getString("label"));
                                db.insert(AppEntryManager.AppEntry.TABLE_NAME, null, values);

                                if(!categories.contains(currentEntry.getJSONObject("category").getJSONObject("attributes").getString("label"))){
                                    categories.add(currentEntry.getJSONObject("category").getJSONObject("attributes").getString("label"));
                                }
                                showCategories(db);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "There was an error with the request, getting local catgories", Toast.LENGTH_LONG).show();
                        showCategories(db);
                    }
        });
        queue.add(stringRequest);
    }

}
