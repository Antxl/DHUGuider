package dhu.cst.zhangxuhong171310126.dhuguider.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.xuexiang.xui.widget.guidview.GuideCaseView;

import dhu.cst.zhangxuhong171310126.dhuguider.R;

public class WelcomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void click_use(View v){
        finish();
    }

    public void click_btn1(View v){
        new GuideCaseView.Builder(this)
                .focusRectAtPosition(320, 650, 230, 75)
                .roundRectRadius(60)
                .focusBorderColor(Color.GREEN)
                .build()
                .show();
    }

    public void click_btn2(View v) {
        new GuideCaseView.Builder(this)
                .focusRectAtPosition(535, 650, 230, 75)
                .roundRectRadius(60)
                .focusBorderColor(Color.GREEN)
                .build()
                .show();
    }

    public void click_btn3(View v) {
        new GuideCaseView.Builder(this)
                .focusRectAtPosition(320, 745, 230, 75)
                .roundRectRadius(60)
                .focusBorderColor(Color.GREEN)
                .build()
                .show();
    }
}