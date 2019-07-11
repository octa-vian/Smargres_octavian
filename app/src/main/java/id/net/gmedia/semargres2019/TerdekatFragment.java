package id.net.gmedia.semargres2019;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.ImageSlider.ImageSlider;
import com.leonardus.irfan.ImageSlider.ImageSliderAdapter;
import com.leonardus.irfan.JSONBuilder;
import com.leonardus.irfan.LoadMoreScrollListener;
import com.leonardus.locationmanager.GoogleLocationManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.semargres2019.Merchant.MerchantAdapter;
import id.net.gmedia.semargres2019.Merchant.MerchantModel;
import id.net.gmedia.semargres2019.Util.Constant;


/**
 * A simple {@link Fragment} subclass.
 */
public class TerdekatFragment extends Fragment {

    private View v;
    private Context context;

    private MerchantAdapter adapter;
    private List<MerchantModel> listTempat = new ArrayList<>();
    private LoadMoreScrollListener loadManager;

    private List<String> listPromoImage = new ArrayList<>();
    private ImageSliderAdapter sliderAdapter;

    private double latitude;
    private double longitude;

    public GoogleLocationManager locationManager;
    private boolean needLoad = true;

    public TerdekatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        // Inflate the layout for this fragment
        if(v == null){
            v = inflater.inflate(R.layout.fragment_terdekat, container, false);

            ImageSlider slider = v.findViewById(R.id.slider);

            sliderAdapter = new ImageSliderAdapter(context, listPromoImage, true);
            slider.setAdapter(sliderAdapter);
            sliderAdapter.setAutoscroll(3000);

            RecyclerView rv_tempat = v.findViewById(R.id.rv_terdekat);
            rv_tempat.setItemAnimator(new DefaultItemAnimator());
            rv_tempat.setLayoutManager(new LinearLayoutManager(context));
            adapter = new MerchantAdapter(getActivity(), listTempat);
            rv_tempat.setAdapter(adapter);
            loadManager = new LoadMoreScrollListener() {
                @Override
                public void onLoadMore() {
                    if(latitude != 0 && longitude != 0){
                        loadTempat(false, latitude, longitude);
                    }
                }
            };

            locationManager = new GoogleLocationManager((MainActivity)context,
                    new GoogleLocationManager.LocationUpdateListener() {
                        @Override
                        public void onChange(Location location) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();

                            loadTempat(true, latitude, longitude);
                            locationManager.stopLocationUpdates();
                        }
                    });

            loadSlider();
        }

        locationManager.startLocationUpdates();
        if(needLoad){
            AppLoading.getInstance().showLoading(context, new AppLoading.CancelListener() {
                @Override
                public void onCancel() {
                    ((Activity)context).onBackPressed();
                }
            });
        }

        return v;
    }

    private void loadTempat(final boolean init, double latitude, double longitude){
        if(init){
            loadManager.initLoad();
        }

        JSONBuilder body = new JSONBuilder();
        body.add("latitude", latitude);
        body.add("longitude", longitude);
        body.add("jarak", 10);
        body.add("start", loadManager.getLoaded());
        body.add("count", 10);

        ApiVolleyManager.getInstance().addSecureRequest(context, Constant.URL_MERCHANT_TERDEKAT_IKLAN, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(context), body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
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

                            needLoad = false;
                        }
                        catch (JSONException e){
                            Toast.makeText(context, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                            loadManager.finishLoad(0);
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        loadManager.finishLoad(0);
                        AppLoading.getInstance().stopLoading();
                    }
                }));
    }

    private void loadSlider(){
        ApiVolleyManager.getInstance().addSecureRequest(context, Constant.URL_PROMO,
                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(context),
                new AppRequestCallback(new AppRequestCallback.SimpleRequestListener() {
                    @Override
                    public void onSuccess(String result) {
                        try{
                            listPromoImage.clear();
                            JSONArray response = new JSONArray(result);

                            for(int i = 0; i < response.length(); i++){
                                JSONObject kategori = response.getJSONObject(i);
                                /*listPromo.add(new SimpleImageObjectModel(kategori.getString("id_i"),
                                        kategori.getString("gambar"), kategori.getString("title")));*/
                                listPromoImage.add(kategori.getString("gambar"));
                            }

                            sliderAdapter.reloadImages(listPromoImage);
                            //sliderAdapter.setImages(listPromoImage);
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
