package dhu.cst.zhangxuhong171310126.dhuguider.utils;

import android.os.Handler;
import android.os.Message;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BDSpeechToText {
    private static String AccessToken=null;

    private static final String grant_type="client_credentials",
            client_id="sWSFzTpW6vIDinCdqedmRHB3",
            client_secret="wg7PYDaGcpuRGhifUTfwdDp73RVwSQ6T";

    private static final String tokenUrl="https://aip.baidubce.com/oauth/2.0/token"
            ,apiUrl="https://vop.baidu.com/server_api";

    private static String WMac;
    @Getter
    @Setter
    private static class TokenResponse{
        private String refresh_token,scope,session_key,access_token,session_secret;
        private int expires_in;
    }

    @Getter
    @Setter
    private static class ResultResponse{
        private String corpus_no,err_msg;
        private List<String> result;
        private int err_no;
    }

    public BDSpeechToText(String wifiMac)
    {
        if (AccessToken==null)
            initAccessToken();
        WMac=wifiMac;
    }

    private void initAccessToken()
    {
        new Thread(new Runnable(){
            public void run() {
                RequestBody body=new FormBody.Builder()
                        .add("grant_type",grant_type)
                        .add("client_id",client_id)
                        .add("client_secret",client_secret)
                        .build();//构建请求体
                Request request=new Request.Builder()
                        .url(tokenUrl)
                        .post(body).build();//构建请求
                new OkHttpClient().newCall(request).enqueue(new Callback(){//发送异步请求
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        e.printStackTrace();
                    }

                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if (response.isSuccessful()){
                            AccessToken=new Gson()
                                    .fromJson(response.body().string(),TokenResponse.class)
                                    .access_token;
                        }
                    }
                });
            }
        }).start();
    }

    public void transfer(final File audio, final Handler responseHandler, final int megArc)
    {
        new Thread(new Runnable() {
            public void run() {
                RequestBody body=MultipartBody.create(audio,MediaType.parse("audio/amr; rate=16000"));
                Request request=new Request.Builder()
                        .url(apiUrl+"?cuid="+WMac+"&token="+AccessToken)
                        .post(body)
                        .header("Content-Type","audio/amr; rate=16000")//make sure there is a space before rate
                        .build();
                new OkHttpClient().newCall(request).enqueue(new Callback(){//发送异步请求
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        e.printStackTrace();
                    }

                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if (response.isSuccessful()){
                            ResultResponse rep=new Gson()
                                    .fromJson(response.body().string(),ResultResponse.class);
                            Message msg=new Message();
                            msg.arg1=megArc;
                            msg.obj=rep.result;
                            responseHandler.sendMessage(msg);
                        }
                    }
                });
            }
        }).start();
    }

    public void transfer(final File audio, final Handler responseHandler)
    {
        transfer(audio, responseHandler,0);
    }
}