package com.hexflix.pdfreader;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class PDFViewerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.DKGRAY));

        String name;
        name = getIntent().getStringExtra("name");

        if (name == null)
            name = getIntent().getStringExtra("file_name");
        getSupportActionBar().setTitle(name);

        final String URL = getIntent().getStringExtra("url");
        if (URL != null) {
            new RetrievePDFStream(PDFViewerActivity.this).execute(URL);
            return;
        }

        final Uri uri = Uri.parse(getIntent().getStringExtra("file_path"));
        if (uri != null) {
            PDFView pdfView = findViewById(R.id.pdf_view);
            pdfView.fromUri(uri).load();
        }
    }

    private static class RetrievePDFStream extends AsyncTask<String, Void, InputStream> {
        private final WeakReference<PDFViewerActivity> activityReference;

        RetrievePDFStream(PDFViewerActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }
            } catch (IOException e) {
                return null;
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            PDFViewerActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;

            PDFView pdfView = activity.findViewById(R.id.pdf_view);
            pdfView.fromStream(inputStream).load();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}