package de.blinkt.openvpn.util.openvpn;

import android.support.annotation.StringDef;

@StringDef({
        VpnConnectionStatusStringDef.NO_PROCESS,
        VpnConnectionStatusStringDef.VPN_GENERATE_CONFIG,
        VpnConnectionStatusStringDef.RESOLVE,
        VpnConnectionStatusStringDef.TCP_CONNECT,
        VpnConnectionStatusStringDef.WAIT,
        VpnConnectionStatusStringDef.AUTH,
        VpnConnectionStatusStringDef.GET_CONFIG,
        VpnConnectionStatusStringDef.ASSIGN_IP,
        VpnConnectionStatusStringDef.ADD_ROUTES,
        VpnConnectionStatusStringDef.CONNECTED
})
public @interface VpnConnectionStatusStringDef {

    String NO_PROCESS = "NOPROCESS";
    String VPN_GENERATE_CONFIG = "VPN_GENERATE_CONFIG";
    String RESOLVE = "RESOLVE";
    String TCP_CONNECT = "TCP_CONNECT";
    String WAIT = "WAIT";
    String AUTH = "AUTH";
    String GET_CONFIG = "GET_CONFIG";
    String ASSIGN_IP = "ASSIGN_IP";
    String ADD_ROUTES = "ADD_ROUTES";
    String CONNECTED = "CONNECTED";
}
