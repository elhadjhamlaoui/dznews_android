package com.app_republic.dznews.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.app_republic.dznews.activity.MainActivity;
import com.app_republic.dznews.activity.NewsItemActivity;
import com.app_republic.dznews.interfaces.ArticleDao;
import com.app_republic.dznews.ui.CommentsFragment;
import com.app_republic.dznews.utils.AppSingleton;
import com.app_republic.dznews.R;
import com.app_republic.dznews.pojo.Article;
import com.app_republic.dznews.utils.StaticConfig;
import com.app_republic.dznews.utils.UnifiedNativeAdViewHolder;
import com.app_republic.dznews.utils.Utils;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.app_republic.dznews.utils.StaticConfig.CONTENT_ITEM_VIEW_TYPE;
import static com.app_republic.dznews.utils.StaticConfig.UNIFIED_NATIVE_AD_VIEW_TYPE;

public class ArticlesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Object> list;
    private Context context;
    private Picasso picasso;

    public ArticlesAdapter(Context context, List<Object> list) {
        this.list = list;
        this.context = context;
        picasso = AppSingleton.getInstance(this.context).getPicasso();

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType) {
            case UNIFIED_NATIVE_AD_VIEW_TYPE:
                View unifiedNativeLayoutView = LayoutInflater.from(
                        parent.getContext()).inflate(R.layout.ad_unified_news,
                        parent, false);
                return new UnifiedNativeAdViewHolder(unifiedNativeLayoutView);

            default:
                View view = LayoutInflater.from(context).inflate(R.layout.item_article, parent, false);
                ArticleViewHolder viewHolder = new ArticleViewHolder(view);
                return viewHolder;
        }


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case UNIFIED_NATIVE_AD_VIEW_TYPE:
                UnifiedNativeAd nativeAd = (UnifiedNativeAd) list.get(position);
                UnifiedNativeAdViewHolder.populateNativeAdView(nativeAd, ((UnifiedNativeAdViewHolder) holder).getAdView());
                break;
            case CONTENT_ITEM_VIEW_TYPE:

                ArticleViewHolder articleViewHolder = (ArticleViewHolder) holder;
                Article article = (Article) list.get(position);

                articleViewHolder.title.setText(article.getTitle());
                articleViewHolder.source.setText(article.getSourceAr());
                articleViewHolder.time.setText(article.getReadableTime());

                if (article.getImage() != null)
                    picasso.load(article.getImage()).fit().into(articleViewHolder.image);
                else
                    articleViewHolder.image.setImageResource(R.drawable.news);
                break;
        }



    }

    @Override
    public int getItemViewType(int position) {

        Object recyclerViewItem = list.get(position);
        if (recyclerViewItem instanceof UnifiedNativeAd) {
            return UNIFIED_NATIVE_AD_VIEW_TYPE;
        }
        return CONTENT_ITEM_VIEW_TYPE;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ArticleViewHolder extends RecyclerView.ViewHolder {
        private TextView title, source, time;
        private ImageView image;
        private View V_root;
        ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            source = itemView.findViewById(R.id.source);
            time = itemView.findViewById(R.id.time);
            image = itemView.findViewById(R.id.image);
            V_root = itemView.findViewById(R.id.root);

            V_root.setOnClickListener(view -> {


                Utils.loadInterstitialAd(((AppCompatActivity)context).getSupportFragmentManager(), "any","news",
                        context, () -> {
                            Intent intent = new Intent(context, NewsItemActivity.class);
                            intent.putExtra(StaticConfig.ARTICLE, (Article) list.get(getAdapterPosition()));
                            context.startActivity(intent);
                        });


            });

        }
    }
}
