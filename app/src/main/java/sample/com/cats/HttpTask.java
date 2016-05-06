package sample.com.cats;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by seanchuang on 5/5/16.
 */
public class HttpTask extends AsyncTask<String, Void, String> {
    public enum HTTP_TASK {
        AUTH, TOKEN, PROFILE, FRIEND, CREATE_POST, INVITE, BILLING
    }
    private static String TAG = "HttpTask";
    private MainActivity mActivity;
    private HTTP_TASK mTask;
    private String mToken;

    public HttpTask(MainActivity activity, HTTP_TASK task, String token) {
        this.mActivity = activity;
        this.mTask = task;
        this.mToken = token;
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
//                mActivity.setToken(result);
                break;
            case PROFILE:
                mActivity.setProfile(result);
                break;
            case FRIEND:
//                mActivity.showFriend(result);
                break;
            case CREATE_POST:
//                mActivity.showCreatePost(result);
                break;
            case INVITE:
//                mActivity.showInvite(result);
                break;
            case BILLING:
//                mActivity.showBilling(result);
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
//                NetworkManager networkManager = new NetworkManager();
//                try {
//                    List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
//                    nameValuePair.add(new BasicNameValuePair("code", mActivity.getAuthCode().trim()));
//                    nameValuePair.add(new BasicNameValuePair("client_id", LoginActivity.CLIENT_ID));
//                    nameValuePair.add(new BasicNameValuePair("redirect_uri", LoginActivity.REDIRECT_URI));
//                    nameValuePair.add(new BasicNameValuePair("grant_type", "authorization_code"));
//                    ServerResponse response = networkManager.postData("https://oauth.mig.me/oauth/token", nameValuePair);
//                    if (response.getStatusCode() == HttpStatus.SC_OK) {
//                        Log.d(TAG, "OK");
//                        Log.d(TAG, response.getJsonObj().toString());
//                        if(response.getJsonObj() instanceof JSONObject) {
//                            JSONObject jObj = (JSONObject) response.getJsonObj();
//                            result = jObj.getString("access_token");
//                        }
//                    } else {
//                        Log.d(TAG, "NOT OK");
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                break;
            }
            case PROFILE: {
                Log.e(TAG, "Request PROFILE");
                NetworkManager networkManager = new NetworkManager();
                try {
                    ServerResponse response = networkManager.getJsonData("https://mig.me/datasvc/API/user/profile", mToken.trim());
                    if (response.getStatusCode() == HttpStatus.SC_OK) {
                        Log.d(TAG, "OK");
                        Log.d(TAG, response.getJsonObj().toString());
                        if(response.getJsonObj() instanceof JSONObject) {
                            JSONObject jObj = (JSONObject) response.getJsonObj();
                            result = jObj.toString();
                        }
                    } else {
                        Log.d(TAG, "NOT OK");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case FRIEND: {
                Log.e(TAG, "Request FRIEND");
                NetworkManager networkManager = new NetworkManager();
                try {
                    ServerResponse response = networkManager.getJsonData("https://mig.me/datasvc/API/user/friends?limit=-1&offset=0", mToken.trim());
                    if (response.getStatusCode() == HttpStatus.SC_OK) {
                        Log.d(TAG, "OK");
                        Log.d(TAG, response.getJsonObj().toString());
                        if(response.getJsonObj() instanceof JSONObject) {
                            JSONObject jObj = (JSONObject) response.getJsonObj();
                            result = jObj.toString();
                        }
                    } else {
                        Log.d(TAG, "NOT OK");
                    }
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
                    jObj.put("body", "$%^&*  @by migme test app");
                    jObj.put("privacy", 0);
                    jObj.put("reply_permission", 0);
                    jObj.put("originality", 1);
                    jObj.put("_version", "1.0");
                    JSONObject subJObj = new JSONObject();
                    subJObj.put("latitude", 0.0);
                    subJObj.put("longitude", 0.0);
                    subJObj.put("displayName", "test");
                    jObj.put("location", subJObj);

                    ServerResponse response = networkManager.postJsonData("https://mig.me/datasvc/API/post/create", jObj, mToken.trim());
                    if (response.getStatusCode() == HttpStatus.SC_OK) {
                        Log.d(TAG, "OK");
                        Log.d(TAG, response.getJsonObj().toString());
                        if(response.getJsonObj() instanceof JSONObject) {
                            JSONObject resJObj = (JSONObject) response.getJsonObj();
                            result = resJObj.toString();
                        }
                    } else {
                        Log.d(TAG, "NOT OK");
                    }
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
                    destinationsObj.put("invite string", "");
                    jObj.put("destinations", destinationsObj);
                    jObj.put("invitationEmailType", 6);
                    jObj.put("thirdPartyAppID", 1);

                    ServerResponse response = networkManager.postJsonData("https://mig.me/datasvc/API/event/invite?method=@email&confirmToInvitationEngine=true", jObj, mToken.trim());
                    if (response.getStatusCode() == HttpStatus.SC_OK) {
                        Log.d(TAG, "OK");
                        Log.d(TAG, response.getJsonObj().toString());
                        if(response.getJsonObj() instanceof JSONObject) {
                            JSONObject resJObj = (JSONObject) response.getJsonObj();
                            result = resJObj.toString();
                        }
                    } else {
                        Log.d(TAG, "NOT OK");
                    }
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

                    ServerResponse response = networkManager.postJsonData("https://mig.me/datasvc/API/user/bill", jObj, mToken.trim());
                    if (response.getStatusCode() == HttpStatus.SC_OK) {
                        Log.d(TAG, "OK");
                        Log.d(TAG, response.getJsonObj().toString());
                        if(response.getJsonObj() instanceof JSONObject) {
                            JSONObject resJObj = (JSONObject) response.getJsonObj();
                            result = resJObj.toString();
                        }
                    } else {
                        Log.d(TAG, "NOT OK");
                    }
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

