package id.net.gmedia.semargres2019.Kupon;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leonardus.irfan.SimpleObjectModel;

import java.util.LinkedHashMap;
import java.util.List;

import id.net.gmedia.semargres2019.R;

public class KuponAdapter extends RecyclerView.Adapter<KuponAdapter.KuponViewHolder>{

    private Context context;
    private List<String> listHeader;
    private LinkedHashMap<String, List<SimpleObjectModel>> listKupon;

    KuponAdapter(Context context, List<String> listHeader, LinkedHashMap<String,
            List<SimpleObjectModel>> listKupon){
        this.context = context;
        this.listHeader = listHeader;
        this.listKupon = listKupon;
    }

    @NonNull
    @Override
    public KuponViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new KuponViewHolder(LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.item_kupon, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull KuponViewHolder holder, int i) {
        String key = listHeader.get(i);
        holder.txt_nama.setText(key);

        holder.rv_kupon.setItemAnimator(new DefaultItemAnimator());
        holder.rv_kupon.setLayoutManager(new GridLayoutManager(context, 2));
        holder.rv_kupon.setAdapter(new KuponChildAdapter(context, listKupon.get(key)));
    }

    @Override
    public int getItemCount() {
        return listHeader.size();
    }

    class KuponViewHolder extends RecyclerView.ViewHolder{

        RecyclerView rv_kupon;
        TextView txt_nama;

        KuponViewHolder(@NonNull View itemView) {
            super(itemView);
            rv_kupon = itemView.findViewById(R.id.rv_kupon);
            txt_nama = itemView.findViewById(R.id.txt_nama);
        }
    }
}
