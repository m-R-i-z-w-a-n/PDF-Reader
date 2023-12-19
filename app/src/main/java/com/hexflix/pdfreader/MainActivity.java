package com.hexflix.pdfreader;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.hexflix.pdfreader.model.Upload;

public class MainActivity extends AppCompatActivity {
    private Button btnChooseFile, btnUploadFile;
    private EditText edtFileName;
    private ProgressBar progressBar;
    private Uri pdfUri;
        private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private StorageTask uploadTask;
    private final Context CONTEXT = MainActivity.this;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.DKGRAY));

        btnChooseFile = findViewById(R.id.btn_choose_file);
        btnUploadFile = findViewById(R.id.btn_upload_file);
        edtFileName = findViewById(R.id.edt_file_name);
        progressBar = findViewById(R.id.progress_bar_upload);

        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        databaseReference = FirebaseDatabase.getInstance().getReference("uploads");

        btnChooseFile.setOnClickListener(view -> {
            openFileChooser();
        });

        btnUploadFile.setOnClickListener(view -> {
            if (uploadTask != null && uploadTask.isInProgress()) {
                Toast.makeText(CONTEXT, "An upload is being carried out already!", Toast.LENGTH_SHORT).show();
                return;
            }
            uploadFile();
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent()
                .setType("application/pdf")
                .setAction(Intent.ACTION_GET_CONTENT);
        choosePDFActivityLauncher.launch(intent);

//        Dexter.withContext(CONTEXT)
//                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
//                .withListener(new PermissionListener() {
//                    @Override
//                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
//                        Intent intent = new Intent()
//                                .setType("application/pdf")
//                                .setAction(Intent.ACTION_GET_CONTENT);
//                        choosePDFActivityLauncher.launch(intent);
//                    }
//
//                    @Override
//                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
//                        Toast.makeText(CONTEXT, "Storage permission is necessary!", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
//                        permissionToken.continuePermissionRequest();
//                    }
//                }).check();
    }

    private final ActivityResultLauncher<Intent> choosePDFActivityLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null && data.getData() != null)
                        pdfUri = data.getData();
                }
            });

    private void uploadFile() {
        String fileName = edtFileName.getText().toString().trim();

        if (pdfUri == null) {
            Toast.makeText(CONTEXT, "Select a file to upload!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (fileName.equals("")) {
            Toast.makeText(CONTEXT, "Enter a file name to continue!", Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference fileReference = storageReference.child(fileName + ".pdf");
        uploadTask = fileReference.putFile(pdfUri)
                .addOnSuccessListener(taskSnapshot -> {
                    new Handler().postDelayed(() -> progressBar.setProgress(0), 500);

                    fileReference.getDownloadUrl().addOnSuccessListener( uri -> {
                        Upload upload = new Upload(fileName, uri.toString());

                        String uploadID = databaseReference.push().getKey();
                        databaseReference.child(uploadID).setValue(upload);

                        Toast.makeText(CONTEXT, "Uploaded Successfully.", Toast.LENGTH_LONG).show();
                        pdfUri = null;
                        edtFileName.setText("");
                        startActivity(new Intent(getApplicationContext(), UploadsActivity.class));
                        finish();
                    });
                })
                .addOnFailureListener(exception -> {
                    Toast.makeText(CONTEXT, exception.getMessage(), Toast.LENGTH_SHORT).show();
                })
                .addOnProgressListener(snapshot -> {
                    double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    progressBar.setProgress((int) progress);
                });
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        menu.add("Downloads").setIcon(R.drawable.baseline_download_24).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        startActivity(new Intent(CONTEXT, DownloadsActivity.class));

//        Dexter.withContext(CONTEXT)
//                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
//                .withListener(new PermissionListener() {
//                    @Override
//                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
//                        startActivity(new Intent(CONTEXT, DownloadsActivity.class));
//                    }
//
//                    @Override
//                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
//                        Toast.makeText(CONTEXT, "Storage permission is necessary!", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
//                        permissionToken.continuePermissionRequest();
//                    }
//                }).check();

        return super.onOptionsItemSelected(item);
    }
}