package de.blinkt.openvpn.util.openvpn;

import android.content.Intent;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface IOpenVpnManager {

    void bindVpnService();

    void unBindVpnService();

    void startVpn(@NonNull final String confPath);

    void waitAndStartVpn(@NonNull final String confPath);

    @Nullable
    Intent prepareVpnService();

    void registerCallback();

    void disconnect() throws RemoteException;
}
