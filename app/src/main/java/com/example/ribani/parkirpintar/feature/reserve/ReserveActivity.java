package com.example.ribani.parkirpintar.feature.reserve;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ribani.parkirpintar.R;
import com.example.ribani.parkirpintar.base.mvp.MvpActivity;
import com.example.ribani.parkirpintar.feature.main.MainActivity;
import com.example.ribani.parkirpintar.feature.response.ResponseActivity;
import com.example.ribani.parkirpintar.model.Request;
import com.example.ribani.parkirpintar.presenter.MainPresenter;
import com.example.ribani.parkirpintar.presenter.ResponsePresenter;
import com.example.ribani.parkirpintar.view.ResponseView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReserveActivity extends MvpActivity<MainPresenter> implements ResponseView {

    @BindView(R.id.sp_time_reserve)
    Spinner spTimeReserve;
    @BindView(R.id.tv_block_reserve)
    TextView tvBlockReserve;
    @BindView(R.id.tv_detail_block_reserve)
    TextView tvDetailBlockReserve;
    @BindView(R.id.reserve_button)
    RelativeLayout reserveButton;
    @BindView(R.id.tv_detail_time_reserve)
    TextView tvDetailTimeReserve;
    @BindView(R.id.pb_reserve_btn)
    ProgressBar pbReserveBtn;
    @BindView(R.id.tv_reserve_btn)
    TextView tvReserveBtn;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mRef = database.getReference();
    private Bundle bundle;
    String block;

    @Override
    protected MainPresenter createPresenter() {
        return new MainPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve);
        ButterKnife.bind(this);

        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this,
                R.array.time_array, R.layout.spinner_text_style);

        spTimeReserve.setAdapter(typeAdapter);

        bundle = getIntent().getExtras();

        block = bundle.getString(MainPresenter.BLOK);


        tvBlockReserve.setText(block);
        tvDetailBlockReserve.setText(tvBlockReserve.getText().toString().trim());


        spTimeReserve.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tvDetailTimeReserve.setText(spTimeReserve.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                tvDetailTimeReserve.setText(spTimeReserve.getSelectedItem().toString());
            }
        });
    }

    @Override
    public void onSuccess(Object obj, String tag) {
        switch (tag) {
            case MainPresenter.RESERVED:
                pbReserveBtn.setVisibility(View.GONE);
                tvReserveBtn.setVisibility(View.VISIBLE);
                Request request = (Request) obj;
                Intent intent = new Intent(ReserveActivity.this, ResponseActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt(MainPresenter.TIME_RESERVE, request.getTimeReserve());
                bundle.putString(MainPresenter.BLOK, block);
                bundle.putString(ResponseActivity.CHOOSE_LAYOUT, request.getLayout());
                bundle.putString(MainPresenter.PARK_NUMBER, request.getPark());
                intent.putExtras(bundle);
                startActivity(intent);
                break;
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
                pbReserveBtn.setVisibility(View.VISIBLE);
                tvReserveBtn.setVisibility(View.GONE);
                int timeReserve = spTimeReserve.getSelectedItemPosition();

                Log.d("ReserveActivity", String.valueOf(timeReserve));

                if (timeReserve == 0) {
                    pbReserveBtn.setVisibility(View.GONE);
                    tvReserveBtn.setVisibility(View.VISIBLE);
                    Toast.makeText(ReserveActivity.this, R.string.reserve_no_fill_toast_msg
                            , Toast.LENGTH_SHORT).show();
                } else {
                    String park = bundle.getString(MainActivity.PARK);
                    presenter.doReserve(mRef, ReserveActivity.this, park,
                            block, timeReserve);
                    finish();
                }
                break;
        }
    }
}
