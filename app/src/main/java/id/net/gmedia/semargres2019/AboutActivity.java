package id.net.gmedia.semargres2019;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.AppRequestCallback;

import org.json.JSONException;
import org.json.JSONObject;

import id.net.gmedia.semargres2019.Util.Constant;

public class AboutActivity extends AppCompatActivity {

    private TextView txt_about;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Tentang");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        txt_about = findViewById(R.id.txt_about);

        loadAbout();
    }

    private void loadAbout(){
        AppLoading.getInstance().showLoading(this, new AppLoading.CancelListener() {
            @Override
            public void onCancel() {
                onBackPressed();
            }
        });

        ApiVolleyManager.getInstance().addSecureRequest(this, Constant.URL_CHECK_VERSION,
                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(this),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        Toast.makeText(AboutActivity.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject response = new JSONObject(result);
                            txt_about.setText(response.getString("about"));

                        } catch (JSONException e) {
                            Toast.makeText(AboutActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(AboutActivity.this, message, Toast.LENGTH_SHORT).show();
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
