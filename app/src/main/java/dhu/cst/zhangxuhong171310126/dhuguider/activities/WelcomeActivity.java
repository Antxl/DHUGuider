package dhu.cst.zhangxuhong171310126.dhuguider.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import dhu.cst.zhangxuhong171310126.dhuguider.R;

import static java.lang.Thread.sleep;

public class WelcomeActivity extends AppCompatActivity {
    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        handler=new RedirectHandler();
        new Thread(() -> {
            try {
                sleep(1000);
                handler.sendMessage(new Message());
            } catch (InterruptedException e) {
                handler.sendMessage(new Message());
            }
        }).start();
    }

    private class RedirectHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            finish();
        }
    }
}