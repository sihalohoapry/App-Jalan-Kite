package com.development.jalankite.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.development.jalankite.R;
import com.development.jalankite.ui.home.DataItemTerdekat;

import java.util.List;

public class AdapterLokasiTerdekat extends RecyclerView.Adapter<AdapterLokasiTerdekat.ListViewHolder> {
    private List<DataItemTerdekat> listLokasi;
    private Context context;
    private AdapterListener listener;

    public AdapterLokasiTerdekat(List<DataItemTerdekat> list, Context context, AdapterListener listener) {
        this.listLokasi = list;
        this.context = context ;
        this.listener = listener ;
    }

    @NonNull
    @Override
    public AdapterLokasiTerdekat.ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_lokasi_terdekat,parent, false);
        return  new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterLokasiTerdekat.ListViewHolder holder, int position) {
         DataItemTerdekat dataItem = listLokasi.get(position);
        Glide.with(context)
                .load("http://192.168.0.190:8000/storage/"+ dataItem.getFoto())
                .placeholder(R.drawable.broken_image)
                .error(R.drawable.broken_image)
                .into(holder.imgPhoto);
        holder.tvName.setText(dataItem.getNamaLokasi());
        String jarak = String.valueOf(dataItem.getJarak());
        holder.tvAlamat.setText(dataItem.getAlamatLokasi());
        holder.tvJarak.setText(jarak);
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
        TextView tvName, tvAlamat, tvJarak;
        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPhoto = itemView.findViewById(R.id.iv_poto_terdekat);
            tvName = itemView.findViewById(R.id.tv_nama_lokasi_terdekat);
            tvAlamat = itemView.findViewById(R.id.tv_alamat_terdekat);
            tvJarak = itemView.findViewById(R.id.jarak);
        }
    }

    public void  setdata(List<DataItemTerdekat> newResult) {
        listLokasi.clear();
        listLokasi.addAll(newResult);
        notifyDataSetChanged();
    }

    public interface AdapterListener {
        void onClick(DataItemTerdekat result);
    }
}
