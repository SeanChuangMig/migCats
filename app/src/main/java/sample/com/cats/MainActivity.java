package sample.com.cats;

import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.LinkedList;


public class MainActivity extends FragmentActivity {
    private static final String TAG = "MainActivity";

    public static final String KEY_TYPE = "type";
    public static final String KEY_APP = "appLink";

    private String mToken;

    private String[] mNavigationDrawerItemTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private DrawerItemCustomAdapter mDrawerAdapter;
    private ActionBarDrawerToggle mDrawerToggle;

    private Fragment mCatFragment;
    private ProfileFragment mProfileFragment;
    private FriendFragment mFriendFragment;
//    private PostFragment mPostFragment;
    private OtherFragment mOtherFragment;

    private LinkedList<Integer> mFragmentIndexStack = new LinkedList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkIfHaveToken();

        String type = getIntent().getStringExtra(KEY_TYPE);
        if (type == null) {
            type = "";
        }

        Log.e(TAG, "My Type: " + type);

        mCatFragment = new CatFragment(type);
        mProfileFragment = new ProfileFragment(mToken);
        mFriendFragment = new FriendFragment(mToken);
//        mPostFragment = new PostFragment(mToken);
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
        selectItem(0, false);
    }

    @Override
    public void onBackPressed() {
        if (mFragmentIndexStack.size() > 1)
            selectItem(mFragmentIndexStack.pop(), true);
        else
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
                selectItem(position, false);
            }
        });
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_launcher, R.string.drawer_open, R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void selectItem(int position, Boolean pressBack) {
        if (mFragmentIndexStack.size() > 0 && mFragmentIndexStack.get(0).equals(position))
            return;

        if(!pressBack)
            mFragmentIndexStack.push(position);

        int targetView = mFragmentIndexStack.get(0);
        Fragment fragment = null;
        switch (targetView) {
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
                fragment = mOtherFragment;
                break;
            default:
                break;
        }

        if (fragment != null) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

            mDrawerList.setItemChecked(targetView, true);
            mDrawerList.setSelection(targetView);
            getActionBar().setTitle(mNavigationDrawerItemTitles[targetView]);
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
