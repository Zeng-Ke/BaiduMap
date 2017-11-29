package com.zk.baidumap;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MapViewLayoutParams;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;


import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static com.baidu.mapapi.map.BitmapDescriptorFactory.fromResource;

/**
 * author: ZK.
 * date:   On 2017/9/6.
 */
public class MapManager {

    private final Context mContext;
    private LocationClient mLocationClient;
    private final BaiduMap mBaiduMap;
    private SparseArray<Overlay> mSingleShowOverlayMap;
    private SparseArray<WeakReference<BitmapDescriptor>> mOverlayBitmapMap = new SparseArray<>();
    private BDLocationListener mBDLocationListener;
    private boolean mBooleanRegisterLoc = false;
    private GeoCoder mGeoCoder;
    private MapView mMapView;


    public static final double ERROR_LATITUDE = 4.9E-324;
    public static final int LOCATE_SUCCESS_NETWORK_TYPE = 161;
    public static final int LOCATE_SUCCESS_GPS_TYPE = 61;


    public MapManager(MapView view, Context context) {
        mContext = context;
        mMapView = view;
        mBaiduMap = view.getMap();
    }


    /**
     * 隐藏原生控件
     */
    public void hidenMapNativeWiget() {
        // 隐藏百度logo
        mMapView.removeViewAt(1);
        // 不显示缩放控件
        mMapView.showZoomControls(false);
        // 不显示比例尺控件
        mMapView.showScaleControl(false);

        mBaiduMap.setCompassEnable(false);
    }


    /**
     * 设置地图大小
     *
     * @param zoomLevel
     */
    public void setZoomLevel(float zoomLevel) {
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(zoomLevel).build()));
    }


    public void setLocateData(double latitude, double longitude) {
        mBaiduMap.setMyLocationEnabled(true);
        MyLocationData locationData = new MyLocationData.Builder().direction(100).latitude(latitude).longitude(longitude).build();
        mBaiduMap.setMyLocationData(locationData);
        /*BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(drawableId);
        MyLocationConfiguration configuration = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING, true,
                bitmapDescriptor);
        mBaiduMap.setMyLocationConfiguration(configuration);*/
    }


    /**
     * 配置定位参数
     *
     * @param span : 自动定位的周期时间可选，默认0，即仅定位一次
     */
    public void initLocationConfig(int span, Integer drawableId) {
        mLocationClient = new LocationClient(mContext);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备

        option.setCoorType("bd09ll");
        //可选，默认gcj02，设置返回的定位结果坐标系

        option.setScanSpan(span);
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的

        option.setIsNeedAddress(true);
        //可选，设置是否需要地址信息，默认不需要

        option.setOpenGps(true);
        //可选，默认false,设置是否使用gps

        option.setLocationNotify(true);
        //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果

        option.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”

        option.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到

        option.setIgnoreKillProcess(false);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死

        option.SetIgnoreCacheException(false);
        //可选，默认false，设置是否收集CRASH信息，默认收集

        option.setEnableSimulateGps(false);
        //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要

        option.setWifiCacheTimeOut(5 * 60 * 1000);
        //可选，7.2版本新增能力，如果您设置了这个接口，首次启动定位时，会先判断当前WiFi是否超出有效期，超出有效期的话，会先重新扫描WiFi，然后再定位

        mLocationClient.setLocOption(option);

        mBaiduMap.setMyLocationEnabled(true);
        if (drawableId != null) {
            BitmapDescriptor mLocationOverlay = BitmapDescriptorFactory.fromResource(drawableId);
            mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                    MyLocationConfiguration.LocationMode.NORMAL, true, mLocationOverlay));
        }

    }


    public void startLocate() {
        if (mLocationClient == null) {
            Log.e("maputil", "定位前必须先配置定位参数，initLocateConfig()");
            return;
        }
        //若项目对权限申请进行了封装，可以放在这里判断
        if (mBDLocationListener != null && !mBooleanRegisterLoc) {
            mBooleanRegisterLoc = true;
            mLocationClient.registerLocationListener(mBDLocationListener);
        }
        mLocationClient.start();
    }


    public void reqeustLocation() {
        if (mLocationClient != null)
            mLocationClient.requestLocation();
    }

    //获取中心点
    public Point getCenterPoint() {
        return mBaiduMap.getMapStatus().targetScreen;
    }

    //在百度地图上添加View(通过屏幕坐标点)
    public void addView(View ima, Point poi) {
        MapViewLayoutParams mlp = new MapViewLayoutParams.Builder().layoutMode(MapViewLayoutParams.ELayoutMode.absoluteMode)
                .point(poi).build();
        mMapView.addView(ima, mlp);
    }

    //在百度地图上添加View(通过地图坐标)
    public void addView(View ima, LatLng latLng) {
        MapViewLayoutParams mlp = new MapViewLayoutParams.Builder().layoutMode(MapViewLayoutParams.ELayoutMode.mapMode)
                .position(latLng).build();
        mMapView.addView(ima, mlp);
    }


    /**
     * 平移地图到指定坐标，效果比moveMap平滑(会触发MapStageChangeListener)
     *
     * @param latitude
     * @param longitude
     */
    public void animateMap(double latitude, double longitude) {
        LatLng cenpt = new LatLng(latitude, longitude);
        MapStatus mMapStatus = new MapStatus.Builder().target(cenpt)
                .build();
        // 定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory
                .newMapStatus(mMapStatus);
        mBaiduMap.animateMapStatus(mMapStatusUpdate);
    }

    /**
     * 平移地图到指定坐标(不会触发MaStageChangeListener)
     *
     * @param latitude
     * @param longitude
     */
    public void moveMap(double latitude, double longitude) {
        LatLng cenpt = new LatLng(latitude, longitude);
        MapStatus mMapStatus = new MapStatus.Builder().target(cenpt)
                .build();
        // 定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory
                .newMapStatus(mMapStatus);
        mBaiduMap.setMapStatus(mMapStatusUpdate);
    }


    public void clear() {
        mBaiduMap.clear();
    }


    public void showInfoWindow(InfoWindow infoWindow) {
        mBaiduMap.showInfoWindow(infoWindow);
    }

    public void hideInfoWindow() {
        mBaiduMap.hideInfoWindow();
    }


    public Overlay addOverlay(double latitude, double longitude, @DrawableRes int resId, Serializable object, String key) {
        return addOverlay(latitude, longitude, resId, object, key, 3);
    }

    /**
     * 在某个坐标点添加标记并返回（便于调用者remove）
     *
     * @param latitude
     * @param longitude
     * @param resId
     * @return
     */
    public Overlay addOverlay(double latitude, double longitude, @DrawableRes int resId, Serializable object, String key, int zIndex) {
        //定义Maker坐标点
        LatLng point = new LatLng(latitude, longitude);
        //构建Marker图标
        BitmapDescriptor bitmap = null;
        if (mOverlayBitmapMap.get(resId) != null)
            bitmap = mOverlayBitmapMap.get(resId).get();
        if (bitmap == null) {
            bitmap = fromResource(resId);
            mOverlayBitmapMap.put(resId, new WeakReference<>(bitmap));
        }
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmap)
                .zIndex(zIndex);
        //在地图上添加Marker，并显示
        Marker marker = (Marker) mBaiduMap.addOverlay(option);
        if (object != null && key != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(key, object);
            marker.setExtraInfo(bundle);
        }
        return marker;
    }


    public void setMarkerIcon(Marker marker, @DrawableRes int resId) {
        BitmapDescriptor bitmapDescriptor = null;
        if (mOverlayBitmapMap.get(resId) != null)
            bitmapDescriptor = mOverlayBitmapMap.get(resId).get();
        if (bitmapDescriptor == null) {
            bitmapDescriptor = BitmapDescriptorFactory.fromResource(resId);
            mOverlayBitmapMap.put(resId, new WeakReference<>(bitmapDescriptor));
        }
        marker.setIcon(bitmapDescriptor);
    }


    /**
     * 地图上只允许存在一个的标注物，如定位。注：根据标注的图片id区分
     *
     * @param latitude
     * @param longitude
     * @param resId     :图片资源id
     */
    public void singleAddOverLay(double latitude, double longitude, @DrawableRes int resId, Serializable object, String key) {
        if (mSingleShowOverlayMap == null)
            mSingleShowOverlayMap = new SparseArray<>();
        Overlay overlay = mSingleShowOverlayMap.get(resId);
        if (overlay != null)
            overlay.remove();
        mSingleShowOverlayMap.put(resId, addOverlay(latitude, longitude, resId, object, key));

    }

    /**
     * 在展示弹出覆盖物
     *
     * @param latitude
     * @param longitude
     * @param view
     * @param offsetY   Y轴偏移量
     */
    public void showInfoWindow(double latitude, double longitude, View view, int offsetY) {
        InfoWindow mInfoWindow = new InfoWindow(view, new LatLng(latitude, longitude), offsetY);
        mBaiduMap.showInfoWindow(mInfoWindow);
    }

    public void hideInfowWindow() {
        mBaiduMap.hideInfoWindow();
    }


    public double distanceByLngLat(double lng1, double lat1, double lng2,
                                   double lat2) {
        double radLat1 = lat1 * Math.PI / 180;
        double radLat2 = lat2 * Math.PI / 180;
        double a = radLat1 - radLat2;
        double b = lng1 * Math.PI / 180 - lng2 * Math.PI / 180;
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * 6378137.0;
        s = Math.round(s * 10000) / 10000;
        return s;
    }


    /**
     * 反编码：从经纬度得到地址信息
     */
    public void reverseGeoCode(double latitude, double Longitude) {
        if (mGeoCoder == null) {
           Log.d("baidumap", "please  initGeoCoder  beafore call this method");
            return;
        }
        LatLng code = new LatLng(latitude, Longitude);
        ReverseGeoCodeOption reverseCode = new ReverseGeoCodeOption();
        ReverseGeoCodeOption result = reverseCode
                .location(code);
        mGeoCoder.reverseGeoCode(result);
    }

    public void destoryGeoCoder() {
        if (mGeoCoder != null) {
            mGeoCoder.destroy();
            mGeoCoder = null;
        }

    }

    public void destoryMapView() {
        mMapView.onDestroy();
        mMapView = null;
    }


    /**
     * 停止定位
     */
    public void stopLocate() {
        if (mLocationClient == null)
            return;
        mLocationClient.stop();
    }

    public void restartLocate() {
        if (mLocationClient == null)
            return;
        mLocationClient.restart();
    }


    /**
     * 停止定位，页面destory时调用
     */
    public void destoryLocate() {
        if (mLocationClient == null)
            return;
        mLocationClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        if (mBDLocationListener != null) {
            mLocationClient.unRegisterLocationListener(mBDLocationListener);
            mBDLocationListener = null;
        }
        mBooleanRegisterLoc = false;
        mLocationClient = null;
    }


    public void initGeoCoder(final OnGeoCoderResultCallBackListener callBackListener) {
        mGeoCoder = GeoCoder.newInstance();
        mGeoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                Boolean isError = (geoCodeResult == null || geoCodeResult.error != SearchResult.ERRORNO.NO_ERROR);
                LatLngBean bean = new LatLngBean();
                if (!isError) {
                    LatLng latLng = geoCodeResult.getLocation();
                    bean.latitude = latLng.latitude;
                    bean.longitude = latLng.longitude;
                    callBackListener.addressToLatLng(latLng.latitude, latLng.longitude);
                } else callBackListener.onError();
            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                if (reverseGeoCodeResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
                    callBackListener.requestNoFound();
                    return;
                }
                if (reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR || reverseGeoCodeResult == null) {
                    callBackListener.onError();
                    return;
                }
                String city = reverseGeoCodeResult.getAddressDetail().city;
                String province = reverseGeoCodeResult.getAddressDetail().province;
                List<ReverseAddressBean> addressBeanList = new ArrayList<>();
                ReverseAddressBean reverseAddressBean = new ReverseAddressBean();
                reverseAddressBean.addressName = reverseGeoCodeResult.getAddress();
                reverseAddressBean.district = reverseGeoCodeResult.getAddress();
                reverseAddressBean.city = city;
                reverseAddressBean.province = province;
                reverseAddressBean.isChecked = true;
                reverseAddressBean.latitude = reverseGeoCodeResult.getLocation().latitude;
                reverseAddressBean.longitude = reverseGeoCodeResult.getLocation().longitude;
                addressBeanList.add(reverseAddressBean);

                List<PoiInfo> listInf = reverseGeoCodeResult.getPoiList();// POI数据
                if (listInf != null && listInf.size() != 0) {
                    for (int i = 0; i < listInf.size(); i++) {
                        PoiInfo p = listInf.get(i);

                        ReverseAddressBean reverseBean = new ReverseAddressBean();
                        reverseBean.addressName = p.name;
                        reverseBean.district = p.address;
                        reverseBean.city = city;
                        reverseBean.isChecked = false;
                        reverseBean.province = province;
                        reverseBean.latitude = p.location.latitude;
                        reverseBean.longitude = p.location.longitude;
                        addressBeanList.add(reverseBean);
                    }
                }
                callBackListener.callBackSuggestLocationData(addressBeanList);

            }
        });
    }

    public interface OnGeoCoderResultCallBackListener {

        void addressToLatLng(double latitude, double longitude);//根据地址返回坐标

        void onError();//查找失败

        void requestNoFound();//没有查找结果

        void callBackSuggestLocationData(List<ReverseAddressBean> addressBeanList);//当GeoCoder调用reverseGeoCode查询指定地址的周边信息时，通过此回调

    }


    /**
     * 手势操作地图的监听
     *
     * @param mapStausChangeListener
     */
    public void setOnMapStausChangeListener(BaiduMap.OnMapStatusChangeListener mapStausChangeListener) {
        if (mapStausChangeListener != null)
            mBaiduMap.setOnMapStatusChangeListener(mapStausChangeListener);
    }

    /**
     * 地图覆盖物点击监听
     *
     * @param markerClickListener
     */
    public void setOnMarkerClickListener(BaiduMap.OnMarkerClickListener markerClickListener) {
        if (markerClickListener != null)
            mBaiduMap.setOnMarkerClickListener(markerClickListener);
    }


    public void setOnLocateListener(BDLocationListener listener) {
        mBDLocationListener = listener;
    }

    public void setOnMapTouchListener(BaiduMap.OnMapTouchListener mapTouchListener) {
        if (mapTouchListener != null)
            mBaiduMap.setOnMapTouchListener(mapTouchListener);
    }

    public void setOnMapLoadedCallBack(BaiduMap.OnMapLoadedCallback mapLoadedCallBack) {
        if (mapLoadedCallBack != null)
            mBaiduMap.setOnMapLoadedCallback(mapLoadedCallBack);
    }


}
