package com.hexflix.pdfreader.model;

import com.google.firebase.database.Exclude;

public class Upload {
    private String name, pdfURL, key;

    public Upload() {
    }

    public Upload(String name, String imageURL) {
        this.name = name;
        this.pdfURL = imageURL;
    }

    @Exclude
    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public String getPdfURL() {
        return pdfURL;
    }

    @Exclude
    public String getKey() {
        return key;
    }
}
