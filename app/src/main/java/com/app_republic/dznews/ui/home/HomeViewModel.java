package com.app_republic.dznews.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.app_republic.dznews.pojo.Article;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<List<Object>> articles;

    public HomeViewModel() {
        articles = new MutableLiveData<>();
        articles.setValue(new ArrayList<>());
    }

    LiveData<List<Object>> getArticles() {
        return articles;
    }

    void setArticles(List<Object> articles) {
        this.articles.setValue(articles);
    }
}