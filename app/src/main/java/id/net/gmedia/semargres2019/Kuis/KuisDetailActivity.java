package id.net.gmedia.semargres2019.Kuis;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.octa.vian.ApiVolleyManager;
import com.octa.vian.AppLoading;
import com.octa.vian.AppRequestCallback;
import com.octa.vian.Converter;

import org.json.JSONException;
import org.json.JSONObject;

import id.net.gmedia.semargres2019.Util.Constant;
import id.net.gmedia.semargres2019.R;

public class KuisDetailActivity extends AppCompatActivity {

    private String id_kuis = "";
    private TextView txt_merchant, txt_pertanyaan, txt_periode, txt_hadiah, txt_terms_condition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kuis_detail);

        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Kuis");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        txt_merchant = findViewById(R.id.txt_merchant);
        txt_pertanyaan = findViewById(R.id.txt_pertanyaan);
        txt_hadiah = findViewById(R.id.txt_hadiah);
        txt_periode = findViewById(R.id.txt_periode);
        txt_terms_condition = findViewById(R.id.txt_terms_condition);

        if(getIntent().hasExtra(Constant.EXTRA_ID_KUIS)){
            id_kuis = getIntent().getStringExtra(Constant.EXTRA_ID_KUIS);
            loadKuis();
        }
    }

    private void loadKuis(){
        AppLoading.getInstance().showLoading(this);
        String parameter = "/" + id_kuis;
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_KUIS_DETAIL + parameter,
                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(this), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        Toast.makeText(KuisDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            JSONObject kuis = new JSONObject(result).getJSONObject("quiz");
                            txt_merchant.setText(kuis.getString("nama_merchant"));
                            txt_pertanyaan.setText(kuis.getString("soal"));
                            txt_hadiah.setText(kuis.getString("hadiah"));
                            String periode = "Periode " + Converter.DToStringInverse
                                    (Converter.stringDToDate(kuis.getString("periode_start")))
                                    + " - " + Converter.DToStringInverse
                                    (Converter.stringDToDate(kuis.getString("periode_end")));
                            txt_periode.setText(periode);
                            txt_terms_condition.setText(kuis.getString("term_condition"));
                        }
                        catch (JSONException e){
                            Toast.makeText(KuisDetailActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(KuisDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }
                }));

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
