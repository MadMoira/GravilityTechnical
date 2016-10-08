package com.example.mirag.gravilitytechnical;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Set;

public class AppListActivity extends AppCompatActivity {

    private String listQuery = "SELECT * FROM apps WHERE category = '%s'";
    private HashMap<String, String> apps = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);

        final DatabaseManager appHelper = new DatabaseManager(getApplicationContext());
        final SQLiteDatabase db = appHelper.getWritableDatabase();
        final LinearLayout layout = (LinearLayout) findViewById(R.id.activity_app_list);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String categoryName = extras.getString("EXTRA_CATEGORY_NAME");
            TextView tv = (TextView) findViewById(R.id.category_text_view);
            tv.setText(categoryName);

            Cursor apps_cursor = db.rawQuery(
                    String.format(
                            listQuery,
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
        }

        Set<String> appsKeys = apps.keySet();

        for(String key : appsKeys) {
            Button appButton = new Button(this.getApplicationContext());
            appButton.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
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
