package sample.com.cats;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


public class MainActivity extends FragmentActivity {
    private static final String TAG = "MainActivity";

    public static final String KEY_TYPE = "type";
    public static final String KEY_APP = "appLink";

    private String mToken;

    private String[] mNavigationDrawerItemTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private DrawerItemCustomAdapter mDrawerAdapter;

    private Fragment mCatFragment;
    private ProfileFragment mProfileFragment;
    private FriendFragment mFriendFragment;
    private PostFragment mPostFragment;
    private OtherFragment mOtherFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkIfHaveToken();

        String type = getIntent().getStringExtra(KEY_TYPE);
        if (type == null) {
            type = "";
        }

        mCatFragment = new CatFragment(type);
        mProfileFragment = new ProfileFragment(mToken);
        mFriendFragment = new FriendFragment(mToken);
        mPostFragment = new PostFragment(mToken);
        mOtherFragment = new OtherFragment(mToken);

        initViews();
    }

    private void checkIfHaveToken() {
        mToken = getSharedPreferences(LoginActivity.MyPREFERENCES, getApplicationContext().MODE_PRIVATE)
                .getString(LoginActivity.MyTOKEN, "");
    }

    @Override
    public void onStart() {
        super.onStart();
        checkIfHaveRuntimePermission();
        selectItem(0);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void initViews() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mNavigationDrawerItemTitles = getResources().getStringArray(R.array.navigation_drawer_items_array);
        mDrawerAdapter = new DrawerItemCustomAdapter(this, R.layout.listview_item_row, mNavigationDrawerItemTitles);
        mDrawerList.setAdapter(mDrawerAdapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });
    }

    private void selectItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = mCatFragment;
                break;
            case 1:
                fragment = mProfileFragment;
                break;
            case 2:
                fragment = mFriendFragment;
                break;
            case 3:
                fragment = mPostFragment;
                break;
            case 4:
                fragment = mOtherFragment;
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            getActionBar().setTitle(mNavigationDrawerItemTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);

        } else {
            Log.e("MainActivity", "Error in creating fragment");
        }
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

}
