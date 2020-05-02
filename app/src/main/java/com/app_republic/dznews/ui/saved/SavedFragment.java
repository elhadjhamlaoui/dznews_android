package com.app_republic.dznews.ui.saved;

import android.os.AsyncTask;
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
import com.app_republic.dznews.interfaces.ArticleDao;
import com.app_republic.dznews.pojo.Article;
import com.app_republic.dznews.utils.AppSingleton;
import com.app_republic.dznews.utils.Utils;
import com.google.android.gms.ads.formats.UnifiedNativeAd;

import java.util.ArrayList;
import java.util.List;

import static com.app_republic.dznews.utils.StaticConfig.NUMBER_OF_NATIVE_ADS_NEWS;

public class SavedFragment extends Fragment {

    private SavedViewModel savedViewModel;
    private RecyclerView recyclerView;
    private ArticlesAdapter articlesAdapter;
    private boolean isLoading = false;
    private int page = 0;
    private AppSingleton appSingleton;
    private Handler handler;
    List<UnifiedNativeAd> mNativeAds = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        savedViewModel =
                ViewModelProviders.of(this).get(SavedViewModel.class);
        View root = inflater.inflate(R.layout.fragment_saved, container, false);

        appSingleton = AppSingleton.getInstance(getActivity());
        handler = new Handler();

        recyclerView = root.findViewById(R.id.recyclerView);

        articlesAdapter = new ArticlesAdapter(getActivity(), savedViewModel.getArticles().getValue());
        recyclerView.setAdapter(articlesAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        savedViewModel.getArticles().observe(getViewLifecycleOwner(), articles -> articlesAdapter.notifyDataSetChanged());

        AsyncTask.execute(() -> getArticles(0));
        return root;
    }

    private void getArticles(int pageNumber) {
        ArticleDao articleDao = appSingleton.getLocalDB().articleDao();

        List<Article> articles = articleDao.getAll();
        handler.post(() -> {



            if (getActivity() != null) {
                savedViewModel.getArticles().getValue().addAll(Utils.getFilteredArticles(getActivity(),articles));
                savedViewModel.setArticles(savedViewModel.getArticles().getValue());


                if (articles.size() <= 5)
                    AppSingleton.getInstance(getActivity()).loadNativeAds(mNativeAds, recyclerView, articlesAdapter,
                            articles, savedViewModel.getArticles().getValue(), 1, 0);
                else
                    AppSingleton.getInstance(getActivity()).loadNativeAds(mNativeAds, recyclerView, articlesAdapter,
                            articles, savedViewModel.getArticles().getValue(), NUMBER_OF_NATIVE_ADS_NEWS, 2);
            }

        });

    }


}
