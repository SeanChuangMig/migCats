package sample.com.cats;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import org.apache.http.HttpStatus;
import org.json.JSONObject;

import java.util.concurrent.Executors;

/**
 * Created by seanchuang on 5/9/16.
 */
public class OtherFragment extends Fragment {
    private static final String TAG = "OtherFragment";
    private static final String DEFAULT_NUMBER_OF_TRANSACTIONS = "10";

    private String mToken;

    private RadioGroup mInviteRadioGroup;
    private EditText mMailEditText;
    private String mMail;
    private String mInviteType;

    private RadioGroup mCurrencyRadioGroup;
    private EditText mBillEditText;
    private EditText mDescriptionEditText;
    private String mCurrency;
    private double mAmount;
    private String mDescription;

    public OtherFragment(String token) {
        mToken = token;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_other, container, false);
        mInviteRadioGroup = (RadioGroup) rootView.findViewById(R.id.invite_RadioGroup);
        mMailEditText = (EditText) rootView.findViewById(R.id.mail_EditText);
        rootView.findViewById(R.id.invite_Button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMail = mMailEditText.getText().toString();
                mInviteType = getInviteType();
                if (mMail.length() > 0 && mToken.length() > 0)
                    new inviteFriendTask().executeOnExecutor(Executors.newCachedThreadPool());
            }
        });

        mCurrencyRadioGroup = (RadioGroup) rootView.findViewById(R.id.currency_RadioGroup);
        mBillEditText = (EditText) rootView.findViewById(R.id.billing_EditText);
        mDescriptionEditText = (EditText) rootView.findViewById(R.id.description_EditText);
        rootView.findViewById(R.id.deduct_Button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAmount = Double.parseDouble(mBillEditText.getText().toString());
                mDescription = mDescriptionEditText.getText().toString();
                mCurrency = getCurrency();
                if (mAmount > 0 && mToken.length() > 0 && mCurrency.length() > 0)
                    new billingTask().executeOnExecutor(Executors.newCachedThreadPool());
            }
        });

        rootView.findViewById(R.id.locker_Button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mToken.length() > 0)
                    new lockerTask().executeOnExecutor(Executors.newCachedThreadPool());
            }
        });

        return rootView;
    }

    private String getInviteType() {
        String type = "";
        switch (mInviteRadioGroup.getCheckedRadioButtonId()) {
            case R.id.migme_RadioGroup:
                type = "6";
                break;
            case R.id.mig_games_RadioGroup:
                type = "14";
                break;
            default:
                break;
        }
        return type;
    }

    private String getCurrency() {
        String currency = "";
        switch (mCurrencyRadioGroup.getCheckedRadioButtonId()) {
            case R.id.usd_RadioGroup:
                currency = "USD";
                break;
            case R.id.sgd_RadioGroup:
                currency = "SGD";
                break;
            case R.id.aud_RadioGroup:
                currency = "AUD";
                break;
            default:
                break;
        }
        return currency;
    }

    public class inviteFriendTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            NetworkManager networkManager = new NetworkManager();
            try {
                JSONObject jObj = new JSONObject();
                JSONObject destinationsObj = new JSONObject();
                destinationsObj.put(mMail, "referralIdA");
                jObj.put("destinations", destinationsObj);
                jObj.put("invitationEmailType", mInviteType);
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
        }
    }

    public class billingTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String result = "";
            NetworkManager networkManager = new NetworkManager();
            try {
                JSONObject jObj = new JSONObject();
                jObj.put("reference", "ref1");
                jObj.put("description", mDescription);
                jObj.put("currency", mCurrency);
                jObj.put("amount", mAmount);

                ServerResponse response = networkManager.postJsonData("https://mig.me/datasvc/API/user/bill", jObj, mToken.trim());
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
            Log.e(TAG, "Get billing result: " + result);
        }
    }

    public class lockerTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String result = "";
            NetworkManager networkManager = new NetworkManager();
            try {
                ServerResponse response = networkManager
                        .getJsonData("https://api.mig.me/cxb/transactions?limit=" + DEFAULT_NUMBER_OF_TRANSACTIONS, mToken.trim());
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
            Log.e(TAG, "Get locker result: " + result);
        }
    }
}
