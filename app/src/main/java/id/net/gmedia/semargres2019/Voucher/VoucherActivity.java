package id.net.gmedia.semargres2019.Voucher;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.Converter;
import com.leonardus.irfan.JSONBuilder;
import com.leonardus.irfan.LoadMoreScrollListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.semargres2019.Util.Constant;
import id.net.gmedia.semargres2019.R;

public class VoucherActivity extends AppCompatActivity {

    private List<VoucherModel> listVoucher = new ArrayList<>();
    private VoucherAdapter adapter;
    private LoadMoreScrollListener loadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Voucher");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        RecyclerView rv_voucher = findViewById(R.id.rv_voucher);
        rv_voucher.setItemAnimator(new DefaultItemAnimator());
        rv_voucher.setLayoutManager(new LinearLayoutManager(this));
        adapter = new VoucherAdapter(this, listVoucher);
        rv_voucher.setAdapter(adapter);
        loadManager = new LoadMoreScrollListener() {
            @Override
            public void onLoadMore() {
                loadVoucher(false);
            }
        };
        rv_voucher.addOnScrollListener(loadManager);

        loadVoucher(true);
    }

    public void useVoucher(String id){
        AppLoading.getInstance().showLoading(this);
        JSONBuilder body = new JSONBuilder();
        body.add("id", id);

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_VOUCHER_USE,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(this),
                body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        Toast.makeText(VoucherActivity.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        Toast.makeText(VoucherActivity.this, result, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();

                        loadVoucher(true);
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(VoucherActivity.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }
                }));
    }

    private void loadVoucher(final boolean init){
        if(init){
            AppLoading.getInstance().showLoading(this);
            loadManager.initLoad();
        }

        final int LOAD_COUNT = 20;
        JSONBuilder body = new JSONBuilder();
        body.add("start", loadManager.getLoaded());
        body.add("limit", LOAD_COUNT);
        body.add("search", "");

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_VOUCHER_LIST,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(this),
                body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        if(init){
                            listVoucher.clear();
                            adapter.notifyDataSetChanged();
                        }

                        loadManager.finishLoad(0);
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            if(init){
                                listVoucher.clear();
                            }

                            JSONArray response = new JSONObject(result).getJSONArray("vouchers");
                            for(int i = 0; i < response.length(); i++){
                                JSONObject voucher = response.getJSONObject(i);
                                listVoucher.add(new VoucherModel(voucher.getString("id"),
                                        voucher.getString("nama_merchant"),
                                        voucher.getDouble("nominal"),
                                        Converter.stringDTSToDate(voucher.getString("valid_start")),
                                        Converter.stringDTSToDate(voucher.getString("valid_end")),
                                        voucher.getString("tipe").equals("P")?VoucherModel.PERSEN:VoucherModel.RUPIAH));
                            }

                            adapter.notifyDataSetChanged();
                            loadManager.finishLoad(response.length());
                        }
                        catch (JSONException e){
                            Toast.makeText(VoucherActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                            loadManager.finishLoad(0);
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(VoucherActivity.this, message, Toast.LENGTH_SHORT).show();
                        loadManager.finishLoad(0);
                        AppLoading.getInstance().stopLoading();
                    }
                }));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
