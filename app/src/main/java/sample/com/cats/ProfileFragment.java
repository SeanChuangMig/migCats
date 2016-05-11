package sample.com.cats;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executors;

/**
 * Created by seanchuang on 5/8/16.
 */
public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    private String mToken;
    private ImageView mImageView;
    private TextView mNameTextView;
    private TextView mCountyTextView;
    private TextView mLevelTextView;
    private TextView mRegisterTextView;
    private TextView mMibBotImageTextView;
    private TextView mFriendsTextView;
    private TextView mFollowerTextView;
    private TextView mFollowingTextView;
    private TextView mMailTextView;


    private JSONObject mProfileData;

    public ProfileFragment(String token) {
        mToken = token;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        mImageView = (ImageView) rootView.findViewById(R.id.avatar_ImageView);
        mNameTextView = (TextView) rootView.findViewById(R.id.name_TextView);
        mCountyTextView = (TextView) rootView.findViewById(R.id.country_TextView);
        mLevelTextView = (TextView) rootView.findViewById(R.id.level_TextView);
        mRegisterTextView = (TextView) rootView.findViewById(R.id.register_TextView);
        mMibBotImageTextView = (TextView) rootView.findViewById(R.id.migbot_image_TextView);
        mFriendsTextView = (TextView) rootView.findViewById(R.id.friend_TextView);
        mFollowerTextView = (TextView) rootView.findViewById(R.id.follwer_TextView);
        mFollowingTextView = (TextView) rootView.findViewById(R.id.following_TextView);
        mMailTextView = (TextView) rootView.findViewById(R.id.email_TextView);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mProfileData == null && mToken.length() > 0) {
            new profileTask().executeOnExecutor(Executors.newCachedThreadPool());
            return;
        }
        updateView();
    }

    private void updateView() {
        try {
            JSONObject data = mProfileData.getJSONObject("data");
            new DownloadImageTask().executeOnExecutor(Executors.newCachedThreadPool(), data.getString("avatarPictureUrl"));
            mNameTextView.setText(data.getString("username"));
            mCountyTextView.setText(data.getString("country"));
            mLevelTextView.setText(data.getString("migLevel"));
            mRegisterTextView.setText(data.getString("dateRegistered"));
            mMibBotImageTextView.setText(data.getString("migBotImage"));
            mFriendsTextView.setText(data.getString("numOfFriends"));
            mFollowerTextView.setText(data.getString("numOfFollowers"));
            mFollowingTextView.setText(data.getString("numOfFollowing"));
            mMailTextView.setText(data.getString("externalEmail"));
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
            Log.d(TAG, "Get profile result: " + result);
            try {
                mProfileData = new JSONObject(result);
                if (mProfileData.getJSONObject("error").getInt("errno") == 0) {
                    Toast.makeText(getActivity(), "Request profile success", Toast.LENGTH_SHORT).show();
                    updateView();
                } else {
                    Toast.makeText(getActivity(), "Request profile failed", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Request profile failed");
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        protected Bitmap doInBackground(String... urls) {
            Bitmap bmp = null;
            try {
                URL url = new URL(urls[0]);
                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bmp;
        }

        protected void onPostExecute(Bitmap result) {
            if (result != null)
                mImageView.setImageBitmap(result);
        }
    }
}
