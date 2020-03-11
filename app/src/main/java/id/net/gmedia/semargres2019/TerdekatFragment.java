package id.net.gmedia.semargres2019;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.octa.vian.ApiVolleyManager;
import com.octa.vian.AppLoading;
import com.octa.vian.AppRequestCallback;
import com.octa.vian.DialogFactory;
import com.octa.vian.ImageSlider.ImageSlider;
import com.octa.vian.JSONBuilder;
import com.octa.vian.LoadMoreScrollListener;
import com.leonardus.locationmanager.GoogleLocationManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.semargres2019.Merchant.AdapterFilterMerchant;
import id.net.gmedia.semargres2019.Merchant.AdapterMerchantTerdekat;
import id.net.gmedia.semargres2019.Merchant.MerchantModel;
import id.net.gmedia.semargres2019.Util.Constant;
import id.net.gmedia.semargres2019.Util.SimpleImageObjectModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class TerdekatFragment extends Fragment {

    private View v;
    private Context context;

    private AdapterMerchantTerdekat adapter;
    private List<MerchantModel> listTempat = new ArrayList<>();
    private LoadMoreScrollListener loadManager;

    public static List<Integer> list_kategori = new ArrayList<>();

    private JSONObject json_urut = new JSONObject();
    private JSONObject js_urut;
    private List<String> listPromoImage = new ArrayList<>();
    //private ImageSliderAdapter sliderAdapter;

    private double latitude;
    private double longitude;

    private Button btn_filter, btn_urutkan;

    public GoogleLocationManager locationManager;
    private boolean needLoad = true;
    private JSONArray array_kategori = new JSONArray();
    private JSONArray array_listKategori ;
    private List<MerchantModel> listkategori = new ArrayList<>();
    private AdapterFilterMerchant adapterFilter;
    private RecyclerView rv_kategor;
    private String kategori = "";
    private final String nama = "asc";
    private final String favorit = "";

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

            /*sliderAdapter = new ImageSliderAdapter(context, listPromoImage, true);
            slider.setAdapter(sliderAdapter);
            sliderAdapter.setAutoscroll(3000);*/


            RecyclerView rv_tempat = v.findViewById(R.id.rv_terdekat);
            rv_tempat.setItemAnimator(new DefaultItemAnimator());
            rv_tempat.setLayoutManager(new GridLayoutManager(context,2));
            adapter = new AdapterMerchantTerdekat(getActivity(), listTempat);
            rv_tempat.setAdapter(adapter);

            loadManager = new LoadMoreScrollListener() {
                @Override
                public void onLoadMore() {
                    if(latitude != 0 && longitude != 0){
                        loadTempat(false, latitude, longitude, "load");
                    }
                }
            };



            locationManager = new GoogleLocationManager((MainActivity)context,
                    new GoogleLocationManager.LocationUpdateListener() {
                        @Override
                        public void onChange(Location location) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();

                            loadTempat(true, latitude, longitude, "load");
                            locationManager.stopLocationUpdates();
                        }
                    });

            //loadSlider();
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

        v.findViewById(R.id.btn_filter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               list_kategori.clear();
               final Dialog filter = new Dialog(context);
               filter.setContentView(R.layout.item_popup_filter);
               filter.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                rv_kategor = filter.findViewById(R.id.list_filter);
                rv_kategor.setItemAnimator(new DefaultItemAnimator());
                rv_kategor.setLayoutManager(new LinearLayoutManager(context));
                adapterFilter = new AdapterFilterMerchant(getActivity(), listkategori);
                rv_kategor.setAdapter(adapterFilter);

                filter.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       /* array_listKategori = new JSONArray(list_kategori);
                        Log.d("test", String.valueOf(array_listKategori));*/
                      //  listTempat.clear();
//                      array_listKategori.put("id_kategori");
//                        String result ="";
//                        for(int i =0; i< list_kategori.size(); i++){
//                            result += list_kategori.get(i)+" > ";
//                        }
//                        Log.d("test",result);
//                        System.out.println(list_kategori);
                        loadTempat(false, latitude, longitude, "search");
                        adapter.notifyDataSetChanged();
                        filter.dismiss();
                    }
                });
                loadKategori();
               filter.show();
            }
        });

        v.findViewById(R.id.btn_urutkan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog filter = new Dialog(context);
                filter.setContentView(R.layout.item_popup_urut);
                filter.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                final RadioGroup radio;
                final Button btn_oke;
                final RadioButton a, b;
                a = filter.findViewById(R.id.radio0);
                b = filter.findViewById(R.id.radio1);
                radio = filter.findViewById(R.id.radio_nama);
                btn_oke = filter.findViewById(R.id.btn_ok);
                btn_oke.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String nm = "asc";
                        String fv = "asc";
                        int selectedId = radio.getCheckedRadioButtonId();

                        switch (selectedId){
                            case R.id.radio0 :
                                try {
                                    Log.d("coba", String.valueOf(json_urut));
                                    json_urut.getString(nama);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case R.id.radio1 :
                                Toast.makeText(context,"Clicked "+((RadioButton)filter.findViewById(selectedId)).getText(), Toast.LENGTH_SHORT).show();
                                break;
                        }
                        loadTempat(false, latitude, longitude, "search");
                        filter.dismiss();
                    }
                });
                filter.show();
            }
        });

        return v;
    }

    private void loadKategori(){
        ApiVolleyManager.getInstance().addSecureRequest(context, Constant.URL_KATEGORI,
                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(context),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        listkategori.clear();
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            listkategori.clear();
                            JSONArray response = new JSONArray(result);

                            for(int i = 0; i < response.length(); i++){
                                JSONObject kategori = response.getJSONObject(i);
                                listkategori.add(new MerchantModel(kategori.getString("id_k"),
                                        kategori.getString("nama"), kategori.getString("icon")));
                            }

                            adapterFilter.notifyDataSetChanged();
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


    private void loadTempat(final boolean init, double latitude, double longitude, final String data){
        if(init){
            loadManager.initLoad();
        }
        try {
            Log.d("testing", String.valueOf(json_urut));
            json_urut.put("nama", nama);
            json_urut.put("favorit", favorit);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        array_listKategori = new JSONArray(list_kategori);
        Log.d("test", String.valueOf(array_listKategori));
        JSONBuilder body = new JSONBuilder();
        body.add("start", loadManager.getLoaded());
        body.add("limit", 10);
        body.add("search", "");
        body.add("latitude", latitude);
        body.add("longitude", longitude);
        body.add("id_kategori",array_listKategori);
        body.add("order", json_urut);

        ApiVolleyManager.getInstance().addSecureRequest(context, Constant.URL_MERCHANT_TERDEKAT_BARU, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(context), body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        if(init){
                            listTempat.clear();
                            adapter.notifyDataSetChanged();
                        }

                        if (data.equals("search")){
                            listTempat.clear();
                            adapter.notifyDataSetChanged();
                            loadManager.finishLoad(0);
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

                            if (data.equals("search")){
                                listTempat.clear();
                                adapter.notifyDataSetChanged();
                                loadManager.finishLoad(0);
                            }

                            JSONArray response = new JSONArray(result);

                            for(int i = 0; i < response.length(); i++){
                                JSONObject tempat = response.getJSONObject(i);
                                listTempat.add(new MerchantModel(tempat.getString("id"),
                                        tempat.getString("nama"), tempat.getString("alamat"),
                                        tempat.getString("foto")));
                                /*if(tempat.getString("flag_tipe").equals("merchant")){
                                    double latitude = tempat.getString("latitude").equals("")?0:tempat.getDouble("latitude");
                                    double longitude = tempat.getString("longitude").equals("")?0:tempat.getDouble("longitude");


                                }*/
                                //iklan
                               /* else if(tempat.getString("flag_tipe").equals("merchant")){
                                    listTempat.add(new MerchantModel(tempat.getString("foto"),
                                            tempat.getString("link")));
                                }*/
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

                    //pakek yg ini        //sliderAdapter.reloadImages(listPromoImage);
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
