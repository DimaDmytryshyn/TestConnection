package de.blinkt.openvpn.ui.testconnectivity.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.test.testconnection.R;

import de.blinkt.openvpn.annotation.LateInit;
import de.blinkt.openvpn.util.connection.ConnectionCheckResultListener;
import de.blinkt.openvpn.util.connection.ConnectionUtil;
import de.blinkt.openvpn.util.connection.IConnectionUtil;
import de.blinkt.openvpn.util.openvpn.IOpenVpnManager;
import de.blinkt.openvpn.util.openvpn.OpenVpnManager;
import de.blinkt.openvpn.util.openvpn.VpnConnectionListener;
import de.blinkt.openvpn.util.openvpn.VpnConnectionStatusStringDef;

import static de.blinkt.openvpn.util.openvpn.OpenVpnManager.ICS_OPEN_VPN_PERMISSION;
import static de.blinkt.openvpn.util.openvpn.OpenVpnManager.START_PROFILE;

public class TestConnectionFragment extends Fragment implements VpnConnectionListener, View.OnClickListener {

    private static final String VPN_CONF_FILE_PATH = "ovpn/Germany-Berlin-UDP-TUN-normal.ovpn";
    private static final String VPN_CONF_URL = "de.eu.smoketunnel.com";

    @LateInit
    private TextView statusTextView;

    @LateInit
    private ImageView vpnStatusImageView;

    @LateInit
    private IOpenVpnManager vpnManager;

    private final IConnectionUtil connectionUtil = new ConnectionUtil();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        statusTextView = view.findViewById(R.id.statusTextView);
        vpnStatusImageView = view.findViewById(R.id.vpnStatusImageView);
        view.findViewById(R.id.startVpn).setOnClickListener(this);
        view.findViewById(R.id.disconnect).setOnClickListener(this);
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        vpnManager = new OpenVpnManager(context, this);
        vpnManager.bindVpnService();
    }

    @Override
    public void onDestroy() {
        vpnManager.unBindVpnService();
        super.onDestroy();
    }

    @Override
    public void onConnected(@Nullable Intent intent) {
        if (intent != null) {
            startActivityForResult(intent, ICS_OPEN_VPN_PERMISSION);
        } else {
            onActivityResult(ICS_OPEN_VPN_PERMISSION, Activity.RESULT_OK, null);
        }
    }

    @Override
    public void onDisconnected() {
        connectionUtil.isConnectionAvailable(VPN_CONF_URL, new ConnectionCheckResultListener() {

            @Override
            public void onConnectionChecked(boolean isConnectionAvailable) {
                if (isConnectionAvailable) {
                    vpnManager.waitAndStartVpn(VPN_CONF_FILE_PATH);
                } else {
                    Toast.makeText(getContext(), getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onStatusChanged(@NonNull final String message,
                                @Nullable @VpnConnectionStatusStringDef final String status) {
        statusTextView.setText(message);
        setStateImageViewDrawableByState(status);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startVpn:
                prepareStartProfile();
                break;
            case R.id.disconnect:
                try {
                    vpnManager.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case START_PROFILE:
                    connectionUtil.isConnectionAvailable(VPN_CONF_URL, new ConnectionCheckResultListener() {
                        @Override
                        public void onConnectionChecked(boolean isConnectionAvailable) {
                            if (isConnectionAvailable) {
                                vpnManager.startVpn(VPN_CONF_FILE_PATH);
                            } else {
                                Toast.makeText(getContext(), getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    break;
                case ICS_OPEN_VPN_PERMISSION:
                    vpnManager.registerCallback();
            }
        }
    }

    private void prepareStartProfile() {
        Intent requestPermission = vpnManager.prepareVpnService();
        if (requestPermission == null) {
            onActivityResult(OpenVpnManager.START_PROFILE, Activity.RESULT_OK, null);
        } else {
            startActivityForResult(requestPermission, OpenVpnManager.START_PROFILE);
        }
    }

    private void setStateImageViewDrawableByState(@Nullable @VpnConnectionStatusStringDef String status) {
        if (status == null) {
            return;
        }
        switch (status) {
            case VpnConnectionStatusStringDef.NO_PROCESS:
                vpnStatusImageView.setImageResource(R.drawable.ic_vpn_disconnected_red_24dp);
                break;
            case VpnConnectionStatusStringDef.CONNECTED:
                vpnStatusImageView.setImageResource(R.drawable.ic_vpn_connected_green_24dp);
        }
    }
}