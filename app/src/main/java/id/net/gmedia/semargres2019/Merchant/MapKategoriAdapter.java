package id.net.gmedia.semargres2019.Merchant;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.octa.vian.SimpleSelectableObjectModel;

import java.util.List;

import id.net.gmedia.semargres2019.R;
import id.net.gmedia.semargres2019.Wisata.WisataActivity;

public class MapKategoriAdapter extends RecyclerView.Adapter<MapKategoriAdapter.MapKategoriViewHolder> {

    private Activity activity;
    private List<SimpleSelectableObjectModel> listKategori;

    private int last_selected = 0;

    public MapKategoriAdapter(Activity activity, List<SimpleSelectableObjectModel> listKategori){
        this.activity = activity;
        this.listKategori = listKategori;
    }

    @NonNull
    @Override
    public MapKategoriViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MapKategoriViewHolder(LayoutInflater.from(activity).inflate(R.layout.item_map_kategori, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MapKategoriViewHolder holder, int i) {
        SimpleSelectableObjectModel s = listKategori.get(i);
        final int position = holder.getAdapterPosition();

        holder.txt_kategori.setText(s.getValue());
        if(position == last_selected){
            holder.txt_kategori.setTextColor(activity.getResources().getColor(R.color.colorPrimary));
        }
        else{
            holder.txt_kategori.setTextColor(activity.getResources().getColor(R.color.black));
        }

        holder.txt_kategori.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set kategori dalam halaman tampilan
                if(activity instanceof MapActivity){
                    ((MapActivity)activity).setKategori(listKategori.get(position).getId());
                }
                else if(activity instanceof WisataActivity){
                    ((WisataActivity)activity).setKategori(listKategori.get(position).getId());
                }

                int previous = last_selected;
                last_selected = position;
                notifyItemChanged(previous);
                notifyItemChanged(last_selected);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listKategori.size();
    }

    class MapKategoriViewHolder extends RecyclerView.ViewHolder{

        TextView txt_kategori;

        MapKategoriViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_kategori = itemView.findViewById(R.id.txt_kategori);
        }
    }
}
