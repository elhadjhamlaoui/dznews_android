
package com.app_republic.dznews.pojo;

import java.util.List;
import com.google.gson.annotations.Expose;

@SuppressWarnings("unused")
public class ArticlesApiResponse {

    @Expose
    private List<Article> articles;

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

}
