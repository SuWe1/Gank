package com.gank.bean;

import com.gank.adapter.BookMarksAdapter;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Default;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

import java.util.ArrayList;

/**
 * Created by Swy on 2017/3/4.
 */

public class GankNews extends BaseBean {
    private String error;
    private ArrayList<Question> results;


    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public ArrayList<Question> getResults() {
        return results;
    }

    public void setResults(ArrayList<Question> results) {
        this.results = results;
    }

    @Table("Gank")
    public class Question extends BaseBean {

        public Question() {
            this.beanTeype = BeanTeype.TYPE_Gank;
        }

        public static final String COL_MARK = "mark";
        public static final String COL_ID = "_id";
        @PrimaryKey(AssignType.AUTO_INCREMENT)
        private int id;
        @Column("images")
        private ArrayList<String> images;
        @Column(COL_ID)
        private String _id;
        @Column("desc")
        private String desc;
        @Column("type")
        private String type;
        @Column("url")
        private String url;
        @Default("false")
        @Column(COL_MARK)
        public boolean mark = false;

        @Column("publishedAt")
        private String publishedAt;
        @Column("who")
        private String who;

        public String getPublishedAt() {
            return publishedAt;
        }

        public void setPublishedAt(String publishedAt) {
            this.publishedAt = publishedAt;
        }

        public String getWho() {
            return who;
        }

        public void setWho(String who) {
            this.who = who;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public ArrayList<String> getImages() {
            return images;
        }

        public void setImages(ArrayList<String> images) {
            this.images = images;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
