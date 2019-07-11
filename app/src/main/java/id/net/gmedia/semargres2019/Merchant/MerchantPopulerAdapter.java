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

import com.leonardus.irfan.ImageLoader;

import java.util.List;

import id.net.gmedia.semargres2019.Util.Constant;
import id.net.gmedia.semargres2019.R;

public class MerchantPopulerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int HEADER = 90;

    private Activity activity;
    private List<MerchantModel> listMerchant;

    public MerchantPopulerAdapter(Activity activity, List<MerchantModel> listMerchant){
        this.activity = activity;
        this.listMerchant = listMerchant;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return HEADER;
        }
        else{
            return super.getItemViewType(position);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(i == HEADER){
            return new HeaderViewHolder(LayoutInflater.from(activity).inflate(R.layout.item_merchant_populer_header, viewGroup, false));
        }
        else{
            return new MerchantPopulerViewHolder(LayoutInflater.from(activity).inflate(R.layout.item_merchant_populer, viewGroup, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if(viewHolder instanceof MerchantPopulerViewHolder){
            MerchantPopulerViewHolder holder = (MerchantPopulerViewHolder) viewHolder ;
            final MerchantModel m = listMerchant.get(i - 1);

            ImageLoader.load(activity, m.getFoto(), holder.img_tempat);
            holder.txt_tempat.setText(m.getNama());
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
        return listMerchant.size() + 1;
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder{

        TextView txt_header;

        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_header = itemView.findViewById(R.id.txt_header);
        }
    }


    class MerchantPopulerViewHolder extends RecyclerView.ViewHolder{

        View layout_root;
        ImageView img_tempat;
        TextView txt_tempat;

        MerchantPopulerViewHolder(@NonNull View itemView) {
            super(itemView);
            img_tempat = itemView.findViewById(R.id.img_tempat);
            layout_root = itemView.findViewById(R.id.layout_root);
            txt_tempat = itemView.findViewById(R.id.txt_tempat);
        }
    }
}
