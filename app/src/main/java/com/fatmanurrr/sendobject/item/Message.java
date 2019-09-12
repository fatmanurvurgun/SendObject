package com.fatmanurrr.sendobject.item;

import com.fatmanurrr.sendobject.adapter.DataListAdapter;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by Lenovo on 5.09.2019.
 */

public class Message {

    private int id;
    @SerializedName("message_body")
    private String message_body;

    @SerializedName("image_path")
    private String image_path;

    private String date;

    private boolean status;

    public void setId(int id) {
        this.id = id;
    }

    public void setMessage_body(String message_body) {
        this.message_body = message_body;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getMessage_body() {
        return message_body;
    }

    public String getImage_path() {
        return image_path;
    }

    public String getDate() {
        return date;
    }

    public boolean isStatus() {
        return status;
    }

}
