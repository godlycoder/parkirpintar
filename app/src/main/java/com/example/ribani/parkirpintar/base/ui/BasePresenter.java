package com.example.ribani.parkirpintar.base.ui;

public class BasePresenter <V> {
    public V view;

    public void attachView(V view) {
        this.view = view;
    }

    public void deattachView() {
        this.view = null;
    }
}
