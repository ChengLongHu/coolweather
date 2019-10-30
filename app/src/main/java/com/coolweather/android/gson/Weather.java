package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 我爱王金ge on 2019/10/28.
 */

public class Weather {
    //数据返回情况
    public String status;
    //基本情况
    public Basic basic;
    //现在的数据
    public Now now;
    //更新时间
    public Update update;
    public class Update {
        @SerializedName("loc")
        public String updateTime;
    }
}
