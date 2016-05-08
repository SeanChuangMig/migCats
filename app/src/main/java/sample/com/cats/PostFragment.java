package sample.com.cats;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.apache.http.HttpStatus;
import org.json.JSONObject;

import java.util.concurrent.Executors;

/**
 * Created by seanchuang on 5/9/16.
 */
public class PostFragment extends Fragment {
    private static final String TAG = "PostFragment";
    private String mToken;

    public PostFragment(String token) {
        mToken = token;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_post, container, false);
        rootView.findViewById(R.id.post_Button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new postMigboTask().executeOnExecutor(Executors.newCachedThreadPool());
            }
        });

        return rootView;
    }

    public class postMigboTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String result = "";
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
                    if (response.getJsonObj() instanceof JSONObject) {
                        JSONObject resJObj = (JSONObject) response.getJsonObj();
                        result = resJObj.toString();
                    }
                } else {
                    Log.d(TAG, "NOT OK");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(TAG, "Get post result: " + result);
        }
    }
}
