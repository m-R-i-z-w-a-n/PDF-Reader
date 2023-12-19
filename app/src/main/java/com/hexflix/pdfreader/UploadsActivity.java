package com.hexflix.pdfreader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hexflix.pdfreader.adapters.UploadsAdapter;
import com.hexflix.pdfreader.model.Upload;

import java.util.ArrayList;
import java.util.List;

public class UploadsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private UploadsAdapter uploadsAdapter;
    private List<Upload> uploads;
    private ProgressBar progressBarCircular;
    private DatabaseReference databaseReference;
    private ValueEventListener listener;
    private final Context CONTEXT = UploadsActivity.this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploads);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.DKGRAY));
        getSupportActionBar().setTitle("Files in cloud");

        uploads = new ArrayList<>();
        initializeRecyclerView();

        progressBarCircular = findViewById(R.id.progress_bar_circular);
        databaseReference = FirebaseDatabase.getInstance().getReference("uploads");

        retrieveData();
    }

    private void initializeRecyclerView() {
        recyclerView = findViewById(R.id.pdf_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        uploadsAdapter = new UploadsAdapter(CONTEXT, uploads);
        recyclerView.setAdapter(uploadsAdapter);
    }

    private void retrieveData() {
        listener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                uploads.clear();

                if (snapshot.exists()) {
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        Upload upload = childSnapshot.getValue(Upload.class);
                        assert upload != null;
                        upload.setKey(childSnapshot.getKey());
                        uploads.add(upload);
                    }
                    uploadsAdapter.notifyDataSetChanged();
                    progressBarCircular.setVisibility(View.INVISIBLE);
                } else
                    Toast.makeText(CONTEXT, "No Data Exists!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CONTEXT, error.getMessage(), Toast.LENGTH_SHORT).show();
                progressBarCircular.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        menu.add("Upload new file").setIcon(R.drawable.baseline_add_24).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add("Downloads").setIcon(R.drawable.baseline_download_24).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getTitle().toString()) {
            case "Downloads":
                startActivity(new Intent(CONTEXT, DownloadsActivity.class));
//                Dexter.withContext(CONTEXT)
//                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
//                        .withListener(new PermissionListener() {
//                            @Override
//                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
//                                startActivity(new Intent(CONTEXT, DownloadsActivity.class));
//                            }
//
//                            @Override
//                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
//                                Toast.makeText(CONTEXT, "Storage permission is necessary!", Toast.LENGTH_SHORT).show();
//                            }
//
//                            @Override
//                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
//                                permissionToken.continuePermissionRequest();
//                            }
//                        }).check();
                break;

            case "Upload new file":
                startActivity(new Intent(CONTEXT, MainActivity.class));
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(listener);
    }
}