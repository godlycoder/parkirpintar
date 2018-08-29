package com.example.ribani.parkirpintar;

import com.example.ribani.parkirpintar.model.Profile;
import com.orhanobut.hawk.Hawk;

public class Preferences {
    public class Key {
        public static final String UID = "UID";
        public static final String PROFILE = "PROFILE";
    }

    public static void saveUid(String uid) {
        Hawk.put(Key.UID, uid);
    }

    public static void saveProfile(Profile profile) {
        Hawk.put(Key.PROFILE, profile);
    }

    public static String getUid(){
        return Hawk.get(Key.UID);
    }

    public static Profile getProfile(){
        return Hawk.get(Key.PROFILE);
    }

    public static void logout(){
        Hawk.delete(Key.UID);
        Hawk.delete(Key.PROFILE);
    }
}
