package com.example.ribani.parkirpintar.model;

public class ReserveRecorded {
    String blok;
    int biaya, lama, tipeOrder;

    public String getBlok() {
        return blok;
    }

    public void setBlok(String blok) {
        this.blok = blok;
    }

    public int getTipeOrder() {
        return tipeOrder;
    }

    public void setTipeOrder(int tipeOrder) {
        this.tipeOrder = tipeOrder;
    }

    public int getBiaya() {
        return biaya;
    }

    public void setBiaya(int biaya) {
        this.biaya = biaya;
    }

    public int getLama() {
        return lama;
    }

    public void setLama(int lama) {
        this.lama = lama;
    }

    public String dirTipeOrder() {
        switch (getTipeOrder()) {
            case 1 :
                return "Reservasi";
            case 2 :
                return "Parkir";
            default:
                return "";

        }
    }

    public String dirLama() {
        if(getLama() >= 60) {
            return (getLama()/60)+" Jam";
        } else {
            return getLama()+" Menit";
        }
    }
}
