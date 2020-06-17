package dhu.cst.zhangxuhong171310126.dhuguider.activities;

import android.content.Intent;
import android.graphics.PointF;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.button.shadowbutton.ShadowButton;
import com.xuexiang.xui.widget.searchview.MaterialSearchView;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTouch;
import dhu.cst.zhangxuhong171310126.dhuguider.R;
import dhu.cst.zhangxuhong171310126.dhuguider.R2;
import dhu.cst.zhangxuhong171310126.dhuguider.models.Location;
import dhu.cst.zhangxuhong171310126.dhuguider.utils.BDSpeechToText;
import dhu.cst.zhangxuhong171310126.dhuguider.utils.LocationResourcesReader;
import dhu.cst.zhangxuhong171310126.dhuguider.utils.Recorder;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.cam_btn)
    ShadowButton camBtn;
    @BindView(R.id.voice_btn)
    ImageButton voiceBtn;
    @BindView(R.id.search_view)
    MaterialSearchView searchBar;
    @BindView(R.id.bgimg)
    SubsamplingScaleImageView map;
    @BindView(R.id.titlebar)
    TitleBar title;
    private Recorder recorder;
    private BDSpeechToText stt;
    private String lastRecordName;
    private Handler handler;
    private List<Location> resorts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initComponents();
        recorder=new Recorder(this);
        stt=new BDSpeechToText(getDeviceMac());
        lastRecordName=null;
        handler=new ResponseHandler();
        new LocationResourcesReader(this);
        //resorts=LocationResourcesReader.getResorts();
    }

    private void initComponents()
    {
        title.disableLeftView()
                .addAction(new TitleBar.ImageAction(R.drawable.ic_baseline_search_24) {
                    @Override
                    public void performAction(View view) {
                        searchBar.showSearch();
                    }
                });
        searchBar.setHint("在这里输入想去的地点吧");
        searchBar.setEllipsize(true);
        searchBar.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //TODO search and close search View
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchBar.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                voiceBtn.setVisibility(View.VISIBLE);
            }
            @Override
            public void onSearchViewClosed() {
                voiceBtn.setVisibility(View.GONE);
            }
        });
        searchBar.setSubmitOnClick(true);

    }

    private String getDeviceMac() {
        WifiManager wm=getSystemService(WifiManager.class);
        return wm.getConnectionInfo().getMacAddress();
    }

    @OnTouch(R2.id.bgimg)
    boolean onMapTouched(View v,MotionEvent e)
    {
        SubsamplingScaleImageView img=(SubsamplingScaleImageView)v;
        PointF p=img.viewToSourceCoord(e.getX(),e.getY());
        Toast.makeText(this,"x:"+p.x+",y:"+p.y,Toast.LENGTH_LONG).show();
        return false;
    }

    @OnTouch(R2.id.voice_btn)
    void onVoiceBtnStatusChanged(View v, MotionEvent e)
    {
        switch (e.getAction()){
            case MotionEvent.ACTION_DOWN:
                lastRecordName=recorder.start();break;
            case MotionEvent.ACTION_UP:{
                recorder.stop();
                File record=new File(lastRecordName);
                stt.transfer(record,handler);
                searchBar.closeSearch();
            }
        }
    }

    class ResponseHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.obj==null)
                return;
            StringBuilder sb=new StringBuilder();
            List<String> rs=(List<String>)msg.obj;
            for (String s:rs)
                sb.append(s);
            sb.append('\n');
            //TODO search and close search View
        }
    }

    public void liulan(View v){
        Intent intent = new Intent(MainActivity.this, LiulanActivity.class);
        startActivity(intent);
    }
}