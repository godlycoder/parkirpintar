package com.example.ribani.parkirpintar.presenter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.example.ribani.parkirpintar.LandingResponse;
import com.example.ribani.parkirpintar.base.ui.BasePresenter;
import com.example.ribani.parkirpintar.model.Bayar;
import com.example.ribani.parkirpintar.model.Profile;
import com.example.ribani.parkirpintar.view.ResponseView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import static com.google.firebase.analytics.FirebaseAnalytics.Event.SIGN_UP;

public class LandingPresenter extends BasePresenter<ResponseView> {
    public final static String LOGIN_FAILED = "LOGIN_FAILED";
    public static final String EMPTY_INPUT_SIGNUP = "EMPTY_INPUT_SIGNUP";
    public static final String SIGNUP = "SIGNUP";
    public static final String LOGIN = "LOGIN";
    public static final String LOGIN_ERROR = "LOGIN_ERROR";
    public static final String NO_REF = "NO_REF";
    public static final String REF_USED = "REF_USED";
    public static final String EMPTY_INPUT_LOGIN = "EMPTY_INPUT_LOGIN";

    public LandingPresenter(ResponseView view) {
        super.attachView(view);
    }

    public void doLogin(final FirebaseAuth mAuth, final DatabaseReference mRef, Activity activity, String email, String password) {

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            view.onFailed(EMPTY_INPUT_LOGIN);
        } else {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                final LandingResponse response = new LandingResponse();
                                FirebaseUser user = mAuth.getCurrentUser();
                                final String uid = user.getUid();
                                mRef.child("pengguna").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Profile profile = dataSnapshot.getValue(Profile.class);
                                        response.setProfile(profile);
                                        response.setUid(uid);

                                        view.onSuccess(response, LOGIN);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        view.onFailed(LOGIN_ERROR);
                                    }
                                });
                            } else {
                                view.onFailed(LOGIN_FAILED);
                            }
                        }
                    });
        }

    }

    public void doSignUp(final FirebaseAuth mAuth, final DatabaseReference mRef, final Activity activity, String nama, final String email,
                         final String password, final String noRef) {
        if (TextUtils.isEmpty(nama) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            view.onFailed(EMPTY_INPUT_SIGNUP);
        } else {

            final Profile profile = new Profile();
            final LandingResponse response = new LandingResponse();

            profile.setNama(nama);
            profile.setEmail(email);

            mRef.child("no_referensi").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        final String noRefDb = snapshot.getKey();
                        Log.d("NoRef", noRefDb);

                        if (noRefDb.equals(noRef)) {
                            int statusRef = snapshot.child("status").getValue(Integer.class);

                            if (statusRef == 0) {
                                final String cardNumber = snapshot.child("uid").getValue(String.class);

                                mAuth.createUserWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    FirebaseUser user = mAuth.getCurrentUser();

                                                    mRef.child("no_referensi").child(noRefDb)
                                                            .child("status").setValue(1);

                                                    mRef.child("card_number").child(cardNumber)
                                                            .setValue(user.getUid());

                                                    response.setUid(user.getUid());
                                                    response.setProfile(profile);



                                                    mRef.child("pengguna").child(response.getUid()).
                                                            setValue(response.getProfile());

                                                    Bayar bayar = new Bayar();

                                                    bayar.setTotalBayar(0);
                                                    bayar.setTotalOrder(0);
                                                    bayar.setStatus(0);

                                                    mRef.child("bayar").child(user.getUid()).setValue(bayar);

                                                    view.onSuccess(response, SIGNUP);
                                                } else {
                                                    view.onFailed(SIGNUP);
                                                }
                                            }
                                        });
                            } else {
                                view.onFailed(REF_USED);
                            }


                        } else {
                            view.onFailed(NO_REF);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }
    }


}
