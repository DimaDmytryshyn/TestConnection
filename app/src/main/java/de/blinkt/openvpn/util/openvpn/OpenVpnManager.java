package de.blinkt.openvpn.util.openvpn;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.List;

import de.blinkt.openvpn.api.APIVpnProfile;
import de.blinkt.openvpn.api.IOpenVPNAPIService;
import de.blinkt.openvpn.api.IOpenVPNStatusCallback;

public class OpenVpnManager implements IOpenVpnManager {

    private static final String TAG = "OpenVpnManager";

    private static final String OPEN_VPN_PACKAGE_NAME = "de.blinkt.openvpn";
    private static final int WAIT_TIME_MILLIS = 10000;

    public static final int START_PROFILE = 2;
    public static final int ICS_OPEN_VPN_PERMISSION = 7;

    private Handler handler = new Handler(Looper.getMainLooper());

    @NonNull
    private WeakReference<Context> weakContext;

    @NonNull
    private VpnConnectionListener listener;

    @Nullable
    private IOpenVPNAPIService mService;

    @NonNull
    private final IOpenVPNStatusCallback mCallback = new IOpenVPNStatusCallback.Stub() {

        @Override
        public void newStatus(String uuid, final String state, final String message, String level) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onStatusChanged(message, getStatus(state));
                }
            });
        }
    };

    public OpenVpnManager(@NonNull final Context context, @NonNull final VpnConnectionListener listener) {
        this.weakContext = new WeakReference<>(context);
        this.listener = listener;
    }

    @Override
    public void bindVpnService() {
        Intent intent = new Intent(IOpenVPNAPIService.class.getName()).setPackage(OPEN_VPN_PACKAGE_NAME);
        weakContext.get().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void unBindVpnService() {
        weakContext.get().unbindService(mConnection);
    }

    @Override
    public void startVpn(@NonNull final String confPath) {
        if (mService == null) {
            return;
        }
        try {
            String conf = readConf(weakContext.get().getAssets().open(confPath));
            if (!isProfileExist(confPath)) {
                mService.addNewVPNProfile(confPath, true, conf);
                mService.startVPN(conf);
                return;
            }
            mService.startProfile(getUUIDByName(confPath));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void waitAndStartVpn(@NonNull final String confPath) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startVpn(confPath);
            }
        }, WAIT_TIME_MILLIS);
    }

    @Nullable
    @Override
    public Intent prepareVpnService() {
        try {
            return mService == null ? null : mService.prepareVPNService();
        } catch (RemoteException e) {
            return null;
        }
    }

    @Override
    public void registerCallback() {
        try {
            if (mService != null) {
                mService.registerStatusCallback(mCallback);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void disconnect() throws RemoteException {
        if (mService != null) {
            mService.disconnect();
        }
    }

    private String readConf(@NonNull final InputStream conf) throws IOException {
        InputStreamReader isr = new InputStreamReader(conf);
        BufferedReader br = new BufferedReader(isr);
        StringBuilder config = new StringBuilder();
        String line;
        while (true) {
            line = br.readLine();
            if (line == null)
                break;
            config.append(line).append("\n");
        }
        br.readLine();
        return config.toString();
    }

    private boolean isProfileExist(@NonNull final String profileName) {
        try {
            if (mService == null) {
                return false;
            }
            boolean result = false;
            List<APIVpnProfile> profiles = mService.getProfiles();
            for (APIVpnProfile profile : profiles) {
                if (profile.mName != null && profile.mName.equals(profileName)) {
                    result = true;
                    break;
                }
            }
            return result;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
    }

    @Nullable
    private String getUUIDByName(@NonNull final String name) throws RemoteException {
        if (mService == null) {
            return null;
        }
        for (APIVpnProfile profile : mService.getProfiles()) {
            if (profile.mName != null && profile.mName.equals(name)) {
                return profile.mUUID;
            }
        }
        return null;
    }

    private String getStatus(@NonNull final String state) {
        switch (state) {
            case VpnConnectionStatusStringDef.NO_PROCESS:
                return VpnConnectionStatusStringDef.NO_PROCESS;
            case VpnConnectionStatusStringDef.VPN_GENERATE_CONFIG:
                return VpnConnectionStatusStringDef.VPN_GENERATE_CONFIG;
            case VpnConnectionStatusStringDef.RESOLVE:
                return VpnConnectionStatusStringDef.RESOLVE;
            case VpnConnectionStatusStringDef.TCP_CONNECT:
                return VpnConnectionStatusStringDef.TCP_CONNECT;
            case VpnConnectionStatusStringDef.WAIT:
                return VpnConnectionStatusStringDef.WAIT;
            case VpnConnectionStatusStringDef.AUTH:
                return VpnConnectionStatusStringDef.AUTH;
            case VpnConnectionStatusStringDef.GET_CONFIG:
                return VpnConnectionStatusStringDef.GET_CONFIG;
            case VpnConnectionStatusStringDef.ASSIGN_IP:
                return VpnConnectionStatusStringDef.ASSIGN_IP;
            case VpnConnectionStatusStringDef.ADD_ROUTES:
                return VpnConnectionStatusStringDef.ADD_ROUTES;
            case VpnConnectionStatusStringDef.CONNECTED:
                return VpnConnectionStatusStringDef.CONNECTED;
        }
        return null;
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {

            mService = IOpenVPNAPIService.Stub.asInterface(service);
            if (mService == null || weakContext.get() == null) {
                return;
            }

            try {
                Intent intent = mService.prepare(weakContext.get().getPackageName());
                listener.onConnected(intent);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            listener.onDisconnected();
            mService = null;
        }
    };
}
