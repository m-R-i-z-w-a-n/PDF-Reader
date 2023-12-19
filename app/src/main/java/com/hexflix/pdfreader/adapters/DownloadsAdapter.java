package com.hexflix.pdfreader.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.hexflix.pdfreader.R;
import com.hexflix.pdfreader.PDFViewerActivity;

import java.io.File;
import java.util.List;

public class DownloadsAdapter extends RecyclerView.Adapter<DownloadsAdapter.DownloadsViewHolder> {
    private List<File> pdfFiles;

    public DownloadsAdapter(List<File> pdfFiles) {
        this.pdfFiles = pdfFiles;
    }

    @NonNull
    @Override
    public DownloadsAdapter.DownloadsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DownloadsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pdf_download, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DownloadsAdapter.DownloadsViewHolder holder, int position) {
        File currentFile = pdfFiles.get(position);
        holder.txtName.setText(currentFile.getName());
        holder.imageView.setImageResource(R.drawable.pdf_icon);

        holder.pdfFileView.setOnClickListener(view -> {
            Context context = view.getContext();

            Uri uri = Uri.fromFile(currentFile);

            Intent intent = new Intent(context, PDFViewerActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("file_path", uri.toString());
            bundle.putString("file_name", currentFile.getName().replace(".pdf", ""));
            intent.putExtras(bundle);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return pdfFiles.size();
    }

    static class DownloadsViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtName;
        private final ImageView imageView;
        private final CardView pdfFileView;

        public DownloadsViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.item_edt_name_downloads);
            imageView = itemView.findViewById(R.id.image_view_downloads);
            pdfFileView = itemView.findViewById(R.id.pdf_file_view_downloads);
        }
    }
}
