package sample.com.cats;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Created by seanchuang on 5/8/16.
 */
public class FriendFragment extends Fragment {
    private static final String TAG = "FriendFragment";
    private String mToken;
    private ListView mListView;
    private JSONObject mFriendsData;
    private String[] mFriends;

    public FriendFragment(String token) {
        mToken = token;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friend, container, false);
        mListView = (ListView) rootView.findViewById(R.id.friend_ListView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e(TAG, "onActivityCreated");
        if (mFriends == null && mToken.length() > 0) {
            new friendTask().executeOnExecutor(Executors.newCachedThreadPool());
            return;
        }

        updateView();

    }

    private void updateView() {
        try {
            JSONArray jsonFriends = mFriendsData.getJSONArray("data");
            List<String> friends = new ArrayList<String>(10);
            for (int i = 0; i < jsonFriends.length(); i++) {
                friends.add(jsonFriends.getJSONObject(i).getString("username"));
            }
            mFriends = new String[friends.size()];
            mFriends = friends.toArray(mFriends);

            mListView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_expandable_list_item_1, mFriends));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class friendTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            NetworkManager networkManager = new NetworkManager();
            try {
                ServerResponse response = networkManager.getJsonData("https://mig.me/datasvc/API/user/friends?limit=10&offset=0", mToken.trim());
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
                mFriendsData = new JSONObject(result);
                updateView();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
