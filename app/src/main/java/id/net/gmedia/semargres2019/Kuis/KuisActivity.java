package id.net.gmedia.semargres2019.Kuis;

import android.app.Dialog;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.octa.vian.ApiVolleyManager;
import com.octa.vian.AppLoading;
import com.octa.vian.AppRequestCallback;
import com.octa.vian.Converter;
import com.octa.vian.DialogFactory;
import com.octa.vian.JSONBuilder;
import com.octa.vian.LoadMoreScrollListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import id.net.gmedia.semargres2019.Util.Constant;
import id.net.gmedia.semargres2019.R;

public class KuisActivity extends AppCompatActivity {

    private int tab_active = 0;

    //variabel data
    private List<KuisModel> listKuis = new ArrayList<>();
    private List<KuisModel> listDijawab = new ArrayList<>();
    private List<KuisModel> listSelesai = new ArrayList<>();

    //variabel Penampil data
    private RecyclerView rv_kuis;
    private LoadMoreScrollListener loadManager;
    private KuisAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kuis);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Kuis");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        final TabLayout tab_kuis = findViewById(R.id.tab_kuis);
        tab_kuis.addTab(tab_kuis.newTab().setText("Berlangsung"));
        tab_kuis.addTab(tab_kuis.newTab().setText("Selesai"));
        tab_kuis.addTab(tab_kuis.newTab().setText("Dijawab"));
        tab_kuis.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab_active = tab.getPosition();
                switchKuis();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //Inisialisasi Recycler View
        rv_kuis = findViewById(R.id.rv_kuis);
        rv_kuis.setLayoutManager(new LinearLayoutManager(this));
        rv_kuis.setItemAnimator(new DefaultItemAnimator());
        adapter = new KuisAdapter(this, listKuis, KuisAdapter.KUIS_BERLANGSUNG);
        rv_kuis.setAdapter(adapter);
        loadManager = new LoadMoreScrollListener() {
            @Override
            public void onLoadMore() {
                if(tab_active == 0){
                    loadKuis(false);
                }
                else if(tab_active == 1){
                    loadSelesai(false);
                }
                else{
                    loadDijawab(false);
                }
            }
        };
        rv_kuis.addOnScrollListener(loadManager);

        if(getIntent().hasExtra(Constant.EXTRA_START_KUIS)){
            boolean berlangsung = getIntent().getBooleanExtra(Constant.EXTRA_START_KUIS, true);
            if(!berlangsung){
                Objects.requireNonNull(tab_kuis.getTabAt(2)).select();
            }
            else{
                loadKuis(true);
            }
        }
        else{
            loadKuis(true);
        }
    }

    private void switchKuis(){
        if(tab_active == 0){
            loadKuis(true);
        }
        else if(tab_active == 1){
            loadSelesai(true);
        }
        else{
            loadDijawab(true);
        }
    }

    public void jawabKuis(final String id){
        final Dialog dialog = DialogFactory.getInstance().createDialog(this,
                R.layout.popup_kuis, 75, 50);
        TextView txt_cancel = dialog.findViewById(R.id.txt_cancel);
        final EditText txt_jawaban = dialog.findViewById(R.id.txt_jawaban);
        Button btn_kirim = dialog.findViewById(R.id.btn_kirim);

        txt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        btn_kirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txt_jawaban.getText().toString().equals("")){
                    Toast.makeText(KuisActivity.this, "Jawaban tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }
                else{
                    kirimJawaban(dialog, id, txt_jawaban.getText().toString());
                }
            }
        });
        dialog.show();
    }

    private void kirimJawaban(final Dialog dialog, String id, String jawaban){
        AppLoading.getInstance().showLoading(this);
        JSONBuilder body = new JSONBuilder();
        body.add("id_quiz", id);
        body.add("jawaban", jawaban);

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_KUIS_JAWAB,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(this),
                body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        Toast.makeText(KuisActivity.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        Toast.makeText(KuisActivity.this, result, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                        dialog.dismiss();

                        loadKuis(true);
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(KuisActivity.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }
                }));
    }

    private void loadDijawab(final boolean init){
        if(init){
            loadManager.initLoad();
            AppLoading.getInstance().showLoading(this);
        }

        final int LOAD_COUNT = 20;
        JSONBuilder body = new JSONBuilder();
        body.add("start", loadManager.getLoaded());
        body.add("limit", LOAD_COUNT);

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_KUIS_DIJAWAB,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(this), body.create(),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        if(init){
                            listDijawab.clear();
                            adapter = new KuisAdapter(KuisActivity.this, listDijawab, KuisAdapter.KUIS_DIJAWAB);
                            rv_kuis.setAdapter(adapter);
                        }

                        AppLoading.getInstance().stopLoading();
                        loadManager.finishLoad(0);
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            if(init){
                                listDijawab.clear();
                            }

                            JSONArray quiz = new JSONObject(result).getJSONArray("quiz");
                            for(int i = 0; i < quiz.length(); i++){
                                JSONObject kuis = quiz.getJSONObject(i);
                                listDijawab.add(new KuisDijawabModel(kuis.getString("id"),
                                        kuis.getString("nama_merchant"), kuis.getString("soal"),
                                        kuis.getString("jawaban"), Converter.stringDTSToDate
                                        (kuis.getString("answered_at")), kuis.getInt("is_win")==1));
                            }

                            loadManager.finishLoad(quiz.length());
                            adapter = new KuisAdapter(KuisActivity.this, listDijawab, KuisAdapter.KUIS_DIJAWAB);
                            rv_kuis.setAdapter(adapter);
                        }
                        catch (JSONException e){
                            Toast.makeText(KuisActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                            loadManager.finishLoad(0);
                        }
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(KuisActivity.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                        loadManager.finishLoad(0);
                    }
                }));
    }

    private void loadKuis(final boolean init){
        if(init){
            loadManager.initLoad();
            AppLoading.getInstance().showLoading(this);
        }

        final int LOAD_COUNT = 20;
        JSONBuilder body = new JSONBuilder();
        body.add("start", loadManager.getLoaded());
        body.add("limit", LOAD_COUNT);

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_KUIS_LIST,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(this), body.create(),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        if(init){
                            listKuis.clear();
                            adapter = new KuisAdapter(KuisActivity.this,
                                    listKuis, KuisAdapter.KUIS_BERLANGSUNG);
                            rv_kuis.setAdapter(adapter);
                        }

                        AppLoading.getInstance().stopLoading();
                        loadManager.finishLoad(0);
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            if(init){
                                listKuis.clear();
                            }

                            JSONArray quiz = new JSONObject(result).getJSONArray("quiz");
                            for(int i = 0; i < quiz.length(); i++){
                                JSONObject kuis = quiz.getJSONObject(i);
                                listKuis.add(new KuisModel(kuis.getString("id"), kuis.getString("nama_merchant"),
                                        kuis.getString("soal"), Converter.stringDToDate(kuis.getString("periode_start")),
                                        Converter.stringDToDate(kuis.getString("periode_end")),kuis.getString("hadiah")));
                            }

                            loadManager.finishLoad(quiz.length());
                            adapter = new KuisAdapter(KuisActivity.this, listKuis, KuisAdapter.KUIS_BERLANGSUNG);
                            rv_kuis.setAdapter(adapter);
                        }
                        catch (JSONException e){
                            Toast.makeText(KuisActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                            loadManager.finishLoad(0);
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(KuisActivity.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                        loadManager.finishLoad(0);
                    }
                }));
    }

    private void loadSelesai(final boolean init){
        if(init){
            loadManager.initLoad();
            AppLoading.getInstance().showLoading(this);
        }

        final int LOAD_COUNT = 20;
        JSONBuilder body = new JSONBuilder();
        body.add("start", loadManager.getLoaded());
        body.add("limit", LOAD_COUNT);

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_KUIS_SELESAI,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(this), body.create(),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        if(init){
                            listSelesai.clear();
                            adapter = new KuisAdapter(KuisActivity.this, listSelesai, KuisAdapter.KUIS_SELESAI);
                            rv_kuis.setAdapter(adapter);
                        }

                        AppLoading.getInstance().stopLoading();
                        loadManager.finishLoad(0);
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            if(init){
                                listSelesai.clear();
                            }

                            JSONArray quiz = new JSONObject(result).getJSONArray("quiz");
                            for(int i = 0; i < quiz.length(); i++){
                                JSONObject kuis = quiz.getJSONObject(i);
                                listSelesai.add(new KuisSelesaiModel(kuis.getString("id"),
                                        kuis.getString("nama_merchant"), kuis.getString("soal"),
                                        Converter.stringDToDate(kuis.getString("tanggal_mulai")),
                                        Converter.stringDToDate(kuis.getString("tanggal_selesai")),
                                        kuis.getString("hadiah"), kuis.getJSONObject("pemenang").getString("nama"),
                                        kuis.getJSONObject("pemenang").getString("jawaban"),
                                        kuis.getJSONObject("pemenang").getString("email")));
                            }

                            loadManager.finishLoad(quiz.length());
                            adapter = new KuisAdapter(KuisActivity.this, listSelesai, KuisAdapter.KUIS_SELESAI);
                            rv_kuis.setAdapter(adapter);
                        }
                        catch (JSONException e){
                            Toast.makeText(KuisActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                            loadManager.finishLoad(0);
                        }
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(KuisActivity.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                        loadManager.finishLoad(0);
                    }
                }));
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
