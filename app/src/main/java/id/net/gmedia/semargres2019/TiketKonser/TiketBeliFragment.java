package id.net.gmedia.semargres2019.TiketKonser;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.Converter;
import com.leonardus.irfan.DialogFactory;
import com.leonardus.irfan.ImageLoader;
import com.leonardus.irfan.JSONBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import id.net.gmedia.semargres2019.Kuis.KuisActivity;
import id.net.gmedia.semargres2019.Util.Constant;
import id.net.gmedia.semargres2019.R;

public class TiketBeliFragment extends Fragment {

    private Activity activity;
    private List<TiketKonserModel> listJenisTiket = new ArrayList<>();
    private ArrayAdapter<TiketKonserModel> adapter;

    private String denah = "";
    private int selected_jenis = -1;

    private SwipeRefreshLayout layout_refresh;
    private LinearLayout layout_diskon;
    private ImageView img_konser;
    private AppCompatSpinner spn_jumlah;
    private EditText txt_promo, txt_referral;
    private TextView txt_total, txt_diskon, lbl_jenis_tiket;
    private CheckBox cb_syarat_ketentuan;
    private String syarat_ketentuan = "";

    //VARIABEL MIDTRANS
   /* private static final String MERCHANT_BASE_CHECKOUT_URL = "http://gmedia.bz/semargres/konser/prepayment/";
    //SANDBOX
    private static final String MERCHANT_CLIENT_KEY = "SB-Mid-client-0cRh_J3j8_RfFmB2";
    //PRODUCTION
    //public static final String MERCHANT_CLIENT_KEY = "Mid-client-Zed8LaibsmZjq-HN";*/

    public TiketBeliFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = getActivity();
        View v = inflater.inflate(R.layout.fragment_tiket_beli, container, false);

        AppCompatSpinner spn_jenis = v.findViewById(R.id.spn_jenis);
        lbl_jenis_tiket = v.findViewById(R.id.lbl_jenis_tiket);
        layout_diskon = v.findViewById(R.id.layout_diskon);
        img_konser = v.findViewById(R.id.img_konser);
        spn_jumlah = v.findViewById(R.id.spn_jumlah);
        txt_promo = v.findViewById(R.id.txt_promo);
        txt_referral = v.findViewById(R.id.txt_referral);
        txt_diskon = v.findViewById(R.id.txt_diskon);
        txt_diskon.setText("-");
        txt_total = v.findViewById(R.id.txt_total);
        txt_total.setText(Converter.doubleToRupiah(0));
        cb_syarat_ketentuan = v.findViewById(R.id.cb_syarat_ketentuan);
        layout_refresh = v.findViewById(R.id.layout_refresh);

        adapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, listJenisTiket);
        spn_jenis.setAdapter(adapter);
        spn_jenis.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_jenis = position;
                updateTotal();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        v.findViewById(R.id.btn_beli).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spn_jumlah.getSelectedItemPosition() < 0){
                    Toast.makeText(activity, "Jumlah tiket belum dipilih", Toast.LENGTH_SHORT).show();
                }
                else if(selected_jenis == -1){
                    Toast.makeText(activity, "Jenis tiket belum dipilih", Toast.LENGTH_SHORT).show();
                }
                else if(!cb_syarat_ketentuan.isChecked()){
                    Toast.makeText(activity, "Anda belum menyetujui syarat dan ketentuan", Toast.LENGTH_SHORT).show();
                    cb_syarat_ketentuan.requestFocus();
                }
                else{
                    prepayment();
                }
            }
        });

        spn_jumlah.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateTotal();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        /*txt_jumlah.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(selected_jenis != -1){
                    if(s.toString().equals("")){
                        txt_total.setText(Converter.doubleToRupiah(0));
                    }
                    else{
                        txt_total.setText(Converter.doubleToRupiah(
                                listJenisTiket.get(selected_jenis).getHarga() *
                                Integer.parseInt(txt_jumlah.getText().toString())));
                    }
                }
            }
        });*/

        v.findViewById(R.id.btn_promo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selected_jenis != -1){
                    cekPromo();
                }
                else{
                    Toast.makeText(activity, "Jenis tiket belum dipilih", Toast.LENGTH_SHORT).show();
                }
            }
        });

        v.findViewById(R.id.txt_denah).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!denah.equals("")){
                    Intent i = new Intent(activity, DenahActivity.class);
                    i.putExtra(Constant.EXTRA_DENAH, denah);
                    startActivity(i);
                }
                else{
                    Toast.makeText(activity, "Data denah belum termuat", Toast.LENGTH_SHORT).show();
                }
            }
        });

        v.findViewById(R.id.lbl_syarat_ketentuan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cb_syarat_ketentuan.setChecked(!cb_syarat_ketentuan.isChecked());
            }
        });

        v.findViewById(R.id.txt_syarat_ketentuan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(syarat_ketentuan.isEmpty()){
                    Toast.makeText(activity, "Syarat dan ketentuan belum termuat", Toast.LENGTH_SHORT).show();
                }
                else{
                    showSyaratKetentuan();
                }
            }
        });

        layout_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadJenisTiket();
            }
        });

        loadJenisTiket();

        return v;
    }

    private void showSyaratKetentuan(){
        final Dialog dialog = DialogFactory.getInstance().createDialog(activity, R.layout.popup_syarat_ketentuan,
                90, 70, new DialogFactory.DialogActionListener() {
            @Override
            public void onBackPressed(Dialog dialog) {
                    dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.txt_tutup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        TextView txt_syarat_ketentuan = dialog.findViewById(R.id.txt_syarat_ketentuan);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            txt_syarat_ketentuan.setText(Html.fromHtml(syarat_ketentuan, Html.FROM_HTML_MODE_COMPACT));
        } else {
            txt_syarat_ketentuan.setText(Html.fromHtml(syarat_ketentuan));
        }

        dialog.show();
    }

    private void loadJenisTiket(){
        AppLoading.getInstance().showLoading(activity);
        ApiVolleyManager.getInstance().addSecureRequest(activity, Constant.URL_TIKET_KONSER,
                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(activity),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();

                        if(layout_refresh.isRefreshing()){
                            layout_refresh.setRefreshing(false);
                        }
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            JSONObject konser = new JSONObject(result).getJSONObject("konser");
                            String kategori = new JSONObject(result).getString("kategori");

                            ImageLoader.load(activity, konser.getString("gambar"), img_konser);
                            denah = konser.getString("denah");
                            syarat_ketentuan = konser.getString("syarat_ketentuan");

                            if(!kategori.isEmpty()){
                                String label = "Jenis Tiket (" + kategori + ")";
                                lbl_jenis_tiket.setText(label);
                            }
                            else{
                                lbl_jenis_tiket.setText("Jenis Tiket");
                            }

                            JSONArray jenis_tiket = new JSONObject(result).getJSONArray("jenis_tiket");
                            for(int i = 0; i < jenis_tiket.length(); i++){
                                JSONObject tiket = jenis_tiket.getJSONObject(i);
                                if(tiket.getInt("status_sold_out")==0){
                                    listJenisTiket.add(new TiketKonserModel(tiket.getString("id"),
                                            tiket.getString("nama"), tiket.getDouble("harga"),
                                            tiket.getInt("status_sold_out")==1));
                                }
                            }

                            adapter.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            Toast.makeText(activity, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }

                        AppLoading.getInstance().stopLoading();
                        if(layout_refresh.isRefreshing()){
                            layout_refresh.setRefreshing(false);
                        }
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                        if(layout_refresh.isRefreshing()){
                            layout_refresh.setRefreshing(false);
                        }
                    }
                }));
    }

    private void updateTotal(){
        if(selected_jenis != -1){
            if(txt_promo.getText().toString().isEmpty()){
                txt_total.setText(Converter.doubleToRupiah(
                        listJenisTiket.get(selected_jenis).getHarga() *
                                (spn_jumlah.getSelectedItemPosition() + 1)));
            }
            else{
                cekPromo();
            }
        }
        else{
            txt_total.setText(Converter.doubleToRupiah(0));
        }
    }

    private void cekPromo(){
        AppLoading.getInstance().showLoading(activity, new AppLoading.CancelListener() {
            @Override
            public void onCancel() {

            }
        });

        JSONBuilder body = new JSONBuilder();
        body.add("id_jenis_tiket", listJenisTiket.get(selected_jenis).getId());
        body.add("jumlah", spn_jumlah.getSelectedItemPosition() + 1);
        body.add("kode_promo", txt_promo.getText().toString());

        ApiVolleyManager.getInstance().addSecureRequest(activity, Constant.URL_TIKET_CEK_PROMO,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(activity), body.create(),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        layout_diskon.setVisibility(View.GONE);
                        if(selected_jenis != -1){
                            txt_total.setText(Converter.doubleToRupiah(
                                    listJenisTiket.get(selected_jenis).getHarga() *
                                            (spn_jumlah.getSelectedItemPosition() + 1)));
                        }
                        else{
                            txt_total.setText(Converter.doubleToRupiah(0));
                        }

                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            layout_diskon.setVisibility(View.VISIBLE);
                            JSONObject response = new JSONObject(result);

                            int persen = response.getInt("discount_percent");
                            txt_total.setText(Converter.doubleToRupiah(response.getDouble("gross_amount")));

                            String persen_string = persen + "%";
                            txt_diskon.setText(persen_string);
                        }
                        catch (JSONException e){
                            layout_diskon.setVisibility(View.GONE);

                            Toast.makeText(activity, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        layout_diskon.setVisibility(View.GONE);
                        if(selected_jenis != -1){
                            txt_total.setText(Converter.doubleToRupiah(
                                    listJenisTiket.get(selected_jenis).getHarga() *
                                            (spn_jumlah.getSelectedItemPosition() + 1)));
                        }
                        else{
                            txt_total.setText(Converter.doubleToRupiah(0));
                        }

                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }
                })
        );
    }

    private void prepayment(){
        AlertDialog dialog = new AlertDialog.Builder(activity).setTitle("Konfirmasi pembelian")
                .setMessage("Yakin ingin melanjutkan pembelian ini?")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pembelian();
                    }
                })
                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create();
        dialog.show();
        dialog.getButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE).
                setTextColor(getResources().getColor(R.color.light_gray));
    }

    private void pembelian(){
        AppLoading.getInstance().showLoading(activity);
        JSONBuilder body = new JSONBuilder();
        body.add("id_jenis_tiket", listJenisTiket.get(selected_jenis).getId());
        body.add("jumlah", spn_jumlah.getSelectedItemPosition() + 1);
        body.add("kode_promo", txt_promo.getText().toString());
        body.add("kode_referral", txt_referral.getText().toString());

        ApiVolleyManager.getInstance().addSecureRequest(activity, Constant.URL_TIKET_BELI,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(activity), body.create(),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            JSONObject response = new JSONObject(result);

                            Dialog dialog = DialogFactory.getInstance().createDialog(activity,
                                    R.layout.popup_tiket_beli, 80, 50);

                            TextView txt_total_bayar = dialog.findViewById(R.id.txt_total_bayar);
                            TextView txt_waktu_pembayaran = dialog.findViewById(R.id.txt_waktu_pembayaran);
                            TextView txt_jenis_tiket, txt_jumlah_tiket, txt_email;
                            txt_jenis_tiket = dialog.findViewById(R.id.txt_jenis_tiket);
                            txt_jumlah_tiket = dialog.findViewById(R.id.txt_jumlah_tiket);
                            txt_email = dialog.findViewById(R.id.txt_email);

                            txt_jenis_tiket.setText(response.getString("jenis_tiket"));
                            txt_jumlah_tiket.setText(response.getString("jumlah"));
                            txt_email.setText(response.getString("email"));
                            txt_total_bayar.setText(Converter.doubleToRupiah(response.getDouble("total_payment")));
                            Date waktu_pembayaran = Converter.stringDTSToDate(response.getString("expired_at"));
                            String waktu = String.format(Locale.getDefault(), "%s %s",
                                    Converter.DToStringInverse(waktu_pembayaran), Converter.TToString(waktu_pembayaran));
                            txt_waktu_pembayaran.setText(waktu);

                            dialog.show();
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

    //============================== MIDTRANS ======================================================
    /*private void validasiPembelian(){
        JSONBuilder body = new JSONBuilder();
        body.add("id_jenis_tiket", listJenisTiket.get(selected_jenis).getId());
        body.add("jumlah", txt_jumlah.getText().toString());
        body.add("kode_promo", txt_promo.getText().toString());

        AppLoading.getInstance().showLoading(activity);
        ApiVolleyManager.getInstance().addSecureRequest(activity, Constant.URL_TIKET_BELI,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(activity), body.create(),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            JSONObject transaksi = new JSONObject(result);

                            order_id = transaksi.getString("order_id");
                            diskon = transaksi.getDouble("discount_amount");
                            total = transaksi.getDouble("gross_amount");

                            verifyUser();
                        }
                        catch (JSONException e){
                            Toast.makeText(activity, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());

                            AppLoading.getInstance().stopLoading();
                        }
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }
                }));
    }*/

    /*private void initTransaksi(){
        //Inisialisasi transaksi Midtrans
        SdkUIFlowBuilder.init()
                .setClientKey(MERCHANT_CLIENT_KEY) // client_key is mandatory
                .setContext(activity)// context is mandatory
                .setTransactionFinishedCallback(new TransactionFinishedCallback() {
                    @Override
                    public void onTransactionFinished(TransactionResult result) {
                        if (result.getResponse() != null) {
                            Intent i;
                            switch (result.getStatus()) {
                                case TransactionResult.STATUS_SUCCESS:
                                    Toast.makeText(activity, "Transaksi berhasil", Toast.LENGTH_SHORT).show();
                                    Log.d(Constant.TAG, "Transaction Finished. ID: " + result.getResponse().getTransactionId());

                                    i = new Intent(activity, MainActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);

                                    break;
                                case TransactionResult.STATUS_PENDING:
                                    Toast.makeText(activity, "Menunggu pembayaran", Toast.LENGTH_SHORT).show();
                                    Log.d(Constant.TAG, "Menunggu pembayaran. ID: " + result.getResponse().getTransactionId());

                                    i = new Intent(activity, MainActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);

                                    break;
                                case TransactionResult.STATUS_FAILED:
                                    Toast.makeText(activity, "Transaksi gagal", Toast.LENGTH_SHORT).show();
                                    Log.d(Constant.TAG, "Transaction gagal. ID: " + result.getResponse().getTransactionId() + ". Message: " + result.getResponse().getStatusMessage());
                                    break;
                            }
                            result.getResponse().getValidationMessages();
                        } else if (result.isTransactionCanceled()) {
                            Toast.makeText(activity, "Transaksi dibatalkan", Toast.LENGTH_LONG).show();
                        } else {
                            if (result.getStatus().equalsIgnoreCase(TransactionResult.STATUS_INVALID)) {
                                Toast.makeText(activity, "Transaksi Invalid", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(activity, "Transaksi selesai dengan kegagalan.", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }) // set transaction finish callback (sdk callback)
                .setMerchantBaseUrl(MERCHANT_BASE_CHECKOUT_URL) //set merchant url (required) BASE_URL
                .enableLog(true)// enable sdk log (optional)
                .buildSDK();
    }*/

    /*private void verifyUser(){
        ApiVolleyManager.getInstance().addSecureRequest(activity, Constant.URL_PROFIL, ApiVolleyManager.METHOD_GET,
                Constant.getTokenHeader(activity), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            JSONObject akun = new JSONObject(result);

                            UserDetail userDetail = new UserDetail();
                            userDetail.setUserId(AppSharedPreferences.getUid(activity));
                            userDetail.setUserFullName(akun.getString("profile_name"));
                            userDetail.setEmail(akun.getString("email"));
                            userDetail.setPhoneNumber(akun.getString("no_telp"));

                            ArrayList<UserAddress> userAddresses = new ArrayList<>();

                            //alamat billing
                            UserAddress userAddress = new UserAddress();
                            userAddress.setAddress(akun.getString("alamat"));
                            userAddress.setAddressType(Constants.ADDRESS_TYPE_BILLING);
                            userAddress.setCountry("IDN");
                            userAddresses.add(userAddress);

                            //alamat shipping
                            userAddress = new UserAddress();
                            userAddress.setAddress(akun.getString("alamat"));
                            userAddress.setAddressType(Constants.ADDRESS_TYPE_SHIPPING);
                            userAddress.setCountry("IDN");
                            userAddresses.add(userAddress);

                            userDetail.setUserAddresses(userAddresses);

                            beliTiket();
                        }
                        catch (JSONException e){
                            Log.e(Constant.TAG, e.getMessage());
                            Toast.makeText(activity, R.string.error_json, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }
                }));
    }*/

    /*private void beliTiket(){
        initTransaksi();

        int jumlah = Integer.parseInt(txt_jumlah.getText().toString());
        TiketKonserModel tiket = listJenisTiket.get(selected_jenis);

        ArrayList<ItemDetails> itemDetailsList = new ArrayList<>();
        itemDetailsList.add(new ItemDetails(tiket.getId(), tiket.getHarga(),
                jumlah, tiket.getNama()));
        if(diskon > 0){
            itemDetailsList.add(new ItemDetails("0", -diskon,
                    1, "Diskon " + txt_promo.getText().toString()));
        }

        TransactionRequest transactionRequest = new TransactionRequest(order_id,
                total - diskon);
        transactionRequest.setItemDetails(itemDetailsList);

        BillInfoModel billInfoModel = new BillInfoModel("Semargres 2019", "Transaksi");
        // Set the bill info on transaction details
        transactionRequest.setBillInfoModel(billInfoModel);

        MidtransSDK.getInstance().setTransactionRequest(transactionRequest);
        MidtransSDK.getInstance().startPaymentUiFlow(activity);
        activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        AppLoading.getInstance().stopLoading();
    }*/
}
