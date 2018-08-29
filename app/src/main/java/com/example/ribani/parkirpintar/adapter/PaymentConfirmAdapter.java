package com.example.ribani.parkirpintar.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ribani.parkirpintar.R;
import com.example.ribani.parkirpintar.model.ReserveRecorded;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PaymentConfirmAdapter extends RecyclerView.Adapter<PaymentConfirmAdapter.PaymentConfirmHolder> {

    private List<ReserveRecorded> transaksiList = new ArrayList<>();

    public PaymentConfirmAdapter(List<ReserveRecorded> transaksiList) {
        this.transaksiList = transaksiList;
    }

    @NonNull
    @Override
    public PaymentConfirmHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.payment_item,
                parent, false);
        return new PaymentConfirmHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentConfirmHolder holder, int position) {
        ReserveRecorded transaksi = transaksiList.get(position);
        holder.tvTipeOrder.setText(transaksi.dirTipeOrder());
        holder.tvLamaOrder.setText(transaksi.dirLama());
        holder.tvBiayaOrder.setText(String.valueOf(transaksi.getBiaya()));
        holder.tvBlokOrder.setText(String.valueOf(transaksi.getBlok()));
    }

    @Override
    public int getItemCount() {
        return transaksiList.size();
    }

    public class PaymentConfirmHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_tipe_order)
        TextView tvTipeOrder;
        @BindView(R.id.tv_lama_order)
        TextView tvLamaOrder;
        @BindView(R.id.tv_biaya_order)
        TextView tvBiayaOrder;
        @BindView(R.id.tv_blok_order)
        TextView tvBlokOrder;

        public PaymentConfirmHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
