package com.example.ribani.parkirpintar.feature.reserve;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.ribani.parkirpintar.R;
import com.example.ribani.parkirpintar.base.mvp.MvpActivity;
import com.example.ribani.parkirpintar.feature.main.MainActivity;
import com.example.ribani.parkirpintar.presenter.MainPresenter;
import com.example.ribani.parkirpintar.view.ResponseView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReserveActivity extends MvpActivity<MainPresenter> implements ResponseView {

    @BindView(R.id.sp_time_reserve)
    Spinner spTimeReserve;
    @BindView(R.id.et_num_police)
    EditText etNumPolice;
    @BindView(R.id.tv_block_reserve)
    TextView tvBlockReserve;
    @BindView(R.id.tv_detail_block_reserve)
    TextView tvDetailBlockReserve;
    @BindView(R.id.reserve_button)
    RelativeLayout reserveButton;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mRef = database.getReference();

    @Override
    protected MainPresenter createPresenter() {
        return new MainPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve);

        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this,
                R.array.time_array, R.layout.spinner_text_style);

        spTimeReserve.setAdapter(typeAdapter);

        String block = getIntent().getStringExtra(MainActivity.BLOCK);
        tvBlockReserve.setText(block);
        tvDetailBlockReserve.setText(tvBlockReserve.getText().toString().trim());


        etNumPolice.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        etNumPolice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                presenter.doTrimText(s);
            }
        });
    }

    @Override
    public void onSuccess(Object obj, String tag) {
        switch (tag) {
            case MainPresenter.TRIM_TEXT:
                String response = (String) obj;
                etNumPolice.setText(response);
                etNumPolice.setSelection(response.length());
                break;
            case MainPresenter.RESERVED :
                Intent intent = (Intent) obj;
                startActivity(intent);
        }
    }

    @Override
    public void onFailed(String tag) {

    }

    @OnClick({R.id.iv_back_press, R.id.reserve_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back_press:
                super.onBackPressed();
                break;
            case R.id.reserve_button:
                int timeReserve = spTimeReserve.getSelectedItemPosition();
                presenter.doReserve(mRef, this, tvDetailBlockReserve.getText().toString(),
                        etNumPolice.getText().toString(), timeReserve);
                break;
        }
    }
}
