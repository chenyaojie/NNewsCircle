package com.wetter.nnewscircle.base;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.wetter.nnewscircle.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Wetter on 2016/8/24.
 */
public abstract class BaseActivity extends AppCompatActivity{

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        initView();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe
    public void onEvent(Boolean empty){

    }

    protected abstract void initView();

    /**
     * 隐藏小键盘
     */
    public void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    /*
        修改SnackBar颜色
    */
    public void setSnackbarColor(Snackbar snackbar, int messageColor, int backgroundColor) {
        View view = snackbar.getView();//获取Snackbar的view
        if(view!=null){
            view.setBackgroundColor(backgroundColor);//修改view的背景色
            ((TextView) view.findViewById(R.id.snackbar_text)).setTextColor(messageColor);//获取Snackbar的message控件，修改字体颜色
        }
    }
}
