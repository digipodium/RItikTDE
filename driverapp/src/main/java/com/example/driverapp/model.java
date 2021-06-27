package com.example.driverapp;

public class model {

    private String imageUrl;
    public model(){

    }
    public model(String imageUrl){
        this.imageUrl=imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
