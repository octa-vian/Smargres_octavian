package id.net.gmedia.semargres2019.Merchant;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import id.net.gmedia.semargres2019.Util.Constant;
import id.net.gmedia.semargres2019.PromoEvent.PromoAdapter;
import id.net.gmedia.semargres2019.PromoEvent.PromoModel;
import id.net.gmedia.semargres2019.R;

public class MerchantDetailActivity extends AppCompatActivity {

    private String id_kategori = "";
    private String id_merchant = "";
    private String nama_barang = "";

    private double latitude = 0;
    private double longitude = 0;

    //Variabel UI
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbar;
    private TextView txt_title, txt_nama, txt_alamat, txt_telp, txt_buka,
            txt_diskon_diberikan, txt_diskon_aplikasi;
    private ImageView img_merchant, img_fb, img_ig;

    private List<PromoModel> listPromo = new ArrayList<>();

    private PromoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_detail);

        if(getIntent().hasExtra(Constant.EXTRA_MERCHANT_ID)){
            id_merchant = getIntent().getStringExtra(Constant.EXTRA_MERCHANT_ID);
        }
        if(getIntent().hasExtra(Constant.EXTRA_KATEGORI_ID)){
            id_kategori = getIntent().getStringExtra(Constant.EXTRA_KATEGORI_ID);
        }

        /*if(getIntent().hasExtra(Constant.EXTRA_LATITUDE) &&
                getIntent().hasExtra(Constant.EXTRA_LONGITUDE)){
            latitude = getIntent().getDoubleExtra(Constant.EXTRA_LATITUDE, 0);
            longitude = getIntent().getDoubleExtra(Constant.EXTRA_LONGITUDE, 0);
        }*/

        //Inisialisasi UI
        toolbar = findViewById(R.id.toolbar);
        collapsingToolbar = findViewById(R.id.main_collapsing);
        txt_title = findViewById(R.id.txt_title);
        img_merchant = findViewById(R.id.img_merchant);
        txt_nama = findViewById(R.id.txt_nama);
        txt_telp = findViewById(R.id.txt_telp);
        txt_buka = findViewById(R.id.txt_buka);
        txt_alamat = findViewById(R.id.txt_alamat);
        img_fb = findViewById(R.id.img_fb);
        img_ig = findViewById(R.id.img_ig);
        txt_diskon_diberikan = findViewById(R.id.txt_diskon_diberikan);
        txt_diskon_aplikasi = findViewById(R.id.txt_diskon_aplikasi);

        RecyclerView rv_promo = findViewById(R.id.rv_promo);
        rv_promo.setItemAnimator(new DefaultItemAnimator());
        rv_promo.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new PromoAdapter(this, listPromo);
        rv_promo.setAdapter(adapter);

        findViewById(R.id.img_map).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(latitude != 0 && longitude != 0){
                    Intent i = new Intent(MerchantDetailActivity.this, MapActivity.class);
                    i.putExtra(Constant.EXTRA_LATITUDE, latitude);
                    i.putExtra(Constant.EXTRA_LONGITUDE, longitude);
                    startActivity(i);
                }
            }
        });

        findViewById(R.id.img_telp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!txt_telp.getText().toString().equals("")){
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + txt_telp.getText().toString()));
                    startActivity(intent);
                }
            }
        });

        initToolbar();
        loadMerchant();
    }

    private void loadMerchant(){
        AppLoading.getInstance().showLoading(this);
        String parameter = String.format(Locale.getDefault(), "/%s", id_merchant);
        if(!id_kategori.isEmpty()){
            parameter += "/" + id_kategori;
        }
        ApiVolleyManager.getInstance().addSecureRequest(this, Constant.URL_MERCHANT + parameter,
                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(this),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        Toast.makeText(MerchantDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            JSONObject merchant = new JSONArray(result).getJSONObject(0);
                            nama_barang = merchant.getString("nama");
                            txt_nama.setText(nama_barang);
                            txt_alamat.setText(merchant.getString("alamat"));
                            if(!merchant.getString("notelp").equals("")){
                                findViewById(R.id.layout_telepon).setVisibility(View.VISIBLE);
                                txt_telp.setText(merchant.getString("notelp"));
                            }

                            if(!merchant.getString("link_fb").isEmpty()){
                                final String fb = merchant.getString("link_fb");
                                img_fb.setVisibility(View.VISIBLE);
                                img_fb.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        redirrectToLink(fb);
                                    }
                                });
                            }
                            if(!merchant.getString("link_ig").isEmpty()){
                                final String ig = merchant.getString("link_ig");
                                img_ig.setVisibility(View.VISIBLE);
                                img_ig.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        redirrectToLink(ig);
                                    }
                                });
                            }

                            latitude = merchant.getDouble("latitude");
                            longitude = merchant.getDouble("longitude");

                            txt_buka.setText(merchant.getString("jam_buka"));

                            ImageLoader.load(MerchantDetailActivity.this, merchant.getString("foto"), img_merchant);
                            JSONArray list_promo = merchant.getJSONArray("promo");
                            for(int i = 0; i < list_promo.length(); i++){
                                JSONObject promo = list_promo.getJSONObject(i);
                                listPromo.add(new PromoModel(promo.getString("id_i"),
                                        promo.getString("title"), promo.getString("gambar"),
                                        promo.getString("keterangan"), promo.getString("link")));
                            }

                            txt_diskon_diberikan.setText(merchant.getString("diskon_default"));
                            if(!merchant.getString("diskon_user_app").equals("")){
                                findViewById(R.id.layout_diskon_aplikasi).setVisibility(View.VISIBLE);
                                txt_diskon_aplikasi.setText(merchant.getString("diskon_user_app"));
                            }
                            /*listPromo.add(Constant.getPathfromDrawable(R.drawable.promo1));
                            listPromo.add(Constant.getPathfromDrawable(R.drawable.promo2));
                            listPromo.add(Constant.getPathfromDrawable(R.drawable.promo3));
                            listPromo.add(Constant.getPathfromDrawable(R.drawable.promo4));*/

                            adapter.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            Toast.makeText(MerchantDetailActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(MerchantDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
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
                startActivity(browserIntent);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
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
                            if(nama_barang != null){
                                txt_title.setText(nama_barang);
                            }
                            isShow = true;
                            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        } else if (isShow) {
                            txt_title.setText("");
                            isShow = false;
                            getSupportActionBar().setBackgroundDrawable(getResources().
                                    getDrawable(R.drawable.style_rectangle_gradient_black));
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
}
