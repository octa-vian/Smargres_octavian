package id.net.gmedia.semargres2019.Kupon;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.octa.vian.ApiVolleyManager;
import com.octa.vian.AppLoading;
import com.octa.vian.AppRequestCallback;
import com.octa.vian.SimpleObjectModel;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import id.net.gmedia.semargres2019.Util.Constant;
import id.net.gmedia.semargres2019.R;

public class KuponActivity extends AppCompatActivity {

    private RecyclerView rv_kupon;
    private KuponAdapter adapter;

    private List<String> listHeader = new ArrayList<>();
    private LinkedHashMap<String, List<SimpleObjectModel>> listKupon = new LinkedHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kupon);

        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("E-KUPON");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        rv_kupon = findViewById(R.id.rv_kupon);
        rv_kupon.setItemAnimator(new DefaultItemAnimator());
        rv_kupon.setLayoutManager(new LinearLayoutManager(this));

        loadKupon();
    }

    private void loadKupon(){
        AppLoading.getInstance().showLoading(this);
        ApiVolleyManager.getInstance().addSecureRequest(this, Constant.URL_KUPON_RIWAYAT,
                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(this),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        listHeader.clear();
                        listKupon.clear();

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            JSONArray array = new JSONArray(result);
                            for(int i = 0; i < array.length(); i++){
                                String key = array.getJSONObject(i).getString("merchant");

                                boolean exist = false;
                                for(String s : listHeader){
                                    if(s.equals(key)){
                                        exist = true;
                                        Objects.requireNonNull(listKupon.get(s)).add(new SimpleObjectModel(
                                                array.getJSONObject(i).getString("id"),
                                                array.getJSONObject(i).getString("nomor")));
                                        break;
                                    }
                                }

                                if(!exist) {
                                    listHeader.add(key);
                                    ArrayList<SimpleObjectModel> arr_kupon = new ArrayList<>();
                                    arr_kupon.add(new SimpleObjectModel(
                                            array.getJSONObject(i).getString("id"),
                                            array.getJSONObject(i).getString("nomor")));
                                    listKupon.put(key, arr_kupon);
                                }
                            }

                            adapter = new KuponAdapter(KuponActivity.this, listHeader, listKupon);
                            rv_kupon.setAdapter(adapter);
                        }
                        catch (JSONException e){
                            Toast.makeText(KuponActivity.this, R.string.error_json, Toast.LENGTH_LONG).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(KuponActivity.this, message, Toast.LENGTH_SHORT).show();
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
