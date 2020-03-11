package id.net.gmedia.semargres2019.Qr;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.octa.vian.ApiVolleyManager;
import com.octa.vian.AppLoading;
import com.octa.vian.AppRequestCallback;
import com.octa.vian.DialogFactory;
import com.octa.vian.ImageLoader;
import com.octa.vian.JSONBuilder;
import com.octa.vian.SimpleObjectModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.semargres2019.MainActivity;
import id.net.gmedia.semargres2019.R;
import id.net.gmedia.semargres2019.RegisterActivity;
import id.net.gmedia.semargres2019.Util.Constant;

/**
 * A simple {@link Fragment} subclass.
 */
public class QrFragment extends Fragment {

    private View v;
    private MainActivity activity;
    private boolean user_qr = false;
    private String user_qr_url = "";

    private double nominal = 0;
    private String id_cara_bayar = "";

    private List<SimpleObjectModel> listCaraBayar;
    ArrayAdapter<SimpleObjectModel> adapterCaraBayar;

    public QrFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(getActivity() instanceof MainActivity){
            activity = (MainActivity)getActivity();
        }

        if(v == null || !user_qr){
            v = inflater.inflate(R.layout.fragment_qr, container, false);

            v.findViewById(R.id.btn_scan).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog();
                }
            });

            v.findViewById(R.id.btn_user).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadUserQr();
                }
            });
        }
        else if(user_qr_url.equals("")){
            v = inflater.inflate(R.layout.fragment_qr_user_kosong, container, false);
            v.findViewById(R.id.btn_edit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(activity, RegisterActivity.class));
                }
            });
        }
        else{
            v = inflater.inflate(R.layout.fragment_qr_user, container, false);
            ImageLoader.load(activity, user_qr_url, (ImageView) v.findViewById(R.id.img_qr));
        }

        if(listCaraBayar == null){
            loadCaraBayar();
        }
        return v;
    }

    public boolean isUser_qr() {
        return user_qr;
    }

    public void offUser_qr(){
        user_qr = false;
    }

    private void loadCaraBayar(){
        ApiVolleyManager.getInstance().addSecureRequest(activity, Constant.URL_CARA_BAYAR,
                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(activity),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {

                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            JSONArray response = new JSONArray(result);

                            listCaraBayar = new ArrayList<>();
                            for(int i = 0; i < response.length(); i++){
                                listCaraBayar.add(new SimpleObjectModel(
                                        response.getJSONObject(i).getString("id"),
                                        response.getJSONObject(i).getString("nama")
                                ));
                            }

                            if(adapterCaraBayar != null){
                                adapterCaraBayar.notifyDataSetChanged();
                            }
                        }
                        catch (JSONException e){
                            Toast.makeText(activity, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void loadUserQr(){
        AppLoading.getInstance().showLoading(activity);
        ApiVolleyManager.getInstance().addSecureRequest(activity, Constant.URL_QR, ApiVolleyManager.METHOD_GET,
                Constant.getTokenHeader(activity), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        user_qr_url = "";
                        user_qr = true;
                        resetFragment();

                        //Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            user_qr_url = new JSONObject(result).getString("url");
                            user_qr = true;
                            resetFragment();
                        }
                        catch (JSONException e){
                            Toast.makeText(activity, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }
                }));
    }

    public void resetFragment(){
        if(getActivity() != null){
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.detach(this);
            ft.attach(this);
            ft.commit();
        }
    }

    public void scanBarcode(String barcode){
        AppLoading.getInstance().showLoading(activity);
        JSONBuilder body = new JSONBuilder();
        body.add("hasil_scan", barcode);
        body.add("total_bayar", nominal);
        body.add("cara_bayar", id_cara_bayar);

        ApiVolleyManager.getInstance().addRequest(activity, Constant.URL_QR_SCAN_MERCHANT,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(activity), body.create(),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        Toast.makeText(activity, "Pembayaran berhasil", Toast.LENGTH_SHORT).show();

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }
                }));
    }

    private void showDialog(){
        if(listCaraBayar == null){
            Toast.makeText(activity, "Data pembayaran belum termuat", Toast.LENGTH_SHORT).show();
            loadCaraBayar();
            return;
        }

        final Dialog dialog = DialogFactory.getInstance().createDialog(activity, R.layout.popup_qr,
                70, 50);

        final EditText txt_nominal = dialog.findViewById(R.id.txt_nominal);
        final AppCompatSpinner spn_cara_bayar = dialog.findViewById(R.id.spn_cara_bayar);
        Button btn_ok = dialog.findViewById(R.id.btn_ok);

        adapterCaraBayar = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, listCaraBayar);
        spn_cara_bayar.setAdapter(adapterCaraBayar);
        if(listCaraBayar == null){
            loadCaraBayar();
        }

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txt_nominal.getText().toString().equals("") ||
                        Double.parseDouble(txt_nominal.getText().toString()) <= 0){
                    Toast.makeText(activity, "Nominal tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }
                else if(spn_cara_bayar.getSelectedItemPosition() == -1){
                    Toast.makeText(activity, "Cara bayar belum dipilih", Toast.LENGTH_SHORT).show();
                }
                else{

                    nominal = Double.parseDouble(txt_nominal.getText().toString());
                    id_cara_bayar = listCaraBayar.get(spn_cara_bayar.getSelectedItemPosition()).getId();

                    //Buka Activity scan barcode
                    IntentIntegrator integrator = new IntentIntegrator(getActivity());
                    integrator.setOrientationLocked(true);
                    //integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
                    integrator.setPrompt("Baca barcode");
                    //integrator.setCameraId(0);  // Use a specific camera of the device
                    integrator.setBeepEnabled(true);
                    //integrator.setBarcodeImageEnabled(true);
                    integrator.initiateScan();

                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }
}
