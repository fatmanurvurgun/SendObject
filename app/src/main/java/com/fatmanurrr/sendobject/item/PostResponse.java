package com.fatmanurrr.sendobject.item;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Lenovo on 6.09.2019.
 */

public class PostResponse {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @SerializedName("id")
    private int id;
}
