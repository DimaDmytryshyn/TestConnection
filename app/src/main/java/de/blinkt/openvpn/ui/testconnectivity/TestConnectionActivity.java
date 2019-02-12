package de.blinkt.openvpn.ui.testconnectivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.test.testconnection.R;

import de.blinkt.openvpn.ui.testconnectivity.fragment.TestConnectionFragment;

public class TestConnectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_connection);
        showTestConnectivityFragment();
    }

    private void showTestConnectivityFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.testConnectivityContainer, new TestConnectionFragment())
                .commit();
    }
}
