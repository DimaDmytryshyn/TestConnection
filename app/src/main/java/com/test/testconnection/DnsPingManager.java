package com.test.testconnection;

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.util.Log;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

class DnsPingManager {

    private static final String TAG = "DnsPingManager";

    private static final int MAIN_DNS_PORT = 53;
    private static final int MAIN_HTTP_PORT = 443;

    private static final int TIMEOUT = 1000;

    @WorkerThread
    boolean isConnectionAvailable(@NonNull String address) {
        return testDnsPort(address) && testHttpPort(address);
    }

    private boolean testHttpPort(@NonNull String address) {
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

    private boolean testDnsPort(@NonNull String address) {
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
