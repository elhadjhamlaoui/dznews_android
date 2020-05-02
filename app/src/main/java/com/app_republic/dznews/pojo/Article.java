
package com.app_republic.dznews.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity
public class Article implements Parcelable {

    @ColumnInfo(name = "__v")
    @SerializedName("__v")
    private Long _V;
    @PrimaryKey
    @NonNull
    @Expose
    private String _id;
    @ColumnInfo(name = "created_at")
    @SerializedName("created_at")
    private String createdAt;
    @ColumnInfo(name = "date_time")
    @Expose
    private String dateTime;
    @ColumnInfo(name = "image")
    @Expose
    private String image;
    @ColumnInfo(name = "link")
    @Expose
    private String link;
    @ColumnInfo(name = "readable_time")
    @Expose
    private String readableTime;
    @ColumnInfo(name = "source")
    @Expose
    private String source;
    @ColumnInfo(name = "source_ar")
    @Expose
    private String sourceAr;
    @ColumnInfo(name = "title")
    @Expose
    private String title;

    public Article() {
    }

    protected Article(Parcel in) {
        if (in.readByte() == 0) {
            _V = null;
        } else {
            _V = in.readLong();
        }
        _id = in.readString();
        createdAt = in.readString();
        dateTime = in.readString();
        image = in.readString();
        link = in.readString();
        readableTime = in.readString();
        source = in.readString();
        sourceAr = in.readString();
        title = in.readString();
    }

    public static final Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };

    public Long get_V() {
        return _V;
    }

    public void set_V(Long _V) {
        this._V = _V;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getReadableTime() {
        return readableTime;
    }

    public void setReadableTime(String readableTime) {
        this.readableTime = readableTime;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSourceAr() {
        return sourceAr;
    }

    public void setSourceAr(String sourceAr) {
        this.sourceAr = sourceAr;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        if (_V == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(_V);
        }
        parcel.writeString(_id);
        parcel.writeString(createdAt);
        parcel.writeString(dateTime);
        parcel.writeString(image);
        parcel.writeString(link);
        parcel.writeString(readableTime);
        parcel.writeString(source);
        parcel.writeString(sourceAr);
        parcel.writeString(title);
    }
}
