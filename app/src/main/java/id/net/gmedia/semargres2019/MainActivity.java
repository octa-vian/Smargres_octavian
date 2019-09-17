package id.net.gmedia.semargres2019;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.DialogFactory;
import com.leonardus.irfan.JSONBuilder;
import com.leonardus.locationmanager.GoogleLocationManager;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import id.net.gmedia.semargres2019.Kuis.KuisActivity;
import id.net.gmedia.semargres2019.PromoEvent.PromoDetailActivity;
import id.net.gmedia.semargres2019.Qr.QrFragment;
import id.net.gmedia.semargres2019.Util.AppSharedPreferences;
import id.net.gmedia.semargres2019.Util.Constant;
import id.net.gmedia.semargres2019.Util.SimpleImageObjectModel;

public class MainActivity extends AppCompatActivity {

    //flag untuk double klik exit
    private boolean exit = false;

    private int current_menu = 0;

    //Variabel UI
    private BottomNavigationView bottombar;
    private ProfilFragment fragmentProfil;
    private HomeFragment fragmentHome;
    private QrFragment fragmentQr;
    private TerdekatFragment fragmentTerdekat;

    private Dialog dialogVersion;
    private String version = "";
    private String link = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Cek apakah sudah login
        if(!AppSharedPreferences.isLoggedIn(this)){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        //masuk intent dari notif
        if(getIntent().hasExtra("jenis")){
            String jenis = getIntent().getStringExtra("jenis");
            if(jenis.equals("promo")){
                if(getIntent().hasExtra("id_promo")){
                    Intent i = new Intent(this, PromoDetailActivity.class);
                    i.putExtra(Constant.EXTRA_PROMO_ID, getIntent().getStringExtra("id_promo"));
                    startActivity(i);
                }
            }
            else if(jenis.equals("quiz")){
                Intent i = new Intent(this, KuisActivity.class);
                i.putExtra(Constant.EXTRA_START_KUIS, false);
                startActivity(i);
            }
        }

        bottombar = findViewById(R.id.bottombar);
        bottombar.inflateMenu(R.menu.menu_main);
        //Ketika menu bottombar dipilih, switch fragment
        bottombar.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switchMenu(item.getOrder());
                return true;
            }
        });

        subscribeFirebaseTopic();
    }

    @Override
    protected void onResume() {
        switchMenu(current_menu);
        updateFcmId();
        if(dialogVersion != null){
            dialogVersion.dismiss();
        }
        checkVersion();
        super.onResume();
    }

    private void switchMenu(int order){
        switch (order){
            case 0:
                current_menu = 0;
                checkMenangKuis();
                if(fragmentHome == null){
                    fragmentHome = new HomeFragment();
                }
                loadFragment(fragmentHome);
                break;
            case 1:
                current_menu = 1;
                if(fragmentTerdekat == null){
                    Bundle bundle = new Bundle();
                    fragmentTerdekat = new TerdekatFragment();
                    fragmentTerdekat.setArguments(bundle);
                }
                loadFragment(fragmentTerdekat);
                break;
            case 2:
                current_menu = 2;
                if(fragmentQr == null){
                    fragmentQr = new QrFragment();
                }
                fragmentQr.offUser_qr();
                loadFragment(fragmentQr);
                break;
            case 3:
                current_menu = 3;
                loadFragment(new NewsFragment());
                break;
            case 4:
                current_menu = 4;
                if(fragmentProfil == null){
                    fragmentProfil = new ProfilFragment();
                }
                loadFragment(new ProfilFragment());

                break;
        }
    }

    private void checkMenangKuis(){
        ApiVolleyManager.getInstance().addSecureRequest(this, Constant.URL_KUIS_MENANG_CHECK,
                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(this),
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
                                showMenangKuis();
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

    private void showMenangKuis(){
        final Dialog dialog = DialogFactory.getInstance().createDialog(
                MainActivity.this, R.layout.popup_menang_kuis,
                R.style.DialogAnimation,  75, 100);
        TextView txt_nanti, txt_sekarang;
        txt_nanti = dialog.findViewById(R.id.txt_nanti);
        txt_sekarang = dialog.findViewById(R.id.txt_sekarang);

        txt_nanti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        txt_sekarang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, KuisActivity.class);
                i.putExtra(Constant.EXTRA_START_KUIS, false);
                startActivity(i);

                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void loadFragment(Fragment fragment){
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        /*trans.setCustomAnimations(android.R.anim.fade_in,
                android.R.anim.fade_out);*/
        trans.replace(R.id.frame, fragment);
        trans.commitAllowingStateLoss();
    }

    private void subscribeFirebaseTopic(){
        FirebaseApp.initializeApp(MainActivity.this);
        FirebaseMessaging.getInstance().subscribeToTopic("gmedia_semargres_2019");
    }

    public void showAllCategory(List<SimpleImageObjectModel> listKategori){
       /* Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        int device_TotalWidth = size.x;
        int device_TotalHeight = size.y;

        Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_kategori);

        if(dialog.getWindow() != null){
            dialog.getWindow().setLayout(device_TotalWidth, device_TotalHeight); // set here your value
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.gravity = Gravity.BOTTOM;
            lp.windowAnimations = R.style.DialogAnimation;
            dialog.getWindow().setAttributes(lp);
        }

        RecyclerView rv_kategori = dialog.findViewById(R.id.rv_kategori);
        rv_kategori.setItemAnimator(new DefaultItemAnimator());
        rv_kategori.setAdapter(new KategoriAdapter(this, listKategori, true));
        rv_kategori.setLayoutManager(new GridLayoutManager(this, 3));

        dialog.show();*/
       if(((SlidingUpPanelLayout)findViewById(R.id.layout_parent)).getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED){
           ((SlidingUpPanelLayout)findViewById(R.id.layout_parent)).setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
       }
       else{
           ((SlidingUpPanelLayout)findViewById(R.id.layout_parent)).setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
           RecyclerView rv_kategori = findViewById(R.id.rv_kategori_all);
           rv_kategori.setItemAnimator(new DefaultItemAnimator());
           rv_kategori.setAdapter(new KategoriAdapter(this, listKategori, true));
           rv_kategori.setLayoutManager(new GridLayoutManager(this, 3));
       }
    }

    @Override
    public void onBackPressed() {
        //jika flag exit sudah menyala, maka keluar aplikasi
        if(current_menu == 0 && ((SlidingUpPanelLayout)findViewById(R.id.layout_parent)).
                getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED){
            ((SlidingUpPanelLayout)findViewById(R.id.layout_parent)).setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
        else if(current_menu == 2 && fragmentQr.isUser_qr()){
            fragmentQr.offUser_qr();
            fragmentQr.resetFragment();
        }
        else if(current_menu != 0){
            bottombar.setSelectedItemId(R.id.action_home);
        }
        else if(exit){
            super.onBackPressed();
        }
        //jika flag exit belum menyala, maka nyalakan flag exit selama 2 detik
        else{
            exit = true;
            Toast.makeText(MainActivity.this, "Klik sekali lagi untuk keluar", Toast.LENGTH_SHORT).show();
            Handler handle = new Handler();
            handle.postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 2000);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Ambil data pembacaan barcode
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                if(fragmentQr == null){
                    fragmentQr = new QrFragment();
                }
                fragmentQr.offUser_qr();
                fragmentQr.scanBarcode(result.getContents());
            }
        } else {
            if(requestCode == GoogleLocationManager.ACTIVATE_LOCATION ){
                if(resultCode == RESULT_OK){
                    if(fragmentTerdekat == null){
                        Bundle bundle = new Bundle();
                        fragmentTerdekat = new TerdekatFragment();
                        fragmentTerdekat.setArguments(bundle);
                    }
                    fragmentTerdekat.locationManager.startLocationUpdates();
                }
                else{
                    AppLoading.getInstance().stopLoading();
                }
            }
            else{
                super.onActivityResult(requestCode, resultCode, data);
            }
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
                    if(fragmentTerdekat == null){
                        Bundle bundle = new Bundle();
                        fragmentTerdekat = new TerdekatFragment();
                        fragmentTerdekat.setArguments(bundle);
                    }
                    fragmentTerdekat.locationManager.startLocationUpdates();
                } else {
                    AppLoading.getInstance().stopLoading();
                }
            } else {
                AppLoading.getInstance().stopLoading();
            }
        }
    }

    private void updateFcmId(){
        JSONBuilder body = new JSONBuilder();
        body.add("fcm_id", AppSharedPreferences.getFcmId(this));

        ApiVolleyManager.getInstance().addSecureRequest(this, Constant.URL_FCM_UPDATE,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(this),
                body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        Log.e(Constant.TAG, message);
                    }

                    @Override
                    public void onSuccess(String result) {
                        Log.d(Constant.TAG, result);
                    }

                    @Override
                    public void onFail(String message) {
                        Log.e(Constant.TAG, message);
                    }
                }));
    }


    private void checkVersion(){
        PackageInfo pInfo = null;
        version = "";

        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(Constant.TAG, e.getMessage());
        }

        if(pInfo != null){
            version = pInfo.versionName;
        }

        ApiVolleyManager.getInstance().addSecureRequest(this, Constant.URL_CHECK_VERSION,
                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(this),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        Log.e(Constant.TAG, message);
                    }

                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject response = new JSONObject(result);
                            String latestVersion = response.getString("build_version");
                            link = response.getString("link_update");
                            boolean updateRequired = response.getString("wajib").equals("1");

                            if (!version.trim().equals(latestVersion.trim()) && link.length() > 0) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                if (updateRequired) {

                                    builder.setIcon(R.mipmap.ic_launcher)
                                            .setTitle("Update")
                                            .setMessage("Versi terbaru " + latestVersion +
                                                    " telah tersedia, mohon download versi terbaru.")
                                            .setPositiveButton("Update Sekarang", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                                                    startActivity(browserIntent);
                                                }
                                            });
                                    dialogVersion = builder.create();
                                    dialogVersion.setCancelable(false);
                                    dialogVersion.show();
                                } else {

                                    builder.setIcon(R.mipmap.ic_launcher)
                                            .setTitle("Update")
                                            .setMessage("Versi terbaru " + latestVersion +
                                                    " telah tersedia, mohon download versi terbaru.")
                                            .setPositiveButton("Update Sekarang", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                                                    startActivity(browserIntent);
                                                }
                                            })
                                            .setNegativeButton("Update Nanti", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                    dialogVersion = builder.create();
                                    dialogVersion.show();
                                }
                            }
                        } catch (JSONException e) {
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
    protected void onDestroy() {
        super.onDestroy();
    }
}
