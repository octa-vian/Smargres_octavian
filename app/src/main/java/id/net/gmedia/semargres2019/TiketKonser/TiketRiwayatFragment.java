package id.net.gmedia.semargres2019.TiketKonser;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.Converter;
import com.leonardus.irfan.JSONBuilder;
import com.leonardus.irfan.LoadMoreScrollListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.semargres2019.Util.Constant;
import id.net.gmedia.semargres2019.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TiketRiwayatFragment extends Fragment {

    private TiketKonserActivity activity;
    private TiketRiwayatAdapter adapter;
    private LoadMoreScrollListener loadManager;
    private SwipeRefreshLayout layout_refresh;

    private List<TiketRiwayatModel> listRiwayat = new ArrayList<>();

    public TiketRiwayatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activity = (TiketKonserActivity) getActivity();
        View v = inflater.inflate(R.layout.fragment_tiket_riwayat, container, false);

        layout_refresh = v.findViewById(R.id.layout_refresh);

        RecyclerView rv_tiket = v.findViewById(R.id.rv_tiket);
        rv_tiket.setItemAnimator(new DefaultItemAnimator());
        rv_tiket.setLayoutManager(new LinearLayoutManager(activity));
        adapter = new TiketRiwayatAdapter(activity, listRiwayat);
        rv_tiket.setAdapter(adapter);
        loadManager = new LoadMoreScrollListener() {
            @Override
            public void onLoadMore() {
                loadRiwayat(false);
            }
        };
        rv_tiket.addOnScrollListener(loadManager);

        layout_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadRiwayat(true);
                layout_refresh.setRefreshing(false);
            }
        });

        loadRiwayat(true);

        return v;
    }

    private void loadRiwayat(final boolean init){
        final int LOAD_COUNT = 10;

        if(init){
            AppLoading.getInstance().showLoading(activity);
            loadManager.initLoad();
        }

        JSONBuilder body = new JSONBuilder();
        body.add("start", loadManager.getLoaded());
        body.add("count", LOAD_COUNT);

        //Log.d(Constant.TAG, body.create());
        ApiVolleyManager.getInstance().addSecureRequest(activity, Constant.URL_TIKET_KONSER_RIWAYAT,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(activity), body.create(),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        if(init){
                            listRiwayat.clear();
                            adapter.notifyDataSetChanged();
                        }

                        AppLoading.getInstance().stopLoading();
                        loadManager.failedLoad();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            if(init){
                                listRiwayat.clear();
                            }

                            JSONArray tickets = new JSONObject(result).getJSONArray("tickets");

                            for(int i = 0; i < tickets.length(); i++){
                                JSONObject tiket = tickets.getJSONObject(i);
                                listRiwayat.add(new TiketRiwayatModel(tiket.getString("id"),
                                        tiket.getString("order_id"),
                                        tiket.getString("jenis_tiket"),
                                        tiket.getInt("jumlah"),
                                        tiket.getDouble("total_harga"),
                                        /*Converter.stringDTSToDate(tiket.getString("expired_at")),
                                        tiket.getString("nama_bank") + " - " + tiket.getString("no_rekening"),*/
                                        tiket.getString("keterangan_pembayaran"),
                                        Converter.stringDTSToDate(tiket.getString("waktu_transaksi")),
                                        tiket.getString("link_download"),
                                        tiket.getString("status"),
                                        tiket.getInt("allow_download") == 1,
                                        tiket.getString("kode_promo")));
                            }

                            adapter.notifyDataSetChanged();
                            loadManager.finishLoad(tickets.length());
                        }
                        catch (JSONException e){
                            Toast.makeText(activity, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                            loadManager.failedLoad();
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        Log.e(Constant.TAG, message);

                        AppLoading.getInstance().stopLoading();
                        loadManager.failedLoad();
                    }
                }));
    }
}
