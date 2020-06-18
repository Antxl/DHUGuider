package dhu.cst.zhangxuhong171310126.dhuguider.utils;

import android.os.Handler;
import android.os.Message;

import com.baidu.aip.imagesearch.AipImageSearch;
import com.luck.picture.lib.entity.LocalMedia;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BDPicSearchUtil {
    private static final String APP_ID="20467246",
            API_KEY="jt4ASzAGeXBHLdLLj3b4IY6I",
            SECRET_KEY="DE13VbPoM610192u1DeTZZSQfCNQDW6o";

    private static AipImageSearch client=null;

    public BDPicSearchUtil() {
        if (client == null)
            client = new AipImageSearch(APP_ID, API_KEY, SECRET_KEY);
    }


    public void parsePicLocation(LocalMedia img, Handler handler, int arg)
    {
        new Thread(() -> {
            JSONObject res=client.similarSearch(img.getCompressPath(),null);
            try {
                JSONArray arr=res.getJSONArray("result");
                Message msg=new Message();
                msg.arg1=arg;
                if (arr.length()==0){
                    msg.arg2=-1;
                    handler.sendMessage(msg);
                    return;
                }
                JSONObject obj=arr.getJSONObject(0);
                msg.arg2=1;
                msg.obj=obj.getString("brief");
                handler.sendMessage(msg);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void parsePicLocation(LocalMedia img, Handler handler)
    {
        parsePicLocation(img, handler,2);
    }
}
