package id.net.gmedia.semargres2019.Voucher;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.octa.vian.Converter;

import java.util.List;
import java.util.Locale;

import id.net.gmedia.semargres2019.R;

public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.VoucherViewHolder> {

    private VoucherActivity activity;
    private List<VoucherModel> listVoucher;

    VoucherAdapter(VoucherActivity activity, List<VoucherModel> listVoucher){
        this.activity = activity;
        this.listVoucher = listVoucher;
    }

    @NonNull
    @Override
    public VoucherViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new VoucherViewHolder(LayoutInflater.from(activity).inflate(R.layout.item_voucher, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VoucherViewHolder holder, int i) {
        final VoucherModel voucher = listVoucher.get(i);

        if(voucher.getTYPE() == VoucherModel.PERSEN){
            String persen = String.format(Locale.getDefault(), "%.0f %%", voucher.getNominal());
            holder.txt_nominal.setText(persen);
        }
        else{
            holder.txt_nominal.setText(Converter.doubleToRupiah(voucher.getNominal()));
        }

        holder.txt_nama.setText(voucher.getNama());
        holder.txt_periode.setText(voucher.getPeriode());

        holder.btn_pakai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                dialog.setTitle("Pakai Voucher");
                dialog.setMessage("Yakin ingin menggunakan Voucher?");
                dialog.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.useVoucher(voucher.getId());
                    }
                });
                dialog.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                dialog.create().show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listVoucher.size();
    }

    class VoucherViewHolder extends RecyclerView.ViewHolder{

        ImageView img_voucher;
        TextView txt_nama, txt_nominal, txt_periode;
        Button btn_pakai;

        VoucherViewHolder(@NonNull View itemView) {
            super(itemView);
            img_voucher = itemView.findViewById(R.id.img_voucher);
            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_nominal = itemView.findViewById(R.id.txt_nominal);
            txt_periode = itemView.findViewById(R.id.txt_periode);
            btn_pakai = itemView.findViewById(R.id.btn_pakai);
        }
    }
}
