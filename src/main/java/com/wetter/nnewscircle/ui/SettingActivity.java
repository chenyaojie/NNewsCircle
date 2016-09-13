package com.wetter.nnewscircle.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.wetter.nnewscircle.R;
import com.wetter.nnewscircle.base.BaseActivity;

public class SettingActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_setting);
    }

    @Override
    protected void initView() {
        setupToolbar();
        setupFragment();
    }

    private void setupFragment() {
        getFragmentManager().beginTransaction()
                .replace(R.id.setting_container,new SettingFragment())
                .commit();
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.setting_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}
