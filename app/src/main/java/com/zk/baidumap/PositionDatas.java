package com.zk.baidumap;

import java.util.ArrayList;
import java.util.List;

/**
 * author: ZK.
 * date:   On 2017/11/28.
 */
public class PositionDatas {


    public static List<AddressBean> getPositionDatas() {

        List<AddressBean> list = new ArrayList<>();
        list.add(new AddressBean(new LatLngBean(113.329857, 23.112304), "广州塔"));
        list.add(new AddressBean(new LatLngBean(113.34847, 23.131448), "广州云莱斯堡酒店"));
        list.add(new AddressBean(new LatLngBean(113.355225, 23.144408), "华南师范大学五山校区"));
        list.add(new AddressBean(new LatLngBean(113.368951, 23.108381), "广州市国际会展中心"));
        list.add(new AddressBean(new LatLngBean(113.35156, 23.118885), "珠江新城海滨花园"));
        list.add(new AddressBean(new LatLngBean(113.31455, 23.114098), "广州星海音乐厅"));
        list.add(new AddressBean(new LatLngBean(113.331869, 23.143677), "广州市天河体育中心"));
        list.add(new AddressBean(new LatLngBean(113.303411, 23.186604), "广州市白云山"));
        list.add(new AddressBean(new LatLngBean(113.411639, 23.055254), "广州市江南水果交易区"));
        list.add(new AddressBean(new LatLngBean(113.411639, 23.055254), "华南理工大学大学城校区"));

        return list;

    }
}
