package com.zk.baidumap;

/**
 * author: ZK.
 * date:   On 2017/9/26
 */

import java.io.Serializable;

/**
 * 选择一个地址SearchBean搜索后，百度地图返回的改地址附近所有地址信息的实体类
 */

public class ReverseAddressBean implements Serializable {

    public String addressName;
    public String district;
    public String province;
    public String city;
    public double latitude;
    public double longitude;
    public boolean isChecked;
}
