package com.example.abc.inetaddress;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {
    LinearLayout linearLayout;
    String[] datas={"1"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        linearLayout = (LinearLayout) findViewById(R.id.ll);
        for(int i=0;i<datas.length;i++){
            Button button = new Button(this);
            button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
            button.setText(datas[i]);
            button.setId(i+100);
            linearLayout.addView(button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()){
                        case 100:
                            new Thread(){
                                @Override
                                public void run() {
                                    inetAddressDemo();
                                }
                            }.start();
                            break;
                    }
                }
            });
        }
    }

    //NetworkOnMainThreadException 在主线程中进行网络相关操作异常
    //必须开启一个支线程来进行网络相关的操作
    public void inetAddressDemo(){
        try {
            InetAddress inetAddress = InetAddress.getByName("www.sina.com");
            Log.i("InetAddress",inetAddress.getHostAddress());//获取域名的ip地址
            Log.i("是否能够连接ip地址","====="+inetAddress.isReachable(1000));
        } catch (UnknownHostException e) {//无法解析域名异常
            e.printStackTrace();//打印异常堆栈信息
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getData(){
        try {
            //URL 访问网络资源的指针，URL url = new URL（传入访问的接口地址）
            URL url = new URL("http://apis.baidu.com/apistore/weatherservice/citylist");
            //通过URL对象获得本次连接的对象并强转成HttpURLConnection的对象
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();//调用连接方法，建立本次的HTTP请求的连接
            //获得请求返回的状态码得知是否连接成功
            //如果返回码为200则表示该次请求连接成功，可以进去读取输入流的操作
            //2xx 服务器已受理请求的返回码
            //3xx 连接重定向
            //4xx 页面不存在/服务器拒绝等问题
            //5xx 服务器的问题
            if(httpURLConnection.getResponseCode() == 200){
                //通过连接对象获得输入流的对象
                InputStream inputStream = httpURLConnection.getInputStream();
//                byte[] bytes = new byte[1024];
//                inputStream.read(bytes);
//                String str = new String(bytes);
//                Log.i("str",str);
                //字节流通向字符流的桥梁，设置对应的字符编码为utf-8
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream,"utf-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder stringBuilder = new StringBuilder();//一个可变的字符序列，在读取流的时候，可以用缓存字符串
                String str;
                while ((str = bufferedReader.readLine()) != null){//每次读一行直到读取到的数据为null则不继续读取
                    stringBuilder.append(str);//读取出来的每行字符添加到字符序列
                }
                Log.i("InputStream",stringBuilder.toString());
//                String data = stringBuilder.toString();//将字符序列转成字符串
//                String subStr = data.substring(1,data.length());
//                Log.i("subStr====>",""+subStr);//"errNum":300202,"errMsg":"Missing apikey"
//                String[] strs = subStr.split(",");//"errNum":300202    "errMsg":"Missing apikey"
//                String strs1 = strs[0];//"errNum":300202
//                String[] subStrs1 = strs1.split(":");
//                String subStrs11 = subStrs1[0];
//                String s = subStrs11.replace('"',' ').trim();
//                Log.i("key1",""+s);
//                String subStrs12 = subStrs1[1];
//                Log.i("value1",""+subStrs12);
//
//                String strs2 = strs[1];//"errMsg":"Missing apikey"
//                String[] subStrs2 = strs2.split(":");
//                String subStrs21 = subStrs2[0];
//                String s1 = subStrs21.replace('"',' ').trim();
//                Log.i("key1",""+s1);
//                String subStrs22 = subStrs2[1];
//                String s2 = subStrs22.replace('"',' ').trim();
//                Log.i("value1",""+s2);

                String data = "";
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    JSONArray jsonArray = jsonObject.optJSONArray("list");
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        long errNum = jsonObject1.getLong("errNum");//通过key找到value
                        long errNum1 = jsonObject1.optLong("errNum",-1);//通过key找到value，如果没有找到返回默认值
                        String errMsg = jsonObject1.getString("errMsg");
                        Log.i("errNum",""+errNum);
                        Log.i("errMsg",errMsg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //单独解析
//                try {
//                    JSONObject jsonObject = new JSONObject(data);
//                    long errNum = jsonObject.getLong("errNum");//通过key找到value
//                    long errNum1 = jsonObject.optLong("errNum",-1);//通过key找到value，如果没有找到返回默认值
//                    String errMsg = jsonObject.getString("errMsg");
//                    Log.i("errNum",""+errNum);
//                    Log.i("errMsg",errMsg);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

            }else {//如果连接失败，则打印状态码
                Log.i("getResponseCode()",""+httpURLConnection.getResponseCode());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void getDemo(){
        HttpURLConnection httpURLConnection = null;
        String httpURL = "";
        try {
            URL url = new URL(httpURL+"?"+"cityname="+ URLEncoder.encode("重庆","utf-8"));
            //URLDecoder.decode("","utf-8");//将数据解码成普通字符串的格式
            httpURLConnection = (HttpURLConnection) url.openConnection();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
