package com.example.mirag.gravilitytechnical;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AppDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_detail);

        final DatabaseManager appHelper = DatabaseManager.getInstance(getApplicationContext());
        final SQLiteDatabase db = appHelper.getWritableDatabase();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String appId = extras.getString("EXTRA_APP_ID");
            LoadAppData(db, appId);
        }
    }

    @SuppressLint("SimpleDateFormat")
    private void LoadAppData(SQLiteDatabase db, String appId) {
        Cursor app_cursor = db.rawQuery(
                String.format(
                        AppEntryManager.queryAppByID,
                        appId
                ),
                null
        );

        int nameColumnIndex = app_cursor.getColumnIndex(AppEntryManager.AppEntry.COLUMN_NAME_NAME);
        int imageColumnIndex = app_cursor.getColumnIndex(AppEntryManager.AppEntry.COLUMN_NAME_IMAGE_3);
        int summaryColumnIndex = app_cursor.getColumnIndex(AppEntryManager.AppEntry.COLUMN_NAME_SUMMARY);
        int priceColumnIndex = app_cursor.getColumnIndex(AppEntryManager.AppEntry.COLUMN_NAME_PRICE_VALUE);
        int currencyColumnIndex = app_cursor.getColumnIndex(AppEntryManager.AppEntry.COLUMN_NAME_PRICE_CURRENCY);
        int releaseDateColumnIndex = app_cursor.getColumnIndex(AppEntryManager.AppEntry.COLUMN_NAME_RELEASE_DATE);

        while(app_cursor.moveToNext()) {

            String name = app_cursor.getString(nameColumnIndex);
            String image = app_cursor.getString(imageColumnIndex);
            String summary = app_cursor.getString(summaryColumnIndex);
            String price = app_cursor.getString(priceColumnIndex);
            String currency = app_cursor.getString(currencyColumnIndex) + " ";
            String releaseDate = app_cursor.getString(releaseDateColumnIndex);
            String formattedDate = "";
            try {
                Date releaseDateDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(releaseDate);
                formattedDate = new SimpleDateFormat("dd/MM/yyyy").format(releaseDateDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            ((TextView) findViewById(R.id.title_text_view)).setText(name);
            ((TextView) findViewById(R.id.release_date_text_view)).setText(formattedDate);
            ((TextView) findViewById(R.id.summary_text_view)).setText(summary);
            ((TextView) findViewById(R.id.price_value_text_view)).setText(price);
            ((TextView) findViewById(R.id.price_currency_text_view)).setText(currency);

            // If There is no connection, don't try to download the image
            if(NetworkReceiver.isConnected(getApplicationContext())) {
                new DownloadImageTask((ImageView) findViewById(R.id.image1_image_view))
                        .execute(image);
            } else {
                Toast.makeText(getApplicationContext(), "No internet connection, try again later", Toast.LENGTH_LONG).show();
            }
        }

        app_cursor.close();
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urlDisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urlDisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
