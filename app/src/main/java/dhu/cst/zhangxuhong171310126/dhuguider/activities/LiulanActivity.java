package dhu.cst.zhangxuhong171310126.dhuguider.activities;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.xuexiang.xui.widget.actionbar.TitleBar;

import java.util.Locale;

import dhu.cst.zhangxuhong171310126.dhuguider.R;

public class LiulanActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    TextView titletext;
    TextView introduce;
    ImageView img;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liulan);
        TitleBar title = findViewById(R.id.titlebar);
        title.disableLeftView();
        titletext = findViewById(R.id.title);
        introduce = findViewById(R.id.introducetext);
        img = findViewById(R.id.img);
        tts = new TextToSpeech(LiulanActivity.this, LiulanActivity.this);
        tts.setPitch(1.0f);
        tts.setSpeechRate(1.3f);
        settext(1);
    }

    public void settext(int flag) {
        if (flag == 0) {
            titletext.setText("图书馆");
            introduce.setText("\n    东华大学图书馆馆舍面积为9266平方米，其中阅览室面积为2250平方米，馆内设有五层大型书库与九个不同类型的阅览室，阅览室座位有738席，全馆采用全开架服务形式，建立了图书馆集成管理系统，设有公共查询终端，使图书馆的书目查询以及书刊管理全部实行计算机化。\n    根据学校的办学特色，图书馆还建立了电子检索室和多媒体阅览室，实现了光盘检索系统升级，通过校园网为全校师生提供文献信息服务，使本馆的光盘检索系统进入重点高校的先进行列。2004年11月，一座现代化的图文信息中心在松江校区落成，馆舍面积为25000平方米。");
            img.setImageResource(R.drawable.library);
        } else if (flag == 1) {
            titletext.setText("北门");
            introduce.setText("\n   东华大学，位于上海市。是中华人民共和国教育部直属的全国重点大学，是国家“世界一流学科建设高校”、“211工程“建设高校。入选国家“2011计划”牵头高校、“111计划”、“双万计划”、卓越工程师教育培养计划、国家大学生创新性实验计划、中非高校20+20合作计划、国家级大学生创新创业训练计划。\n    学校创建于1951年，时名华东纺织工学院，由交通大学纺织系等华东、中南、西南高校的纺织院系合并而成，1985年更名为中国纺织大学，1999年更名为东华大学。");
            img.setImageResource(R.drawable.beimeng);
        }
    }

    public void speak(View v) {
        if (introduce.getText().length() >= 1) {
            tts.speak(introduce.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
        } else {
            Toast.makeText(LiulanActivity.this,"内容为空", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onInit(int status) {
        if (status == tts.SUCCESS) {
            int result1 = tts.setLanguage(Locale.CHINA);
            if (result1 == TextToSpeech.LANG_MISSING_DATA|| result1==TextToSpeech.LANG_NOT_SUPPORTED)
            {
                Toast.makeText(this, "数据丢失或不支持:"+result1+";", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
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