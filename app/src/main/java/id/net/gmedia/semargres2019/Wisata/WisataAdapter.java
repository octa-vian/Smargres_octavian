package id.net.gmedia.semargres2019.Wisata;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.leonardus.irfan.ImageLoader;

import java.util.List;

import id.net.gmedia.semargres2019.Util.Constant;
import id.net.gmedia.semargres2019.R;

public class WisataAdapter extends RecyclerView.Adapter<WisataAdapter.WisataViewHolder> {

    private Activity activity;
    private List<WisataModel> listWisata;

    public WisataAdapter(Activity activity, List<WisataModel> listWisata){
        this.activity = activity;
        this.listWisata = listWisata;
    }

    @NonNull
    @Override
    public WisataViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new WisataViewHolder(LayoutInflater.from(activity).inflate(R.layout.item_wisata, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull WisataViewHolder wisataViewHolder, int i) {
        final WisataModel w = listWisata.get(i);

        ImageLoader.load(activity, w.getFoto(), wisataViewHolder.img_tempat);
        wisataViewHolder.txt_tempat.setText(w.getNama());

        wisataViewHolder.layout_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                Intent i = new Intent(activity, WisataActivity.class);
                i.putExtra(Constant.EXTRA_WISATA, gson.toJson(w));
                activity.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listWisata.size() > 6?6:listWisata.size();
    }


    class WisataViewHolder extends RecyclerView.ViewHolder{

        View layout_root;
        TextView txt_tempat;
        ImageView img_tempat;

        WisataViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_tempat = itemView.findViewById(R.id.txt_tempat);
            img_tempat = itemView.findViewById(R.id.img_tempat);
            layout_root = itemView.findViewById(R.id.layout_root);
        }
    }
}
