package id.net.gmedia.semargres2019.Kuis;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leonardus.irfan.Converter;

import java.util.List;

import id.net.gmedia.semargres2019.Util.Constant;
import id.net.gmedia.semargres2019.R;

public class KuisAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    static final int KUIS_BERLANGSUNG = 12;
    static final int KUIS_DIJAWAB = 13;

    private KuisActivity activity;
    private List<KuisModel> listKuis;
    private int type;

    KuisAdapter(KuisActivity activity, List<KuisModel> listKuis, int type){
        this.activity = activity;
        this.listKuis = listKuis;
        this.type = type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(type == KUIS_DIJAWAB){
            return new KuisDijawabViewHolder(LayoutInflater.from(activity).
                    inflate(R.layout.item_kuis_dijawab, viewGroup, false));
        }
        else{
            return new KuisViewHolder(LayoutInflater.from(activity).
                    inflate(R.layout.item_kuis, viewGroup, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        final KuisModel kuis = listKuis.get(i);

        if(holder instanceof KuisDijawabViewHolder){
            KuisDijawabViewHolder kuisHolder = (KuisDijawabViewHolder) holder;
            final KuisDijawabModel kuisDijawab =  (KuisDijawabModel) kuis;

            kuisHolder.txt_merchant.setText(kuisDijawab.getMerchant());
            kuisHolder.txt_jawaban.setText(kuisDijawab.getJawaban());
            kuisHolder.txt_pertanyaan.setText(kuisDijawab.getPertanyaan());
            kuisHolder.txt_tanggal.setText(Converter.DToStringInverse(kuisDijawab.getDijawab()));
            kuisHolder.txt_lihat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(kuisDijawab.isMenang()){
                        Intent i = new Intent(activity, KuisDetailActivity.class);
                        i.putExtra(Constant.EXTRA_ID_KUIS, kuis.getId());
                        activity.startActivity(i);
                    }
                }
            });

            if(kuisDijawab.isMenang()){
                kuisHolder.txt_lihat.setVisibility(View.VISIBLE);
                kuisHolder.layout_menang.setVisibility(View.VISIBLE);
            }
            else{
                kuisHolder.txt_lihat.setVisibility(View.GONE);
                kuisHolder.layout_menang.setVisibility(View.GONE);
            }
        }
        else if(holder instanceof KuisViewHolder){
            KuisViewHolder kuisHolder = (KuisViewHolder) holder;

            kuisHolder.txt_merchant.setText(kuis.getMerchant());
            kuisHolder.txt_pertanyaan.setText(kuis.getPertanyaan());
            String periode = "Periode " + Converter.DToStringInverse(kuis.getMulai())
                    + " - " + Converter.DToStringInverse(kuis.getSelesai());
            kuisHolder.txt_periode.setText(periode);
            String hadiah = "Hadiah : " + kuis.getHadiah();
            kuisHolder.txt_hadiah.setText(hadiah);

            kuisHolder.layout_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.jawabKuis(kuis.getId());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listKuis.size();
    }

    class KuisViewHolder extends RecyclerView.ViewHolder{

        View layout_parent;
        TextView txt_merchant, txt_pertanyaan, txt_periode, txt_hadiah;

        KuisViewHolder(@NonNull View itemView) {
            super(itemView);
            layout_parent = itemView.findViewById(R.id.layout_parent);
            txt_merchant = itemView.findViewById(R.id.txt_merchant);
            txt_pertanyaan = itemView.findViewById(R.id.txt_pertanyaan);
            txt_periode = itemView.findViewById(R.id.txt_periode);
            txt_hadiah = itemView.findViewById(R.id.txt_hadiah);
        }
    }

    class KuisDijawabViewHolder extends RecyclerView.ViewHolder {

        View layout_menang;
        TextView txt_tanggal, txt_lihat, txt_jawaban;
        TextView txt_merchant, txt_pertanyaan;

        KuisDijawabViewHolder(@NonNull View itemView) {
            super(itemView);
            layout_menang = itemView.findViewById(R.id.layout_menang);
            txt_tanggal = itemView.findViewById(R.id.txt_tanggal);
            txt_lihat = itemView.findViewById(R.id.txt_lihat);
            txt_jawaban = itemView.findViewById(R.id.txt_jawaban);
            txt_merchant = itemView.findViewById(R.id.txt_merchant);
            txt_pertanyaan = itemView.findViewById(R.id.txt_pertanyaan);
        }
    }
}
