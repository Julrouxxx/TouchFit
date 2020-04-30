package uqac.bigbrainstudio.touchfit.controllers.devices;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.snackbar.Snackbar;
import uqac.bigbrainstudio.touchfit.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.*;
import java.util.*;

public class DevicesDataRunnable extends AsyncTask<Device, Integer, Integer> {
    private WeakReference<ProgressBar> progressBar;
    private WeakReference<RecyclerView> recyclerView;
    private WeakReference<SwipeRefreshLayout> mSwipeRefreshLayout;
    public DevicesDataRunnable(RecyclerView recyclerView){
        this.recyclerView = new WeakReference<>(recyclerView);
    }
    public DevicesDataRunnable(){

    }
    public DevicesDataRunnable(ProgressBar progressBar){
        this.progressBar = new WeakReference<>(progressBar);
        this.progressBar.get().setMax(100);
    }

    public DevicesDataRunnable(RecyclerView recyclerView, SwipeRefreshLayout mSwipeRefreshLayout) {
        this.recyclerView = new WeakReference<>(recyclerView);
        this.mSwipeRefreshLayout = new WeakReference<>(mSwipeRefreshLayout);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {

        if(progressBar != null) progressBar.get().setProgress(values[0], true);
    }

    @Override
    protected Integer doInBackground(Device... devices) {
        InetAddress broadcast = null;
        Socket client;
        DatagramSocket client_socket;
        publishProgress(0);
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (networkInterface.isLoopback())
                    continue;
                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    if (interfaceAddress.getBroadcast() == null)
                        continue;
                    broadcast = interfaceAddress.getBroadcast();
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        if (broadcast == null) {
            if(recyclerView != null)
            Snackbar.make(recyclerView.get(), R.string.connect_to_wifi, Snackbar.LENGTH_INDEFINITE).setAction(R.string.activate_wifi, v -> {
                WifiManager wifiManager = (WifiManager) recyclerView.get().getContext().getSystemService(Context.WIFI_SERVICE);
                assert wifiManager != null;
                wifiManager.setWifiEnabled(true);
            }).show();
            return 0;
        }


        ServerSocket pingSocket = null;
        try {
            client_socket = new DatagramSocket(3255);
            byte[] data = "R.".getBytes();
            DatagramPacket send_packet = new DatagramPacket(data, data.length, broadcast, 3255);
            client_socket.send(send_packet);
            client_socket.close();


            pingSocket = new ServerSocket(3255);
            pingSocket.setSoTimeout(300);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int connected = 0;
        for(Device device : devices)
            device.setConnected(false);
        for (int i = 1; i <= devices.length; i++) {
            publishProgress(Math.floorDiv(i * 100, devices.length));

            try {
                assert pingSocket != null;
                client = pingSocket.accept();
                client.setSoTimeout(100);
                BufferedReader inFromClient =
                        new BufferedReader(new InputStreamReader(client.getInputStream()));
                char[] buffer = new char[36];
                //noinspection ResultOfMethodCallIgnored
                inFromClient.read(buffer, 0, 36);
                StringBuilder stringBuilder = new StringBuilder();
                for (char c : buffer) {
                    stringBuilder.append(c);
                }
                client.close();
                UUID ident = UUID.fromString(stringBuilder.toString());
                for (Device device1 : devices) {
                    if (ident.equals(device1.getUuid())) {
                        InetAddress address = client.getInetAddress();
                        device1.setHostname(address.getHostName() + "/" + address.getHostAddress());
                        device1.setIp(address);
                        device1.setConnected(true);
                        connected++;
                        break;
                    }
                }

                //device.setConnected(address.isReachable(1000));

            } catch (IOException e) {
                Log.i("TouchFit", "No response from a compatible devices, ignoring...");
            }

        }
        if(recyclerView != null)
        recyclerView.get().post(() -> Objects.requireNonNull(recyclerView.get().getAdapter()).notifyDataSetChanged());

        try {
            if(pingSocket != null)
            pingSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return connected;
    }

    @Override
    protected void onPostExecute(Integer aInteger) {
        super.onPostExecute(aInteger);
        if(mSwipeRefreshLayout != null) mSwipeRefreshLayout.get().setRefreshing(false);
    }
}
