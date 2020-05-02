package com.app_republic.dznews.ui.corona;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app_republic.dznews.R;
import com.app_republic.dznews.adapter.ArticlesAdapter;
import com.app_republic.dznews.pojo.ArticlesApiResponse;
import com.app_republic.dznews.utils.AppSingleton;
import com.app_republic.dznews.utils.Utils;
import com.google.android.gms.ads.formats.UnifiedNativeAd;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app_republic.dznews.utils.StaticConfig.NUMBER_OF_NATIVE_ADS_NEWS;

public class CoronaFragment extends Fragment {

    private CoronaViewModel coronaViewModel;
    private RecyclerView recyclerView;
    private ArticlesAdapter articlesAdapter;
    private boolean isLoading = false;
    private int page = 0;
    private Handler handler;
    List<UnifiedNativeAd> mNativeAds = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        coronaViewModel =
                ViewModelProviders.of(this).get(CoronaViewModel.class);
        View root = inflater.inflate(R.layout.fragment_corona, container, false);

        handler = new Handler();

        recyclerView = root.findViewById(R.id.recyclerView);

        articlesAdapter = new ArticlesAdapter(getActivity(), coronaViewModel.getArticles().getValue());
        recyclerView.setAdapter(articlesAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        coronaViewModel.getArticles().observe(getViewLifecycleOwner(), articles -> articlesAdapter.notifyDataSetChanged());

        //AsyncTask.execute(() ->
        getArticles(0, 2);
        /*recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!recyclerView.canScrollVertically(1) && dy > 0 && !isLoading) {

                    isLoading = true;
                    getArticles(page, page * 10);
                }
            }
        });*/


        return root;
    }

    /*private void getArticles(int pageNumber) {

        final WebView webView = new WebView(getActivity());

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(final WebView view, String url) {
                webView.loadUrl("javascript: void AndroidHook.loadHTML(document.getElementsByTagName('body')[0].innerHTML);");
            }
        });

        webView.addJavascriptInterface(new JSInterface(), "AndroidHook");

        webView.loadUrl("https://www.skynewsarabia.com/tag?s=%D8%A3%D8%AE%D8%A8%D8%A7%D8%B1%20%D9%83%D9%88%D8%B1%D9%88%D9%86%D8%A7");


    }*/

    private void getArticles(int pageNumber, int startIndex) {

        Call<ArticlesApiResponse> call1 =  AppSingleton.getInstance(getContext()).getApiInterface().getCorona("", pageNumber);
        call1.enqueue(new Callback<ArticlesApiResponse>() {
            @Override
            public void onResponse(Call<ArticlesApiResponse> call, Response<ArticlesApiResponse> apiResponse) {
                if (getActivity() != null) {
                    isLoading = false;
                    page++;
                    ArticlesApiResponse response = apiResponse.body();
                    coronaViewModel.getArticles().getValue().addAll(Utils.getFilteredArticles(getActivity(),response.getArticles()));
                    coronaViewModel.setArticles(coronaViewModel.getArticles().getValue());

                    if (response.getArticles().size() <= 5)
                        AppSingleton.getInstance(getActivity()).loadNativeAds(mNativeAds, recyclerView, articlesAdapter,
                                response.getArticles(), coronaViewModel.getArticles().getValue(), 1, 0);
                    else
                        AppSingleton.getInstance(getActivity()).loadNativeAds(mNativeAds, recyclerView, articlesAdapter,
                                response.getArticles(), coronaViewModel.getArticles().getValue(), 15, startIndex);
                }

            }

            @Override
            public void onFailure(Call<ArticlesApiResponse> call, Throwable t) {
                isLoading = false;
                t.printStackTrace();
                call.cancel();
            }
        });

    }

    /*private void parseHtml(String html) {
        Document doc = Jsoup.parse(html);
        Elements articleElms = doc.select("div[class~=each-result]");

        ArrayList<Article> articles = new ArrayList<>();
        for (Element element : articleElms) {
            Article article = new Article();


            article.setSource(StaticConfig.SOURCE_SKYNEWS);
            article.setSourceAr("سكاي نيوز");

            article.setImage("https://www.skynewsarabia.com" + element.select("img[class=ng-scope]")
                    .attr("data-sna-lazy-src")
            .replace("-4/-2", "300/169")
            );

            article.setTitle(element.select("h3")
                    .text());

            article.setLink("https://www.skynewsarabia.com" + element.select("a[class~=item-wrapper]")
                    .attr("href"));

            article.set_id(article.getLink());

            article.setReadableTime(element.select("div[class~=date-time] > span")
                    .text());

            articles.add(article);
        }

        handler.post(() -> {
            if (getActivity() != null) {
                isLoading = false;
                page++;
                coronaViewModel.getArticles().getValue().addAll(Utils.getFilteredArticles(getActivity(), articles));
                coronaViewModel.setArticles(coronaViewModel.getArticles().getValue());

                if (articles.size()  <= 5)
                    AppSingleton.getInstance(getActivity()).loadNativeAds(mNativeAds, recyclerView, articlesAdapter,
                            articles, coronaViewModel.getArticles().getValue(), 1, 0);
                else
                    AppSingleton.getInstance(getActivity()).loadNativeAds(mNativeAds, recyclerView, articlesAdapter,
                            articles, coronaViewModel.getArticles().getValue(), NUMBER_OF_NATIVE_ADS_NEWS, 2);
            }


        });

    }

    public class JSInterface {
        @JavascriptInterface
        public void loadHTML(final String html) {
            AsyncTask.execute(() -> parseHtml(html));
        }
    }*/
}
