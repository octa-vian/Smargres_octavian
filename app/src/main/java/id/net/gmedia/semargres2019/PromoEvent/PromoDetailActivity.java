package id.net.gmedia.semargres2019.PromoEvent;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.gson.Gson;
import com.octa.vian.ApiVolleyManager;
import com.octa.vian.AppLoading;
import com.octa.vian.AppRequestCallback;
import com.octa.vian.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import id.net.gmedia.semargres2019.R;
import id.net.gmedia.semargres2019.Util.Constant;

public class PromoDetailActivity extends AppCompatActivity {

    private PromoModel promo;
    private String jenis = "";

    private boolean image_zoomed = false;

    private Animation anim_popin, anim_popout;

    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbar;
    private ImageView img_promo, btn_close;
    private TextView txt_title, txt_judul, txt_keterangan, txt_link;
    //private ImageView img_gambar_zoom;
    private ConstraintLayout layout_overlay;
    private PhotoView img_gambar_zoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promo_detail);

        //Inisialisasi Variabel
        layout_overlay = findViewById(R.id.layout_overlay);
        img_gambar_zoom = findViewById(R.id.img_gambar_zoom);
        img_promo = findViewById(R.id.img_promo);
        txt_title = findViewById(R.id.txt_title);
        txt_judul = findViewById(R.id.txt_judul);
        txt_link = findViewById(R.id.txt_link);
        txt_keterangan = findViewById(R.id.txt_keterangan);
        toolbar = findViewById(R.id.toolbar);
        collapsingToolbar = findViewById(R.id.main_collapsing);
        btn_close = findViewById(R.id.btn_close);

        if(getIntent().hasExtra(Constant.EXTRA_PROMO)){
            Gson gson = new Gson();
            promo = gson.fromJson(getIntent().getStringExtra
                    (Constant.EXTRA_PROMO), PromoModel.class);
            jenis = "Promo";
            initPromo();
        }
        else if(getIntent().hasExtra(Constant.EXTRA_EVENT)){
            Gson gson = new Gson();
            promo = gson.fromJson(getIntent().getStringExtra
                    (Constant.EXTRA_EVENT), PromoModel.class);
            jenis = "Event";
            initPromo();
        }
        else if(getIntent().hasExtra(Constant.EXTRA_PROMO_ID)){
            String id = getIntent().getStringExtra(Constant.EXTRA_PROMO_ID);
            Log.d(Constant.TAG, id);
            if(id.length() > 0){
                loadPromo(id);
            }
        }

        initToolbar();
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
                            if(promo.getTitle() != null){
                                txt_title.setText(promo.getTitle());
                            }
                            else{
                                txt_title.setText(jenis);
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

    private void loadPromo(String id){
        AppLoading.getInstance().showLoading(this);
        String parameter = String.format("/%s", id);
        Log.d(Constant.TAG, Constant.getTokenHeader(this).toString());
        ApiVolleyManager.getInstance().addSecureRequest(this, Constant.URL_PROMO_DETAIL
                        + parameter, ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(this),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        Log.e(Constant.TAG, message);
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            JSONArray response = new JSONArray(result);
                            if(response.length() > 0){
                                JSONObject promo_json = response.getJSONObject(0);
                                promo = new PromoModel(promo_json.getString("id"), promo_json.getString("title"),
                                        promo_json.getString("gambar"), promo_json.getString("keterangan"),
                                        promo_json.getString("link"));
                                initPromo();
                            }
                            else{
                                Toast.makeText(PromoDetailActivity.this,
                                        "Promo tidak ditemukan", Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (JSONException e){
                            Toast.makeText(PromoDetailActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Log.e(Constant.TAG, message);
                        AppLoading.getInstance().stopLoading();
                    }
                }));
    }

    private void initPromo(){
        ImageLoader.load(this, promo.getImage(), img_promo);

        txt_judul.setText(promo.getTitle());
        txt_keterangan.setText(promo.getKeterangan());
        img_promo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setView(promo.getImage());
            }
        });
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_gambar_zoom.startAnimation(anim_popout);
            }
        });
        txt_link.setText(promo.getLink());
        txt_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirrectToLink(promo.getLink());
            }
        });

        //Inisialisasi popup detail foto galeri
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        //Inisialisasi animasi popup
        anim_popin = AnimationUtils.loadAnimation(this, R.anim.anim_pop_in);
        anim_popout = AnimationUtils.loadAnimation(this, R.anim.anim_pop_out);
        anim_popout.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                image_zoomed=false;
                layout_overlay.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void setView(String image){
        //Fungsi untuk menampilkan foto secara popup
        /*ImageLoader.preload(this, image, new ImageLoader.ImageLoadListener() {
            @Override
            public void onLoaded(Bitmap image, float width, float height) {
                ImageLoader.load(PromoDetailActivity.this, image, img_gambar_zoom);
                img_gambar_zoom.startAnimation(anim_popin);
            }
        });*/

        ImageLoader.load(PromoDetailActivity.this, image, img_gambar_zoom, new ColorDrawable(Color.TRANSPARENT));

        img_gambar_zoom.setScale(1, false);
        layout_overlay.setVisibility(View.VISIBLE);
        image_zoomed = true;

        img_gambar_zoom.startAnimation(anim_popin);
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

    @Override
    public void onBackPressed() {
        if(image_zoomed){
            img_gambar_zoom.startAnimation(anim_popout);
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
