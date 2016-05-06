package sample.com.cats;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import okhttp3.Response;


/**
 * Created by seanchuang on 5/5/16.
 */
public class HttpTask extends AsyncTask<String, Void, String> {
    public enum HTTP_TASK {
        AUTH, TOKEN, PROFILE, FRIEND, CREATE_POST, INVITE, BILLING
    }
    private static String TAG = "HttpTask";
    private LoginActivity mActivity;
    private HTTP_TASK mTask;

    public HttpTask(LoginActivity activity, HTTP_TASK task) {
        this.mActivity = activity;
        this.mTask = task;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String result) {
        Log.i(TAG, result);
        switch(mTask) {
            case TOKEN:
                mActivity.setToken(result);
                break;
            case PROFILE:
                mActivity.showProfile(result);
                break;
            case FRIEND:
                mActivity.showFriend(result);
                break;
            case CREATE_POST:
                mActivity.showCreatePost(result);
                break;
            case INVITE:
                mActivity.showInvite(result);
                break;
            case BILLING:
                mActivity.showBilling(result);
                break;
        }
    }

    protected String doInBackground(String... strUrlFile) {
        String result = "";
        switch(mTask) {
            case AUTH: {
                Log.e(TAG, "Request AUTH");
                break;
            }
            case TOKEN: {
                Log.e(TAG, "Request TOKEN");
                NetworkManager networkManager = new NetworkManager();
                try {
                    JSONObject jobj = networkManager.postAuthData("https://oauth.mig.me/oauth/token", mActivity.getAuthCode().trim());
                    result = jobj.getString("access_token");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case PROFILE: {
                Log.e(TAG, "Request PROFILE");
                NetworkManager networkManager = new NetworkManager();
                try {
                    JSONObject jobj = networkManager.requestJsonData("https://mig.me/datasvc/API/user/profile", mActivity.getToken().trim());
                    result = jobj.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case FRIEND: {
                Log.e(TAG, "Request FRIEND");
                NetworkManager networkManager = new NetworkManager();
                try {
                    JSONObject jobj = networkManager.requestJsonData("https://mig.me/datasvc/API/user/friends?limit=-1&offset=0", mActivity.getToken().trim());
                    result = jobj.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case CREATE_POST: {
                Log.e(TAG, "Request CREATE_POST");
                NetworkManager networkManager = new NetworkManager();
                try {
                    JSONObject jObj = new JSONObject();
                    jObj.put("body", "$%^ @by migme test app");
                    jObj.put("privacy", 0);
                    jObj.put("reply_permission", 0);
                    jObj.put("originality", 1);
                    jObj.put("_version", "1.0");
                    JSONObject subJObj = new JSONObject();
                    subJObj.put("latitude", 0.0);
                    subJObj.put("longitude", 0.0);
                    subJObj.put("displayName", "test");
                    jObj.put("location", subJObj);

                    JSONObject jobj = networkManager.requestJsonData("https://mig.me/datasvc/API/post/create", jObj, mActivity.getToken().trim());
                    result = jobj.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case INVITE: {
                Log.e(TAG, "Request INVITE");
                NetworkManager networkManager = new NetworkManager();
                try {
                    JSONObject jObj = new JSONObject();
                    JSONObject destinationsObj = new JSONObject();
                    destinationsObj.put("user input invite stream", "");
                    jObj.put("destinations", destinationsObj);
                    jObj.put("invitationEmailType", 6);
                    jObj.put("thirdPartyAppID", 1);

                    JSONObject jobj = networkManager.requestJsonData("https://mig.me/datasvc/API/event/invite?method=@email&confirmToInvitationEngine=true", jObj, mActivity.getToken().trim());
                    result = jobj.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case BILLING:
                Log.e(TAG, "Request BILLING");
                NetworkManager networkManager = new NetworkManager();
                try {
                    JSONObject jObj = new JSONObject();
                    jObj.put("reference", "ref1");
                    jObj.put("description", "desc");
                    jObj.put("currency", "SGD");
                    jObj.put("amount", "0.001");

                    JSONObject jobj = networkManager.requestJsonData("https://mig.me/datasvc/API/user/bill", jObj, mActivity.getToken().trim());
                    result = jobj.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        return result;
    }

    public void stop() {

    }

}

