package com.senssun.myquickrunning;

import android.app.Application;

import com.antfortune.freeline.FreelineCore;

/**
 * description
 * <p>
 * Created by sunjian on 2017/6/24.
 */

public class APP extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FreelineCore.init(this);

    }
}
