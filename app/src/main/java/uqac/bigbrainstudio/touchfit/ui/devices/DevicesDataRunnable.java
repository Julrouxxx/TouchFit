package uqac.bigbrainstudio.touchfit.ui.devices;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.snackbar.Snackbar;
import uqac.bigbrainstudio.touchfit.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.*;
import java.util.Enumeration;
import java.util.Objects;
import java.util.UUID;

public class DevicesDataRunnable extends AsyncTask<Devices, Void, Void> {
    private WeakReference<RecyclerView> recyclerView;
    private WeakReference<SwipeRefreshLayout> mSwipeRefreshLayout;
    public DevicesDataRunnable(RecyclerView recyclerView){
        this.recyclerView = new WeakReference<>(recyclerView);
    }

    public DevicesDataRunnable(RecyclerView recyclerView, SwipeRefreshLayout mSwipeRefreshLayout) {
        this.recyclerView = new WeakReference<>(recyclerView);
        this.mSwipeRefreshLayout = new WeakReference<>(mSwipeRefreshLayout);
    }

    @Override
    protected Void doInBackground(Devices... devices) {
        InetAddress broadcast = null;
        Socket client;
        DatagramSocket client_socket;
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
            Snackbar.make(recyclerView.get(), R.string.connect_to_wifi, Snackbar.LENGTH_INDEFINITE).setAction(R.string.activate_wifi, v -> {
                WifiManager wifiManager = (WifiManager) recyclerView.get().getContext().getSystemService(Context.WIFI_SERVICE);
                assert wifiManager != null;
                wifiManager.setWifiEnabled(true);
            }).show();
            return null;
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
        for(Devices device : devices)
            device.setConnected(false);
        for (Devices device : devices) {
            try {
                client = pingSocket.accept();
                client.setSoTimeout(100);
                BufferedReader inFromClient =
                        new BufferedReader(new InputStreamReader(client.getInputStream()));
                char[] buffer = new char[36];
                int r = inFromClient.read(buffer, 0, 36);
                StringBuilder stringBuilder = new StringBuilder();
                for (char c : buffer) {
                    stringBuilder.append(c);
                }
                client.close();
                UUID ident = UUID.fromString(stringBuilder.toString());
                for (Devices device1 : devices) {
                    if (ident.equals(device1.getUuid())) {
                        InetAddress address = client.getInetAddress();
                        device1.setHostname(address.getHostName());
                        device1.setIp(address);
                        device1.setConnected(true);
                        break;
                    }
                }

                //device.setConnected(address.isReachable(1000));

            } catch (IOException e) {
                Log.i("TouchFit", "No response from a compatible devices, ignoring...");
            }finally {
                recyclerView.get().post(() -> Objects.requireNonNull(recyclerView.get().getAdapter()).notifyItemChanged(device.getPosition()));
            }

        }
        try {
            pingSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(mSwipeRefreshLayout != null) mSwipeRefreshLayout.get().setRefreshing(false);
    }
}
