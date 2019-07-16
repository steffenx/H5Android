package com.example.steffenxuan.yncloudapp.core.netUtil;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Steffen_xuan on 2019/6/13.
 */

public class HttpGetPicUtils {
    private final static String URL_PATH = "http://ww4.sinaimg.cn/bmiddle/9e58dccejw1e6xow22oc6j20c80gyaav.jpg";
    public static InputStream getImageViewInputStream() throws IOException {
        InputStream inputStream = null;
        URL url = new URL(URL_PATH);
        if (url != null) {

            HttpURLConnection httpURLConnection = (HttpURLConnection) url
                    .openConnection();
            httpURLConnection.setConnectTimeout(3000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoInput(true);
            int response_code = httpURLConnection.getResponseCode();
            if (response_code == 200) {
                inputStream = httpURLConnection.getInputStream();
            }
        }

        return inputStream;
    }
}
