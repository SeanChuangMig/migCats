package sample.com.cats;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Created by seanchuang on 5/8/16.
 */
public class FriendFragment extends Fragment {
    private static final String TAG = "FriendFragment";
    private static final String INVITE_TO_MIGME = "6";
    private static final String INVITE_TO_MIGGAME = "14";

    private String mToken;
    private ListView mListView;
    private JSONObject mFriendsData;
    private List<MigFriend> mFriends;
    private List<Boolean> mInvitedFriend;

    public FriendFragment(String token) {
        mToken = token;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friend, container, false);
        mListView = (ListView) rootView.findViewById(R.id.friend_ListView);
        rootView.findViewById(R.id.migme_Button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inviteToMigme();
            }
        });
        rootView.findViewById(R.id.miggame_Button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inviteToMigGame();
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e(TAG, "onActivityCreated");
        if (mFriends == null && mToken.length() > 0) {
            new getFriendsTask().executeOnExecutor(Executors.newCachedThreadPool());
            return;
        }

        updateView();

    }

    private void updateView() {
        try {
            JSONArray jsonFriends = mFriendsData.getJSONArray("data");
            mFriends = new ArrayList<MigFriend>(jsonFriends.length());
            for (int i = 0; i < jsonFriends.length(); i++) {
                mFriends.add(new MigFriend(jsonFriends.getJSONObject(i)));
            }

            mInvitedFriend = new ArrayList<Boolean>(mFriends.size());
            for (int i = 0; i < mFriends.size(); i++)
                mInvitedFriend.add(false);

            mListView.setAdapter(new FriendAdapter());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void inviteToMigme() {
        showInvitedFriend();
        new inviteFriendTask().executeOnExecutor(Executors.newCachedThreadPool(), INVITE_TO_MIGME);
    }

    private void inviteToMigGame() {
        showInvitedFriend();
        new inviteFriendTask().executeOnExecutor(Executors.newCachedThreadPool(), INVITE_TO_MIGGAME);

    }

    private void showInvitedFriend() {
        String invitePeople = "";
        for (int i = 0; i < mInvitedFriend.size(); i++) {
            if (mInvitedFriend.get(i) == true)
                invitePeople += " " + mFriends.get(i);
        }
        Log.e(TAG, "Invite people: " + invitePeople);
    }

    private class FriendAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mFriends.size();
        }

        @Override
        public MigFriend getItem(int position) {
            return mFriends.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                convertView = inflater.inflate(R.layout.friend_item, parent, false);
            }

            TextView nameTextView = (TextView) convertView.findViewById(R.id.name_TextView);
            nameTextView.setText(getItem(position).getName());
            final int pos = position;
            CheckBox inviteCheckBox = (CheckBox) convertView.findViewById(R.id.invite_CheckBox);
            inviteCheckBox.setChecked(mInvitedFriend.get(position));
            inviteCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mInvitedFriend.set(pos, isChecked);
                }
            });

            return convertView;
        }
    }

    public class getFriendsTask extends AsyncTask<String, Void, String> {

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
            Log.e(TAG, "Get friends result: " + result);
            try {
                mFriendsData = new JSONObject(result);
                if (mFriendsData.getJSONObject("error").getInt("errno") == 0) {
                    Toast.makeText(getActivity(), "Request friends success", Toast.LENGTH_SHORT).show();
                    updateView();
                } else {
                    Toast.makeText(getActivity(), "Request friends failed", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Request friends failed");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class inviteFriendTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            NetworkManager networkManager = new NetworkManager();
            try {
                JSONObject jObj = new JSONObject();
                JSONObject destinationsObj = new JSONObject();

                for (int i = 0; i < mFriends.size(); i++)
                    if (mInvitedFriend.get(i) == true)
                        if (params[0].equals(INVITE_TO_MIGME))
                            destinationsObj.put(mFriends.get(i).getMail(), "referralIdA");
                        else
                            destinationsObj.put(mFriends.get(i).getName(), "referralIdA");

                jObj.put("destinations", destinationsObj);
                jObj.put("invitationEmailType", params[0]);
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
            Log.e(TAG, "Get invite result: " + result);
            try {
                JSONObject jobj = new JSONObject(result);
                if (jobj.getString("data").equals("ok"))
                    Toast.makeText(getActivity(), "Invite success", Toast.LENGTH_SHORT).show();
                else {
                    Toast.makeText(getActivity(), "Invite failed", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Invite failed");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
