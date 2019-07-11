package id.net.gmedia.semargres2019.Merchant;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.JSONBuilder;
import com.leonardus.irfan.LoadMoreScrollListener;
import com.leonardus.locationmanager.GoogleLocationManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.semargres2019.Util.Constant;
import id.net.gmedia.semargres2019.R;

public class MerchantActivity extends AppCompatActivity {

    public String kategori_id = "";
    private String search = "";

    private MerchantAdapter adapter;
    private List<MerchantModel> listTempat = new ArrayList<>();
    private LoadMoreScrollListener loadManager;

    private SearchView searchView;

    private GoogleLocationManager locationManager;

    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant);

        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("");
            if(getIntent().hasExtra(Constant.EXTRA_KATEGORI_ID)){
                getSupportActionBar().setTitle(getIntent().getStringExtra(Constant.EXTRA_KATEGORI_NAMA));
            }
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        EditText txt_search = findViewById(R.id.txt_search);

        RecyclerView rv_tempat = findViewById(R.id.rv_terdekat);
        rv_tempat.setItemAnimator(new DefaultItemAnimator());
        rv_tempat.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MerchantAdapter(this, listTempat);
        rv_tempat.setAdapter(adapter);
        loadManager = new LoadMoreScrollListener() {
            @Override
            public void onLoadMore() {
                loadMerchant(false);
            }
        };

        if(getIntent().hasExtra(Constant.EXTRA_KATEGORI_ID)){
            kategori_id = getIntent().getStringExtra(Constant.EXTRA_KATEGORI_ID);
        }

        locationManager = new GoogleLocationManager(this,
                new GoogleLocationManager.LocationUpdateListener() {
                    @Override
                    public void onChange(Location location) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        loadMerchant(true);
                    }
                });
        locationManager.startLocationUpdates();

        txt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                search = s.toString();
                loadMerchant(true);
            }
        });

        AppLoading.getInstance().showLoading(this,
                new AppLoading.CancelListener() {
            @Override
            public void onCancel() {
                onBackPressed();
            }
        });
    }

    private void loadMerchant(final boolean init){
        if(init){
            loadManager.initLoad();
        }

        JSONBuilder body = new JSONBuilder();
        body.add("latitude", latitude);
        body.add("longitude", longitude);
        body.add("jarak", 10);
        body.add("start", loadManager.getLoaded());
        body.add("kategori", kategori_id);
        body.add("count", 10);
        body.add("keyword", search);

        ApiVolleyManager.getInstance().addSecureRequest(this, Constant.URL_MERCHANT_TERDEKAT_IKLAN, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(this), body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        if(init){
                            listTempat.clear();
                            adapter.notifyDataSetChanged();
                        }

                        loadManager.finishLoad(0);
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            if(init){
                                listTempat.clear();
                            }

                            JSONArray response = new JSONArray(result);

                            for(int i = 0; i < response.length(); i++){
                                JSONObject tempat = response.getJSONObject(i);
                                if(tempat.getString("flag_tipe").equals("merchant")){
                                    double latitude = tempat.getString("latitude").equals("")?0:tempat.getDouble("latitude");
                                    double longitude = tempat.getString("longitude").equals("")?0:tempat.getDouble("longitude");

                                    listTempat.add(new MerchantModel(tempat.getString("id_m"),
                                            tempat.getString("nama"), tempat.getString("alamat"),
                                            tempat.getString("foto"), latitude, longitude));
                                }
                                else if(tempat.getString("flag_tipe").equals("iklan")){
                                    listTempat.add(new MerchantModel(tempat.getString("foto"),
                                            tempat.getString("link")));
                                }
                            }

                            loadManager.finishLoad(response.length());
                            adapter.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            Toast.makeText(MerchantActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                            loadManager.finishLoad(0);
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(MerchantActivity.this, message, Toast.LENGTH_SHORT).show();
                        loadManager.finishLoad(0);
                        AppLoading.getInstance().stopLoading();
                    }
                }));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == GoogleLocationManager.ACTIVATE_LOCATION){
            if(resultCode == RESULT_OK){
                locationManager.startLocationUpdates();
            }
            else{
                AppLoading.getInstance().stopLoading();
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        if (requestCode == GoogleLocationManager.PERMISSION_LOCATION) {
            if (grantResults.length > 0) {
                boolean finePermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                boolean coarsePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                if (finePermission && coarsePermission) {
                    locationManager.startLocationUpdates();
                } else {
                    AppLoading.getInstance().stopLoading();
                }
            } else {
                AppLoading.getInstance().stopLoading();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                search = s;
                loadMerchant(true);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!searchView.isIconified() && TextUtils.isEmpty(s)) {
                    search = "";
                    loadMerchant(true);
                }
                return true;
            }
        });

        return true;
    }


    @Override
    protected void onDestroy() {
        locationManager.stopLocationUpdates();
        super.onDestroy();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
