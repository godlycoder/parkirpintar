package com.example.ribani.parkirpintar.model;

import android.content.Context;
import android.content.Intent;

import com.example.ribani.parkirpintar.feature.reserve.ReserveActivity;

public class MainItem {

    public static final String BLOCK = "BLOCK";

    String namaBlok, park;
    int status;
    Reserved res;

    public String getPark() {
        return park;
    }

    public void setPark(String park) {
        this.park = park;
    }

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
        return res;
    }

    public void setReserved(Reserved res) {
        this.res = res;
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

}
