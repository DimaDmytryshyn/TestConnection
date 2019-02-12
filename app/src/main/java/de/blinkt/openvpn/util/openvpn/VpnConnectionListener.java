package de.blinkt.openvpn.util.openvpn;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface VpnConnectionListener {

    void onConnected(@Nullable Intent intent);

    void onDisconnected();

    void onStatusChanged(@NonNull final String message,
                         @Nullable @VpnConnectionStatusStringDef final String status);
}
