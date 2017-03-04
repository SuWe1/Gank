package com.gank.bean;

import java.util.ArrayList;

/**
 * Created by 11033 on 2017/3/4.
 */

public class GankNews {
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

    public class Question{
        private ArrayList<String> images;
        private String desc;
        private int type;
        private int url;
        private int _id;

        public int get_id() {
            return _id;
        }

        public void set_id(int _id) {
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

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getUrl() {
            return url;
        }

        public void setUrl(int url) {
            this.url = url;
        }
    }
}
