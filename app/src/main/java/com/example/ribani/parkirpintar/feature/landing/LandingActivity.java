package com.example.ribani.parkirpintar.feature.landing;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ribani.parkirpintar.LandingResponse;
import com.example.ribani.parkirpintar.Preferences;
import com.example.ribani.parkirpintar.R;
import com.example.ribani.parkirpintar.base.mvp.MvpActivity;
import com.example.ribani.parkirpintar.feature.main.MainActivity;
import com.example.ribani.parkirpintar.presenter.LandingPresenter;
import com.example.ribani.parkirpintar.view.ResponseView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LandingActivity extends MvpActivity<LandingPresenter> implements ResponseView {

    @BindView(R.id.rl_login)
    RelativeLayout rlLogin;
    @BindView(R.id.rl_signup)
    RelativeLayout rlSignup;
    @BindView(R.id.et_email_login)
    EditText etEmailLogin;
    @BindView(R.id.et_password_login)
    EditText etPasswordLogin;
    @BindView(R.id.et_email_signup)
    EditText etEmailSignup;
    @BindView(R.id.et_password_signup)
    EditText etPasswordSignup;
    @BindView(R.id.et_nama_signup)
    EditText etNamaSignup;
    @BindView(R.id.pb_login_btn)
    ProgressBar pbLoginBtn;
    @BindView(R.id.pb_signup_btn)
    ProgressBar pbSignupBtn;
    @BindView(R.id.tv_login_btn)
    TextView tvLoginBtn;
    @BindView(R.id.tv_signup_btn)
    TextView tvSignupBtn;
    @BindView(R.id.et_refer_signup)
    EditText etReferSignup;


    private FirebaseAuth mAuth;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mRef;

    @Override
    protected LandingPresenter createPresenter() {
        return new LandingPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        ButterKnife.bind(this);

        mRef = database.getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onSuccess(Object obj, String tag) {
        switch (tag) {
            case LandingPresenter.SIGNUP:
                pbSignupBtn.setVisibility(View.GONE);
                tvSignupBtn.setVisibility(View.VISIBLE);
                LandingResponse responseSignup = (LandingResponse) obj;
                Preferences.saveUid(responseSignup.getUid());
                Preferences.saveProfile(responseSignup.getProfile());
                startActivity(new Intent(LandingActivity.this, MainActivity.class));
                finish();
                break;
            case LandingPresenter.LOGIN:
                pbLoginBtn.setVisibility(View.GONE);
                tvLoginBtn.setVisibility(View.VISIBLE);
                LandingResponse responseLogin = (LandingResponse) obj;
                Preferences.saveUid(responseLogin.getUid());
                Preferences.saveProfile(responseLogin.getProfile());
                startActivity(new Intent(LandingActivity.this, MainActivity.class));
                finish();
                break;
        }
    }

    @Override
    public void onFailed(String tag) {
        switch (tag) {
            case LandingPresenter.SIGNUP:
                pbSignupBtn.setVisibility(View.GONE);
                tvSignupBtn.setVisibility(View.VISIBLE);
                Toast.makeText(LandingActivity.this, R.string.signup_fail_msg,
                        Toast.LENGTH_SHORT).show();
                break;
            case LandingPresenter.LOGIN_FAILED:
                pbLoginBtn.setVisibility(View.GONE);
                tvLoginBtn.setVisibility(View.VISIBLE);
                Toast.makeText(LandingActivity.this, R.string.login_fail_msg,
                        Toast.LENGTH_SHORT).show();
                break;
            case LandingPresenter.LOGIN_ERROR:
                pbLoginBtn.setVisibility(View.GONE);
                tvLoginBtn.setVisibility(View.VISIBLE);
                Toast.makeText(LandingActivity.this, R.string.database_error_msg,
                        Toast.LENGTH_SHORT).show();
                break;
            case LandingPresenter.NO_REF :
                pbSignupBtn.setVisibility(View.GONE);
                tvSignupBtn.setVisibility(View.VISIBLE);
                Toast.makeText(LandingActivity.this, R.string.no_ref_error,
                        Toast.LENGTH_SHORT).show();
                break;
            case LandingPresenter.REF_USED :
                pbSignupBtn.setVisibility(View.GONE);
                tvSignupBtn.setVisibility(View.VISIBLE);
                Toast.makeText(LandingActivity.this, R.string.rer_used_msg,
                        Toast.LENGTH_SHORT).show();
                break;
            case LandingPresenter.EMPTY_INPUT_LOGIN :
                pbLoginBtn.setVisibility(View.GONE);
                tvLoginBtn.setVisibility(View.VISIBLE);
                Toast.makeText(LandingActivity.this, R.string.empty_form_msg,
                        Toast.LENGTH_SHORT).show();
                break;
            case LandingPresenter.EMPTY_INPUT_SIGNUP :
                pbSignupBtn.setVisibility(View.GONE);
                tvSignupBtn.setVisibility(View.VISIBLE);
                Toast.makeText(LandingActivity.this, R.string.empty_form_msg,
                        Toast.LENGTH_SHORT).show();
                break;
        }

    }

    @OnClick({R.id.tv_daftar, R.id.tv_masuk})
    public void onSwitchLandClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_daftar:
                rlSignup.setVisibility(View.VISIBLE);
                rlLogin.setVisibility(View.GONE);
                break;
            case R.id.tv_masuk:
                rlLogin.setVisibility(View.VISIBLE);
                rlSignup.setVisibility(View.GONE);
                break;
        }
    }


    @OnClick({R.id.bt_masuk, R.id.bt_daftar})
    public void onLandingButtonClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_masuk:
                pbLoginBtn.setVisibility(View.VISIBLE);
                tvLoginBtn.setVisibility(View.GONE);
                String emailLogin = etEmailLogin.getText().toString();
                String passwordLogin = etPasswordLogin.getText().toString();
                presenter.doLogin(mAuth, mRef, this, emailLogin, passwordLogin);
                break;
            case R.id.bt_daftar:
                pbSignupBtn.setVisibility(View.VISIBLE);
                tvSignupBtn.setVisibility(View.GONE);
                String namaSignup = etNamaSignup.getText().toString();
                String emailSignup = etEmailSignup.getText().toString();
                String passwordSignup = etPasswordSignup.getText().toString();
                String referSignup = etReferSignup.getText().toString();
                presenter.doSignUp(mAuth, mRef, this, namaSignup, emailSignup
                        , passwordSignup, referSignup);
                break;
        }
    }
}
