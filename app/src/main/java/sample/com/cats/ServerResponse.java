package sample.com.cats;

import org.apache.http.Header;

/**
 * Created by seanchuang on 5/6/16.
 */
public class ServerResponse {

    private Object mObj;
    private int mStatusCode;
    private Header[] mHeader;

    public Object getJsonObj() {
        return mObj;
    }

    public void setJsonObj(Object mObj) {
        this.mObj = mObj;
    }

    public int getStatusCode() {
        return mStatusCode;
    }

    public void setStatusCode(int mStatusCode) {
        this.mStatusCode = mStatusCode;
    }

    public Header[] getHeader() {
        return mHeader;
    }

    public void setHeader(Header[] mHeader) {
        this.mHeader = mHeader;
    }
}
