package id.net.gmedia.semargres2019.Merchant;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.octa.vian.ImageLoader;

import java.util.List;

import id.net.gmedia.semargres2019.R;
import id.net.gmedia.semargres2019.Util.Constant;

public class AdapterMerchantTerdekat extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int HEADER = 90;

    private Activity activity;
    private List<MerchantModel> listMerchant;

    public AdapterMerchantTerdekat(Activity activity, List<MerchantModel> listMerchant){
        this.activity = activity;
        this.listMerchant = listMerchant;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new MerchantPopulerViewHolder(LayoutInflater.from(activity).
                    inflate(R.layout.item_merchan_terdekat_baru, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if(viewHolder instanceof MerchantPopulerViewHolder){
            MerchantPopulerViewHolder holder = (MerchantPopulerViewHolder) viewHolder ;
            final MerchantModel m = listMerchant.get(i);

            ImageLoader.load(activity, m.getFoto(), holder.img_tempat);
            holder.txt_nama.setText(m.getNama());
            holder.layout_root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(activity, MerchantDetailActivity.class);
                    i.putExtra(Constant.EXTRA_MERCHANT_ID, m.getId());
                    activity.startActivity(i);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listMerchant.size();
    }


    class MerchantPopulerViewHolder extends RecyclerView.ViewHolder{

        View layout_root;
        ImageView img_tempat;
        TextView txt_nama;

        MerchantPopulerViewHolder(@NonNull View itemView) {
            super(itemView);
            img_tempat = itemView.findViewById(R.id.img_tempat);
            layout_root = itemView.findViewById(R.id.layout_root);
            txt_nama = itemView.findViewById(R.id.txt_nama);
        }
    }
}
