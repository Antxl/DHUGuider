package dhu.cst.zhangxuhong171310126.dhuguider.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.luck.picture.lib.PictureSelectionModel;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.button.shadowbutton.ShadowButton;
import com.xuexiang.xui.widget.dialog.MiniLoadingDialog;
import com.xuexiang.xui.widget.guidview.GuideCaseView;
import com.xuexiang.xui.widget.searchview.MaterialSearchView;

import java.io.File;
import java.util.List;
import java.util.Locale;

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

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{
    @BindView(R.id.drawer)
    DrawerLayout menu;
    @BindView(R.id.cam_btn)
    ShadowButton camBtn;
    @BindView(R.id.que_btn)
    ShadowButton qaBtn;
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
    @BindView(R.id.map_area)
    FrameLayout root;
    private Recorder recorder;
    private BDSpeechToText stt;
    private String lastRecordName;
    private Handler handler;
    private PictureSelectionModel model;
    private BDPicSearchUtil util;
    private MiniLoadingDialog dialog;
    private ImageView pointer;
    private TextToSpeech tts;
    private boolean shouldAnswer;
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
        pointer=null;
        tts=new TextToSpeech(this,this);
        shouldAnswer=false;
    }

    private void initComponents()
    {
        title.setLeftImageResource(R.drawable.ic_baseline_menu_24)
                .setLeftClickListener(v -> {
                    menu.openDrawer(Gravity.LEFT);
                })
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
        if (shouldAnswer){
            if (dest!=null){
                if (keyword.contains("哪")||keyword.contains("介绍")||keyword.contains("讲")){
                    if (keyword.contains("哪"))
                        tts.speak(dest.getName()+"位于"+dest.getDirection(), TextToSpeech.QUEUE_FLUSH, null,"answerWhere");
                    if (keyword.contains("介绍")||keyword.contains("讲"))
                        tts.speak(getResources().getString(dest.getDescriptionResource()),TextToSpeech.QUEUE_FLUSH, null,"introduce");
                }
                else
                    tts.speak("我不太清楚您想问的是什么，能不能换一个简单一点的问法？",TextToSpeech.QUEUE_FLUSH, null,"introduce");
            }
            else
                tts.speak("没有找到您想知道的地方，或许它还没有收录。",TextToSpeech.QUEUE_FLUSH, null,"introduce");
            shouldAnswer=false;
        }
        else{
            if (dest!=null){
                final PointF target=dest.getCenterPF();
                map.animateCenter(dest.getCenterPF())
                        .withDuration(500)
                        .withOnAnimationEventListener(new SubsamplingScaleImageView.OnAnimationEventListener() {
                            @Override
                            public void onComplete() {
                                if (pointer!=null)
                                    pointer.setVisibility(View.GONE);
                                pointer=new ImageView(root.getContext());
                                pointer.setImageResource(R.drawable.ic_baseline_location_on_24);
                                PointF dest=map.sourceToViewCoord(target);
                                root.addView(pointer,new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
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
                        }).start();
            }
            else
                Toast.makeText(this,"没有结果哦，可能这个地点还没收录呢",Toast.LENGTH_SHORT).show();
        }
    }

    public void click_btn1(View v){
        int[] location=new int[2];
        camBtn.getLocationOnScreen(location);
        new GuideCaseView.Builder(this)
                .focusRectAtPosition(location[0], (int)title.getY()+50, 230, 75)
                .roundRectRadius(60)
                .focusBorderColor(Color.GREEN)
                .build()
                .show();
    }

    public void click_btn2(View v) {
        int[] location=new int[2];
        camBtn.getLocationOnScreen(location);
        new GuideCaseView.Builder(this)
                .focusRectAtPosition(location[0], location[1], 230, 75)
                .roundRectRadius(60)
                .focusBorderColor(Color.GREEN)
                .build()
                .show();
    }

    public void click_btn3(View v) {
        int[] location=new int[2];
        qaBtn.getLocationOnScreen(location);
        new GuideCaseView.Builder(this)
                .focusRectAtPosition(location[0], location[1], 230, 75)
                .roundRectRadius(60)
                .focusBorderColor(Color.GREEN)
                .build()
                .show();
    }

    @OnTouch(R2.id.bgimg)
    boolean onMapTouched(View v,MotionEvent e)
    {
        if (pointer!=null&&e.getAction()==MotionEvent.ACTION_DOWN) {
            pointer.setVisibility(View.GONE);
            pointer=null;
        }
        if (e.getAction()==MotionEvent.ACTION_UP){
            SubsamplingScaleImageView img=(SubsamplingScaleImageView)v;
            PointF p=img.viewToSourceCoord(e.getX(),e.getY());
            Location l=LocationResources.find(p);
            if (l!=null){
                Intent intent=new Intent(this,LiulanActivity.class);
                intent.putExtra("location",l.getName());
                startActivity(intent);
                return true;
            }
        }
        return false;
    }

    @OnClick(R2.id.cam_btn)
    void onCameraClicked(View v)
    {
        model.forResult(2);//打开照片选择器
    }

    @OnTouch({R2.id.voice_btn,R2.id.que_btn})
    void onVoiceBtnStatusChanged(View v, MotionEvent e)
    {
        if (v.getId()==R.id.que_btn)
            shouldAnswer=true;
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

    public void onInit(int status) {
        if (status == tts.SUCCESS) {
            int result1 = tts.setLanguage(Locale.CHINA);
            if (result1 == TextToSpeech.LANG_MISSING_DATA||result1==TextToSpeech.LANG_NOT_SUPPORTED)
                Toast.makeText(this,"数据丢失或不支持:"+result1+";",Toast.LENGTH_SHORT).show();
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

    protected void onDestroy() {
        super.onDestroy();
        if (tts != null) {
            //停止
            tts.stop();
            //关闭
            tts.shutdown();
        }
    }
}