package sample.com.cats;

import android.util.Log;

import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


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

    private static OkHttpClient getHttpClient(boolean usingSSL) {
        OkHttpClient httpclient = null;
        if (usingSSL) {
            httpclient = createSSLHttpClient();
        } else {
            httpclient = new OkHttpClient.Builder().build();
        }
        return httpclient;
    }

    public static OkHttpClient createSSLHttpClient() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            // Install the all-trusting trust manager
            // SSL ? TLS ?
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            return new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory())
                    .hostnameVerifier(hostnameVerifier)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new OkHttpClient.Builder().build();
    }

    public JSONObject postAuthData(String uri, String authCode) throws Exception {
        JSONObject jobj = null;
        if (uri != null) {
            Log.d(TAG, "postData() uri = " + uri);

            OkHttpClient httpclient = getHttpClient((uri.indexOf("https://") == 0) ? true : false);

            Request request = new Request.Builder()
                    .url(uri)
                    .addHeader("code", authCode)
                    .addHeader("client_id", LoginActivity.CLIENT_ID)
                    .addHeader("redirect_uri", LoginActivity.REDIRECT_URI)
                    .addHeader("grant_type", "authorization_code")
                    .build();

            Response response = httpclient.newCall(request).execute();
            if (!response.isSuccessful()) throw new Exception("Unexpected code " + response);
            jobj = new JSONObject(response.body().toString());
            response.body().close();
        }
        return jobj;
    }

    public JSONObject requestJsonData(String uri, JSONObject obj, String token) throws Exception {
        JSONObject jobj = null;
        if (uri != null && obj != null) {
            Log.d(TAG, "postJsonData() uri=" + uri + ", jsonObj=" + obj.toString());

            OkHttpClient httpclient = getHttpClient((uri.indexOf("https://") == 0) ? true : false);

            RequestBody body = RequestBody.create(getMediaType(), obj.toString());
            Request request = new Request.Builder()
                    .url(uri)
                    .addHeader("Authorization", "Bearer " + token.trim())
                    .post(body)
                    .build();

            Response response = httpclient.newCall(request).execute();
            if (!response.isSuccessful()) throw new Exception("Unexpected code " + response);
            jobj = new JSONObject(response.body().toString());
            response.body().close();
        }
        return jobj;
    }

    public JSONObject requestJsonData(String uri, String token) throws Exception {
        JSONObject jobj = null;
        if (uri != null) {
            Log.d(TAG, "postJsonData() uri=" + uri);

            OkHttpClient httpclient = getHttpClient((uri.indexOf("https://") == 0) ? true : false);

            Request request = new Request.Builder()
                    .url(uri)
                    .addHeader("Authorization", "Bearer " + token.trim())
                    .build();

            Response response = httpclient.newCall(request).execute();
            if (!response.isSuccessful()) throw new Exception("Unexpected code " + response);
            jobj = new JSONObject(response.body().toString());
            response.body().close();
        }
        return jobj;
    }

    private MediaType getMediaType(){
        return MediaType.parse("application/json; charset=utf-8");
    }
}

