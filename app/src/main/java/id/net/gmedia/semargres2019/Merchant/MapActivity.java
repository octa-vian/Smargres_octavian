package id.net.gmedia.semargres2019.Merchant;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.octa.vian.ApiVolleyManager;
import com.octa.vian.AppLoading;
import com.octa.vian.AppRequestCallback;
import com.octa.vian.JSONBuilder;
import com.octa.vian.LoadMoreScrollListener;
import com.octa.vian.SimpleSelectableObjectModel;
import com.leonardus.locationmanager.GoogleLocationManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.semargres2019.Util.Constant;
import id.net.gmedia.semargres2019.R;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private double latitude;
    private double longitude;
    private GoogleMap mMap;

    private GoogleLocationManager locationManager;

    private MerchantAdapter merchantAdapter;
    private MapKategoriAdapter kategoriAdapter;
    private List<MerchantModel> listMerchant = new ArrayList<>();
    private List<SimpleSelectableObjectModel> listKategori = new ArrayList<>();
    private LoadMoreScrollListener loadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if(getIntent().hasExtra(Constant.EXTRA_LATITUDE) &&
                getIntent().hasExtra(Constant.EXTRA_LONGITUDE)){
            latitude = getIntent().getDoubleExtra(Constant.EXTRA_LATITUDE, 0);
            longitude = getIntent().getDoubleExtra(Constant.EXTRA_LONGITUDE, 0);
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if(mapFragment != null){
            mapFragment.getMapAsync(this);
        }

        RecyclerView rv_merchant = findViewById(R.id.rv_merchant);
        rv_merchant.setItemAnimator(new DefaultItemAnimator());
        rv_merchant.setLayoutManager(new LinearLayoutManager(this));
        merchantAdapter = new MerchantAdapter(this, listMerchant);
        rv_merchant.setAdapter(merchantAdapter);
        loadManager = new LoadMoreScrollListener() {
            @Override
            public void onLoadMore() {
                loadTempat(false, latitude, longitude);
            }
        };

        RecyclerView rv_kategori = findViewById(R.id.rv_kategori);
        rv_kategori.setItemAnimator(new DefaultItemAnimator());
        rv_kategori.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        kategoriAdapter = new MapKategoriAdapter(this, listKategori);
        rv_kategori.setAdapter(kategoriAdapter);

        loadKategori();
    }

    public void setKategori(String id){
        loadTempat(true, latitude, longitude, id);
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
                        Toast.makeText(MapActivity.this, message, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(MapActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(MapActivity.this, message, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(MapActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                            loadManager.finishLoad(0);
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(MapActivity.this, message, Toast.LENGTH_SHORT).show();
                        loadManager.finishLoad(0);
                        AppLoading.getInstance().stopLoading();
                    }
                }));
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(latitude != 0 && longitude != 0){
            LatLng lokasi = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions().position(lokasi).title("Lokasi"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lokasi, 15.0f));
        }
        else{
            locationManager = new GoogleLocationManager(this,
                    new GoogleLocationManager.LocationUpdateListener() {
                @Override
                public void onChange(Location location) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    LatLng lokasi = new LatLng(latitude, longitude);
                    mMap.addMarker(new MarkerOptions().position(lokasi).title("Lokasi"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lokasi, 15.0f));

                    locationManager.stopLocationUpdates();
                }
            });
            locationManager.startLocationUpdates();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
