package com.app_republic.dznews.ui.breaking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app_republic.dznews.R;
import com.app_republic.dznews.utils.AppSingleton;
import com.app_republic.dznews.pojo.ArticlesApiResponse;
import com.app_republic.dznews.adapter.ArticlesAdapter;
import com.app_republic.dznews.utils.Utils;
import com.google.android.gms.ads.formats.UnifiedNativeAd;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app_republic.dznews.utils.StaticConfig.NUMBER_OF_NATIVE_ADS_NEWS;

public class BreakingFragment extends Fragment {

    private BreakingViewModel breakingViewModel;
    private RecyclerView recyclerView;
    private ArticlesAdapter articlesAdapter;
    private boolean isLoading = false;
    private int page = 0;
    List<UnifiedNativeAd> mNativeAds = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        breakingViewModel =
                ViewModelProviders.of(this).get(BreakingViewModel.class);
        View root = inflater.inflate(R.layout.fragment_breaking, container, false);


        recyclerView = root.findViewById(R.id.recyclerView);

        articlesAdapter = new ArticlesAdapter(getContext(), breakingViewModel.getArticles().getValue());
        recyclerView.setAdapter(articlesAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        breakingViewModel.getArticles().observe(getViewLifecycleOwner(), articles -> articlesAdapter.notifyDataSetChanged());

        getArticles(0, 2);

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);


                if (!recyclerView.canScrollVertically(1) && dy > 0 && !isLoading) {
                    isLoading = true;
                    getArticles(page, page * 10);
                }
            }
        });


        return root;
    }
    private void getArticles(int pageNumber, int startIndex) {

        Call<ArticlesApiResponse> call1 = AppSingleton.getInstance(getContext()).getApiInterface().getBreaking("", pageNumber);
        call1.enqueue(new Callback<ArticlesApiResponse>() {
            @Override
            public void onResponse(Call<ArticlesApiResponse> call, Response<ArticlesApiResponse> apiResponse) {
                if (getActivity() != null) {
                    page++;

                    if (apiResponse.body() != null) {
                        ArticlesApiResponse response = apiResponse.body();
                        breakingViewModel.getArticles().getValue().addAll(Utils.getFilteredArticles(getActivity(),response.getArticles()));
                        breakingViewModel.setArticles(breakingViewModel.getArticles().getValue());

                        if (response.getArticles().size() <= 5)
                            AppSingleton.getInstance(getActivity()).loadNativeAds(mNativeAds, recyclerView, articlesAdapter,
                                    response.getArticles(), breakingViewModel.getArticles().getValue(), 1, 0);
                        else
                            AppSingleton.getInstance(getActivity()).loadNativeAds(mNativeAds, recyclerView, articlesAdapter,
                                    response.getArticles(), breakingViewModel.getArticles().getValue(), NUMBER_OF_NATIVE_ADS_NEWS, startIndex);
                    }

                }

            }

            @Override
            public void onFailure(Call<ArticlesApiResponse> call, Throwable t) {
                t.printStackTrace();
                call.cancel();
            }
        });

    }
}
