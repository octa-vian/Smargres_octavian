package id.net.gmedia.semargres2019;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.octa.vian.ApiVolleyManager;
import com.octa.vian.AppRequestCallback;
import com.octa.vian.ImageLoader;
import com.octa.vian.ImageSlider.ImageSlider;
import com.octa.vian.ImageSlider.ImageSliderAdapter;
import com.octa.vian.JSONBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.semargres2019.Kuis.KuisActivity;
import id.net.gmedia.semargres2019.Kupon.KuponActivity;
import id.net.gmedia.semargres2019.Merchant.MerchantModel;
import id.net.gmedia.semargres2019.Merchant.MerchantPopulerAdapter;
import id.net.gmedia.semargres2019.TiketKonser.TiketKonserActivity;
import id.net.gmedia.semargres2019.Util.Constant;
import com.octa.vian.DynamicHeightImageView;
import id.net.gmedia.semargres2019.Util.SimpleImageObjectModel;
import id.net.gmedia.semargres2019.Voucher.VoucherActivity;
import id.net.gmedia.semargres2019.Wisata.WisataAdapter;
import id.net.gmedia.semargres2019.Wisata.WisataModel;

public class HomeFragment extends Fragment {

    private Activity activity;
    private View v;

    //Variabel UI
    private ImageView img_quiz_check;
    private DynamicHeightImageView img_iklan_sgm;
    //private TextView txt_tiket;
    private KategoriAdapter kategoriAdapter;
    private MerchantPopulerAdapter merchantAdapter;
    private WisataAdapter wisataAdapter;

    private List<SimpleImageObjectModel> listKategori = new ArrayList<>();
    //private List<SimpleImageObjectModel> listPromo = new ArrayList<>();
    private List<String> listPromoImage = new ArrayList<>();
    private List<String> listPromoLink = new ArrayList<>();
    private List<MerchantModel> listMerchant = new ArrayList<>();
    private List<WisataModel> listWisata = new ArrayList<>();

    private ImageSliderAdapter sliderAdapter;
    private SwipeRefreshLayout swipeLayout;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        activity = getActivity();
        if(v == null){
            v = inflater.inflate(R.layout.fragment_home, container, false);

            //txt_tiket = v.findViewById(R.id.txt_tiket);
            final ImageSlider slider = v.findViewById(R.id.slider);
            img_quiz_check = v.findViewById(R.id.img_quiz_check);
            img_iklan_sgm = v.findViewById(R.id.img_iklan_sgm);

            sliderAdapter = new ImageSliderAdapter(activity, listPromoImage,true);
            sliderAdapter.setAutoscroll(3000);
            slider.setAdapter(sliderAdapter);
            slider.setListener(new ImageSlider.SliderEventListener() {
                @Override
                public void onClick() {
                    //Jika ada promo & link
                    if(listPromoImage.size() > 0 &&
                            listPromoLink.size() > 0){

                        //Buka url atau masuk ke pembelian tiket
                        String link = listPromoLink.get(sliderAdapter.getCurrentPage());
                        if(link.equals("http://semaranggreatmusic.com/")){
                            Intent i = new Intent(activity, TiketKonserActivity.class);
                            activity.startActivity(i);
                        }
                        else if(!link.isEmpty()){
                            redirrectToLink(link);
                        }
                    }
                }
            });

            RecyclerView rv_kategori = v.findViewById(R.id.rv_kategori);
            rv_kategori.setItemAnimator(new DefaultItemAnimator());
            rv_kategori.setLayoutManager(new GridLayoutManager(activity, 4));
            kategoriAdapter = new KategoriAdapter(getActivity(), listKategori, false);
            rv_kategori.setAdapter(kategoriAdapter);

            RecyclerView rv_wisata = v.findViewById(R.id.rv_wisata);
            rv_wisata.setItemAnimator(new DefaultItemAnimator());
            rv_wisata.setLayoutManager(new GridLayoutManager(activity, 3));
            wisataAdapter = new WisataAdapter(getActivity(), listWisata);
            rv_wisata.setAdapter(wisataAdapter);

            RecyclerView rv_merchant_populer = v.findViewById(R.id.rv_merchant_populer);
            rv_merchant_populer.setItemAnimator(new DefaultItemAnimator());
            rv_merchant_populer.setLayoutManager(new LinearLayoutManager
                    (activity, LinearLayoutManager.HORIZONTAL, false));
            merchantAdapter = new MerchantPopulerAdapter(getActivity(), listMerchant);
            rv_merchant_populer.setAdapter(merchantAdapter);

            v.findViewById(R.id.img_ekupon).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(activity, KuponActivity.class);
                    activity.startActivity(i);
                }
            });

            v.findViewById(R.id.img_voucher).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(activity, VoucherActivity.class);
                    activity.startActivity(i);
                }
            });

            v.findViewById(R.id.img_kuis).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(activity, KuisActivity.class);
                    activity.startActivity(i);
                }
            });

            /*v.findViewById(R.id.img_tiket).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(activity, TiketKonserActivity.class);
                    activity.startActivity(i);
                }
            });*/

            v.findViewById(R.id.img_about).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(activity, AboutActivity.class);
                    activity.startActivity(i);
                }
            });

            swipeLayout = v.findViewById(R.id.layout_parent);
            swipeLayout.setOnRefreshListener(
                    new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            initLoad();
                        }
                    }
            );
        }

        initLoad();
        return v;
    }

    private void initLoad(){
        loadSlider();
        //loadKupon();
        loadKategori();
        loadWisata();
        loadMerchantPopuler();
        loadIklanSgm();

    }

    private void loadIklanSgm(){
        ApiVolleyManager.getInstance().addSecureRequest(activity, Constant.URL_IKLAN_HOME_SGM,
                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(activity),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        img_iklan_sgm.setVisibility(View.GONE);
                        Log.e(Constant.TAG, message);
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            JSONObject iklan = new JSONObject(result);
                            final String link = iklan.getString("link");
                            final String url_gambar = iklan.getString("gambar");

                            img_iklan_sgm.setVisibility(View.VISIBLE);
                            String format = url_gambar.substring(url_gambar.lastIndexOf(".") + 1);
                            if(format.equals("gif")){
                                ApiVolleyManager.trustCertificate();
                                ImageLoader.loadGif(activity, url_gambar, img_iklan_sgm);
                            }
                            else{
                                ImageLoader.preload(activity, url_gambar,
                                    new ImageLoader.ImageLoadListener() {
                                    @Override
                                    public void onLoaded(Bitmap image, float width, float height) {
                                        img_iklan_sgm.setAspectRatio(width/height);
                                        ImageLoader.load(activity, image, img_iklan_sgm);
                                    }
                                });
                            }

                            img_iklan_sgm.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(link.equals("http://semaranggreatmusic.com/")){
                                        Intent i = new Intent(activity, TiketKonserActivity.class);
                                        activity.startActivity(i);
                                    }
                                    else if(!link.isEmpty()){
                                        redirrectToLink(link);
                                    }
                                }
                            });
                        }
                        catch (JSONException e){
                            img_iklan_sgm.setVisibility(View.GONE);
                            Log.e(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onFail(String message) {
                        img_iklan_sgm.setVisibility(View.GONE);
                        Log.e(Constant.TAG, message);
                    }
                }));
    }

    private void redirrectToLink(String link){
        if(link.length() > 0){
            try{
                if (!link.toLowerCase().startsWith("http://") && !link.toLowerCase().startsWith("https://")) {
                    link = "http://" + link;
                }

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                activity.startActivity(browserIntent);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void checkQuiz(){
        ApiVolleyManager.getInstance().addSecureRequest(activity, Constant.URL_KUIS_CHECK,
                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(activity),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        Log.e(Constant.TAG, message);
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            String unread = new JSONObject(result).getString("unread");
                            if(unread.equals("true")){
                                img_quiz_check.setVisibility(View.VISIBLE);
                            }
                            else{
                                img_quiz_check.setVisibility(View.GONE);
                            }
                        }
                        catch (JSONException e){
                            Log.e(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onFail(String message) {
                        Log.e(Constant.TAG, message);
                    }
                }));
    }

    @Override
    public void onResume() {
        checkQuiz();
        super.onResume();
    }

    private void loadKategori(){
        ApiVolleyManager.getInstance().addSecureRequest(activity, Constant.URL_KATEGORI,
                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(activity),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        listKategori.clear();
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        swipeLayout.setRefreshing(false);
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            listKategori.clear();
                            JSONArray response = new JSONArray(result);

                            for(int i = 0; i < response.length(); i++){
                                JSONObject kategori = response.getJSONObject(i);
                                listKategori.add(new SimpleImageObjectModel(kategori.getString("id_k"),
                                        kategori.getString("nama"), kategori.getString("icon")));
                            }

                            kategoriAdapter.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            Toast.makeText(activity, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }

                        swipeLayout.setRefreshing(false);
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        swipeLayout.setRefreshing(false);
                    }
                }));
    }

   /* private void loadKupon(){
        ApiVolleyManager.getInstance().addSecureRequest(context, Constant.URL_KUPON_RIWAYAT,
                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(context),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        txt_tiket.setText(String.valueOf(0));
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            JSONArray response = new JSONArray(result);
                            txt_tiket.setText(String.valueOf(response.length()));
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
    }*/

    private void loadSlider(){
        ApiVolleyManager.getInstance().addSecureRequest(activity, Constant.URL_PROMO,
                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(activity),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        listPromoImage.clear();
                        listPromoLink.clear();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            listPromoImage.clear();
                            listPromoLink.clear();

                            JSONArray response = new JSONArray(result);

                            for(int i = 0; i < response.length(); i++){
                                JSONObject kategori = response.getJSONObject(i);
                                /*listPromo.add(new SimpleImageObjectModel(kategori.getString("id_i"),
                                        kategori.getString("gambar"), kategori.getString("title")));*/
                                listPromoImage.add(kategori.getString("gambar"));
                                listPromoLink.add(kategori.getString("link"));
                            }

                            sliderAdapter.reloadImages(listPromoImage);
                            //sliderAdapter.setImages(listPromoImage);
                        }
                        catch (JSONException e){
                            Toast.makeText(activity, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onFail(String message) {
                        listPromoImage.clear();
                        listPromoLink.clear();

                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void loadMerchantPopuler(){
        ApiVolleyManager.getInstance().addSecureRequest(activity, Constant.URL_MERCHANT,
                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(activity),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        listMerchant.clear();
                        merchantAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            listMerchant.clear();

                            JSONArray response = new JSONArray(result);
                            int maks = response.length() > 8 ? 8 : response.length();
                            for(int i = 0; i < maks; i++){
                                JSONObject merchant = response.getJSONObject(i);
                                listMerchant.add(new MerchantModel(merchant.getString("id_m"),
                                        merchant.getString("nama"), merchant.getString("foto")));
                            }

                            merchantAdapter.notifyDataSetChanged();
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

    private void loadWisata(){
        JSONBuilder body = new JSONBuilder();
        body.add("start", 0);
        body.add("limit", 6);

        ApiVolleyManager.getInstance().addSecureRequest(activity, Constant.URL_TEMPAT_WISATA,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(activity), body.create(),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {

                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            listWisata.clear();
                            JSONArray response = new JSONArray(result);
                            for(int i = 0; i < response.length(); i++){
                                JSONObject wisata = response.getJSONObject(i);
                                listWisata.add(new WisataModel(wisata.getString("id"),
                                        wisata.getString("nama"), wisata.getString("gambar"),
                                        wisata.getDouble("latitude"), wisata.getDouble("longitude"),
                                        wisata.getString("nama")));
                            }

                            wisataAdapter.notifyDataSetChanged();
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
}
