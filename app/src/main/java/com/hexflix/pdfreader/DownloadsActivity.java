package com.hexflix.pdfreader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hexflix.pdfreader.adapters.DownloadsAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DownloadsActivity extends AppCompatActivity {
    private RecyclerView downloadsRV;
    public static DownloadsAdapter downloadsAdapter;
    private static List<File> downloadedFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloads);
        getSupportActionBar().setTitle("My Downloads");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.DKGRAY));

        downloadedFiles = new ArrayList<>();

        initializeRecyclerView();
        loadDownloadedFiles();

        if (downloadedFiles.isEmpty()) {
            downloadsRV.setVisibility(View.GONE);

            LinearLayout rootLayout = findViewById(R.id.root);

            TextView noDownloadsTextView = new TextView(getApplicationContext());
            noDownloadsTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            noDownloadsTextView.setText("No Downloads Available!");
            noDownloadsTextView.setTextColor(Color.BLACK);
            noDownloadsTextView.setTextSize(20);

            rootLayout.setGravity(Gravity.CENTER);
            rootLayout.addView(noDownloadsTextView);
        }
    }

    private void loadDownloadedFiles() {
        File downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File pdfReaderDirectory = new File(downloadsDirectory, "PDF-Reader");
        if (pdfReaderDirectory.exists()) {
            File[] files = pdfReaderDirectory.listFiles();
            for (File file : files)
                if (file.isFile())
                    downloadedFiles.add(file);
            downloadsAdapter.notifyDataSetChanged();
        }
    }

    private void initializeRecyclerView() {
        downloadsRV = findViewById(R.id.downloads_recycler_view);
        downloadsRV.setHasFixedSize(true);
        downloadsRV.setLayoutManager(new LinearLayoutManager(this));
        downloadsAdapter = new DownloadsAdapter(downloadedFiles);
        downloadsRV.setAdapter(downloadsAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}