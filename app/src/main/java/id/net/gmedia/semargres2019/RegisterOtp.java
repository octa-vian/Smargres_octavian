package id.net.gmedia.semargres2019;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.octa.vian.ApiVolleyManager;
import com.octa.vian.AppLoading;
import com.octa.vian.AppRequestCallback;
import com.octa.vian.Converter;
import com.octa.vian.DialogFactory;
import com.octa.vian.JSONBuilder;
import com.octa.vian.SimpleObjectModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import id.net.gmedia.semargres2019.Util.AppSharedPreferences;
import id.net.gmedia.semargres2019.Util.Constant;

public class RegisterOtp extends AppCompatActivity {

    private ImageView ivNoKTP, ivNama, ivTempatLahir, ivTanggalLahir, ivAlamat, ivEmail, ivTelepon, ivJenisKelamin;
    private EditText txt_ktp, txt_nama, txt_tempat_lahir, txt_alamat, txt_email, txt_telepon, txt_tanggal_lahir, txt_bulan_lahir, txt_tahun_lahir;
    private Spinner spn_gender;

    private String id_gender = "";

    private List<SimpleObjectModel> listGender = new ArrayList<>();
    private ArrayAdapter<SimpleObjectModel> adapter;
    private String fcm_id = "";
    private String nomer = "";
    private String uid= "";
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Registrasi");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        bundle = getIntent().getExtras();
        if (bundle!=null){
            nomer = bundle.getString("no_telp");
            uid = bundle.getString("uid");
        }

        ivNoKTP = findViewById(R.id.iv_no_ktp);
        ivNama = findViewById(R.id.iv_nama);
        ivTempatLahir = findViewById(R.id.iv_tempat_lahir);
        ivTanggalLahir = findViewById(R.id.iv_tanggal_lahir);
        ivAlamat = findViewById(R.id.iv_alamat);
        ivEmail = findViewById(R.id.iv_email);
        ivTelepon = findViewById(R.id.iv_no_telp);
        ivTelepon.setImageResource(R.mipmap.ic_cb_active);
        ivJenisKelamin = findViewById(R.id.iv_jenis_kelamin);

        txt_ktp = findViewById(R.id.txt_ktp);
        txt_nama = findViewById(R.id.txt_nama);
        txt_tempat_lahir = findViewById(R.id.txt_tempat_lahir);
        txt_tanggal_lahir = findViewById(R.id.txt_tanggal_lahir);
        txt_bulan_lahir = findViewById(R.id.txt_bulan_lahir);
        txt_tahun_lahir = findViewById(R.id.txt_tahun_lahir);
        txt_alamat = findViewById(R.id.txt_alamat);
        txt_email = findViewById(R.id.txt_email);
        txt_telepon = findViewById(R.id.txt_telepon);
        txt_telepon.setText(nomer);
        txt_telepon.setEnabled(false);
        spn_gender = findViewById(R.id.spn_gender);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listGender);
        spn_gender.setAdapter(adapter);


        initEvent();
       initGender();
        //initGenders();
        //initProfil();
        //initData();
    }

   /* private void initProfil(){
        AppLoading.getInstance().showLoading(this);
        ApiVolleyManager.getInstance().addSecureRequest(this, Constant.URL_PROFIL, ApiVolleyManager.METHOD_GET,
                Constant.getTokenHeader(this), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            JSONObject response = new JSONObject(result);
                            txt_ktp.setText(response.getString("no_ktp"));
                            txt_nama.setText(response.getString("profile_name"));
                            txt_tempat_lahir.setText(response.getString("tempat_lahir"));
                            Date lahir = Converter.stringDToDate(response.getString("tgl_lahir"));
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(lahir);
                            txt_tanggal_lahir.setText(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));
                            txt_bulan_lahir.setText(String.valueOf(cal.get(Calendar.MONTH) + 1));
                            txt_tahun_lahir.setText(String.valueOf(cal.get(Calendar.YEAR)));
                            txt_alamat.setText(response.getString("alamat"));
                            txt_email.setText(response.getString("email"));
                            txt_telepon.setText(response.getString("no_telp"));
                            String id_gender = response.getString("id_gender");
                            if(listGender.size() > 0 && adapter != null){
                                for(int i = 0; i < listGender.size(); i++){
                                    if(listGender.get(i).getId().equals(id_gender)){
                                        spn_gender.setSelection(i);
                                    }
                                }
                            }
                            //spn_gender = findViewById(R.id.spn_gender);
                        }
                        catch (JSONException e){
                            Toast.makeText(RegisterOtp.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        AppLoading.getInstance().stopLoading();
                    }
                }));
    }
*/

    private void initEvent() {

        txt_ktp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(editable.length() == 0){
                    ivNoKTP.setImageResource(R.mipmap.ic_cb_uncheck);
                }else{
                    ivNoKTP.setImageResource(R.mipmap.ic_cb_active);
                }
            }
        });

        txt_nama.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(editable.length() == 0){
                    ivNama.setImageResource(R.mipmap.ic_cb_uncheck);
                }else{
                    ivNama.setImageResource(R.mipmap.ic_cb_active);
                }
            }
        });

        txt_tanggal_lahir.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(editable.length() == 0){
                    ivTanggalLahir.setImageResource(R.mipmap.ic_cb_uncheck);
                }else{
                    ivTanggalLahir.setImageResource(R.mipmap.ic_cb_active);
                }
            }
        });

        txt_tempat_lahir.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(editable.length() == 0){
                    ivTempatLahir.setImageResource(R.mipmap.ic_cb_uncheck);
                }else{
                    ivTempatLahir.setImageResource(R.mipmap.ic_cb_active);
                }
            }
        });

        txt_alamat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(editable.length() == 0){
                    ivAlamat.setImageResource(R.mipmap.ic_cb_uncheck);
                }else{
                    ivAlamat.setImageResource(R.mipmap.ic_cb_active);
                }
            }
        });

        txt_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(editable.length() == 0){
                    ivEmail.setImageResource(R.mipmap.ic_cb_uncheck);
                }else{
                    ivEmail.setImageResource(R.mipmap.ic_cb_active);
                }
            }
        });

        txt_telepon.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(editable.length() == 0){
                    ivTelepon.setImageResource(R.mipmap.ic_cb_uncheck);
                }else{
                    ivTelepon.setImageResource(R.mipmap.ic_cb_active);
                }
            }
        });


       /* spn_gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ivJenisKelamin.setImageResource(R.mipmap.ic_cb_active);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                ivJenisKelamin.setImageResource(R.mipmap.ic_cb_uncheck);
            }
        });*/


        // Edit Tanggal
        txt_tanggal_lahir.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(txt_tanggal_lahir.getText().toString().length() > 0 &&
                        txt_bulan_lahir.getText().toString().length() > 0 &&
                        txt_tahun_lahir.getText().toString().length() == 4){

                    ivTanggalLahir.setImageResource(R.mipmap.ic_cb_active);
                }else{
                    ivTanggalLahir.setImageResource(R.mipmap.ic_cb_uncheck);
                }
            }
        });

        txt_bulan_lahir.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(txt_tanggal_lahir.getText().toString().length() > 0 &&
                        txt_bulan_lahir.getText().toString().length() > 0 &&
                        txt_tahun_lahir.getText().toString().length() == 4){

                    ivTanggalLahir.setImageResource(R.mipmap.ic_cb_active);
                }else{
                    ivTanggalLahir.setImageResource(R.mipmap.ic_cb_uncheck);
                }
            }
        });

        txt_tahun_lahir.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(txt_tanggal_lahir.getText().toString().length() > 0 &&
                        txt_bulan_lahir.getText().toString().length() > 0 &&
                        txt_tahun_lahir.getText().toString().length() == 4){

                    ivTanggalLahir.setImageResource(R.mipmap.ic_cb_active);
                }else{
                    ivTanggalLahir.setImageResource(R.mipmap.ic_cb_uncheck);
                }
            }
        });

        spn_gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ivJenisKelamin.setImageResource(R.mipmap.ic_cb_active);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                ivJenisKelamin.setImageResource(R.mipmap.ic_cb_uncheck);
            }
        });

        findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateBerforeSaving();
            }
        });

        findViewById(R.id.btn_skip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegisterOtp.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });
    }

    private void validateBerforeSaving() {

        if(txt_ktp.getText().toString().length() == 0){

            txt_ktp.setError("Nomor KTP harap diisi");
            txt_ktp.requestFocus();
            return;
        }else{
            txt_ktp.setError(null);
        }

        if(txt_nama.getText().toString().length() == 0){

            txt_nama.setError("Nama harap diisi");
            txt_nama.requestFocus();
            return;
        }else{
            txt_nama.setError(null);
        }

        if(txt_tempat_lahir.getText().toString().length() == 0){

            txt_tempat_lahir.setError("Tempat lahir harap diisi");
            txt_tempat_lahir.requestFocus();
            return;
        }else{
            txt_tempat_lahir.setError(null);
        }

        if(Converter.getInteger(txt_tanggal_lahir.getText().toString()) > 31 ||
                Converter.getInteger(txt_tanggal_lahir.getText().toString()) <= 0){

            txt_tanggal_lahir.setError("Format Tanggal tidak benar");
            txt_tanggal_lahir.requestFocus();
            return;
        }else{
            txt_tanggal_lahir.setError(null);
        }

        if(Converter.getInteger(txt_bulan_lahir.getText().toString()) > 12 ||
                Converter.getInteger(txt_bulan_lahir.getText().toString()) <= 0){

            txt_bulan_lahir.setError("Format bulan tidak benar");
            txt_bulan_lahir.requestFocus();
            return;
        }else{
            txt_bulan_lahir.setError(null);
        }

        if(Converter.getInteger(txt_tahun_lahir.getText().toString()) > 2019 ||
                txt_tahun_lahir.getText().toString().length() < 4){

            txt_tahun_lahir.setError("Format tahun tidak benar");
            txt_tahun_lahir.requestFocus();
            return;
        }else{
            txt_tahun_lahir.setError(null);
        }

        if(txt_alamat.getText().toString().length() == 0){

            txt_alamat.setError("Alamat harap diisi");
            txt_alamat.requestFocus();
            return;
        }else{
            txt_alamat.setError(null);
        }

        if(txt_email.getText().toString().length() == 0){

            txt_email.setError("Email harap diisi");
            txt_email.requestFocus();
            return;
        }else{
            txt_email.setError(null);
        }

        /*if(txt_telepon.getText().toString().length() == 0){

            txt_telepon.setError("Nomor telepon harap diisi");
            txt_telepon.requestFocus();
            return;
        }else{
            txt_telepon.setError(null);
        }*/

        if(spn_gender.getSelectedItemPosition() < 0){
            Toast.makeText(this, "Data jenis kelamin belum termuat", Toast.LENGTH_LONG).show();
            return;
        }

        //proses pentimpanan data
        new AlertDialog.Builder(this)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle("Konfirmasi")
                .setMessage("Anda yakin ingin menyimpan data?")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        saveData();
                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }

    private void inputOtp(){
        final Dialog dialog = DialogFactory.getInstance().createDialog(this, R.layout.popup_otp,
                85, 50, new DialogFactory.DialogActionListener() {
                    @Override
                    public void onBackPressed(Dialog d) {
                        d.dismiss();
                    }
                });

        final EditText txt_otp = dialog.findViewById(R.id.txt_otp);
        dialog.findViewById(R.id.btn_cek).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = txt_otp.getText().toString();
                kirimOtp(dialog, txt_telepon.getText().toString(), otp);
            }
        });

        dialog.findViewById(R.id.btn_request).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestOtp(txt_telepon.getText().toString());
            }
        });

        dialog.setCancelable(false);
        dialog.show();
    }

    private void requestOtp(String nomor){
        JSONBuilder body = new JSONBuilder();
        body.add("no_telp", nomor);

        ApiVolleyManager.getInstance().addSecureRequest(this, Constant.URL_REQUEST_OTP,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(this), body.create(),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        Toast.makeText(RegisterOtp.this, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(String result) {
                        Log.d(Constant.TAG, result);
                        Toast.makeText(RegisterOtp.this, "OTP dikirimkan", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(RegisterOtp.this, message, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void kirimOtp(final Dialog dialog, String nomor, String kode){
        JSONBuilder body = new JSONBuilder();
        body.add("no_telp", nomor);
        body.add("kode_otp", kode);

        ApiVolleyManager.getInstance().addSecureRequest(this, Constant.URL_CEK_OTP,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(this), body.create(),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        Toast.makeText(RegisterOtp.this, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(String result) {
                        saveData();
                        dialog.dismiss();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(RegisterOtp.this, message, Toast.LENGTH_SHORT).show();
                    }

                }));
    }

    private void saveData() {
        AppLoading.getInstance().showLoading(this);
        JSONBuilder jBody = new JSONBuilder();
        jBody.add("uid", uid);
        jBody.add("fcm_id",AppSharedPreferences.getFcmId(RegisterOtp.this));
        jBody.add("email", txt_email.getText().toString());
        jBody.add("profile_name", txt_nama.getText().toString());
        jBody.add("tgl_lahir", txt_tahun_lahir.getText().toString() + "-" +
                txt_bulan_lahir.getText().toString() + "-" + txt_tanggal_lahir.getText().toString());
        jBody.add("no_telp", txt_telepon.getText().toString());
        jBody.add("nik", txt_ktp.getText().toString());
        jBody.add("alamat", txt_alamat.getText().toString());
        jBody.add("jenis_kelamin", listGender.get(spn_gender.getSelectedItemPosition()).getId());
        jBody.add("tempat_lahir", txt_tempat_lahir.getText().toString());

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_REGISTER_OTP,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(this),
                jBody.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                            Toast.makeText(RegisterOtp.this, message,Toast.LENGTH_LONG).show();
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        Log.d("testing", result);

                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            AppSharedPreferences.Login(RegisterOtp.this,
                                    jsonObject.getString("uid"),
                                    jsonObject.getString("token"), jsonObject.getString("email"));

                            Intent intent = new Intent(RegisterOtp.this, MainActivity.class);
                            startActivity(intent);

                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                      /*Log.d("kokgagal",String.valueOf(e));
                            Toast.makeText(RegisterOtp.this, R.string.error_json,Toast.LENGTH_LONG).show();
                            Log.e(Constant.TAG, e.getMessage());*/

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(RegisterOtp.this, message,Toast.LENGTH_LONG).show();
                        AppLoading.getInstance().stopLoading();
                    }
                }));
    }


    private void initGenders(){
        SimpleObjectModel m = new SimpleObjectModel("1","Laki - Laki");
        listGender.add(m);
        m = new SimpleObjectModel("2","perempuan");
        listGender.add(m);

        /*int position = 0;
        int x = 0;
        for(SimpleObjectModel item: listGender){
            if(item.getValue().equals("1")) {
                position = x;
                break;
            }
            x++;
        }
        spn_gender.setSelection(position, true);*/
    }

    private void initGender(){
        ApiVolleyManager.getInstance().addSecureRequest(this, Constant.URL_GENDER,
                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(this),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {

                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            listGender.clear();
                            JSONArray response = new JSONArray(result);
                            for(int i = 0; i < response.length(); i++){
                                JSONObject gender = response.getJSONObject(i);
                                listGender.add(new SimpleObjectModel(gender.getString("id"),
                                        gender.getString("jenis_kelamin")));
                            }

                            if(!id_gender.equals("")){
                                for(int i = 0; i < listGender.size(); i++){
                                    if(listGender.get(i).getId().equals(id_gender)){
                                        spn_gender.setSelection(i);
                                    }
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            Toast.makeText(RegisterOtp.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(RegisterOtp.this, message, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    /*
    private void getProfile() {

        // Get Profile
        pbProcess.setVisibility(View.VISIBLE);
        ApiVolley request = new ApiVolley(context, new JSONObject(), "GET", ServerURL.getProfile, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                pbProcess.setVisibility(View.GONE);

                try {

                    JSONObject responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");

                    if(iv.parseNullDouble(status) == 200){

                        JSONObject jo = responseAPI.getJSONObject("response");

                        edtNoKTP.setText(jo.getString("no_ktp"));
                        if(jo.getString("profile_name").length() == 0){
                            edtNama.setText(session.getNama());
                        }else{
                            edtNama.setText(jo.getString("profile_name"));
                        }

                        edtEmail.setText(session.getEmail().length() > 0 ? session.getEmail() : jo.getString("email"));
                        edtTempatLahir.setText(jo.getString("tempat_lahir"));
                        //edtTanggalLahir.setText(iv.ChangeFormatDateString(jo.getString("tgl_lahir"), FormatItem.formatDate, FormatItem.formatDateDisplay));
                        edtDay.setText(iv.ChangeFormatDateString(jo.getString("tgl_lahir"), FormatItem.formatDate, FormatItem.formatDay));
                        edtMonth.setText(iv.ChangeFormatDateString(jo.getString("tgl_lahir"), FormatItem.formatDate, FormatItem.formatMonth));
                        edtYear.setText(iv.ChangeFormatDateString(jo.getString("tgl_lahir"), FormatItem.formatDate, FormatItem.formatYear));
                        edtAlamat.setText(jo.getString("alamat"));
                        edtTelepon.setText(jo.getString("no_telp"));

                        String selectedJenisKelaminJ = jo.getString("id_gender");
                        String selectedAgamaJ = jo.getString("id_agama");
                        String selectedStatusNikahJ = jo.getString("id_marriage");
                        String selectedPekerjaanJ = jo.getString("id_pekerjaan");

                        // Jenis Kelamin
                        int position = 0;
                        int x = 0;
                        for(OptionItem item: listJenisKelamin){
                            if(item.getValue().equals(selectedJenisKelaminJ)) {
                                position = x;
                                break;
                            }
                            x++;
                        }
                        spJenisKelamin.setSelection(position, true);

                        // Agama
                        *//*position = 0;
                        x = 0;
                        for(OptionItem item: listAgama){
                            if(item.getValue().equals(selectedAgamaJ)) {
                                position = x;
                                break;
                            }
                            x++;
                        }
                        spAgama.setSelection(position, true);

                        // status marital
                        position = 0;
                        x = 0;
                        for(OptionItem item: listStatusNikah){
                            if(item.getValue().equals(selectedStatusNikahJ)) {
                                position = x;
                                break;
                            }
                            x++;
                        }
                        spStatusPernikahan.setSelection(position, true);

                        // Pekerjaan
                        position = 0;
                        x = 0;
                        for(OptionItem item: listPekerjaan){
                            if(item.getValue().equals(selectedPekerjaanJ)) {
                                position = x;
                                break;
                            }
                            x++;
                        }
                        spPekerjaan.setSelection(position, true);*//*

                    }else{
                        //showErrorDialog();
                    }

                } catch (JSONException e) {

                    showErrorDialog();
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {

                pbProcess.setVisibility(View.GONE);
                showErrorDialog();
            }
        });
    }*/

    /*private void showLogoutDialog() {

        AlertDialog builder = new AlertDialog.Builder(ProfileActivity.this)
                .setIcon(getResources().getDrawable(R.mipmap.ic_launcher))
                .setTitle("Konfirmasi")
                .setMessage("Apakah anda yakin ingin logout?")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        logOut();
                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
    }*/

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_profile, menu);

        return super.onCreateOptionsMenu(menu);
    }*/

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logout:
                showLogoutDialog();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    /*@Override
    public void onBackPressed() {

        if(isEdit){

            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
        }else{

            logOut();
        }

        *//*super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);*//*
    }*/

    /*private void logOut(){

        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.putExtra("logout", true);
        session.logoutUser(intent);
    }*/

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
