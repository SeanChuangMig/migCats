package sample.com.cats;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by seanchuang on 5/8/16.
 */
public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    private TextView mNameTextView;
    private TextView mCountyTextView;
    private TextView mLevelTextView;

    private JSONObject mProfileData;

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
        if (mProfileData == null)
            return;

        try {
            JSONObject data = mProfileData.getJSONObject("data");
            mNameTextView.setText(data.getString("username"));
            mCountyTextView.setText(data.getString("country"));
            mLevelTextView.setText(data.getString("migLevel"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setProfile(JSONObject profile) {
        mProfileData = profile;
    }
}
