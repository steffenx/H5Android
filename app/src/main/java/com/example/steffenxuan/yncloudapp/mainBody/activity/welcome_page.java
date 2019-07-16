package com.example.steffenxuan.yncloudapp.mainBody.activity;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.steffenxuan.yncloudapp.R;
import com.example.steffenxuan.yncloudapp.core.adapter.GuideViewPagerForWelcomeAdapter;
import com.example.steffenxuan.yncloudapp.core.util.SharedPreferencesForWelcomeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author:     y'y'x
 * @date:
 * @activity：
 * @version:
 * @project:    云平台物联网系统
 * @description:  引导页
 */
public class welcome_page extends AppCompatActivity implements View.OnClickListener {

    private ViewPager vp;
    private GuideViewPagerForWelcomeAdapter adapter;
    private List<View> views;
    private Button startBtn;

    // 引导页图片资源
    private  final int[] pics = {
            R.layout.activity_welcome_one,
            R.layout.activity_welcome_two,
            R.layout.activity_welcome_three
    };

    // 底部小点图片
    private ImageView[] dots;

    // 记录当前选中位置
    private int currentIndex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);

        views = new ArrayList<View>();

        // 初始化引导页视图列表
        for (int i = 0; i < pics.length; i++) {
            View view = LayoutInflater.from(this).inflate(pics[i], null);

            if (i == pics.length - 1) {
                startBtn = (Button) view.findViewById(R.id.btn_enter);
                startBtn.setTag("enter");
                startBtn.setOnClickListener(this);
            }

            views.add(view);

        }

        vp = (ViewPager) findViewById(R.id.vp_guide);
        adapter = new GuideViewPagerForWelcomeAdapter(views);
        vp.setAdapter(adapter);
        vp.addOnPageChangeListener(new PageChangeListener());

        initDots();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 如果切换到后台，就设置下次不进入功能引导页
        SharedPreferencesForWelcomeUtil.putBoolean(welcome_page.this, SharedPreferencesForWelcomeUtil.FIRST_OPEN, false);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter!=null){
            adapter=null;
        }
        System.gc();
    }

    private void initDots() {
        LinearLayout ll = (LinearLayout) findViewById(R.id.ll);
        dots = new ImageView[pics.length];

        // 循环取得小点图片
        for (int i = 0; i < pics.length; i++) {
            // 得到一个LinearLayout下面的每一个子元素
            dots[i] = (ImageView) ll.getChildAt(i);
            dots[i].setEnabled(false);// 都设为灰色
            dots[i].setOnClickListener(this);
            dots[i].setTag(i);// 设置位置tag，方便取出与当前位置对应
        }

        currentIndex = 0;
        dots[currentIndex].setEnabled(true); // 设置为白色，即选中状态
    }

    /**
     * 设置当前view
     *
     * @param position
     */
    private void setCurView(int position) {
        if (position < 0 || position >= pics.length) {
            return;
        }
        vp.setCurrentItem(position);
    }

    /**
     * 设置当前指示点
     *
     * @param position
     */
    private void setCurDot(int position) {
        if (position < 0 || position > pics.length || currentIndex == position) {
            return;
        }
        dots[position].setEnabled(true);
        dots[currentIndex].setEnabled(false);
        currentIndex = position;
    }


    private void enterMainActivity() {
        Intent intent = new Intent(welcome_page.this,
                Login_page.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        SharedPreferencesForWelcomeUtil.putBoolean(welcome_page.this, SharedPreferencesForWelcomeUtil.FIRST_OPEN, false);
        finish();
    }

    @Override
    public void onClick(View view) {
        if (view.getTag().equals("enter")) {
            enterMainActivity();
            return;
        }

        int position = (Integer) view.getTag();
        setCurView(position);
        setCurDot(position);
    }

    private class PageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int position) {

        }

        @Override
        public void onPageScrolled(int position, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int position) {
            // 设置底部小点选中状态
            setCurDot(position);
        }

    }


}
