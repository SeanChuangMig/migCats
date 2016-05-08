package sample.com.cats;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.apache.http.HttpStatus;
import org.json.JSONObject;

import java.util.concurrent.Executors;

/**
 * Created by seanchuang on 5/9/16.
 */
public class InviteFragment extends Fragment {
    private static final String TAG = "InviteFragment";
    private String mToken;
    private EditText mMailEditText;
    private String mMail;

    public InviteFragment(String token) {
        mToken = token;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_invite, container, false);
        mMailEditText = (EditText) rootView.findViewById(R.id.mail_EditText);
        rootView.findViewById(R.id.invite_Button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMail = mMailEditText.getText().toString();
                if (mMail.length() > 0)
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
                JSONObject destinationsObj = new JSONObject();
                destinationsObj.put(mMail, "");
                jObj.put("destinations", destinationsObj);
                jObj.put("invitationEmailType", 6);
                jObj.put("thirdPartyAppID", 1);

                ServerResponse response = networkManager.postJsonData("https://mig.me/datasvc/API/event/invite?method=@email&confirmToInvitationEngine=true", jObj, mToken.trim());
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
