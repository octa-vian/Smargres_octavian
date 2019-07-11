package id.net.gmedia.semargres2019.TiketKonser;

import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.leonardus.irfan.FileDownloadManager;

import id.net.gmedia.semargres2019.R;

public class TiketKonserActivity extends AppCompatActivity{

    private TiketRiwayatModel tiket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tiket_konser);

        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Tiket Konser");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        final TabLayout tab_kuis = findViewById(R.id.tab_tiket);
        tab_kuis.addTab(tab_kuis.newTab().setText("Beli Tiket"));
        tab_kuis.addTab(tab_kuis.newTab().setText("Riwayat"));
        tab_kuis.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switchTab(tab.getPosition() == 0);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        /*layout_refresh = findViewById(R.id.layout_refresh);
        layout_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                switchTab(tab_kuis.getSelectedTabPosition() == 0);
                layout_refresh.setRefreshing(false);
            }
        });*/

        loadFragment(new TiketBeliFragment());
    }

    private void switchTab(boolean beli){
        if(beli){
            loadFragment(new TiketBeliFragment());
        }
        else{
            loadFragment(new TiketRiwayatFragment());
        }
    }

    private void loadFragment(Fragment fragment){
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        /*trans.setCustomAnimations(android.R.anim.fade_in,
                android.R.anim.fade_out);*/
        trans.replace(R.id.frame_tiket, fragment);
        trans.commit();
    }

    public void prepareDownload(TiketRiwayatModel tiket){
        this.tiket = tiket;
        downloadFiles();
    }

    private void downloadFiles(){
        FileDownloadManager downloadManager = new FileDownloadManager(this, FileDownloadManager.TYPE_PDF);
        downloadManager.download(tiket.getLink_download());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(FileDownloadManager.ifFileDownloadManagerPermission(requestCode, grantResults[0])){
            downloadFiles();
        }
        else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
