package com.coolweather.android.db;

import org.litepal.crud.DataSupport;

/**
 * 省
 */
public class Province extends DataSupport {
    private int id;
    private String provinceName;
    private int provinceCode;
    private int getId(){
        return id;
    }
    private void setId(int id){
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}
