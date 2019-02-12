package de.blinkt.openvpn.util.connection;

import android.support.annotation.NonNull;

public interface IConnectionUtil {

    void isConnectionAvailable(@NonNull final String address, @NonNull final ConnectionCheckResultListener listener);
}
