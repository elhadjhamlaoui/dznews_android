package com.app_republic.dznews.interfaces;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.app_republic.dznews.pojo.Article;

import java.util.List;

@Dao
public interface ArticleDao {
    @Query("SELECT * FROM article")
    List<Article> getAll();

    @Query("SELECT * FROM article WHERE _id IN (:userIds)")
    List<Article> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM article WHERE title LIKE :title LIMIT 1")
    Article findByTitle(String title);

    @Query("SELECT * FROM article WHERE _id LIKE :id LIMIT 1")
    Article findById(String id);

    @Insert
    void insertAll(Article... articles);

    @Delete
    void delete(Article article);
}
