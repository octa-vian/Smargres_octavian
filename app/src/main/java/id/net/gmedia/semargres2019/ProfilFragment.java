package id.net.gmedia.semargres2019;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.octa.vian.ApiVolleyManager;
import com.octa.vian.AppRequestCallback;
import com.octa.vian.ImageLoader;
import com.octa.vian.TopCropCircularImageView;

import org.json.JSONException;
import org.json.JSONObject;

import id.net.gmedia.semargres2019.Kupon.KuponActivity;
import id.net.gmedia.semargres2019.Util.AppSharedPreferences;
import id.net.gmedia.semargres2019.Util.Constant;
import id.net.gmedia.semargres2019.Voucher.VoucherActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfilFragment extends Fragment {

    private View v;

    private Context context;
    private TextView txt_nama, txt_alamat, txt_telepon;
    private TopCropCircularImageView img_akun;

    public ProfilFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getContext();
        if(v == null){
            v = inflater.inflate(R.layout.fragment_profil, container, false);

            //Inisialisasi UI
            txt_nama = v.findViewById(R.id.txt_nama);
            txt_alamat = v.findViewById(R.id.txt_alamat);
            txt_telepon = v.findViewById(R.id.txt_telepon);
            img_akun = v.findViewById(R.id.img_akun);

            v.findViewById(R.id.btn_logout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppSharedPreferences.Logout(context);
                    Intent i = new Intent(context, LoginActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
            });

            v.findViewById(R.id.layout_kupon).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, KuponActivity.class);
                    context.startActivity(i);
                }
            });

            v.findViewById(R.id.layout_voucher).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, VoucherActivity.class);
                    context.startActivity(i);
                }
            });

            v.findViewById(R.id.layout_profil).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(context, RegisterActivity.class));
                }
            });
        }

        initProfil();

        return v;
    }

    private void initProfil(){
        ApiVolleyManager.getInstance().addSecureRequest(context, Constant.URL_PROFIL, ApiVolleyManager.METHOD_GET,
                Constant.getTokenHeader(context), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            JSONObject response = new JSONObject(result);
                            txt_alamat.setText(response.getString("alamat"));
                            txt_nama.setText(response.getString("profile_name"));
                            txt_telepon.setText(response.getString("no_telp"));

                            ImageLoader.load(context,
                                    response.getString("foto"),
                                    img_akun);
                        }
                        catch (JSONException e){
                            Toast.makeText(context, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }
                }));
    }
}
