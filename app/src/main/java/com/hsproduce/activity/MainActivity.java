package com.hsproduce.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.hsproduce.R;
import com.hsproduce.fragment.MineFragment;
import com.hsproduce.fragment.VulcanizationFragment;

public class MainActivity extends FragmentActivity {

    private Fragment vulFrag;
    private Fragment mineFrag;
    private Fragment currentFrag;

    private BottomNavigationView navigation;

    private FragmentTransaction ftr;//事务

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//            switch (item.getItemId()) {
//                case R.id.navigation_dashboard:
//                    setSelected(0);
//                    return true;
////                case R.id.navigation_home:
////                    setSelected(1);
////                    return true;
//            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        initEvent();

        setSelected(0);
    }

    //初始化页面控件
    public void initView(){
        //获得底部按钮
//        navigation = (BottomNavigationView) findViewById(R.id.navigation);
    }
    //初始化控件事件
    public void initEvent(){
        //设置底部按钮事件
//        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
    //自定义一个方法，设定布局中间的FrameLayout的选择状态
    private void setSelected(int i) {

        //获取事务
        FragmentManager fm = getSupportFragmentManager();
        ftr  = fm.beginTransaction();//开启一个事务
        //隐藏当前fragment
        if(currentFrag != null){
            ftr.hide(currentFrag);
        }
        switch(i){
            case 0:
                if(vulFrag == null){
                    //实例化fragment
                    vulFrag = new VulcanizationFragment();
                    //将该fragment加入到ftr中
                    ftr.add(R.id.lay_frame, vulFrag);
                }
                //设置当前fragment
                currentFrag = vulFrag;
                ftr.show(vulFrag);
                break;
            case 1:
                if(mineFrag == null){
                    //实例化fragment
                    mineFrag = new MineFragment();
                    //将该fragment加入到ftr中
                    ftr.add(R.id.lay_frame, mineFrag);
                }
                //设置当前fragment
                currentFrag = mineFrag;
                ftr.show(mineFrag);
                break;

        }
        ftr.commit();//提交事务
    }

    public void getPlan(View v){

    }
}
