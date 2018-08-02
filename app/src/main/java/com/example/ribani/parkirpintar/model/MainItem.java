package com.example.ribani.parkirpintar.model;

import android.content.Context;
import android.content.Intent;

import com.example.ribani.parkirpintar.feature.reserve.ReserveActivity;

public class MainItem {

    public static final String BLOCK = "BLOCK";

    String namaBlok;
    int status;
    Reserved reserved;

    public String getNamaBlok() {
        return namaBlok;
    }

    public void setNama_blok(String namaBlok) {
        this.namaBlok = namaBlok;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Reserved getReserved() {
        return reserved;
    }

    public void setReserved(Reserved reserved) {
        this.reserved = reserved;
    }

    public String dirStatus() {
        switch (getStatus()) {
            case 0 :
                return "Tersedia";
            case 1 :
                return "Sedang di pesan";
            default :
                return "";
        }
    }

    public static Intent starter(Context mContext, String block) {
        Intent detailIntent = new Intent(mContext, ReserveActivity.class);
        detailIntent.putExtra(BLOCK, block);

        return detailIntent;
    }

}
