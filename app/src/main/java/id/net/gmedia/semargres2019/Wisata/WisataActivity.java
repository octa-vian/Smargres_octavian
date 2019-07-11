package id.net.gmedia.semargres2019.Wisata;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.ImageLoader;
import com.leonardus.irfan.JSONBuilder;
import com.leonardus.irfan.LoadMoreScrollListener;
import com.leonardus.irfan.SimpleSelectableObjectModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.semargres2019.Util.Constant;
import id.net.gmedia.semargres2019.Merchant.MapKategoriAdapter;
import id.net.gmedia.semargres2019.Merchant.MerchantAdapter;
import id.net.gmedia.semargres2019.Merchant.MerchantModel;
import id.net.gmedia.semargres2019.R;
import id.net.gmedia.semargres2019.Util.ScrollableMapView;

public class WisataActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbar;
    private TextView txt_title;

    private TextView txt_nama_wisata, txt_keterangan_wisata;
    private ImageView img_wisata;

    private WisataModel wisata;

    private MerchantAdapter merchantAdapter;
    private MapKategoriAdapter kategoriAdapter;
    private List<MerchantModel> listMerchant = new ArrayList<>();
    private List<SimpleSelectableObjectModel> listKategori = new ArrayList<>();
    private LoadMoreScrollListener loadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wisata);

        toolbar = findViewById(R.id.toolbar);
        collapsingToolbar = findViewById(R.id.main_collapsing);
        txt_title = findViewById(R.id.txt_title);

        txt_nama_wisata = findViewById(R.id.txt_nama_wisata);
        txt_keterangan_wisata = findViewById(R.id.txt_keterangan_wisata);
        img_wisata = findViewById(R.id.img_wisata);

        if(getIntent().hasExtra(Constant.EXTRA_WISATA)){
            Gson gson = new Gson();
            wisata = gson.fromJson(getIntent().
                    getStringExtra(Constant.EXTRA_WISATA), WisataModel.class);
            initWisata(wisata.getId());
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if(mapFragment != null){
            mapFragment.getMapAsync(this);
        }

        initToolbar();

        RecyclerView rv_merchant = findViewById(R.id.rv_merchant);
        rv_merchant.setItemAnimator(new DefaultItemAnimator());
        rv_merchant.setLayoutManager(new LinearLayoutManager(this));
        merchantAdapter = new MerchantAdapter(this, listMerchant);
        rv_merchant.setAdapter(merchantAdapter);
        loadManager = new LoadMoreScrollListener() {
            @Override
            public void onLoadMore() {
                loadTempat(false, wisata.getLatitude(), wisata.getLongitude());
            }
        };

        RecyclerView rv_kategori = findViewById(R.id.rv_kategori);
        rv_kategori.setItemAnimator(new DefaultItemAnimator());
        rv_kategori.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        kategoriAdapter = new MapKategoriAdapter(this, listKategori);
        rv_kategori.setAdapter(kategoriAdapter);

        AppLoading.getInstance().showLoading(this);
        loadKategori();
    }

    private void initWisata(String id){
        txt_nama_wisata.setText(wisata.getNama());
        ImageLoader.load(this, wisata.getFoto(), img_wisata);
        loadDetail(id);
    }

    private void loadDetail(String id){
        String parameter = String.format("/%s", id);

        ApiVolleyManager.getInstance().addSecureRequest(this, Constant.URL_TEMPAT_WISATA_DETAIL + parameter,
                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(this), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        Toast.makeText(WisataActivity.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            String deskripsi = new JSONObject(result).getString("deskripsi");
                            txt_keterangan_wisata.setText(deskripsi);
                        }
                        catch (JSONException e){
                            Toast.makeText(WisataActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(WisataActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    public void setKategori(String id){
        loadTempat(true, wisata.getLatitude(), wisata.getLongitude(), id);
    }

    private void loadTempat(boolean init, double latitude, double longitude){
        loadTempat(init, latitude, longitude, "");
    }

    private void loadKategori(){
        ApiVolleyManager.getInstance().addSecureRequest(this, Constant.URL_KATEGORI,
                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(this),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        listKategori.clear();
                        Toast.makeText(WisataActivity.this, message, Toast.LENGTH_SHORT).show();

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            listKategori.clear();
                            JSONArray response = new JSONArray(result);

                            for(int i = 0; i < response.length(); i++){
                                JSONObject kategori = response.getJSONObject(i);

                                if(i == 0){
                                    setKategori(kategori.getString("id_k"));
                                }
                                listKategori.add(new SimpleSelectableObjectModel(kategori.getString("id_k"),
                                        kategori.getString("nama")));
                            }

                            kategoriAdapter.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            Toast.makeText(WisataActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());

                            AppLoading.getInstance().stopLoading();
                        }
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(WisataActivity.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }
                }));
    }

    private void loadTempat(final boolean init, double latitude, double longitude, String kategori){
        if(init){
            loadManager.initLoad();
            AppLoading.getInstance().showLoading(this);
        }

        JSONBuilder body = new JSONBuilder();
        body.add("latitude", latitude);
        body.add("longitude", longitude);
        body.add("jarak", 10);
        body.add("start", loadManager.getLoaded());
        body.add("count", 10);
        body.add("kategori", kategori);

        ApiVolleyManager.getInstance().addSecureRequest(this, Constant.URL_MERCHANT_TERDEKAT,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(this), body.create(),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        if(init){
                            listMerchant.clear();
                            merchantAdapter.notifyDataSetChanged();
                        }

                        loadManager.finishLoad(0);
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            if(init){
                                listMerchant.clear();
                            }

                            JSONArray response = new JSONArray(result);

                            for(int i = 0; i < response.length(); i++){
                                JSONObject tempat = response.getJSONObject(i);
                                double latitude = tempat.getString("latitude").equals("")?
                                        0:tempat.getDouble("latitude");
                                double longitude = tempat.getString("longitude").equals("")?
                                        0:tempat.getDouble("longitude");

                                listMerchant.add(new MerchantModel(tempat.getString("id_m"),
                                        tempat.getString("nama"), tempat.getString("alamat"),
                                        tempat.getString("foto"), latitude, longitude));
                            }

                            loadManager.finishLoad(response.length());
                            merchantAdapter.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            Toast.makeText(WisataActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                            loadManager.finishLoad(0);
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(WisataActivity.this, message, Toast.LENGTH_SHORT).show();
                        loadManager.finishLoad(0);
                        AppLoading.getInstance().stopLoading();
                    }
                }));
    }

    private void initToolbar(){
        //Inisialisasi Toolbar
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        collapsingToolbar.setTitle(" ");
        txt_title.setText("");
        AppBarLayout appBarLayout = findViewById(R.id.main_appbar);
        appBarLayout.setExpanded(true);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                boolean isShow = false;

                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    if(getSupportActionBar() != null){
                        if (appBarLayout.getTotalScrollRange() + verticalOffset <= getActionBarHeight()) {
                            if(wisata != null){
                                txt_title.setText(wisata.getNama());
                            }
                            isShow = true;
                            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        } else if (isShow) {
                            txt_title.setText("");
                            isShow = false;
                            getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.style_rectangle_gradient_black));
                        }
                    }
                }
            });
        }
    }

    private int getActionBarHeight() {
        int actionBarHeight = 0;
        if(getSupportActionBar() != null){
            actionBarHeight = getSupportActionBar().getHeight();
            if (actionBarHeight != 0)
                return actionBarHeight;
            final TypedValue tv = new TypedValue();
            if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
                actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        final ScrollableMapView mapView = (ScrollableMapView) getSupportFragmentManager().findFragmentById(R.id.map);
        final NestedScrollView main_scroll = findViewById(R.id.main_scroll);
        if(mapView != null){
            mapView.setListener(new ScrollableMapView.OnTouchListener() {
                @Override
                public void onTouch() {
                    main_scroll.requestDisallowInterceptTouchEvent(true);
                }
            });
        }

        if(wisata != null){
            LatLng lokasi = new LatLng(wisata.getLatitude(), wisata.getLongitude());
            googleMap.addMarker(new MarkerOptions().position(lokasi).title("Lokasi"));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lokasi, 15.0f));
        }
    }
}
