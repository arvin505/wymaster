package com.miqtech.master.client.http;

import android.app.Application;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;
import com.miqtech.master.client.application.WangYuApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RequestUtil {
    private static RequestQueue mRequestQueue;

    public static void init(Application application) {
        instance = new RequestUtil();
        mRequestQueue = Volley.newRequestQueue(application);
    }

    public void cancleAllCallback(Object tag) {
        mRequestQueue.cancelAll(tag);
    }

    private RequestUtil() {
    }

    private static RequestUtil instance;

    public static RequestUtil getInstance() {
        return instance;
    }

    private Map<String, NormalPostRequest> requestMap = new HashMap<>();

    /**
     * @param url      请求地址
     * @param params   请求参数
     * @param listener 请求回调
     */
    public void excutePostRequest(String url,
                                  Map<String, String> params, final ResponseListener listener, final String method) {

        final NormalPostRequest request = new NormalPostRequest(url,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.has("code") && response.getInt("code") == 0) {   //接口请求成功
                                listener.onSuccess(response, method);
                            } else {
                                listener.onFaild(response, method);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new SimpleErrorListener(listener, method), params);
        request.setRetryPolicy(new DefaultRetryPolicy(7 * 1000, 0, 1.0f));   //设置超时，重发
        request.setTag(listener.getClass().getName());
        mRequestQueue.add(request);
        requestMap.put(listener.getClass().getName(), request);
    }

    public void removeTag(String clazz) {
        if (requestMap.containsKey(clazz)) {
            NormalPostRequest request = requestMap.get(clazz);
            if (request != null) {
                request.setTag(null);
                request.mListener = null;
                request.mParams = null;
                request.mErrorListener = null;
                requestMap.remove(clazz);
            }
        }
    }

    private class NormalPostRequest extends Request<JSONObject> {

        private Response.Listener<JSONObject> mListener;
        private Response.ErrorListener mErrorListener;
        private Map<String, String> mParams;

        @Override
        protected Response<JSONObject> parseNetworkResponse(
                NetworkResponse response) {
            try {
                String jsonString = new String(response.data,
                        HttpHeaderParser.parseCharset(response.headers));
                return Response.success(new JSONObject(jsonString),
                        HttpHeaderParser.parseCacheHeaders(response));
            } catch (Exception e) {
                e.printStackTrace();
                return Response.error(new ParseError(e));
            }
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("User-Agent", WangYuApplication.USER_AGENT);
            //           headers.put("User-Agent", DateUtil.getNow().toString());
            return headers;
        }

        public NormalPostRequest(String url, Response.Listener<JSONObject> listener,
                                 Response.ErrorListener errorListener, Map<String, String> params) {
            super(Method.POST, url, errorListener);
            this.mListener = listener;
            this.mErrorListener = errorListener;
            this.mParams = params;
        }

        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
            return mParams;
        }

        @Override
        protected void deliverResponse(JSONObject response) {
            if (mListener != null) {
                mListener.onResponse(response);
            }
        }

        @Override
        public void deliverError(VolleyError error) {
            super.deliverError(error);
            if (mErrorListener != null) {
                mErrorListener.onErrorResponse(error);
            }
        }
    }

    public static String uploadPic(Map<String, String> params,
                                   File file) {
        BufferedReader bufferedReader = null;
        String prefix = "--";
        String boundary = "******";
        String lineEnd = "\r\n";
        String contentType = "multipart/form-data";
        String filePath = file.getAbsolutePath();
        try {
            // String strParams = paramsParseString(params, null);
            // String urlMD5Key = "&MKey=" + getUrlMD5Key(DATA_URL_HEAD +
            // strParams);
            System.setProperty("http.keepAlive", "false");
            String url = HttpConstant.SERVICE_UPLOAD_AREA + "common/upload?" + paramsParseString(params, "UTF-8");
            HttpURLConnection httpURLConnection = getHttpURLConnection(url);

            httpURLConnection.setRequestProperty("Content-Type", contentType
                    + ";boundary=" + boundary);
            httpURLConnection.setRequestProperty("Content-type", "application/x-java-serialized-object");
            OutputStream os = httpURLConnection.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);
            StringBuffer sbBegin = new StringBuffer();
            // sbBegin.append(strParams);
            sbBegin.append(prefix);
            sbBegin.append(boundary);
            sbBegin.append(lineEnd);
            sbBegin.append("Content-Disposition: form-data; name=\"pic\"");
            sbBegin.append(";filename=\""
                    + filePath.substring(filePath.lastIndexOf("/") + 1) + "\"");
            sbBegin.append(lineEnd);
            sbBegin.append(lineEnd);
            // os.write(strParams.toString().getBytes());
            dos.writeBytes(sbBegin.toString());
            FileInputStream fis = new FileInputStream(filePath);
            byte[] buffer = new byte[8 * 1024]; // 8k
            int count = 0;
            while ((count = fis.read(buffer)) != -1) {
                // os.write(buffer, 0, count);
                dos.write(buffer, 0, count);
            }
            fis.close();
            StringBuffer sbEnd = new StringBuffer();
            sbEnd.append(lineEnd);
            sbEnd.append(prefix + boundary + prefix);
            // os.write(sbEnd.toString().getBytes());
            // os.flush();
            dos.writeBytes(sbEnd.toString());
            dos.flush();
            int cah = httpURLConnection.getResponseCode();
            if (cah != 200) throw new RuntimeException("请求url失败");
            InputStream is = httpURLConnection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            bufferedReader = new BufferedReader(isr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return readBuffer(bufferedReader);
    }

    /**
     * 把请求参数转为字符串
     */
    private static String paramsParseString(Map<String, String> params,
                                            String encoding) {
        StringBuffer strParams = new StringBuffer();
        for (Iterator<String> iterator = params.keySet().iterator(); iterator
                .hasNext(); ) {
            String key = (String) iterator.next();
            String value = params.get(key);
            if (encoding != null) {
                try {
                    value = URLEncoder.encode(value, encoding);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            strParams.append(key).append("=");
            strParams.append(value).append("&");
        }
        if (!(strParams.lastIndexOf("&") == -1)) {
            strParams.deleteCharAt(strParams.lastIndexOf("&"));
        }
        return strParams.toString();
    }

    public static HttpURLConnection getHttpURLConnection(String urlStr) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(8000);
            conn.setDoOutput(true);// 允许输出
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(true);
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    private static String readBuffer(BufferedReader bufferedReader) {
        String result = "";
        String line = "";
        if (bufferedReader != null) {
            try {
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static String uploadFile(File file) {
        String filePath = file.getAbsolutePath();
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";//边界标识
        try {
            URL url = new URL(HttpConstant.SERVICE_UPLOAD_AREA + "common/upload?");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
/* 允许Input、Output，不使用Cache */
            con.setDoInput(true);//允许输入流
            con.setDoOutput(true);//允许输出流
            con.setUseCaches(false);//不允许使用缓存
/* 设置传送的method=POST */
            con.setRequestMethod("POST");
/* setRequestProperty 设置编码 */
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Charset", "UTF-8");
            con.setRequestProperty("Content-Type",// "multipart/form-data"这个参数来说明我们这传的是文件不是字符串了
                    "multipart/form-data;boundary=" + boundary);
/* 设置DataOutputStream */
            DataOutputStream ds =
                    new DataOutputStream(con.getOutputStream());
            ds.writeBytes(twoHyphens + boundary + end);
            ds.writeBytes("Content-Disposition: form-data; " +
                    "name=\"pic\";filename=\"" +
                    filePath.substring(filePath.lastIndexOf("/") + 1) + "\"" + end);
            ds.writeBytes(end);


/* 取得文件的FileInputStream */
            FileInputStream fStream = new FileInputStream(filePath);
/* 设置每次写入1024bytes */
            int bufferSize = 8 * 1024;
            byte[] buffer = new byte[bufferSize];
            int length = -1;
/* 从文件读取数据至缓冲区 */
            while ((length = fStream.read(buffer)) != -1) {
/* 将资料写入DataOutputStream中 */
                ds.write(buffer, 0, length);
            }
            ds.writeBytes(end);
            ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
/* close streams */
            fStream.close();
            ds.flush();
/* 取得Response内容 */
            InputStream is = con.getInputStream();
            int ch;
            StringBuffer b = new StringBuffer();
            while ((ch = is.read()) != -1) {
                b.append((char) ch);
            }
/* 将Response显示于Dialog */

// showDialog("上传成功"+b.toString().trim());
/* 关闭DataOutputStream */
            ds.close();
//返回客户端返回的信息
            return b.toString().trim();
        } catch (Exception e) {
//showDialog("上传失败"+e);
            return null;
        }
    }

    private static class SimpleErrorListener implements Response.ErrorListener {

        private String tag;

        private String method;

        WeakReference<ResponseListener> listener;

        public SimpleErrorListener(ResponseListener listener, String method) {
            this.listener = new WeakReference<ResponseListener>(listener);
            this.method = method;
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            if (listener.get() != null) {
                listener.get().onError(error.getMessage(), method);
            }
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getTag() {
            return tag;
        }
    }
}
