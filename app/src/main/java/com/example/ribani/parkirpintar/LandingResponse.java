package com.example.ribani.parkirpintar;

import com.example.ribani.parkirpintar.model.Profile;

public class LandingResponse {
    String uid;
    Profile profile;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }
}
