package com.example.steffenxuan.yncloudapp.core.netUtil;

/**
 * Created by Steffen_xuan on 2019/5/29.
 * 请求数据api集合
 */

public class RequestAPI {
    public static String SERVER_URL = "http://admin.farmereasy.com";

    //登录 api
    public static final String loginApi = SERVER_URL+"/api/user/login";

    //检查token
    public static final String tokenApi = SERVER_URL+"/api/token/check";

    //设备数据 api
    public static final String devicesDataApi = SERVER_URL+"/api/devices/getList";

    //修改控制设备信息 /api/devices/update
    public static final String devicesUpdateApi = SERVER_URL+"/api/devices/update";

    //获取设备数据列表 /api/devicesdata/getList
    public static final String devicesdataListApi = SERVER_URL+"/api/devicesdata/getList";

    //基地数据 api
    public static final String qaedasDataApi = SERVER_URL+"/api/qaedas/getList";

    //录像机数据 api
    public static final String vcrDataApi = SERVER_URL+"/api/vcr/getList";

    //萤石云单视频地址数据 api
    public static final String ysyUrlApi = SERVER_URL+"/api/ysy/getOne";

    //萤石云多视频数据 api
    public static final String ysyAllUrlApi = SERVER_URL+"/api/ysy/getAll";

    //萤石云单视频控制数据 api
    public static final String ysyControlUrlApi = SERVER_URL+"/api/ysy/ptzStart";

    //萤石云单视频停止控制数据 api
    public static final String ysyStopControlUrlApi = SERVER_URL+"/api/ysy/ptzStop";

    //检查更新 api
    public static final String upgradeAppApi = "http://y.farmereasy.com:7100/APP/YNCloudH5/checkupdate.php";

}
