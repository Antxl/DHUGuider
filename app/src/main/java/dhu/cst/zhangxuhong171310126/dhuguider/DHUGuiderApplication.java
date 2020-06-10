package dhu.cst.zhangxuhong171310126.dhuguider;

import android.app.Application;

import com.xuexiang.xui.XUI;

public class DHUGuiderApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        XUI.init(this);
        XUI.debug(true);
    }
}
