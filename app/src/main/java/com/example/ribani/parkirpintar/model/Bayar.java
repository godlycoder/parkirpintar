package com.example.ribani.parkirpintar.model;

import java.util.List;

public class Bayar {
    int totalBayar, totalOrder, status;
    List<ReserveRecorded> transaksiList;

    public int getTotalOrder() {
        return totalOrder;
    }

    public void setTotalOrder(int totalOrder) {
        this.totalOrder = totalOrder;
    }

    public int getTotalBayar() {
        return totalBayar;
    }

    public void setTotalBayar(int totalBayar) {
        this.totalBayar = totalBayar;
    }

    public List<ReserveRecorded> getTransaksiList() {
        return transaksiList;
    }

    public void setTransaksiList(List<ReserveRecorded> transaksiList) {
        this.transaksiList = transaksiList;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
