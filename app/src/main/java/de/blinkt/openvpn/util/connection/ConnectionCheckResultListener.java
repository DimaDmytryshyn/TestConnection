package de.blinkt.openvpn.util.connection;

public interface ConnectionCheckResultListener {

    void onConnectionChecked(boolean isConnectionAvailable);
}
