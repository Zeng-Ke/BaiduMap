package com.zk.baidumap;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.Projection;
import com.baidu.mapapi.map.WinRound;
import com.baidu.mapapi.model.LatLng;

import java.util.List;

import static com.zk.baidumap.SearchAddressActivity.KEY_RESULT_ADDRESS;

public class MainActivity extends AppCompatActivity {

    private MapView mMapView;
    private MapManager mMapManager;

    private final int REQUET_CODE_LOCATION_PERMISSION = 1001;
    public final int REQUESt_CODE_SEARCH_ADDRESS = 1002;

    private final String KEY_MARKER = "key_marker";
    private MapProjectInfoWindowView mInfoWindowView;
    private Animation mMapInfoWindowAnimation;

    private Projection mProjection;
    private TextView mBtnLocation;
    private boolean mHasLocatePermission;
    private TextView mBtnSearchAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMapView = (MapView) findViewById(R.id.mapview);
        mBtnLocation = (TextView) findViewById(R.id.btn_location);
        mBtnSearchAddress = (TextView) findViewById(R.id.btn_search_address);

        mMapManager = new MapManager(mMapView, getApplicationContext());
        mInfoWindowView = new MapProjectInfoWindowView(this);
        mMapManager.hidenMapNativeWiget();
        mMapManager.setZoomLevel(14.5f);
        mMapManager.initLocationConfig(5000, R.mipmap.map_locate);

        initListener();
    }

    private void initListener() {
        mMapManager.setOnMapLoadedCallBack(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mProjection = mMapView.getMap().getProjection();
                WinRound winRound = mMapView.getMap().getMapStatus().winRound;

            }
        });


        //定位监听(定位成功后设置定位图层，移动地图，加载数据，改变列表模式页面的坐标)
        mMapManager.setOnLocateListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                if (bdLocation.getLocType() == MapManager.LOCATE_SUCCESS_GPS_TYPE || bdLocation.getLocType() == MapManager
                        .LOCATE_SUCCESS_NETWORK_TYPE) {
                    String mCity = bdLocation.getCity();
                    mMapManager.setLocateData(bdLocation.getLatitude(), bdLocation.getLongitude());
                    mMapManager.setZoomLevel(14.5f);

                    mMapManager.moveMap(bdLocation.getLatitude(), bdLocation.getLongitude());
                    //mMapManager.clear();
                    List<AddressBean> addressBeanList = PositionDatas.getPositionDatas();
                    for (AddressBean addressBean : addressBeanList) {
                        mMapManager.addOverlay(addressBean.mLatLngBean.latitude, addressBean.mLatLngBean.longitude, R.mipmap
                                .map_marker_project, addressBean.name, KEY_MARKER);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "定位失败，请检查网络设置", Toast.LENGTH_LONG).show();
                    return;
                }
                mMapManager.stopLocate();
            }
        });
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager
                .PERMISSION_GRANTED) {
            mMapManager.startLocate();
            mHasLocatePermission = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUET_CODE_LOCATION_PERMISSION);
        }

        mMapManager.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getExtraInfo() != null) {
                    String name = (String) marker.getExtraInfo().get(KEY_MARKER);
                    if (name != null && name != "") {
                        mInfoWindowView.setData(name);
                        showInfowWindow(marker.getPosition());

                    }
                }
                return false;
            }
        });

        mBtnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mHasLocatePermission) {
                    mInfoWindowView.post(new Runnable() {
                        @Override
                        public void run() {
                            mMapManager.reqeustLocation();
                        }
                    });

                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUET_CODE_LOCATION_PERMISSION);
                }
            }
        });

        mBtnSearchAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SearchAddressActivity.class);
                startActivityForResult(intent, REQUESt_CODE_SEARCH_ADDRESS);
            }
        });

    }


    public void showInfowWindow(LatLng latLng) {
        if (mInfoWindowView != null) {
            mMapView.removeView(mInfoWindowView);
        }
        mInfoWindowView.restoryPopViewTranslation();
        mMapManager.addView(mInfoWindowView, latLng);

        if (mProjection != null) {
            mInfoWindowView.post(new Runnable() {
                @Override
                public void run() {
                    if (mInfoWindowView.getLeft() < 0) {
                        mInfoWindowView.setTranslationX(mInfoWindowView.getLeft() * -1);
                        mInfoWindowView.getArrowBottomView().setTranslationX(mInfoWindowView.getLeft());
                        mInfoWindowView.getArrowTopView().setTranslationX(mInfoWindowView.getLeft());
                    }
                    if (mInfoWindowView.getRight() > DisplayUtils.getScreenWidth()) {
                        float dx = (mInfoWindowView.getRight() - DisplayUtils.getScreenWidth());
                        mInfoWindowView.setTranslationX(dx * -1);
                        mInfoWindowView.getArrowBottomView().setTranslationX(dx);
                        mInfoWindowView.getArrowTopView().setTranslationX(dx);
                    }
                    if (mInfoWindowView.getTop() < 0) {
                        mInfoWindowView.setTranslationY(ViewUtils.getViewHeight(mInfoWindowView.getPopContentView()) + DisplayUtils
                                .dp2px(5));
                        mInfoWindowView.setViewDirection(true);
                    } else {
                        mInfoWindowView.setViewDirection(false);
                    }

                }
            });
            if (mMapInfoWindowAnimation == null)
                mMapInfoWindowAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_showview);
            mInfoWindowView.startAnimation(mMapInfoWindowAnimation);

        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUET_CODE_LOCATION_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mHasLocatePermission = true;
                    mMapManager.startLocate();
                } else
                    ActivityCompat.requestPermissions(this, new String[]{permissions[0]},
                            REQUET_CODE_LOCATION_PERMISSION);
                break;
            default:
                break;

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUESt_CODE_SEARCH_ADDRESS:
                LatLngBean latLngBean = (LatLngBean) data.getSerializableExtra(KEY_RESULT_ADDRESS);
                mMapManager.moveMap(latLngBean.latitude, latLngBean.longitude);
                break;
            default:
                break;

        }
    }
}
