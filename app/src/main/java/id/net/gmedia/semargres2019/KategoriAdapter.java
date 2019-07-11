package id.net.gmedia.semargres2019;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.leonardus.irfan.ImageLoader;

import java.util.List;

import id.net.gmedia.semargres2019.Merchant.MerchantActivity;
import id.net.gmedia.semargres2019.Util.Constant;
import id.net.gmedia.semargres2019.Util.SimpleImageObjectModel;

public class KategoriAdapter extends RecyclerView.Adapter<KategoriAdapter.KategoriViewHolder> {

    private Activity activity;
    private List<SimpleImageObjectModel> listKategori;
    private boolean all;

    KategoriAdapter(Activity activity, List<SimpleImageObjectModel> listKategori, boolean all){
        this.activity = activity;
        this.listKategori = listKategori;

        this.all = all;
    }

    @NonNull
    @Override
    public KategoriViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(all){
            return new KategoriViewHolder(LayoutInflater.from(activity).
                    inflate(R.layout.item_kategori_all, viewGroup, false));
        }
        else{
            return new KategoriViewHolder(LayoutInflater.from(activity).
                    inflate(R.layout.item_kategori, viewGroup, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull KategoriViewHolder kategoriViewHolder, int i) {
        final SimpleImageObjectModel k = listKategori.get(i);
        final int final_position = i;

        if(!all && i == 7){
            kategoriViewHolder.txt_kategori.setText("Lainnya");
            ImageLoader.load(activity, R.drawable.all, kategoriViewHolder.img_kategori);
            kategoriViewHolder.img_kategori.setScaleX(0.8f);
            kategoriViewHolder.img_kategori.setScaleY(0.8f);
        }
        else{
            kategoriViewHolder.txt_kategori.setText(k.getNama());
            ImageLoader.load(activity, k.getGambar(), kategoriViewHolder.img_kategori);
            kategoriViewHolder.img_kategori.setScaleX(1f);
            kategoriViewHolder.img_kategori.setScaleY(1f);
        }

        kategoriViewHolder.layout_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activity instanceof MainActivity){
                    if(!all && final_position == 7){
                        ((MainActivity)activity).showAllCategory(listKategori);
                    }
                    else{
                        Intent i = new Intent(activity, MerchantActivity.class);
                        i.putExtra(Constant.EXTRA_KATEGORI_ID, k.getId());
                        i.putExtra(Constant.EXTRA_KATEGORI_NAMA, k.getNama());
                        activity.startActivity(i);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if(all){
            return listKategori.size();
        }
        else{
            return listKategori.size()>8?8:listKategori.size();
        }
    }

    class KategoriViewHolder extends RecyclerView.ViewHolder{

        View layout_root;
        ImageView img_kategori;
        TextView txt_kategori;

        KategoriViewHolder(@NonNull View itemView) {
            super(itemView);
            layout_root = itemView.findViewById(R.id.layout_root);
            txt_kategori = itemView.findViewById(R.id.txt_kategori);
            img_kategori = itemView.findViewById(R.id.img_kategori);
        }
    }
}
