package sample.com.cats;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by seanchuang on 5/8/16.
 */
public class CatFragment extends Fragment implements AdapterView.OnItemClickListener{
    private static final String TAG = "CatFragment";

    private final String PREFIX_POST = "post";
    private final String PREFIX_CHAT = "chat";
    private final String FROM_RESULT = "result";

    private final String PREFIX_POST_WITH_IMAGE = "migme://post/create?referrer=migcats&hashtag=MigCats,migme&image=";
    private final String PREFIX_CHAT_WITH_IMAGE = "migme://chat/create?referrer=migcats&hashtag=MigCats,migme&image=";
    private final String DEEP_LINK = "{\"al:android:url\":\"migcat://open\",\"al:android:app_name\":\"migCats\",\"al:android:package\":\"sample.com.cats\",\"al:web:url\":\"http://mig.me/search/posts/?query=migcats\"}";

    private Context mContext;
    private String mCurrentURL;
    private String mTempPath;
    private String mType;

    private ProgressDialog mProgressDialog;
    private GridView mGridView;
    private List<String> mCatList = new ArrayList<String>();
    private String[] mCats;
    private Handler mEventHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            sendResultBack();
        }
    };

    public CatFragment(String type){
        mType = type;
        mTempPath = String.format("%s/%s", Environment.getExternalStorageDirectory(), "tempCats.jpg");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cat, container, false);
        mGridView = (GridView) rootView.findViewById(R.id.gridview);
        mGridView.setAdapter(new CustomGrid());
        mGridView.setOnItemClickListener(this);
        return rootView;
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "onCreate type=" + mType);
        try {
            mCats = getActivity().getAssets().list("cats");
            if (mCats != null) {
                int length = mCats.length;
                for (int i = 0; i < length; i++) {
                    mCatList.add(String.format("cats/%s", mCats[i]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        if (TextUtils.isEmpty(mType)) {
            AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
            CharSequence items[] = new CharSequence[]{"migme", "Chat"};
            adb.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface d, int n) {
                    if (n == 0) {
                        mType = PREFIX_POST;
                    } else {
                        mType = PREFIX_CHAT;
                    }
                    d.dismiss();
                    new SavePhotoTask().execute(mCatList.get(position));
                }
            });
            adb.setNegativeButton("Cancel", null);
            adb.setTitle("Share to");
            adb.show();
        } else {
            new SavePhotoTask().execute(mCatList.get(position));
        }
    }

    private class SavePhotoTask extends AsyncTask {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = ProgressDialog.show(getActivity(), null, "Processing...", true);
        }

        @Override
        protected Object doInBackground(Object[] params) {

            try {
                if (params == null || params.length == 0) {
                    return null;
                }
                String path = params[0].toString();
                InputStream is;

                is = getActivity().getAssets().open(path);
                Bitmap bm = BitmapFactory.decodeStream(is);

                FileOutputStream fos = new FileOutputStream(mTempPath);

                bm.compress(Bitmap.CompressFormat.JPEG, 70, fos);

                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            mProgressDialog.dismiss();
            mCurrentURL = null;
            mEventHandler.sendEmptyMessage(0);
        }
    }

    private void sendResultBack() {
        Intent intent;
        String uriString = String.format("%sfile://%s",
                mType.equals(PREFIX_POST) ? PREFIX_POST_WITH_IMAGE : PREFIX_CHAT_WITH_IMAGE, mTempPath);
        final Uri uri = Uri.parse(uriString);

        if(mType.equals(FROM_RESULT)){
            Log.i(TAG, "setResult: " + mType + " " + uriString);
            intent = new Intent();
            intent.setData(uri);
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.putExtra(MainActivity.KEY_APP, DEEP_LINK);
            getActivity().setResult(Activity.RESULT_OK, intent);
        }
        else if(mType.equals(PREFIX_CHAT) || mType.equals(PREFIX_POST)){
            Log.i(TAG, "startActivity: " +mType+ " " + uriString);
            String migmePacketageName = "com.projectgoth";
            intent = getActivity().getPackageManager().getLaunchIntentForPackage(migmePacketageName);
            if (intent != null) {
                intent.setAction(Intent.ACTION_SEND);
                intent.setData(uri);
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.putExtra(MainActivity.KEY_APP, DEEP_LINK);
                intent.setType("text/plain");
                startActivity(intent);
            } else {
                intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("market://details?id=com.projectgoth"));
                startActivity(intent);
            }
        }
        getActivity().finish();
    }

    public class CustomGrid extends BaseAdapter {

        @Override
        public int getCount() {
            return mCatList.size();
        }

        @Override
        public Object getItem(int position) {
            return mCatList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.grid_item, null);
            }

            ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
            try {
                String item = getItem(position).toString();
                // get input stream
                InputStream ims = getActivity().getAssets().open(item);
                // load image as Drawable
                Drawable d = Drawable.createFromStream(ims, null);
                // set image to ImageView
                imageView.setImageDrawable(d);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return convertView;
        }
    }
}
