package com.example.ribani.parkirpintar.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ribani.parkirpintar.R;
import com.example.ribani.parkirpintar.listener.CustomItemClickListener;
import com.example.ribani.parkirpintar.model.MainItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {

    private List<MainItem> itemList = new ArrayList<>();
    private Context mContext;
    private CustomItemClickListener clickListener;

    public MainAdapter(Context mContext, List<MainItem> itemList, CustomItemClickListener clickListener) {
        this.itemList = itemList;
        this.mContext = mContext;
        this.clickListener = clickListener;
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.block_item,
                parent, false);

        return new MainViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, int position) {
        MainItem mainItem = itemList.get(position);
        holder.tvBlockItem.setText(mainItem.getNamaBlok());
        holder.tvNoteItem.setText(mainItem.dirStatus());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class MainViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.tv_block_item) TextView tvBlockItem;
        @BindView(R.id.tv_note_item) TextView tvNoteItem;
        @BindView(R.id.tv_remains_time_item) TextView tvRemainsTimeItem;
        @BindView(R.id.time_remains_layout) LinearLayout timeRemainsLayout;

        public MainViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(v, tvBlockItem.getText().toString().trim());
        }
    }
}
