package com.zk.baidumap;

import java.io.Serializable;

/**
 * author: ZK.
 * date:   On 2017/11/23.
 */
public class LatLngBean  implements Serializable{

    public double latitude;
    public double longitude;

    public LatLngBean() {

    }

    public LatLngBean(double longitude,double latitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
