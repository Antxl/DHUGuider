package dhu.cst.zhangxuhong171310126.dhuguider.utils;

import android.content.Context;
import android.media.MediaRecorder;

import java.io.IOException;

public class Recorder {
    private MediaRecorder recorder;
    private String cacheDir;

    public Recorder(Context context)
    {
        recorder=null;
        cacheDir=context.getCacheDir().getPath()+'/';
    }

    private void initRecorder()
    {
        recorder=new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);//设置麦克风为来源
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);//百度语音识别需要AMR格式音频
        recorder.setMaxDuration(1000*60);
        recorder.setAudioEncodingBitRate(16000);
        recorder.setAudioChannels(1);
    }

    public String start()
    {
        initRecorder();//always make a new recorder before use
        try{
            String filename=cacheDir+System.currentTimeMillis()+".amr";
            recorder.setOutputFile(filename);
            recorder.prepare();
            recorder.start();
            return filename;
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    public void stop()
    {
        if (recorder==null)
            return;
        try {
            recorder.stop();
        }catch (RuntimeException e){
            e.printStackTrace();
        }
        recorder.reset();
        recorder.release();
        recorder=null;
    }
}
