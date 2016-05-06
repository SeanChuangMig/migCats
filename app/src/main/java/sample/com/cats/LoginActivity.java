package sample.com.cats;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.concurrent.Executors;
import sample.com.cats.HttpTask.HTTP_TASK;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    public static int PORT = 34567;

    // old
//    public static final String CLIENT_ID = "309f818242abae8fdd1b";
    public static final String REDIRECT_URI = "http://localhost:" + PORT + "/oauth/callback";

    // new
    public static final String CLIENT_ID = "410df8f0129111e6b79c57492a68b460";
//    public static final String REDIRECT_URI = "migcat://migme/oauth/callback ";
    private static final String SCOPES = "profile test-scope invite payment store-admin payment";

    private WebView mWebView;
    private String mAuthCode;
    private String mToken;
    private SocketServer mSocketServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mSocketServer = new SocketServer(this);
        mSocketServer.start();
        String url = "https://oauth.mig.me/oauth/auth?client_id=" + CLIENT_ID + "&redirect_uri=" + REDIRECT_URI + "&scope=" + SCOPES + "&response_type=code";

        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.loadUrl(url);
        mWebView.setWebViewClient(mWebViewClient);
    }

    public void setAuthCode(String authCode) {
        mAuthCode = authCode;
        mHandler.sendEmptyMessage(HTTP_TASK.AUTH.ordinal());
    }

    public String getAuthCode() {
        return mAuthCode;
    }

    public void setToken(String token){
        mToken = token;
    }

    public String getToken() {
        return mToken;
    }

    public void showProfile(String data) {
        Message msg = mHandler.obtainMessage(HTTP_TASK.PROFILE.ordinal(), data);
        mHandler.sendMessage(msg);
    }

    public void showFriend(String data) {
        Message msg =  mHandler.obtainMessage(HTTP_TASK.FRIEND.ordinal(), data);
        mHandler.sendMessage(msg);
    }

    public void showCreatePost(String data) {
        Message msg =  mHandler.obtainMessage(HTTP_TASK.CREATE_POST.ordinal(), data);
        mHandler.sendMessage(msg);
    }

    public void showInvite(String data) {
        Message msg =  mHandler.obtainMessage(HTTP_TASK.INVITE.ordinal(), data);
        mHandler.sendMessage(msg);
    }

    public void showBilling(String data) {
        Message msg =  mHandler.obtainMessage(HTTP_TASK.BILLING.ordinal(), data);
        mHandler.sendMessage(msg);
    }

    private void handleMessage2(Message msg){
        HTTP_TASK what = HTTP_TASK.values()[msg.what];
        switch (what) {
            case AUTH:
                Log.e(TAG, "Get auth");
                new HttpTask(LoginActivity.this, HTTP_TASK.TOKEN).executeOnExecutor(Executors.newCachedThreadPool());
                break;
            case TOKEN:
                Log.e(TAG, "Get token");
                if(mToken != null) {
                    getAlertDialog("token = " + mToken).show();
                } else {
                    getAlertDialog("get token error").show();
                }
                break;
            case PROFILE:
            case FRIEND:
            case CREATE_POST:
            case INVITE:
            case BILLING:
                getAlertDialog((String) msg.obj).show();
                break;
        }
    }

    WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.e(TAG, "url: " + url);
            view.loadUrl(url);
            return true;
        }

        public void onPageFinished(WebView view, String url){

        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            handleMessage2(msg);
        }
    };

    private AlertDialog getAlertDialog(String message) {
        LinearLayout layout = new LinearLayout(this);
        TextView tv = new TextView(this);
        tv.setText(message);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(10);
        LinearLayout.LayoutParams pm = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.addView(tv, pm);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(20, 20, 20, 20);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(layout);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return builder.create();
    }

}
