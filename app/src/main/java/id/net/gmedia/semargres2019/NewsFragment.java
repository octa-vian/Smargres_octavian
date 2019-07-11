package id.net.gmedia.semargres2019;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.JSONBuilder;
import com.leonardus.irfan.LoadMoreScrollListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.semargres2019.PromoEvent.PromoModel;
import id.net.gmedia.semargres2019.Util.Constant;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewsFragment extends Fragment {

    private Context context;
    private NewsAdapter adapter;

    private List<PromoModel> listNews = new ArrayList<>();
    private LoadMoreScrollListener loadManager;

    public NewsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getContext();
        View v = inflater.inflate(R.layout.fragment_news, container, false);

        RecyclerView rv_news = v.findViewById(R.id.rv_news);
        rv_news.setItemAnimator(new DefaultItemAnimator());
        rv_news.setLayoutManager(new GridLayoutManager(context, 2));
        adapter = new NewsAdapter(getActivity(), listNews);
        rv_news.setAdapter(adapter);
        loadManager = new LoadMoreScrollListener() {
            @Override
            public void onLoadMore() {
                loadNews(false);
            }
        };
        rv_news.addOnScrollListener(loadManager);

        loadNews(true);

        return v;
    }

    private void loadNews(final boolean init){
        if(init){
            AppLoading.getInstance().showLoading(context);
            loadManager.initLoad();
        }

        final int LOAD_COUNT = 20;
        JSONBuilder body = new JSONBuilder();
        body.add("start", loadManager.getLoaded());
        body.add("count", LOAD_COUNT);

        ApiVolleyManager.getInstance().addSecureRequest(context, Constant.URL_EVENT,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(context), body.create(),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        if(init){
                            listNews.clear();
                            adapter.notifyDataSetChanged();
                        }

                        loadManager.finishLoad(0);
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            if(init){
                                listNews.clear();
                            }

                            JSONArray response = new JSONArray(result);
                            for(int i = 0; i < response.length(); i++){
                                JSONObject news = response.getJSONObject(i);
                                listNews.add(new PromoModel(
                                        news.getString("id_i"),
                                        news.getString("title"),
                                        news.getString("gambar"),
                                        news.getString("keterangan"),
                                        news.getString("link")
                                ));
                            }

                            adapter.notifyDataSetChanged();
                            loadManager.finishLoad(response.length());
                        }
                        catch (JSONException e){
                            Toast.makeText(context, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                            loadManager.finishLoad(0);
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        loadManager.finishLoad(0);
                        AppLoading.getInstance().stopLoading();
                    }
                }));
    }
}
