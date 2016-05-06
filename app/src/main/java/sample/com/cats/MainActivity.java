package sample.com.cats;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;


public class MainActivity extends Activity implements AdapterView.OnItemClickListener {
    private static final String TAG = "MainActivity";
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 101;

    private final String KEY_TYPE = "type";
    private final String KEY_APP = "appLink";

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

    private String mToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToken = getIntent().getExtras().getString(LoginActivity.MyTOKEN);
        if(mToken!=null && mToken.length()>0) {
            Log.e(TAG, "Get token than requeset profile");
            requestUserProfile();
        }else{
            Log.e(TAG, "No token");
        }

        mType = getIntent().getStringExtra(KEY_TYPE);
        if (mType == null) {
            mType = "";
        }
        Log.i(TAG, "onCreate type=" + mType);
        mContext = this;

        mTempPath = String.format("%s/%s", Environment.getExternalStorageDirectory(), "tempCats.jpg");

        try {
            mCats = getAssets().list("cats");
            if (mCats != null) {
                int length = mCats.length;
                for (int i = 0; i < length; i++) {
                    mCatList.add(String.format("cats/%s", mCats[i]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        findViews();
    }

    @Override
    public void onStart(){
        super.onStart();
        checkIfHaveRuntimePermission();
    }

    private void requestUserProfile(){
        new HttpTask(MainActivity.this, HttpTask.HTTP_TASK.PROFILE, mToken).executeOnExecutor(Executors.newCachedThreadPool());
    }

    public void setProfile(String profile){
        Log.e(TAG, "Get Profile: " + profile);
        try {
            String name = new JSONObject(profile).getJSONObject("data").getString("username");
            getActionBar().setTitle("Welcome " + name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void findViews() {
        mGridView = (GridView) findViewById(R.id.gridview);
        mGridView.setAdapter(new CustomGrid());
        mGridView.setOnItemClickListener(this);
    }

    private void checkIfHaveRuntimePermission() {
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//            Log.i(TAG, "No runtime permission");
//            requestRuntimePermission();
//        }
//        else {
//            Log.i(TAG, "Already have runtime permission");
//        }
    }

//    private void requestRuntimePermission() {
//        // Should we show an explanation?
//        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//
//            Log.i(TAG, "show explanation");
//            // Show an explanation to the user *asynchronously* -- don't block
//            // this thread waiting for the user's response! After the user
//            // sees the explanation, try again to request the permission.
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
//
//        } else {
//
//            // No explanation needed, we can request the permission.
//            Log.i(TAG, "Request permission");
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
//
//            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
//            // app-defined int constant. The callback method gets the
//            // result of the request.
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                    // permission was granted, yay! Do the
//                    // contacts-related task you need to do.
//                    Log.i(TAG, "Permission granted!");
//                    Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
//
//                } else {
//                    Log.i(TAG, "Permission denied!");
//                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                    finish();
//                }
//                return;
//            }
//
//            // other 'case' lines to check for other
//            // permissions this app might request
//        }
//    }

    private void sendResultBack() {
        Intent intent;
        String uriString = String.format("%sfile://%s",
                mType.equals(PREFIX_POST) ? PREFIX_POST_WITH_IMAGE : PREFIX_CHAT_WITH_IMAGE, mTempPath);
        final Uri uri = Uri.parse(uriString);

        if(mType.equals(FROM_RESULT)){
            Log.i(TAG, "setResult: " +mType+ " " + uriString);
            intent = new Intent();
            intent.setData(uri);
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.putExtra(KEY_APP, DEEP_LINK);
            setResult(Activity.RESULT_OK, intent);
        }
        else if(mType.equals(PREFIX_CHAT) || mType.equals(PREFIX_POST)){
            Log.i(TAG, "startActivity: " +mType+ " " + uriString);
            String migmePacketageName = "com.projectgoth";
            intent = getPackageManager().getLaunchIntentForPackage(migmePacketageName);
            if (intent != null) {
                Log.e(TAG, "startActivity AA");
                intent.setAction(Intent.ACTION_SEND);
                intent.setData(uri);
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.putExtra(KEY_APP, DEEP_LINK);
                intent.setType("text/plain");
                startActivity(intent);
            } else {
                Log.e(TAG, "startActivity BB");
                intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("market://details?id=com.projectgoth"));
                startActivity(intent);
            }
        }
        finish();
    }

    private Handler mEventHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            sendResultBack();
        }
    };

    private class SavePhotoTask extends AsyncTask {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = ProgressDialog.show(mContext, null, "Processing...", true);
        }

        @Override
        protected Object doInBackground(Object[] params) {

            try {
                if (params == null || params.length == 0) {
                    return null;
                }
                String path = params[0].toString();
                InputStream is;

                is = getAssets().open(path);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        if (TextUtils.isEmpty(mType)) {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
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
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.grid_item, null);
            }

            ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
            try {
                String item = getItem(position).toString();
                // get input stream
                InputStream ims = getAssets().open(item);
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
