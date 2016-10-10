package com.example.mirag.gravilitytechnical;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Set;

public class AppListActivity extends AppCompatActivity {

    private HashMap<String, String> apps = new HashMap<>();
    private String currentCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);

        final DatabaseManager appHelper = DatabaseManager.getInstance(getApplicationContext());
        final SQLiteDatabase db = appHelper.getWritableDatabase();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentCategory = extras.getString("EXTRA_CATEGORY_NAME");
            GetApps(db, currentCategory);
        }
    }

    private void GetApps(SQLiteDatabase db, String categoryName) {
        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);

        Cursor apps_cursor = db.rawQuery(
                String.format(
                        AppEntryManager.queryAppsByCategory,
                        categoryName
                ),
                null);

        int nameColumnIndex = apps_cursor.getColumnIndex(AppEntryManager.AppEntry.COLUMN_NAME_NAME);
        int appIdColumnIndex = apps_cursor.getColumnIndex(AppEntryManager.AppEntry.COLUMN_NAME_APP_ID);

        while(apps_cursor.moveToNext()) {
            String name = apps_cursor.getString(nameColumnIndex);
            int appID = apps_cursor.getInt(appIdColumnIndex);
            apps.put(String.valueOf(appID), name);
        }

        apps_cursor.close();

        if(tabletSize) {
            AddElementsToGrid();
        } else {
            AddElementsToList();
        }

    }

    private void AddElementsToGrid() {
        GridLayout layout = (GridLayout) findViewById(R.id.activity_app_list);
        GridLayout.LayoutParams layoutParams;

        Set<String> appsKeys = apps.keySet();
        int counter = 0;
        for(String key: appsKeys) {
            layoutParams = new GridLayout.LayoutParams();
            layoutParams.width = 200;
            layoutParams.height = 200;
            layoutParams.setGravity(Gravity.CENTER_VERTICAL);
            layoutParams.columnSpec = GridLayout.spec(counter);

            Button appButton = new Button(this.getApplicationContext());
            appButton.setLayoutParams(layoutParams);
            appButton.setText(apps.get(key));
            appButton.setTag(key);
            appButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view){
                    Intent categoryIntent = new Intent(AppListActivity.this, AppDetailActivity.class);
                    categoryIntent.putExtra("EXTRA_APP_ID", view.getTag().toString());
                    startActivity(categoryIntent);
                }
            });
            layout.addView(appButton);

            counter += 1;
            if (counter == 5) {
                counter = 0;
            }
        }
    }

    private void AddElementsToList() {

        TextView tv = (TextView) findViewById(R.id.category_text_view);
        tv.setText(currentCategory);

        LinearLayout layout = (LinearLayout) findViewById(R.id.activity_app_list);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        Set<String> appsKeys = apps.keySet();

        for(String key : appsKeys) {
            Button appButton = new Button(this.getApplicationContext());
            appButton.setLayoutParams(layoutParams);
            appButton.setText(apps.get(key));
            appButton.setTag(key);
            appButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view){
                    Intent categoryIntent = new Intent(AppListActivity.this, AppDetailActivity.class);
                    categoryIntent.putExtra("EXTRA_APP_ID", view.getTag().toString());
                    startActivity(categoryIntent);
                }
            });
            layout.addView(appButton);
        }

    }

}
