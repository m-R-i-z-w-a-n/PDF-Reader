package com.hexflix.pdfreader.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hexflix.pdfreader.DownloadsActivity;
import com.hexflix.pdfreader.model.Upload;
import com.hexflix.pdfreader.R;
import com.hexflix.pdfreader.PDFViewerActivity;

import java.io.File;
import java.util.List;

public class UploadsAdapter extends RecyclerView.Adapter<UploadsAdapter.UploadsViewHolder> {
    private final Context context;
    private final List<Upload> uploads;

    public UploadsAdapter(Context context, List<Upload> uploads) {
        this.context = context;
        this.uploads = uploads;
    }

    @NonNull
    @Override
    public UploadsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UploadsViewHolder(LayoutInflater.from(context).inflate(R.layout.item_pdf_upload, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UploadsViewHolder holder, int position) {
        Upload currentUpload = uploads.get(position);
        holder.txtName.setText(currentUpload.getName());
        holder.imageView.setImageResource(R.drawable.pdf_icon);
        holder.btnDownload.setImageResource(R.drawable.ic_download);

        holder.pdfFileView.setOnClickListener(view -> {
            context.startActivity(new Intent(context, PDFViewerActivity.class)
                    .putExtra("url", currentUpload.getPdfURL())
                    .putExtra("name", currentUpload.getName()));
        });

        holder.btnDownload.setOnClickListener(view -> {
            Toast.makeText(context, "Downloading " + currentUpload.getName(), Toast.LENGTH_SHORT).show();
            downloadFile(currentUpload, holder);

//            Dexter.withContext(context)
//                    .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                    .withListener(new PermissionListener() {
//                        @Override
//                        public void onPermissionGranted(PermissionGrantedResponse response) {
//                            downloadFile(currentUpload, holder);
//                        }
//
//                        @Override
//                        public void onPermissionDenied(PermissionDeniedResponse response) {
//                            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show();
//                        }
//
//                        @Override
//                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
//                            token.continuePermissionRequest();
//                        }
//                    }).check();

        });
    }

    @Override
    public int getItemCount() {
        return uploads.size();
    }

    static class UploadsViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtName;
        private final ImageView imageView;
        private final ImageButton btnDownload;
        private final ProgressBar progressBarDownload;
        private final CardView pdfFileView;

        public UploadsViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.item_edt_name);
            pdfFileView = itemView.findViewById(R.id.pdf_file_view);
            imageView = itemView.findViewById(R.id.image_view);
            btnDownload = itemView.findViewById(R.id.btn_download);
            progressBarDownload = itemView.findViewById(R.id.progress_bar_download);
        }

    }

    private void downloadFile(Upload upload, UploadsViewHolder holder) {
        boolean isSuccessful = false;
        File downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File pdfReaderDirectory = new File(downloadsDirectory, "PDF-Reader");
        if (!pdfReaderDirectory.exists())
            isSuccessful = pdfReaderDirectory.mkdir();

        if (isSuccessful || pdfReaderDirectory.exists()) {
            File currentFile = new File(pdfReaderDirectory, upload.getName() + ".pdf");

            StorageReference pdfReference = FirebaseStorage.getInstance().getReferenceFromUrl(upload.getPdfURL());
            pdfReference.getFile(currentFile)
                    .addOnSuccessListener(taskSnapshot -> {
                        new Handler().postDelayed(() -> holder.progressBarDownload.setProgress(0), 500);
                        DownloadsActivity.downloadsAdapter.notifyDataSetChanged();
                        Toast.makeText(context, "Download successful", Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(snapshot -> {
                        double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                        holder.progressBarDownload.setProgress((int) progress);
                    })
                    .addOnFailureListener(exception -> {
                        Toast.makeText(context, exception.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else
            Toast.makeText(context, "Error while creating directory!", Toast.LENGTH_SHORT).show();
    }
}
