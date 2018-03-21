package com.bbw.user.testbbw.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by USER on 3/21/2018.
 */

public class PulsaModel {
    @SerializedName("pulsa")
    public String pulsa;

    @SerializedName("harga")
    public String harga;

    public PulsaModel(String pulsa, String harga) {
        this.pulsa = pulsa;
        this.harga = harga;
    }

    public String getPulsa() {
        return pulsa;
    }

    public String getHarga() {
        return harga;
    }
}
