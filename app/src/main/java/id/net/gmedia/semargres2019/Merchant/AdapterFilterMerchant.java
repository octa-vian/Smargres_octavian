package id.net.gmedia.semargres2019.Merchant;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.octa.vian.ImageLoader;

import java.util.List;

import id.net.gmedia.semargres2019.R;
import id.net.gmedia.semargres2019.TerdekatFragment;
import id.net.gmedia.semargres2019.Util.Constant;

public class AdapterFilterMerchant extends RecyclerView.Adapter<AdapterFilterMerchant.MerchantPopulerViewHolder> {

    private int HEADER = 90;

    private Activity activity;
    private List<MerchantModel> listMerchant;

    public AdapterFilterMerchant(Activity activity, List<MerchantModel> listMerchant){
        this.activity = activity;
        this.listMerchant = listMerchant;
    }


    @NonNull
    @Override
    public MerchantPopulerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MerchantPopulerViewHolder(LayoutInflater.from(activity).
                inflate(R.layout.item_filter_merchant_adapter, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MerchantPopulerViewHolder merchantPopulerViewHolder, final int i) {
        final MerchantModel m = listMerchant.get(i);
        merchantPopulerViewHolder.txt_nama.setText(m.getNama());


        merchantPopulerViewHolder.cb_kategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((CheckBox) v).isChecked();
                if (checked){
                    //merchantPopulerViewHolder.cb_kategory.setChecked(true);
//                    TerdekatFragment.list_kategori.add(Integer.valueOf(m.getId()));
                    TerdekatFragment.list_kategori.add(Integer.parseInt(m.getId()));
//                    Toast.makeText(activity, m.getNama()+" on "+checked, Toast.LENGTH_SHORT).show();

                }else{
//                    Toast.makeText(activity, m.getNama()+" on "+checked, Toast.LENGTH_SHORT).show();
                    //merchantPopulerViewHolder.cb_kategory.setChecked(false);
//                    TerdekatFragment.list_kategori.remove(new int[Integer.valueOf(m.getId())]);
                    TerdekatFragment.list_kategori.remove(Integer.valueOf(m.getId()));
                }
            }
        });

       /* merchantPopulerViewHolder.cb_kategory.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                listMerchant.get(i).setSelected(isChecked);
            }
        });
*/

    }


    @Override
    public int getItemCount() {
        return listMerchant.size();
    }


    class MerchantPopulerViewHolder extends RecyclerView.ViewHolder{

        View layout_root;
        TextView txt_nama;
        CheckBox cb_kategory;

        MerchantPopulerViewHolder(@NonNull View itemView) {
            super(itemView);
            layout_root = itemView.findViewById(R.id.layout_root);
            cb_kategory = itemView.findViewById(R.id.id_checkbox_merchant);
            txt_nama = itemView.findViewById(R.id.nama_merchant);
        }
    }
}
