package id.net.gmedia.semargres2019.Kupon;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leonardus.irfan.SimpleObjectModel;

import java.util.List;

import id.net.gmedia.semargres2019.R;

class KuponChildAdapter extends RecyclerView.Adapter<KuponChildAdapter.KuponChildViewHolder>{

    private Context context;
    private List<SimpleObjectModel> listKuponChild;

    KuponChildAdapter(Context context, List<SimpleObjectModel> listKuponChild){
        this.context = context;
        this.listKuponChild = listKuponChild;
    }

    @NonNull
    @Override
    public KuponChildViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new KuponChildViewHolder(LayoutInflater.from(context).
                inflate(R.layout.item_kupon_child, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull KuponChildViewHolder kuponChildViewHolder, int i) {
        String no = "No : " + listKuponChild.get(i).getValue();
        kuponChildViewHolder.txt_nomor.setText(no);
    }

    @Override
    public int getItemCount() {
        return listKuponChild.size();
    }

    class KuponChildViewHolder extends RecyclerView.ViewHolder{

        TextView txt_nomor;

        KuponChildViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_nomor = itemView.findViewById(R.id.txt_nomor);
        }
    }
}
