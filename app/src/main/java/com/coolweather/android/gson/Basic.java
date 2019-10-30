package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 我爱王金ge on 2019/10/28.
 */

public class Basic {

    //城市
    @SerializedName("location")
    public String cityName;

    //城市id
    @SerializedName("cid")
    public String weatherId;

}
