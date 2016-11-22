package com.zrodo.demo.print;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.zrodo.demo.ServerConst;
import com.zrodo.demo.utils.AppUtils;
import com.zrodo.demo.utils.ComUtils;
import com.zrodo.demo.utils.CoreUtils;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class JSUploadActivity extends Activity {

    private Button btnBack, btnJSFinalUpload, btnJSAsyUpload, btnJSDefUpload;
    private TextView tvMsg;

    public String url;
    public StringEntity se;
    public ByteArrayEntity be;
    private Handler handler = new Handler() {

        @Override
        public void dispatchMessage(Message msg) {
            // TODO Auto-generated method stub
            super.dispatchMessage(msg);
            if (msg.what == 0) {
                tvMsg.setText((String) msg.obj);
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.js_upload);
        AppUtils.setSystemBarVisible(this, false);
        setRequestedOrientation(CoreUtils.ScreenForward);

        uploadOrders();

        tvMsg = (TextView) this.findViewById(R.id.tvJSMsg);
        btnJSFinalUpload = (Button) this.findViewById(R.id.btnJSUpload);
        btnJSAsyUpload = (Button) this.findViewById(R.id.btnJSAsyncUpload);
        btnJSDefUpload = (Button) this.findViewById(R.id.btnJSDefaultUpload);

        btnBack = (Button) this.findViewById(R.id.btnBack);

        btnJSFinalUpload.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//				//用finalhttp传输json数据
                finalPostJson();
            }
        });
        btnJSAsyUpload.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //用asyncHttp传输json数据
                asyncHttpPostJson();
            }
        });
        btnJSDefUpload.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //用http
                new Thread(httpRun).start();
            }
        });

        btnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    /**
     * 批量上传检测单
     *
     */
    public void uploadOrders() {
        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("_method", "post");//上传方式
            jsonObj.put("add_firm", "T004");//接入点
            jsonObj.put("add_from", 2);//检测单来源
            jsonObj.put("add_sign", "智锐达生化速测仪");//标志

            jsonObj.put("add_user", "hahyzj");
            jsonObj.put("region_code", "32080401");
            jsonObj.put("date", "2016-06-03");//datenow->strday

            JSONObject jsonEnterprise = new JSONObject();
            jsonEnterprise.put("name", "测试被检单位");
            jsonEnterprise.put("address", "测试地址");
            jsonEnterprise.put("charge", "测试负责人");
            jsonEnterprise.put("contact", "0513-85927888");
            jsonObj.put("enterprise", jsonEnterprise);

            JSONObject jsonDec = new JSONObject();
            jsonDec.put("name", "测试抽检单位");
            jsonDec.put("contact", "0513-85927999");
            jsonObj.put("detector", jsonDec);

            JSONArray workArr = new JSONArray();
            workArr.put("采样人1");
            jsonObj.put("worker", workArr);

            JSONArray recordArr = new JSONArray();
            JSONObject jsonResult = new JSONObject();
            jsonResult.put("no", "ntkfq20160603-001"); // 样品编号
            jsonResult.put("produce", 1); // 产品分类
            jsonResult.put("name", "菠菜"); // 产品名称
            jsonResult.put("basis", "GB/T 5009.199-2003"); // 依据
            jsonResult.put("result", 2); // 检测结果
            jsonResult.put("method", "酶抑制率法"); // 检测方法
            jsonResult.put("reason", "双氧水"); // 检测项目：不合格项目，result=2，就必须有此项目
            recordArr.put(jsonResult);
            jsonObj.put("record", recordArr);

            ComUtils.print("js_upload_result:" + jsonObj.toString());

            //转为unicode编码
            //todo...

            //访问上传接口
            //check url
            url = ServerConst.formatJSUrl(ServerConst.JS_UPLOAD_API,
                    new String[]{jsonObj.getString("add_user"), ServerConst.JS_PARAM_FROM});//顺序要对
            if (url == null) {
                AppUtils.showToast(this, "format url error");
                return;
            }
            ComUtils.print(" js upload order: url = " + url);

            //set entity
//			se = new StringEntity(jsonObj.toString());

            se = new StringEntity(jsonObj.toString(), "utf-8");
            se.setContentEncoding("UTF-8");
            se.setContentType("application/json");
//			se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

            //test
//			String json = "{\"add_from\":2}";  
//			se = new StringEntity(json,"utf-8");
//			se.setContentEncoding("UTF-8");    
//			se.setContentType("application/json");   

            be = new ByteArrayEntity(jsonObj.toString().getBytes());


            ComUtils.print(" js upload order: entity = " + se.toString());


        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

    }

    /**
     * 用finalhttp传输json
     */
    private void finalPostJson() {
        //post http
        FinalHttp fh = new FinalHttp();
        fh.configRequestExecutionRetryCount(1);
        fh.configTimeout(ServerConst.TIMEOUT_SHORT);
        fh.addHeader(ServerConst.JS_BASICAUTH, ServerConst.JS_BASICAUTH_KEY);//Authorization
        fh.addHeader(ServerConst.JS_CONTENT_TYPE, ServerConst.JS_CONTENT_TYPEVALUE);//content_type

//		fh.addHeader("Accept", "application/json");
//		fh.addHeader("Accept", "text/plain");
//		fh.addHeader("Accept-Charset", "UTF-8");//配置http请求头
//		fh.configCharset("UTF-8");
//		fh.configCookieStore(null);
//		fh.configSSLSocketFactory(null);
//		fh.configUserAgent("Mozilla/5.0");//配置客户端信息		

        fh.post(url, se, ServerConst.JS_CONTENT_TYPEVALUE, new DataUploadCallBack());
    }

    /**
     * 用http传输json
     */
    private void asyncHttpPostJson() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader(ServerConst.JS_BASICAUTH, ServerConst.JS_BASICAUTH_KEY);//Authorization
        client.addHeader(ServerConst.JS_CONTENT_TYPE, ServerConst.JS_CONTENT_TYPEVALUE);//content_type:不写Onfail,写了但是穿不过去，region_xxx
        client.setConnectTimeout(ServerConst.TIMEOUT_SHORT);
//		client.setTimeout(ServerConst.TIMEOUT_SHOR);
//		client.setBasicAuth(ServerConst.JS_BASICAUTH_NAME, ServerConst.JS_BASICAUTH_PSW);

        client.addHeader("Accept", "application/json");
//		client.addHeader("Accept", "text/plain");
//		client.addHeader("Host", "jgj.jsagri.gov.cn:8081");
//		client.addHeader("Content-Length", String.valueOf(contentLenght));
//		client.addHeader("Accept-Charset", HTTP.UTF_8);
//		client.addHeader("User-Agent", Build.DEVICE);

        try {
            //传的内容就在new StringEntity(json.toString())-->entity
            //"text/plain"  "application/json"
            client.post(this, url, se, ServerConst.JS_CONTENT_TYPEVALUE, mResponstHandler);
//			client.post(url, mResponstHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 被弃用了
     * 用http传输json
     * setHeader(name, value)：如果Header中没有定义则添加，如果已定义则用新的value覆盖原用value值。
     * addHeader(name, value)：如果Header中没有定义则添加，如果已定义则保持原有value不改变。
     * 401:Unauthorized
     * 400:Bad Request
     */
    private void httpPostJson() {
        DefaultHttpClient httpClient = new DefaultHttpClient();
//		String proxyHost = "jgj.jsagri.gov.cn";
//		 int proxyPort = 8081;
//		 httpClient.getCredentialsProvider().setCredentials(
//		   new AuthScope(proxyHost, proxyPort),
//		   new UsernamePasswordCredentials(ServerConst.JS_BASICAUTH_NAME, ServerConst.JS_BASICAUTH_PSW));
//		 HttpHost proxy = new HttpHost(proxyHost,proxyPort);
//		 httpClient.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY, proxy);


        HttpPost method = new HttpPost(url);
        //add header
//		method.addHeader("Content-type","application/json; charset=utf-8");  
//        method.addHeader("Accept", "application/json");  
//        method.addHeader("Authorization", ServerConst.JS_BASICAUTH_KEY); 
        //set header
        method.setHeader("Content-type", "application/json");
//        method.setHeader("Accept", "application/json");  
//		method.addHeader("Accept", "text/plain");
        method.setHeader("Authorization", "Basic emhpcnVpZGE6QjAyRDUxN0I0N0Y3NzNBRTUzNEIzMTAyNDgyN0MxNDE=");
//        
//		try {
//			se = new StringEntity(body,"utf-8");
////			se.setContentEncoding("UTF-8");    
////			se.setContentType("application/json"); 
//		} catch (UnsupportedEncodingException e2) {
//			// TODO Auto-generated catch block
//			e2.printStackTrace();
//		}


        method.setEntity(se);

        try {
            HttpResponse result = httpClient.execute(method);
            int code = result.getStatusLine().getStatusCode();
            ComUtils.print("js code = " + code);
            if (code != HttpStatus.SC_OK) {//>299
                sendMsg("Http error code=" + code);
                return;
            }

            String resData = EntityUtils.toString(result.getEntity());
            ComUtils.print("js response result = " + resData);

            JSONObject jsonObj = new JSONObject(resData);
//			 JSONObject jsonObj = JSONObject.parseObject(resData); 
            boolean respCode = jsonObj.getBoolean("status");
            if (respCode) {
                sendMsg("上传成功。");
            } else {
                String error = "";
                if (jsonObj.get("error") instanceof JSONArray) {
                    error = jsonObj.getJSONArray("error").getString(0);
                } else {
                    error = jsonObj.getString("error");
                }

                sendMsg("上传失败：" + error);
            }

        } catch (JSONException e) {
            sendMsg("上传失败，解析失败。");
            e.printStackTrace();
        } catch (ClientProtocolException e1) {
            sendMsg("上传失败，ClientProtocolException。");
            e1.printStackTrace();
        } catch (IOException e1) {
            tvMsg.setText("上传失败，IOException。");
            e1.printStackTrace();
        }

    }

    String body = "{\"_method\":\"post\",\"add_firm\":\"T004\",\"add_from\":2,\"add_sign\":\"a335daa2\",\"add_user\":\"hahyzj\",\"region_code\":\"32080401\",\"enterprise\":{\"name\":\"吴家楠\",\"address\":\"南通智锐达\",\"charge\":\"李总\",\"contact\":\"13555555555\"},\"detector\":{\"name\":\"赵集检测站\",\"contact\":\"15555555555\"},\"source\":1,\"task\":\"抽检\",\"date\":\"2015-11-25\",\"place\":\"强哥家\",\"standard\":\"哎哟喂\",\"category\":1,\"worker\":[\"强哥\"],\"record\":[{\"no\":\"1001\",\"place\":\"强哥家\",\"produce\":1,\"name\":\"菠菜\",\"detect\":1,\"basis\":\"毫无根据\",\"result\":1,\"method\":\"仪器操作的\",\"reason\":\"\"}]}";
//	/**
//	 * 用http传输json
//	 * */
//	private void closehttpPostJson(){
//	    CloseableHttpClient httpClient = null;
//	    HttpPost httpPost = null;
//	    try {
//	        httpClient = HttpClients.createDefault();
//	        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(20000).setConnectTimeout(20000).build();
//	        httpPost = new HttpPost(url);
//	        httpPost.setHeader("Authorization", "Basic emhpcnVpZGE6QjAyRDUxN0I0N0Y3NzNBRTUzNEIzMTAyNDgyN0MxNDE=");
//	        httpPost.setConfig(requestConfig);
//	        StringEntity entity = new StringEntity(body,HTTP.UTF_8);
//	        entity.setContentType("application/json");
//	        httpPost.setEntity(entity);
////	        httpPost.setEntity(se);
//	        
//	        CloseableHttpResponse response = httpClient.execute(httpPost);
//	        HttpEntity httpEntity = response.getEntity();
//	        System.out.println(EntityUtils.toString(httpEntity,"utf-8"));
//	    } catch (ClientProtocolException e) {
//	        e.printStackTrace();
//	    } catch (IOException e) {
//	        e.printStackTrace();
//	    }finally{
//	        try {
//	            if(httpPost!=null){
////	                httpPost.releaseConnection();
//	            }
//	            if(httpClient!=null){
//	                httpClient.close();
//	            }
//	        } catch (IOException e) {
//	            e.printStackTrace();
//	        }
//	    }
//		
//	}

    private void sendMsg(String str) {
        Message msg = new Message();
        msg.obj = str;
        msg.what = 0;
        handler.sendMessage(msg);
    }

    private Runnable httpRun = new Runnable() {
        @Override
        public void run() {
            httpPostJson();
//			closehttpPostJson();
        }

    };

    /**
     * 数据上传的callback
     */
    class DataUploadCallBack extends AjaxCallBack<Object> {

//		public DataUploadCallBack(ItemResultBean dor, List<ItemResultBean> orderResults, int allCount) {
//		}

        @Override
        public void onStart() {
            super.onStart();
        }

        @Override
        public void onFailure(Throwable t, int errorNo, String strMsg) {
            super.onFailure(t, errorNo, strMsg);
            tvMsg.setText("onFailure():errorNo=" + errorNo + ",strMsg=" + strMsg);
        }

        @Override
        public void onLoading(long count, long current) {
            super.onLoading(count, current);
        }

        @Override
        public void onSuccess(Object t) {
            super.onSuccess(t);
            try {
                ComUtils.print("js login result = " + t.toString());

                JSONObject jsonObj = new JSONObject(t.toString());
                boolean respCode = jsonObj.getBoolean("status");
                if (respCode) {
                    tvMsg.setText("上传成功。");
                } else {
                    String error = "";
                    if (jsonObj.get("error") instanceof JSONArray) {
                        error = jsonObj.getJSONArray("error").getString(0);
                    } else {
                        error = jsonObj.getString("error");
                    }

                    tvMsg.setText("上传失败：" + error);
                }

            } catch (JSONException e) {
                tvMsg.setText("上传失败，解析失败。");
                e.printStackTrace();
            }
        }
    }


    @SuppressWarnings("rawtypes")
    private BaseJsonHttpResponseHandler mResponstHandler = new BaseJsonHttpResponseHandler() {

        @Override
        public void onStart() {
            // TODO Auto-generated method stub
            super.onStart();
        }

        @Override
        public void onFailure(int statusCode, Header[] header, Throwable error,
                              String str, Object obj) {
            tvMsg.setText("onFailure():error=" + error.getMessage() + ",str=" + str + ",statusCode=" + statusCode);
            return;
        }

        @Override
        public void onSuccess(int statusCode, Header[] header, String t, Object obj) {
            try {
                ComUtils.print("js login result = " + t.toString());

                JSONObject jsonObj = new JSONObject(t.toString());
                boolean respCode = jsonObj.getBoolean("status");
                if (respCode) {
                    tvMsg.setText("上传成功。");
                } else {
                    String error = "";
                    if (jsonObj.get("error") instanceof JSONArray) {
                        error = jsonObj.getJSONArray("error").getString(0);
                    } else {
                        error = jsonObj.getString("error");
                    }

                    tvMsg.setText("上传失败：" + error);
                }

            } catch (JSONException e) {
                tvMsg.setText("上传失败，解析失败。");
                e.printStackTrace();
            }
            return;

        }

        @Override
        protected Object parseResponse(String arg0, boolean arg1)
                throws Throwable {
            return null;
        }

    };

}
