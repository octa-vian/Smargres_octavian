package id.net.gmedia.semargres2019.TiketKonser;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.octa.vian.Converter;

import java.util.List;

import id.net.gmedia.semargres2019.Util.Constant;
import id.net.gmedia.semargres2019.R;

public class TiketRiwayatAdapter extends RecyclerView.Adapter<TiketRiwayatAdapter.TiketRiwayatViewHolder> {

    private TiketKonserActivity activity;
    private List<TiketRiwayatModel> listRiwayat;

    TiketRiwayatAdapter(TiketKonserActivity activity, List<TiketRiwayatModel> listRiwayat){
        this.activity = activity;
        this.listRiwayat = listRiwayat;
    }

    @NonNull
    @Override
    public TiketRiwayatViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new TiketRiwayatViewHolder(LayoutInflater.from(activity).
                inflate(R.layout.item_tiket_riwayat, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TiketRiwayatViewHolder holder, int i) {
        final TiketRiwayatModel riwayat = listRiwayat.get(i);

        holder.txt_nama.setText(riwayat.getNama());
        holder.txt_jumlah.setText(Converter.doubleToRupiah(riwayat.getTotal()));
        holder.txt_jenis.setText(riwayat.getJenis());
        holder.txt_waktu.setText(Converter.DToStringInverse(riwayat.getWaktu()));
        holder.txt_status.setText(riwayat.getStatus());
        holder.txt_kode_promo.setText(riwayat.getKodepromo());

        if(!riwayat.getKeterangan().equals("")){
            holder.txt_keterangan.setVisibility(View.VISIBLE);
            holder.txt_keterangan.setText(riwayat.getKeterangan());
        }
        else{
            holder.txt_keterangan.setVisibility(View.GONE);
        }

        if(riwayat.isDownload()){
            holder.img_print.setVisibility(View.VISIBLE);
            holder.img_print.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.prepareDownload(riwayat);
                    Log.d(Constant.TAG, riwayat.getLink_download());
                }
            });
        }
        else{
            holder.img_print.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return listRiwayat.size();
    }

    class TiketRiwayatViewHolder extends RecyclerView.ViewHolder{

        LinearLayout img_print;
        TextView txt_nama, txt_jenis, txt_jumlah, txt_waktu,
                txt_status, txt_kode_promo, txt_keterangan;

        TiketRiwayatViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_jenis = itemView.findViewById(R.id.txt_jenis);
            txt_jumlah = itemView.findViewById(R.id.txt_jumlah);
            txt_waktu = itemView.findViewById(R.id.txt_waktu);
            txt_status = itemView.findViewById(R.id.txt_status);
            img_print = itemView.findViewById(R.id.img_print);
            txt_kode_promo = itemView.findViewById(R.id.txt_kode_promo);
            txt_keterangan = itemView.findViewById(R.id.txt_keterangan);
        }
    }
}
