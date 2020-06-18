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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.luck.picture.lib.PictureSelectionModel;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.button.shadowbutton.ShadowButton;
import com.xuexiang.xui.widget.dialog.MiniLoadingDialog;
import com.xuexiang.xui.widget.searchview.MaterialSearchView;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import dhu.cst.zhangxuhong171310126.dhuguider.R;
import dhu.cst.zhangxuhong171310126.dhuguider.R2;
import dhu.cst.zhangxuhong171310126.dhuguider.models.Location;
import dhu.cst.zhangxuhong171310126.dhuguider.utils.BDPicSearchUtil;
import dhu.cst.zhangxuhong171310126.dhuguider.utils.BDSpeechToText;
import dhu.cst.zhangxuhong171310126.dhuguider.utils.LocationResources;
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
    @BindView(R.id.searchbanner)
    LinearLayout banner;
    private Recorder recorder;
    private BDSpeechToText stt;
    private String lastRecordName;
    private Handler handler;
    private PictureSelectionModel model;
    private BDPicSearchUtil util;
    private MiniLoadingDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startActivity(new Intent(this,WelcomeActivity.class));
        LocationResources.init(this);
        ButterKnife.bind(this);
        initComponents();
        recorder=new Recorder(this);
        stt=new BDSpeechToText(getDeviceMac());
        lastRecordName=null;
        handler=new ResponseHandler();
        util=new BDPicSearchUtil();
    }

    private void initComponents()
    {
        title.disableLeftView()
                .addAction(new TitleBar.ImageAction(R.drawable.ic_baseline_search_24) {
                    @Override
                    public void performAction(View view) {
                        if (searchBar.isSearchOpen())
                            searchBar.closeSearch();
                        else
                            searchBar.showSearch();
                    }
                });
        searchBar.setHint("在这里输入想去的地点吧");
        searchBar.setEllipsize(true);
        searchBar.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                doSearch(query);
                searchBar.closeSearch();
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
                banner.bringToFront();
            }
            @Override
            public void onSearchViewClosed() {
                voiceBtn.setVisibility(View.GONE);
            }
        });
        searchBar.setSubmitOnClick(true);
        model=PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())/*允许使用相册的图片*/
                .selectionMode(PictureConfig.SINGLE)/*只允许选择一张*/
                .isCamera(true)/*允许拍照*/
                .compress(true)/*压缩图片以增加网络传输效率*/
                .synOrAsy(false);//异步处理以减少等待时间
        dialog=new MiniLoadingDialog(this,"稍等哦，正在处理中");
    }

    private String getDeviceMac() {
        WifiManager wm=getSystemService(WifiManager.class);
        return wm.getConnectionInfo().getMacAddress();
    }

    private void doSearch(String keyword)
    {
        String[] resorts=LocationResources.resorts();
        Location dest=null;
        for (String s:resorts){
            if (keyword.contains(s)){
                dest=LocationResources.get(s);
                break;
            }
        }
        if (dialog.isLoading())
            dialog.dismiss();
        if (dest!=null){
            map.animateCenter(dest.getCenterPF())
                    .withDuration(500)
                    .withOnAnimationEventListener(new SubsamplingScaleImageView.OnAnimationEventListener() {
                        @Override
                        public void onComplete() {
                            ImageView pointer=new ImageView(MainActivity.this);
                            pointer.setImageResource(R.drawable.ic_baseline_location_on_24);
                            PointF dest=map.sourceToViewCoord(map.getCenter());
                            pointer.setX(dest.x);
                            pointer.setY(0);
                            pointer.animate()
                                    .translationYBy(dest.y)
                                    .setDuration(500)
                                    .start();
                        }

                        @Override
                        public void onInterruptedByUser() {
                        }

                        @Override
                        public void onInterruptedByNewAnim() {
                        }
                    });
        }
        else
            Toast.makeText(this,"没有结果哦，可能这个地点还没收录呢",Toast.LENGTH_SHORT).show();
    }

    @OnTouch(R2.id.bgimg)
    boolean onMapTouched(View v,MotionEvent e)
    {
        SubsamplingScaleImageView img=(SubsamplingScaleImageView)v;
        PointF p=img.viewToSourceCoord(e.getX(),e.getY());
        Location l=LocationResources.find(p);
        if (l!=null){
            Intent intent=new Intent(this,LiulanActivity.class);
            intent.putExtra("location",l.getName());
            startActivity(intent);
            return true;
        }
        return false;
    }

    @OnClick(R2.id.cam_btn)
    void onCameraClicked(View v)
    {
        model.forResult(2);//打开照片选择器
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
                dialog.show();
                stt.transfer(record,handler);
                searchBar.closeSearch();
            }
        }
    }

    class ResponseHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.arg1){
                case 0:{//语音识别发送的消息
                    if (msg.obj==null) {
                        if (dialog.isLoading())
                            dialog.dismiss();
                        return;
                    }
                    StringBuilder sb=new StringBuilder();
                    List<String>rs=(List<String>)msg.obj;
                    for (String s:rs)
                        sb.append(s);
                    doSearch(sb.toString());
                    break;
                }
                case 2:{//图像识别发送的消息
                    if (msg.arg2<0) {
                        Toast.makeText(MainActivity.this,"没有结果哦，可能这个地点还没收录呢",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    doSearch((String)msg.obj);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<LocalMedia> l=PictureSelector.obtainMultipleResult(data);
        if (l.size()<1)
            return;
        dialog.show();
        util.parsePicLocation(l.get(0),handler);
    }
}