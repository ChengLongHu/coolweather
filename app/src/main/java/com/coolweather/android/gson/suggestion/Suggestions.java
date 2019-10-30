package com.coolweather.android.gson.suggestion;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 建议
 */

public class Suggestions {
    //数据返回状况
    @SerializedName("status")
    public String status;
    //生活指数类
    @SerializedName("lifestyle")
    public List<Suggestion> Suggestions;

    public class Suggestion{
        @SerializedName("type")
        public String type;
        @SerializedName("brf")
        public String brf;
        @SerializedName("txt")
        public String info;
    }
}
