package com.app_republic.dznews.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.app_republic.dznews.interfaces.ArticleDao;
import com.app_republic.dznews.pojo.Article;

@Database(entities = {Article.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ArticleDao articleDao();
}