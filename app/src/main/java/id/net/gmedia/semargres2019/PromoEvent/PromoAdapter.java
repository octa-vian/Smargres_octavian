package id.net.gmedia.semargres2019.PromoEvent;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.leonardus.irfan.ImageLoader;

import java.util.List;

import id.net.gmedia.semargres2019.R;
import id.net.gmedia.semargres2019.Util.Constant;

public class PromoAdapter extends RecyclerView.Adapter<PromoAdapter.PromoViewHolder> {

    private Activity activity;
    private List<PromoModel> listPromo;

    public PromoAdapter(Activity activity, List<PromoModel> listPromo){
        this.activity = activity;
        this.listPromo = listPromo;
    }

    @NonNull
    @Override
    public PromoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new PromoViewHolder(LayoutInflater.from(activity).inflate(R.layout.item_promo, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PromoViewHolder promoViewHolder, int i) {
        final PromoModel promo = listPromo.get(i);
        ImageLoader.load(activity, promo.getImage(), promoViewHolder.img_promo);
        promoViewHolder.layout_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                Intent i = new Intent(activity, PromoDetailActivity.class);
                i.putExtra(Constant.EXTRA_PROMO, gson.toJson(promo));
                activity.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listPromo.size();
    }

    class PromoViewHolder extends RecyclerView.ViewHolder{

        View layout_parent;
        ImageView img_promo;

        PromoViewHolder(@NonNull View itemView) {
            super(itemView);
            img_promo = itemView.findViewById(R.id.img_promo);
            layout_parent = itemView.findViewById(R.id.layout_parent);
        }
    }
}
