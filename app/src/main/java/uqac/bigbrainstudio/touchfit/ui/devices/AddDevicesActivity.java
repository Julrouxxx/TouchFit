package uqac.bigbrainstudio.touchfit.ui.devices;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.material.textfield.TextInputEditText;
import uqac.bigbrainstudio.touchfit.R;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static android.net.wifi.WifiManager.EXTRA_NETWORK_INFO;

public class AddDevicesActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String LIGHT_PASS = "LightDevicePass";
    private WifiManager mWifiManager;
    private boolean ready = false;
    private List<String> device;
    private ArrayAdapter<String> dataAdapter;
    private TextInputEditText password;
    private EditText nameDevice;
    private Spinner spinner;
    private String actualSSID;
    private String keyMgt;
    private Button nextButton;
    private ProgressBar progressBar;
    private final BroadcastReceiver mWifiConnect = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            NetworkInfo networkInfo = intent.getParcelableExtra(EXTRA_NETWORK_INFO);
            if (networkInfo.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
                if (mWifiManager.getConnectionInfo().getSSID().startsWith("\"LightDevice-"))
                    new DeviceConnector(context, actualSSID, password.getText().toString(), keyMgt, nameDevice.getText().toString()).start();
            }
            if (networkInfo.getDetailedState() == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
                if (mWifiManager.getConnectionInfo().getSSID().equals(actualSSID)) {
                    unregisterReceiver(mWifiConnect);

                    finish();
                }
            }
        }
    };
    private final BroadcastReceiver mWifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            boolean success = intent.getBooleanExtra(
                    WifiManager.EXTRA_RESULTS_UPDATED, false);
            if (success) {
                List<ScanResult> results = mWifiManager.getScanResults();
                results = results.stream().filter(r -> r.SSID.startsWith("LightDevice-")).collect(Collectors.toList());
                device.clear();

                if (results.isEmpty()) {
                    device.add(getString(R.string.not_found));
                }else {
                    ready = true;

                }
                results.forEach(r -> device.add(r.SSID));
                dataAdapter.notifyDataSetChanged();

            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mWifiScanReceiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1230) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                scan();
            } else {
                finish();
                Toast.makeText(this, R.string.accept_location_permission, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_devices);
        progressBar = findViewById(R.id.connectingProgress);
        nextButton = findViewById(R.id.button_next);
        nextButton.setOnClickListener(this);
        nameDevice = findViewById(R.id.addNameDevice);
        registerReceiver(mWifiScanReceiver,
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
                Toast.makeText(this, R.string.accept_location_permission, Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1230);
            return;
        }

        scan();

    }

    @SuppressWarnings("deprecation")

    private void scan() {
        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        assert mWifiManager != null;
        mWifiManager.setWifiEnabled(true);

        if (mWifiManager.getConnectionInfo().getNetworkId() == -1) {
            finish();
            Toast.makeText(this, R.string.connect_to_your_wifi, Toast.LENGTH_LONG).show();
            return;
        }
        WifiConfiguration actualWifi = mWifiManager.getConfiguredNetworks().stream().filter(w -> w.networkId == mWifiManager.getConnectionInfo().getNetworkId()).findFirst().orElse(null);
        //Log.i("TouchFit", "scan: " + mWifiManager.getConfiguredNetworks().stream().filter(w -> w.status == WifiConfiguration.Status.CURRENT).findFirst().get().allowedKeyManagement);
        TextView networkTextView = findViewById(R.id.networkTextView);
        assert actualWifi != null;
        actualSSID = actualWifi.SSID;
        networkTextView.setText(getString(R.string.network_settings, actualWifi.SSID.substring(1, actualWifi.SSID.length() - 1)));
        TextView securityTextView = findViewById(R.id.networkSecTextView);
        password = findViewById(R.id.passwordWifi);

        switch (actualWifi.allowedKeyManagement.nextSetBit(0)) {
            case 0: {
                keyMgt = "None";
                password.setVisibility(View.GONE);
                nextButton.setEnabled(true);
                break;
            }
            case 1:
                keyMgt = "WPA-PSK";
                break;
            case 2:
                keyMgt = "WPA3";
                break;
            default:
                keyMgt = "NONE";
                break;
        }
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!ready) return;
                if(s.length() >= 8)
                    nextButton.setEnabled(true);
                else
                    nextButton.setEnabled(false);
            }
        });
        securityTextView.setText(getString(R.string.security_s, keyMgt));

        mWifiManager.startScan();
        spinner = findViewById(R.id.spinner_wifi);
        device = new ArrayList<>();
        device.add("Wait...");
        dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, device);
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    @Override
    public void onClick(View v) {
        nextButton.setEnabled(false);
        ready = false;
        progressBar.setVisibility(View.VISIBLE);
        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.SSID = "\"" + spinner.getSelectedItem().toString() + "\"";
        wifiConfiguration.preSharedKey = "\"" + LIGHT_PASS + "\"";
        int id = mWifiManager.addNetwork(wifiConfiguration);
        registerReceiver(mWifiConnect, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
        mWifiManager.enableNetwork(id, true);
    }

    private static class DeviceConnector extends Thread{

        public DeviceConnector(Context context, String wifi, String password, String keyMgmt, String deviceName) {
            this.wifi = wifi;
            this.password = password;
            this.keyMgmt = keyMgmt;
            this.context = context;
            this.deviceName = deviceName;
        }
        static final int PORT = 3243;
        String wifi, password, keyMgmt, deviceName;
        Context context;

        @Override
        public void run() {
            try {
                sleep(2000);
                DatagramSocket client_socket = new DatagramSocket(PORT);
                Devices device = new Devices(DevicesManager.getInstance().getDevices().size(), deviceName);
                DevicesManager.getInstance().addDevices(device);
                byte[] data = (wifi + "\n" + password + "\n" + keyMgmt + "\n" + device.getUuid().toString() + "\n").getBytes();
                DatagramPacket send_packet = new DatagramPacket(data, data.length, InetAddress.getByName("10.0.0.5"), PORT);
                client_socket.send(send_packet);
                client_socket.close();
                WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                mWifiManager.removeNetwork(mWifiManager.getConnectionInfo().getNetworkId());
                mWifiManager.reconnect();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
