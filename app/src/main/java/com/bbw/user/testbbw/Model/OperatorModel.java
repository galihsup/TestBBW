package com.bbw.user.testbbw.Model;

import com.google.gson.annotations.SerializedName;
/**
 * Created by USER on 3/21/2018.
 */

public class OperatorModel {
    @SerializedName("nama")
    public String nama;

    public OperatorModel(String nama) {
        this.nama = nama;
    }

    public String getNama() {
        return nama;
    }
}
