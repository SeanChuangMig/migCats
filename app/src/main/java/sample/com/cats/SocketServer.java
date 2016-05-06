package sample.com.cats;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import android.util.Log;

/**
 * Created by seanchuang on 5/5/16.
 */
public class SocketServer extends java.lang.Thread {
    private static String TAG = "SocketServer";

    private boolean OutServer = false;
    private ServerSocket server;
    private LoginActivity mActivity;


    public SocketServer(LoginActivity activity) {
        try {
            server = new ServerSocket(LoginActivity.PORT);
            mActivity = activity;
        } catch (java.io.IOException e) {
            Log.e(TAG, "Socket start fail !");
            Log.e(TAG, "IOException :" + e.toString());
        }
    }

    public void run() {
        Socket socket;
        java.io.BufferedInputStream in;

        Log.d(TAG, "server start !");
        while (!OutServer) {
            socket = null;
            try {
                synchronized (server) {
                    socket = server.accept();
                }
                Log.d(TAG, "session : InetAddress = "
                        + socket.getInetAddress());
                // TimeOut
                socket.setSoTimeout(15000);

                in = new java.io.BufferedInputStream(socket.getInputStream());
                byte[] b = new byte[1024];
                String data = "";
                int length;
                while ((length = in.read(b)) >= 0) {
                    data += new String(b, 0, length);
                    if (data.contains("\r\n")) {
                        break;
                    }
                }
                if (data.contains("GET /oauth/callback?code=")) {
                    String result = data.substring(data.indexOf("code=") + 5, data.indexOf("HTTP/1.1"));
                    mActivity.setAuthCode(result);
                    Log.d(TAG, "result = " + result);
                }

                in.close();
                in = null;
                socket.close();

            } catch (java.io.IOException e) {
                Log.e(TAG, "Socket fail !");
                Log.e(TAG, "IOException :" + e.toString());
            }

        }
    }

    public void close() {
        OutServer = true;
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
