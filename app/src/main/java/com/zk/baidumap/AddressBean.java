package com.zk.baidumap;

/**
 * author: ZK.
 * date:   On 2017/11/28.
 */
public class AddressBean {

    public LatLngBean mLatLngBean;
    public String name;


    public AddressBean(LatLngBean latLngBean, String name) {
        mLatLngBean = latLngBean;
        this.name = name;
    }
}
