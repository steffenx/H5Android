package com.example.steffenxuan.yncloudapp.mainBody.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.example.steffenxuan.yncloudapp.R;
import com.example.steffenxuan.yncloudapp.core.HtmlUrl;
import com.example.steffenxuan.yncloudapp.core.netUtil.HttpGetPicUtils;
import com.example.steffenxuan.yncloudapp.core.util.AppUpgradeUtil;
import com.example.steffenxuan.yncloudapp.core.util.CheckPermissionUtil;
import com.example.steffenxuan.yncloudapp.core.util.NativeWebViewUtil;
import com.example.steffenxuan.yncloudapp.core.util.RoundImageView;
import com.example.steffenxuan.yncloudapp.core.util.SharedPreferencesUtil;
import com.example.steffenxuan.yncloudapp.core.util.ToastUtil;
import com.example.steffenxuan.yncloudapp.mainBody.presenter.CloudMainPresenter;
import com.example.steffenxuan.yncloudapp.mainBody.view.ICloudMainView;
import com.example.steffenxuan.yncloudapp.sqLite.DatabaseOperation_userInfo;
import com.google.gson.JsonObject;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

public class CloudMainActivity extends CheckPermissionUtil
        implements AMapLocationListener,NavigationView.OnNavigationItemSelectedListener ,ICloudMainView {
    private String TAG="CloudMainActivityDebug";
    //控件定义
    private WebView webview;
    private android.support.v7.widget.Toolbar toolbar;
    private android.support.design.widget.NavigationView navigationview;
    private android.support.v4.widget.DrawerLayout drawerlayout;
    private android.support.v7.widget.AppCompatImageView img_round;
    private TextView companyText;
    private TextView addressText;

    /*创建一个Drawerlayout和Toolbar联动的开关*/
    private ActionBarDrawerToggle toggle;

    private long exitTime=0;//按两次返回退出应用的时间间隔
    private boolean isLocation=false;//是否定位的标志
    //定位服务类
    private AMapLocationClient locationClient = null;
    //定位参数类
    private AMapLocationClientOption locationClientOption = null;

    private JSONObject location=new JSONObject();
    private int REQUEST_CODE_SCAN=1;//扫码跳转标志

    private String v_code;

    private CloudMainPresenter cloudMainPresenter;

    private int terminal_id=21;//默认基地id
    private String terminal_name="川蒲生态基地";//默认基地名称



//        setSupportActionBar(toolbar);
//
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawerlayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawerlayout.addDrawerListener(toggle);
//        toggle.syncState();
//
//        navigationview.setNavigationItemSelectedListener(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_drawer);

        initView();//初始化控件
        setPreenter();

        initMixedPage();//初始化H5页面

        /*隐藏滑动条*/
        hideScrollBar();
        /*设置ActionBar*/
        setActionBar();
        /*设置Drawerlayout开关*/
        setDrawerToggle();
        /*设置监听器*/
        setListener();

        LoadingHead();

    }


    private void initView() {
        v_code= AppUpgradeUtil.getInstance().getVersionCode(this);
        Log.d(TAG, "initView: "+v_code);

        webview=findViewById(R.id.webview);
        webview.setWebChromeClient(new WebChromeClient());//允许js弹窗
        toolbar =  findViewById(R.id.toolbar);
        drawerlayout = findViewById(R.id.drawer_layout);
        navigationview = findViewById(R.id.navigation_view);

        if(navigationview.getHeaderCount() > 0){
            View headerLayout = navigationview.getHeaderView(0);
            img_round = headerLayout.findViewById(R.id.headerpic);
            companyText=headerLayout.findViewById(R.id.companyText);
            addressText=headerLayout.findViewById(R.id.addressText);
        }
        else {
            View headerLayout = navigationview.inflateHeaderView(R.layout.navigation_drawer_header);
            img_round = headerLayout.findViewById(R.id.headerpic);
            companyText=headerLayout.findViewById(R.id.companyText);
            addressText=headerLayout.findViewById(R.id.addressText);
        }

    }
    private void setPreenter() {
        this.cloudMainPresenter = new CloudMainPresenter(this);
    }

    //在消息队列中实现对控件的更改
    private Handler mhandle = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Bitmap bmp=(Bitmap)msg.obj;
                    img_round.setImageBitmap(bmp);
                    System.gc();
                    break;
            }
        };
    };

    /**
     * 加载网络图片/header布局中的数据
     */
    public void LoadingHead(){
        String hmobile= (String) SharedPreferencesUtil.getmInstance().get(this,"account","失败");
        if(hmobile.equals("失败")) return;
        String company=DatabaseOperation_userInfo.getInstance().getCompany(this,hmobile);
        String address=DatabaseOperation_userInfo.getInstance().getAddress(this,hmobile);
        companyText.setText(company);
        addressText.setText(address);
        String picurl= DatabaseOperation_userInfo.getInstance().getAvatar(this,hmobile);
        final String finalPicurl="http://cloud.farmereasy.com"+picurl;
        //新建线程加载图片信息，发送到消息队列中0
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                Bitmap bmp = getURLimage(finalPicurl);
                if (bmp==null) return;
                Message msg = new Message();
                msg.what = 0;
                msg.obj = compressImage(bmp);

                mhandle.sendMessage(msg);
            }
        }).start();
    }


    private Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 10, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while ( baos.toByteArray().length / 1024>100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }


    private Bitmap getURLimage(String imageUrl) {
        Bitmap bmp = null;
        try {
            URL myurl = new URL(imageUrl);
            // 获得连接
            HttpURLConnection conn = (HttpURLConnection) myurl.openConnection();
            conn.setConnectTimeout(6000);//设置超时
            conn.setDoInput(true);
            conn.setUseCaches(false);//不缓存
            conn.connect();
            InputStream is = conn.getInputStream();//获得图片的数据流
            bmp = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;

    }


    /*去掉navigation中的滑动条*/
    private void hideScrollBar() {
        navigationview.getChildAt(0).setVerticalScrollBarEnabled(false);
    }
    /*设置ActionBar*/
    private void setActionBar() {
        setSupportActionBar(toolbar);
        /*显示Home图标*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(terminal_name);
    }
    /*设置Drawerlayout的开关,并且和Home图标联动*/
    private void setDrawerToggle() {
        toggle = new ActionBarDrawerToggle(this, drawerlayout, toolbar, 0, 0);
        drawerlayout.addDrawerListener(toggle);
        /*同步drawerlayout的状态*/
        toggle.syncState();


    }
    /*设置监听器*/
    private void setListener() {
        navigationview.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.single_2:
                        Intent i=new Intent(CloudMainActivity.this,Login_page.class);
                        finish();
                        startActivity(i);
                        SharedPreferencesUtil.getmInstance().put(CloudMainActivity.this,"logintime",100);
                        break;
                    case R.id.item_3:
                        Intent aboutpage=new Intent(CloudMainActivity.this,AboutYNcloudActivity.class);
                        startActivity(aboutpage);
                        break;
                    case R.id.single_3:
                        Intent shopi=new Intent(CloudMainActivity.this,ProductStoreActivity.class);
                        startActivity(shopi);
                        break;
                }
                drawerlayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }


    @SuppressLint("JavascriptInterface")
    private void initMixedPage() {

        terminal_id=(Integer) SharedPreferencesUtil.getmInstance().get(this,"defaultvalue",0);
        terminal_name=(String) SharedPreferencesUtil.getmInstance().get(this,"defaultkey","易农云");
        Log.d(TAG, "initView: "+terminal_id);

        cloudMainPresenter.judgmentUpgrade(this);

        //封装webview
        NativeWebViewUtil nativeWebViewUtil = new NativeWebViewUtil();
        nativeWebViewUtil.WebViewSetting(this,webview);
        //添加Javascript的映射
        webview.addJavascriptInterface(this,"android");
        webview.loadUrl(HtmlUrl.MainHtmlUrl);

        webview.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                // 开始加载页面时

                Calendar calendar = Calendar.getInstance();
                //日
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                //小时
                int hour = calendar.get(Calendar.HOUR_OF_DAY);

                //获取定位时间
                String locationTime=(String) SharedPreferencesUtil.getmInstance().get(CloudMainActivity.this,"locationTime","失败");
                //没有定位时间
                if (locationTime.equals("失败")){
                    //开始定位
                    Log.d("monitorActivityPage","定位1");
                    isLocation=true;
                    LocationOpen();
                }
                else {
                    //分解定位时间 前xx-xx-xx    后xx:xx:xx
                    String qian = locationTime.substring(0, locationTime.indexOf(" "));
                    String hou = locationTime.substring(locationTime.indexOf(" "));

                    //转换int类型
                    Integer locationday=Integer.valueOf(qian.substring(qian.length()-2)).intValue();//天
                    Integer locationhour=Integer.valueOf(hou.substring(1,3)).intValue()+5;//小时
                    //Log.d("monitorActivityPage",hou.substring(1,3));
                    //定位天比今天小||每隔5小时后定位
                    if (locationday < day || locationhour<=hour){
                        //开始定位
                        Log.d("monitorActivityPage","定位");
                        isLocation=true;
                        LocationOpen();
                    }
                }

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // 加载结束
                //没有定位
                if (!isLocation){
                    Log.d("monitorActivityPage","保存的");
                    //加载保存的天气信息
                    String fl=(String)SharedPreferencesUtil.getmInstance().get(CloudMainActivity.this,"fl","--");
                    String cond_txt=(String)SharedPreferencesUtil.getmInstance().get(CloudMainActivity.this,"cond_txt","--");
                    String parent_city=(String)SharedPreferencesUtil.getmInstance().get(CloudMainActivity.this,"parent_city","--");
                    String location=(String)SharedPreferencesUtil.getmInstance().get(CloudMainActivity.this,"location","--");

                    JSONObject jo=new JSONObject();
                    try {
                        jo.put("fl",fl);
                        jo.put("cond_txt",cond_txt);
                        jo.put("parent_city",parent_city);
                        jo.put("location",location);
                        jo.put("locationTime",StringDate());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    webview.evaluateJavascript("javascript:get_oldandroid("+jo+")", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            //此处为 js 返回的结果
                            Log.v("Native",value);
                        }
                    });
                }
            }
        });

    }

    /**
     * 初始化高德定位sdk
     */
    public void LocationOpen(){
        //初始化定位
        locationClient = new AMapLocationClient(getApplicationContext());
        locationClient.setLocationListener(this);

        //定位参数设置
        locationClientOption = new AMapLocationClientOption();
        locationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);

        locationClient.setLocationOption(locationClientOption);

        locationClient.startLocation();
    }

    /**
     * 从js获取点击信息
     * @param data
     */
    @JavascriptInterface
    public void get_data(String data){

        if (data.equals("iotdata")){
            Intent i=new Intent();
            Bundle b = new Bundle();
            b.putString("terminal_id", String.valueOf(terminal_id));
            i.setClass(CloudMainActivity.this,Iot_DataActivity.class);
            i.putExtra("id",b);
            startActivity(i);
        }
        if (data.equals("videomonitor")){
            Intent i=new Intent();
            Bundle b = new Bundle();
            b.putString("terminal_id", String.valueOf(terminal_id));
            b.putString("terminal_name", String.valueOf(terminal_name));
            i.setClass(CloudMainActivity.this,video_monitorActivity.class);
            i.putExtra("id",b);
            startActivity(i);
        }
        if (data.equals("sweepcode")){
            Intent intent = new Intent(this, CaptureActivity.class);
            startActivityForResult(intent, REQUEST_CODE_SCAN);
        }
        if (data.equals("iotcontrol")){
            Intent intent = new Intent(this, TimeCurveActivity.class);
            startActivity(intent);
        }
    }

    /**
     * 从js获取并保存天气信息
     * @param weatherdata
     * @throws JSONException
     */
    @JavascriptInterface
    public void set_weather(String weatherdata) throws JSONException {

        JSONObject jo=new JSONObject(weatherdata);
        //Log.d("monitorActivityPage",jo.toString());
        SharedPreferencesUtil.getmInstance().put(this,"fl",jo.getString("fl"));
        SharedPreferencesUtil.getmInstance().put(this,"cond_txt",jo.getString("cond_txt"));
        SharedPreferencesUtil.getmInstance().put(this,"parent_city",jo.getString("parent_city"));
        SharedPreferencesUtil.getmInstance().put(this,"location",jo.getString("location"));
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 按两次返回退出app
     */
    private void exit(){
        if((System.currentTimeMillis()-exitTime)>2000) {
            ToastUtil.AlentShortToast(this, "再按一次退出程序");
            exitTime = System.currentTimeMillis();
        }
        else{
                finish();
                System.exit(0);
            }
        }

    /**
     * 当位置信息改变的时候
     * @param aMapLocation
     */
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {

            if (aMapLocation.getErrorCode() == 0) {
                try {
                    Log.d("monitorActivityPage","change");
                    location.put("lat",aMapLocation.getLatitude());//纬度
                    location.put("long",aMapLocation.getLongitude());//经度
                    location.put("location",aMapLocation.getAddress());//地理信息

                    //定位时间
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date(aMapLocation.getTime());
                    //保存
                    SharedPreferencesUtil.getmInstance().put(this,"locationTime",df.format(date));
                    location.put("locationTime",StringDate());
                    locationClient.stopLocation();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                try {
                    location.put("lat","0");
                    location.put("long","0");
                    location.put("location",aMapLocation.getErrorInfo());
                    ToastUtil.AlentShortToast(this,"获取天气失败，请打开定位服务");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Log.d("monitorActivityPage", location.toString());
            Message msg=new Message();
            msg.what=0;
            Bundle bundle=new Bundle();
            bundle.putString("msg",location.toString());
            msg.setData(bundle);
            handler.sendMessage(msg);

        }
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Log.d("Ssss",msg.getData().get("msg").toString());
            webview.evaluateJavascript("javascript:get_android("+msg.getData().get("msg").toString()+")", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    //此处为 js 返回的结果
                    //Log.d("monitorActivityPage",value);
                }
            });
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {

                String content = data.getStringExtra(Constant.CODED_CONTENT);
                Intent i=new Intent();
                i.putExtra("url",content);
                i.setClass(this,QR_CodeActivity.class);
                startActivity(i);
            }
        }
    }

    /**
     * 返回当前日期和星期
     * @return
     */
    public static String StringDate(){
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
        String mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
        String mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
        String mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        if("1".equals(mWay)){
            mWay ="天";
        }else if("2".equals(mWay)){
            mWay ="一";
        }else if("3".equals(mWay)){
            mWay ="二";
        }else if("4".equals(mWay)){
            mWay ="三";
        }else if("5".equals(mWay)){
            mWay ="四";
        }else if("6".equals(mWay)){
            mWay ="五";
        }else if("7".equals(mWay)){
            mWay ="六";
        }
        return mYear + "年" + mMonth + "月" + mDay+"日"+" 星期"+mWay;
    }


    /**
     * 添加按钮
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.main, menu);
        String base=(String) SharedPreferencesUtil.getmInstance().get(this,"base","失败");
        //Log.d("monitorActivityPagesss",base);
        if (!base.equals("失败")) {
            try {
                JSONArray ja=new JSONArray(base);
                for (int i=0;i<ja.length();i++){
                    JSONObject jo=ja.getJSONObject(i);
                    menu.add(1, Integer.parseInt(jo.getString("id")), 1, jo.getString("name"));
                    //Log.d("monitorActivityPagesss",jo.toString());
                }

                //Log.d("monitorActivityPagesss",jo.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("monitorActivityPagesss","dd");
            }

            //menu.add(1, 100, 1, "菜单一");//动态添加一个按钮；
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        terminal_name=(String) item.getTitle();
        getSupportActionBar().setTitle(terminal_name);
        terminal_id=id;
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }


    /**
     * 传到p层的版本号
     * @return
     */
    @Override
    public String getV_Code() {
        return v_code;
    }

    @Override
    public void onGetDataSeccess(String apkurl) {
        Log.d(TAG, "onGetDataSeccess: "+apkurl);
        Message msg=new Message();
        msg.what=0;
        Bundle bundle=new Bundle();
        bundle.putString("msg",apkurl);
        msg.setData(bundle);
        downloadhandler.sendMessage(msg);

    }

    Handler downloadhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            AppUpgradeUtil.getInstance().showHintDialog(CloudMainActivity.this,msg.getData().get("msg").toString());
            super.handleMessage(msg);
        }
    };

    @Override
    public void onGetDataFails() {
        ToastUtil.AlentShortToast(this,"出现错误了！");
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationClient != null) {
            locationClient.onDestroy();
            locationClient = null;
            locationClientOption = null;
        }
        cloudMainPresenter=null;
        handler.removeCallbacksAndMessages(null);
        mhandle.removeCallbacksAndMessages(null);
        handler=null;
        mhandle=null;
    }


}
