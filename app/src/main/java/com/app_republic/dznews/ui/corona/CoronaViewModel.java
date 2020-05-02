package com.app_republic.dznews.ui.corona;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.app_republic.dznews.pojo.Article;

import java.util.ArrayList;
import java.util.List;

public class CoronaViewModel extends ViewModel {

    private MutableLiveData<List<Object>> articles;

    public CoronaViewModel() {
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