package com.app_republic.dznews.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.app_republic.dznews.R;
import com.app_republic.dznews.interfaces.ArticleDao;
import com.app_republic.dznews.pojo.Article;
import com.app_republic.dznews.ui.CommentsFragment;
import com.app_republic.dznews.utils.AppSingleton;
import com.app_republic.dznews.utils.StaticConfig;
import com.app_republic.dznews.utils.Utils;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class NewsItemActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView IV_thumb, IV_back, IV_share;
    TextView TV_title, TV_body, TV_time, TV_source;
    FloatingActionButton FB_comments, FB_save;
    Button BT_link;
    Article article;
    Picasso picasso;
    AppSingleton appSingleton;
    private Handler handler;
    ProgressBar progressBar;
    String content = "";
    private boolean checkedSaved = false;
    private boolean isSaved = false;
    private ArticleDao articleDao;
    private String image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        handler = new Handler();


        appSingleton = AppSingleton.getInstance(this);

        picasso = appSingleton.getPicasso();

        article = getIntent().getParcelableExtra(StaticConfig.ARTICLE);
        initialiseViews();

        Utils.loadBannerAd(this, "news");
        AppSingleton.getInstance(this).loadNativeAdBig(findViewById(R.id.adViewNative));

        AsyncTask.execute(() -> loadArticleContent(article.getLink()));

        AsyncTask.execute(() -> checkIfSaved(article.get_id()));

        articleDao = AppSingleton.getInstance(this).getLocalDB().articleDao();




        FB_save.setOnClickListener(view -> {
            if (checkedSaved) {
                try {
                    if (isSaved) {
                        AsyncTask.execute(() -> {
                            articleDao.delete(article);
                            checkIfSaved(article.get_id());
                        });
                    } else {
                        AsyncTask.execute(() -> {
                            articleDao.insertAll(article);
                            checkIfSaved(article.get_id());
                        });

                    }
                } catch (SQLiteConstraintException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    private void checkIfSaved(String id) {
        Article result = articleDao.findById(id);
        handler.post(() -> {
            if (result == null) {
                isSaved = false;
                FB_save.setImageResource(R.drawable.ic_favorite_outline);
            } else {
                isSaved = true;
                FB_save.setImageResource(R.drawable.ic_favorite);
            }
            checkedSaved = true;
        });
    }

    private void initialiseViews() {
        IV_thumb = findViewById(R.id.thumb);
        IV_back = findViewById(R.id.back);
        IV_share = findViewById(R.id.share);

        TV_title = findViewById(R.id.title);
        TV_body = findViewById(R.id.body);
        TV_time = findViewById(R.id.time);
        TV_source = findViewById(R.id.source);
        BT_link = findViewById(R.id.link);
        FB_comments = findViewById(R.id.comments);
        FB_save = findViewById(R.id.save);

        progressBar = findViewById(R.id.progressBar);

        IV_back.setOnClickListener(this);
        IV_share.setOnClickListener(this);
        FB_comments.setOnClickListener(this);
        BT_link.setOnClickListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            IV_thumb.setTransitionName(StaticConfig.THUMB);
        }

        if (article.getImage() != null)
            picasso.load(article.getImage())
                    .fit()
                    .placeholder(R.drawable.news)
                    .into(IV_thumb);
        TV_title.setText(article.getTitle());


        TV_source.setText(article.getSourceAr());
        TV_time.setText(article.getReadableTime());

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                onBackPressed();
                break;

            case R.id.link:
                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(article.getLink()));
                    startActivity(browserIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case R.id.share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        article.getTitle() + "\n\n" + Html.fromHtml(content)
                                + "\n\n" + getString(R.string.app_name) + "\n\n" +
                                getString(R.string.play_store_link)
                );



                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);


                break;

            case R.id.comments:
                CommentsFragment fragment = CommentsFragment.newInstance();
                Bundle args = new Bundle();

                args.putString(StaticConfig.TARGET_TYPE,
                        StaticConfig.ARTICLE);
                args.putString(StaticConfig.TARGET_ID,
                        article.get_id());

                args.putParcelable(StaticConfig.ARTICLE,
                        article);

                fragment.setArguments(args);
                getSupportFragmentManager().beginTransaction()
                        .addToBackStack(StaticConfig.FRAGMENT_COMMENTS)
                        .replace(R.id.container, fragment)
                        .commit();

                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void loadArticleContent(String url) {

        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
            switch (article.getSource()) {
                case StaticConfig.SOURCE_DZAYERNEWS:
                    image = doc.select("figure[class~=single-featured-image] img")
                            .attr("src");

                    content = doc.select("div[class~=entry-content]").html();

                    break;
                case StaticConfig.SOURCE_ECHOROUK:
                    image = doc.select("a[class~=zoom-image]")
                            .attr("href");

                    content = doc.select("div.the-content").text();

                    break;
                case StaticConfig.SOURCE_ELBILAD:
                    image = doc.select("div#post_banner img")
                            .attr("src");

                    content = doc.select("#text_space").html();

                    break;
                case StaticConfig.SOURCE_ENNAHAR:
                    image = doc.select("div[class~=full-article__featured-image] img")
                            .attr("src");

                    content = doc.select("div.full-article__content").html();

                    break;
                case StaticConfig.SOURCE_SKYNEWS:
                    image = doc.select("img[class~=orig_img]")
                            .attr("src");

                    Element element = doc.select("div[class~=article-content]").first();
                    element.removeClass("in_match_summary");
                    element.removeClass("article-tags");
                    element.removeClass("tags");
                    content = element.html();

                    break;
            }


            handler.post(() -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    TV_body.setText(Html.fromHtml(content, Html.FROM_HTML_MODE_COMPACT));
                } else {
                    TV_body.setText(Html.fromHtml(content));
                }
                if (article.getImage() == null && !image.isEmpty()) {
                    picasso.load(image).fit().into(IV_thumb);
                }
                progressBar.setVisibility(View.GONE);

            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
