package sample.com.cats;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by seanchuang on 5/9/16.
 */
public class MigFriend {

    private String mName;
    private String mMail;

    public MigFriend(JSONObject json){
        try {
            mName = json.getString("username");
            mMail = json.getString("mig33Email");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getName(){
        return mName;
    }

    public String getMail(){
        return mMail;
    }
}
