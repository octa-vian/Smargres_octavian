package id.net.gmedia.semargres2019.TiketKonser;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.chrisbanes.photoview.PhotoView;
import com.octa.vian.ImageLoader;

import id.net.gmedia.semargres2019.Util.Constant;
import id.net.gmedia.semargres2019.R;

public class DenahActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_denah);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Denah Konser");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if(getIntent().hasExtra(Constant.EXTRA_DENAH)){
            PhotoView img_denah = findViewById(R.id.img_denah);
            ImageLoader.load(this, getIntent().getStringExtra(Constant.EXTRA_DENAH), img_denah);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
