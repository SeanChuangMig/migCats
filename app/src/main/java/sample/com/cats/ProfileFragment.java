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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Executors;

/**
 * Created by seanchuang on 5/8/16.
 */
public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    private String mToken;
    private TextView mNameTextView;
    private TextView mCountyTextView;
    private TextView mLevelTextView;

    private JSONObject mProfileData;

    public ProfileFragment(String token) {
        mToken = token;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        mNameTextView = (TextView) rootView.findViewById(R.id.name_TextView);
        mCountyTextView = (TextView) rootView.findViewById(R.id.country_TextView);
        mLevelTextView = (TextView) rootView.findViewById(R.id.level_TextView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e(TAG, "onActivityCreated");
        if (mProfileData == null && mToken.length() > 0) {
            new profileTask().executeOnExecutor(Executors.newCachedThreadPool());
            return;
        }
        updateView();
    }

    private void updateView() {
        try {
            JSONObject data = mProfileData.getJSONObject("data");
            mNameTextView.setText(data.getString("username"));
            mCountyTextView.setText(data.getString("country"));
            mLevelTextView.setText(data.getString("migLevel"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public class profileTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            NetworkManager networkManager = new NetworkManager();
            try {
                ServerResponse response = networkManager.getJsonData("https://mig.me/datasvc/API/user/profile", mToken.trim());
                if (response.getStatusCode() == HttpStatus.SC_OK) {
                    Log.d(TAG, "OK");
                    Log.d(TAG, response.getJsonObj().toString());
                    if (response.getJsonObj() instanceof JSONObject) {
                        JSONObject jObj = (JSONObject) response.getJsonObj();
                        result = jObj.toString();
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
            Log.e(TAG, "Get invite result: " + result);
            try {
                mProfileData = new JSONObject(result);
                updateView();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
