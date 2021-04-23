package com.example.firebasep.models;

import com.google.firebase.database.Exclude;



public class Upload {
    private String name;
    private String imgUrl;
    private String key;

    public Upload(){

    }


    public Upload(String name, String imgUrl) {
        if (name.trim().equals(""))
        {
            name="No Name";
        }
        this.name = name;
        this.imgUrl = imgUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    @Exclude
    public String getKey() {
        return key;
    }
    @Exclude
    public void setKey(String key) {
        this.key = key;
    }
}
