package com.example.mirag.gravilitytechnical;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class AppListActivity extends AppCompatActivity {

    private String listQuery = "SELECT * FROM apps WHERE category = '%s'";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);

        final DatabaseManager appHelper = new DatabaseManager(getApplicationContext());
        final SQLiteDatabase db = appHelper.getWritableDatabase();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String categoryName = extras.getString("EXTRA_CATEGORY_NAME");
            TextView tv = (TextView) findViewById(R.id.category_text_view);
            tv.setText(categoryName);

            Cursor c = db.rawQuery(
                    String.format(
                            listQuery,
                            categoryName
                    ),
                    null);

            c.close();

        }

    }
}
