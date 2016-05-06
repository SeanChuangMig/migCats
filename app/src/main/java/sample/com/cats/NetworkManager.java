package sample.com.cats;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;
import android.webkit.CookieSyncManager;

import java.util.List;


/**
 * Created by seanchuang on 5/5/16.
 */
public class NetworkManager {

    private static final String TAG = "NetworkManager";
    private static final int CONNECTION_TIMEOUT = 10000;
    private static final int SOCKET_TIMEOUT = 10000;
    public static final String GET_ALL_API = "http://sensors.hcteam.org/api/v1/getall";
    public static final String DETAIL_API = "http://sensors.hcteam.org/api/v1/getdetail";
    public static final String DETAIL_API_V3 = "http://sensors.hcteam.org/api/v3/output?";
    public static final String WARN_API_V3 = "http://sensors.hcteam.org/api/v3/sensor_warn";
    public static final String LOGIN_API_V3 = "http://sensors.hcteam.org/api/v3/login?name=tester&pass=abcdefghijk";

    private static HttpClient getHttpClient(boolean usingSSL) {
        HttpClient httpclient = null;
        if (usingSSL) {
            httpclient = CustomSSLSocketFactory.createSSLHttpClient();
        } else {
            httpclient = new DefaultHttpClient();
        }
        return httpclient;
    }

    public ServerResponse postData(String uri, List<NameValuePair> data) throws Exception {
        Object jResult = null;
        ServerResponse gResponse = null;
        if (uri != null && data != null) {
            HttpClient httpclient = getHttpClient((uri.indexOf("https://") == 0) ? true : false);
            Log.d(TAG, "postData() uri = " + uri + ", data = " + data.toString());
            HttpParams myParams = httpclient.getParams();
            HttpConnectionParams.setConnectionTimeout(myParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(myParams, SOCKET_TIMEOUT);

            HttpPost httppost = new HttpPost(uri);
            //httppost.setHeader("Content­Type", "application/x-www-form-urlencoded");
            httppost.setEntity(new UrlEncodedFormEntity(data, HTTP.UTF_8));

            HttpResponse response = httpclient.execute(httppost);

            int statusCode = response.getStatusLine().getStatusCode();
            String result;
            if (response.getEntity() == null) {
                Log.d(TAG, "Entity = null");
                result = null;
            } else {
                result = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
            }

            Log.d(TAG, "statusCode = " + statusCode);
            Log.d(TAG, "result = " + result);
            if (null != result && result.length() > 0) {
                Object jsonObj = new JSONTokener(result).nextValue();
                if (jsonObj instanceof JSONObject) {
                    jResult = (JSONObject)jsonObj;
                } else if (jsonObj instanceof JSONArray) {
                    jResult = (JSONArray)jsonObj;
                }
            }
            gResponse = new ServerResponse();
            gResponse.setStatusCode(statusCode);
            gResponse.setJsonObj(jResult);
        }
        return gResponse;
    }

    public ServerResponse postJsonData(String uri, JSONObject obj, String token) throws Exception {
        Object jResult = null;
        ServerResponse gResponse = null;
        if (uri != null && obj != null) {
            HttpClient httpclient = getHttpClient((uri.indexOf("https://") == 0) ? true : false);

            Log.d(TAG, "postJsonData() uri=" + uri + ", jsonObj=" + obj.toString());
            HttpParams myParams = httpclient.getParams();
            HttpConnectionParams.setConnectionTimeout(myParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(myParams, SOCKET_TIMEOUT);

            HttpPost httppost = new HttpPost(uri);
            httppost.setHeader("Authorization", "Bearer " + token.trim());
            httppost.setHeader(HTTP.CONTENT_TYPE, "application/json");

            StringEntity se = new StringEntity(obj.toString(), HTTP.UTF_8);
            se.setContentType("application/json");
            httppost.setEntity(se);

            HttpResponse response = httpclient.execute(httppost);

            int statusCode = response.getStatusLine().getStatusCode();
            String result;
            if (response.getEntity() == null) {
                Log.d(TAG, "Entity = null");
                result = null;
            } else {
                result = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
            }

            Log.d(TAG, "statusCode=" + statusCode + ", result=" + result);

            if (null != result && result.length() > 0) {
                Object jsonObj = new JSONTokener(result).nextValue();
                if (jsonObj instanceof JSONObject) {
                    jResult = (JSONObject)jsonObj;
                } else if (jsonObj instanceof JSONArray) {
                    jResult = (JSONArray)jsonObj;
                }
            }
            gResponse = new ServerResponse();
            gResponse.setStatusCode(statusCode);
            gResponse.setJsonObj(jResult);
        }
        return gResponse;
    }

    public ServerResponse getJsonData(String uri, String token) throws Exception {
        Object jResult = null;
        ServerResponse gResponse = null;
        if (uri != null) {
            HttpClient httpclient = getHttpClient((uri.indexOf("https://") == 0) ? true : false);
            Log.d(TAG, "postJsonData() uri=" + uri);
            HttpParams myParams = httpclient.getParams();
            HttpConnectionParams.setConnectionTimeout(myParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(myParams, SOCKET_TIMEOUT);

            HttpGet httpGet = new HttpGet(uri);

            httpGet.setHeader("Authorization", "Bearer " + token.trim());
            HttpResponse response = httpclient.execute(httpGet);

            int statusCode = response.getStatusLine().getStatusCode();
            String result;
            if (response.getEntity() == null) {
                Log.d(TAG, "Entity = null");
                result = null;
            } else {
                result = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
            }

            Log.d(TAG, "statusCode=" + statusCode + ", result=" + result);

            //result = result.substring(result.indexOf("[")); //for hc team

            if (null != result && result.length() > 0) {
                Object jsonObj = new JSONTokener(result).nextValue();
                if (jsonObj instanceof JSONObject) {
                    jResult = (JSONObject)jsonObj;
                } else if (jsonObj instanceof JSONArray) {
                    jResult = (JSONArray)jsonObj;
                }
            }
            gResponse = new ServerResponse();
            gResponse.setStatusCode(statusCode);
            gResponse.setJsonObj(jResult);
        }
        return gResponse;
    }

    public ServerResponse putJsonData(String uri, JSONObject obj, String token) throws Exception {
        Object jResult = null;
        ServerResponse gResponse = null;
        if (uri != null && obj != null) {
            HttpClient httpclient = getHttpClient((uri.indexOf("https://") == 0) ? true : false);
            Log.d(TAG, "putJsonData() uri=" + uri + ", jsonObj=" + obj.toString());
            HttpParams myParams = httpclient.getParams();
            HttpConnectionParams.setConnectionTimeout(myParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(myParams, SOCKET_TIMEOUT);

            HttpPutWithBody put = new HttpPutWithBody(uri);
            put.setHeader(HTTP.CONTENT_TYPE, "application/json");

            StringEntity se = new StringEntity(obj.toString(), HTTP.UTF_8);
            se.setContentType("application/json");
            put.setEntity(se);

            HttpResponse response = httpclient.execute(put);
            int statusCode = response.getStatusLine().getStatusCode();
            //String result = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
            String result;
            if (response.getEntity() == null) {
                Log.d(TAG, "Entity = null");
                result = null;
            } else {
                result = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
            }

            Log.d(TAG, "statusCode=" + statusCode + ", result=" + result);

            if (null != result && result.length() > 0) {
                Object jsonObj = new JSONTokener(result).nextValue();
                if (jsonObj instanceof JSONObject) {
                    jResult = (JSONObject)jsonObj;
                } else if (jsonObj instanceof JSONArray) {
                    jResult = (JSONArray)jsonObj;
                }
            }
            gResponse = new ServerResponse();
            gResponse.setStatusCode(statusCode);
            gResponse.setJsonObj(jResult);
        }
        return gResponse;
    }

    public static ServerResponse delJsonData(String uri, JSONObject obj) throws Exception {
        Object jResult = null;
        ServerResponse gResponse = null;
        if (uri != null && obj != null) {
            HttpClient httpclient = getHttpClient((uri.indexOf("https://") == 0) ? true : false);
            Log.d(TAG, "delJsonData() uri=" + uri + ", jsonObj=" + obj.toString());
            HttpParams myParams = httpclient.getParams();
            HttpConnectionParams.setConnectionTimeout(myParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(myParams, SOCKET_TIMEOUT);

            HttpDeleteWithBody delete = new HttpDeleteWithBody(uri);
            delete.setHeader(HTTP.CONTENT_TYPE, "application/json");

            StringEntity se = new StringEntity(obj.toString(), HTTP.UTF_8);
            se.setContentType("application/json");
            delete.setEntity(se);

            HttpResponse response = httpclient.execute(delete);
            int statusCode = response.getStatusLine().getStatusCode();
            //String result = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
            String result;
            if (response.getEntity() == null) {
                Log.d(TAG, "Entity = null");
                result = null;
            } else {
                result = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
            }

            Log.d(TAG, "statusCode=" + statusCode + ", result=" + result);

            if (null != result && result.length() > 0) {
                Object jsonObj = new JSONTokener(result).nextValue();
                if (jsonObj instanceof JSONObject) {
                    jResult = (JSONObject)jsonObj;
                } else if (jsonObj instanceof JSONArray) {
                    jResult = (JSONArray)jsonObj;
                }
            }
            gResponse = new ServerResponse();
            gResponse.setStatusCode(statusCode);
            gResponse.setJsonObj(jResult);
        }
        return gResponse;
    }

    public static ServerResponse getJsonData(String uri, JSONObject obj) throws Exception {
        Object jResult = null;
        ServerResponse gResponse = null;
        if (uri != null && obj != null) {
            HttpClient httpclient = getHttpClient((uri.indexOf("https://") == 0) ? true : false);
            Log.d(TAG, "getJsonData() uri=" + uri + ", jsonObj=" + obj.toString());
            HttpParams myParams = httpclient.getParams();
            HttpConnectionParams.setConnectionTimeout(myParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(myParams, SOCKET_TIMEOUT);

            HttpGetWithBody get = new HttpGetWithBody(uri);
            get.setHeader(HTTP.CONTENT_TYPE, "application/json");

            StringEntity se = new StringEntity(obj.toString(), HTTP.UTF_8);
            se.setContentType("application/json");
            get.setEntity(se);

            HttpResponse response = httpclient.execute(get);
            int statusCode = response.getStatusLine().getStatusCode();
            //String result = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
            String result;
            if (response.getEntity() == null) {
                Log.d(TAG, "Entity = null");
                result = null;
            } else {
                result = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
            }

            Log.d(TAG, "statusCode=" + statusCode + ", result=" + result);

            if (null != result && result.length() > 0) {
                Object jsonObj = new JSONTokener(result).nextValue();
                if (jsonObj instanceof JSONObject) {
                    jResult = (JSONObject)jsonObj;
                } else if (jsonObj instanceof JSONArray) {
                    jResult = (JSONArray)jsonObj;
                }
            }

            gResponse = new ServerResponse();
            gResponse.setStatusCode(statusCode);
            gResponse.setJsonObj(jResult);
        }
        return gResponse;
    }
}

