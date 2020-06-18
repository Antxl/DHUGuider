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

import butterknife.BindView;
import butterknife.ButterKnife;
import dhu.cst.zhangxuhong171310126.dhuguider.R;
import dhu.cst.zhangxuhong171310126.dhuguider.models.Location;
import dhu.cst.zhangxuhong171310126.dhuguider.utils.LocationResources;

public class LiulanActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    @BindView(R.id.title)
    TextView titleText;
    @BindView(R.id.introducetext)
    TextView introduce;
    @BindView(R.id.img)
    ImageView img;
    private TextToSpeech tts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liulan);
        ((TitleBar)findViewById(R.id.titlebar)).setLeftClickListener(v -> {
            finish();
        });
        ButterKnife.bind(this);
        tts = new TextToSpeech(this, this);
        tts.setPitch(1f);
        tts.setSpeechRate(1.3f);
        bindResource(getIntent().getStringExtra("location"));
    }

    private void bindResource(String name) {
        Location here= LocationResources.get(name);
        titleText.setText(here.getName());
        introduce.setText(here.getDescriptionResource());
        img.setImageResource(here.getDrawableResource());
    }

    public void speak(View v) {
        if (introduce.getText().length() >= 1)
            tts.speak(introduce.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
        else
            Toast.makeText(this,"内容为空", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInit(int status) {
        if (status == tts.SUCCESS) {
            int result1 = tts.setLanguage(Locale.CHINA);
            if (result1 == TextToSpeech.LANG_MISSING_DATA||result1==TextToSpeech.LANG_NOT_SUPPORTED)
                Toast.makeText(this,"数据丢失或不支持:"+result1+";",Toast.LENGTH_SHORT).show();
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