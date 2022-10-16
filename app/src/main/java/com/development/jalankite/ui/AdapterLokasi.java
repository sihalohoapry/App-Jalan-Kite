package com.development.jalankite.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.development.jalankite.R;
import com.development.jalankite.ui.home.DataItem;

import java.util.ArrayList;
import java.util.List;

public class AdapterLokasi extends RecyclerView.Adapter<AdapterLokasi.ListViewHolder> {
    private List<DataItem> listLokasi;
    private Context context;
    private AdapterListener listener;

    public AdapterLokasi(List<DataItem> list, Context context, AdapterListener listener) {
        this.listLokasi = list;
        this.context = context ;
        this.listener = listener ;
    }

    @NonNull
    @Override
    public AdapterLokasi.ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lokasi_list,parent, false);
        return  new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterLokasi.ListViewHolder holder, int position) {
         DataItem dataItem = listLokasi.get(position);
        Glide.with(context)
                .load("http://192.168.0.190:8000/storage/"+ dataItem.getFoto())
                .placeholder(R.drawable.broken_image)
                .error(R.drawable.broken_image)
                .into(holder.imgPhoto);
        holder.tvName.setText(dataItem.getNamaLokasi());
        holder.tvAlamat.setText(dataItem.getAlamatLokasi());
        Log.d("AlamatLokasi", dataItem.getAlamatLokasi());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(dataItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listLokasi.size();
    }

    public  class ListViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPhoto;
        TextView tvName, tvAlamat;
        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPhoto = itemView.findViewById(R.id.iv_poto);
            tvName = itemView.findViewById(R.id.tv_nama_lokasi);
            tvAlamat = itemView.findViewById(R.id.tv_alamat);
        }
    }

    public void  setdata(List<DataItem> newResult) {
        listLokasi.clear();
        listLokasi.addAll(newResult);
        notifyDataSetChanged();
    }

    public interface AdapterListener {
        void onClick(DataItem result);
    }
}
