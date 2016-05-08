package sample.com.cats;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by seanchuang on 5/8/16.
 */
public class FriendFragment extends Fragment {
    private static final String TAG = "FriendFragment";
    private ListView mListView;
    private JSONArray mFriendsData;
    private String[] mFriends;

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
        if (mFriends == null)
            return;

        mListView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_expandable_list_item_1, mFriends));
    }

    public void setFriends(JSONObject data) {
        try {
            mFriendsData = data.getJSONArray("data");
            List<String> friends = new ArrayList<String>(10);
            for (int i = 0; i < mFriendsData.length(); i++) {
                friends.add(mFriendsData.getJSONObject(i).getString("username"));
            }
            mFriends = new String[friends.size()];
            mFriends = friends.toArray(mFriends);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
