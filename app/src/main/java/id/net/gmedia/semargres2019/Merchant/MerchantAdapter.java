package id.net.gmedia.semargres2019.Merchant;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.octa.vian.ImageLoader;

import java.util.List;

import id.net.gmedia.semargres2019.TiketKonser.TiketKonserActivity;
import id.net.gmedia.semargres2019.Util.Constant;
import id.net.gmedia.semargres2019.MainActivity;
import id.net.gmedia.semargres2019.R;
import com.octa.vian.DynamicHeightImageView;
import id.net.gmedia.semargres2019.Wisata.WisataActivity;

public class MerchantAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int ID_IKLAN = 910;

    private Activity activity;
    private List<MerchantModel> listMerchant;

    public MerchantAdapter(Activity activity, List<MerchantModel> listMerchant){
        this.activity = activity;
        this.listMerchant = listMerchant;
    }

    @Override
    public int getItemViewType(int position) {
        if(listMerchant.get(position).isIklan()){
            return ID_IKLAN;
        }
        else{
            return super.getItemViewType(position);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(i == ID_IKLAN){
            return new IklanViewHolder(LayoutInflater.from(activity).
                    inflate(R.layout.item_iklan, viewGroup, false));
        }
        else{

            if(activity instanceof MainActivity){
                return new MerchantViewHolder(LayoutInflater.from(activity).
                        inflate(R.layout.item_merchan_terdekat_baru, viewGroup, false));
            }
            else if(activity instanceof MapActivity || activity instanceof WisataActivity){
                return new MerchantViewHolder(LayoutInflater.from(activity).
                        inflate(R.layout.item_map_terdekat, viewGroup, false));
            }
            else{
                return new MappedMerchantViewHolder(LayoutInflater.from(activity).
                        inflate(R.layout.item_merchant_baru, viewGroup, false));
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int i) {
        final MerchantModel t = listMerchant.get(i);

        if(h instanceof MerchantViewHolder){
            MerchantViewHolder holder = (MerchantViewHolder)h;
            ImageLoader.load(activity, t.getFoto(), holder.img_tempat);

            holder.txt_nama.setText(t.getNama());
            holder.txt_alamat.setText(t.getAlamat());
            holder.layout_root.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent i = new Intent(activity, MerchantDetailActivity.class);
                    if(t.getLatitude() != 0 && t.getLongitude() != 0){
                        i.putExtra(Constant.EXTRA_LATITUDE, t.getLatitude());
                        i.putExtra(Constant.EXTRA_LONGITUDE, t.getLongitude());
                    }
                    i.putExtra(Constant.EXTRA_MERCHANT_ID, t.getId());
                    if(activity instanceof MerchantActivity){
                        i.putExtra(Constant.EXTRA_KATEGORI_ID, ((MerchantActivity)activity).kategori_id);
                    }
                    activity.startActivity(i);
                }
            });

            /*if(holder instanceof MappedMerchantViewHolder){
                if(t.getLatitude() != 0 && t.getLongitude() != 0){
                    ((MappedMerchantViewHolder)holder).img_map.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(activity, MapActivity.class);
                            i.putExtra(Constant.EXTRA_LATITUDE, t.getLatitude());
                            i.putExtra(Constant.EXTRA_LONGITUDE, t.getLongitude());
                            activity.startActivity(i);
                        }
                    });
                }
                else{
                    ((MappedMerchantViewHolder)holder).img_map.setVisibility(View.INVISIBLE);
                }
            }*/
        }
        else if(h instanceof IklanViewHolder){
            final IklanViewHolder holder = (IklanViewHolder) h;
            ImageLoader.preload(activity, t.getFoto(), new ImageLoader.ImageLoadListener() {
                @Override
                public void onLoaded(Bitmap image, float width, float height) {
                    holder.img_iklan.setAspectRatio(width/height);
                    ImageLoader.load(activity, image, holder.img_iklan);
                }
            });

            holder.layout_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(t.getLink().equals("http://semaranggreatmusic.com/")){
                        Intent i = new Intent(activity, TiketKonserActivity.class);
                        activity.startActivity(i);
                    }
                    else if(!t.getLink().isEmpty()){
                        redirrectToLink(t.getLink());
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listMerchant.size();
    }



    class MerchantViewHolder extends RecyclerView.ViewHolder {

        View layout_root;
        ImageView img_tempat;
        TextView txt_nama, txt_alamat;

        MerchantViewHolder(@NonNull View itemView) {
            super(itemView);
            layout_root = itemView.findViewById(R.id.layout_root);
            img_tempat = itemView.findViewById(R.id.img_tempat);
            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_alamat = itemView.findViewById(R.id.txt_alamat);
        }
    }

    class MappedMerchantViewHolder extends MerchantViewHolder {

        //ImageView img_map;

        MappedMerchantViewHolder(@NonNull View itemView) {
            super(itemView);
            //img_map = itemView.findViewById(R.id.img_map);
        }
    }

    class IklanViewHolder extends RecyclerView.ViewHolder{

        View layout_parent;
        DynamicHeightImageView img_iklan;

        IklanViewHolder(@NonNull View itemView) {
            super(itemView);
            img_iklan = itemView.findViewById(R.id.img_iklan);
            layout_parent = itemView.findViewById(R.id.layout_parent);
        }
    }

    private void redirrectToLink(String link){
        if(link.length() > 0){
            try{
                if (!link.toLowerCase().startsWith("http://") && !link.toLowerCase().startsWith("https://")) {
                    link = "http://" + link;
                }

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                activity.startActivity(browserIntent);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
