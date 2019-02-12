package de.blinkt.openvpn.util.connection;

import android.support.annotation.NonNull;
import android.util.Log;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ConnectionUtil implements IConnectionUtil {

    private static final String TAG = "ConnectionUtil";

    private static final int MAIN_DNS_PORT = 53;
    private static final int MAIN_HTTP_PORT = 443;

    private static final int TIMEOUT = 1000;

    @Override
    public void isConnectionAvailable(@NonNull final String address,
                                      @NonNull final ConnectionCheckResultListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                listener.onConnectionChecked(testDnsPort(address) || testHttpPort(address));
            }
        }).start();
    }

    private boolean testHttpPort(@NonNull final String address) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(address, MAIN_HTTP_PORT), TIMEOUT);
            boolean result = socket.isConnected();
            socket.close();
            return result;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
    }

    private boolean testDnsPort(@NonNull final String address) {
        try {
            DatagramSocket datagramSocket = new DatagramSocket();
            datagramSocket.connect(InetAddress.getByName(address), MAIN_DNS_PORT);
            boolean result = datagramSocket.isConnected();
            datagramSocket.close();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
