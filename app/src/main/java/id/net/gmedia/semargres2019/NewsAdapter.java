package id.net.gmedia.semargres2019;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.leonardus.irfan.ImageLoader;

import java.util.List;

import id.net.gmedia.semargres2019.PromoEvent.PromoDetailActivity;
import id.net.gmedia.semargres2019.PromoEvent.PromoModel;
import id.net.gmedia.semargres2019.Util.Constant;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private Activity activity;
    private List<PromoModel> listNews;

    public NewsAdapter(Activity activity, List<PromoModel> listNews){
        this.activity = activity;
        this.listNews = listNews;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new NewsViewHolder(LayoutInflater.from(activity).inflate(R.layout.item_news, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int i) {
        final PromoModel promo = listNews.get(i);

        holder.txt_news.setText(promo.getTitle());
        ImageLoader.load(activity, promo.getImage(), holder.img_news);
        holder.layout_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                Intent i = new Intent(activity, PromoDetailActivity.class);
                i.putExtra(Constant.EXTRA_EVENT, gson.toJson(promo));
                activity.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listNews.size();
    }

    class NewsViewHolder extends RecyclerView.ViewHolder{
        ImageView img_news;
        TextView txt_news;
        View layout_parent;

        NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            img_news = itemView.findViewById(R.id.img_news);
            txt_news = itemView.findViewById(R.id.txt_news);
            layout_parent = itemView.findViewById(R.id.layout_parent);
        }
    }
}
