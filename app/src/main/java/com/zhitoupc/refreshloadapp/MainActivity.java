package com.zhitoupc.refreshloadapp;

import android.animation.ObjectAnimator;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ViewAnimator;

import com.zhitoupc.refreshloadapp.databinding.ActivityMainBinding;
import com.zhitoupc.refreshloadapp.view.DragRefreshLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mainBinding;
    private MainHandler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        handler = new MainHandler(this);
        List<String> resource = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            resource.add("我是第" + i + "个");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.activity_list_item,
                android.R.id.text1, resource);
        mainBinding.loadMoreLv.setAdapter(adapter);
        mainBinding.loadMoreLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    mainBinding.layoutRefresh.setRefreshing(true);
                    handler.sendEmptyMessageDelayed(0,4000);
                }else{
                    mainBinding.layoutRefresh.setRefreshing(false);
                }
            }
        });

        mainBinding.layoutRefresh.setRefreshStatusChangeListener(new DragRefreshLayout.OnRefreshStatusChangeListener() {
            @Override
            public void onStatusChange(View view,int status) {
                if(status == REFRESHING){
                    view.setPivotY(view.getHeight()/2);
                    view.setPivotX(view.getWidth()/2);
                    RotateAnimation animation = new RotateAnimation(0,360,
                            Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
                    animation.setRepeatCount(-1);
                    animation.setInterpolator(new LinearInterpolator());
                    animation.setDuration(800);
                    view.startAnimation(animation);
                }else if(status == NORMAL){
                    Animation animation = view.getAnimation();
                    if (animation != null) {
                        animation.cancel();
                    }
                }
            }

            @Override
            public void changeAnim(View view, float pro) {
                view.setPivotX(view.getWidth()/2);
                view.setPivotY(view.getHeight()/2);
                if(pro == 0){
                    view.setRotation(0);
                }else if(pro == 1f){
                    view.setRotation(1);
                }else{
                    view.setRotation(pro * 180);
                }
            }
        });
    }

    private static class MainHandler extends Handler{
        private WeakReference<MainActivity> mainActivityWeak;

        private MainHandler(MainActivity mainActivity) {
            this.mainActivityWeak = new WeakReference<MainActivity>(mainActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mainActivityWeak.get() != null) {
                mainActivityWeak.get().mainBinding.layoutRefresh.setRefreshing(false);
            }
        }
    }
}
