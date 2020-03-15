package uqac.bigbrainstudio.touchfit.ui.devices;

import android.os.AsyncTask;
import android.util.Log;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.*;
import java.util.Enumeration;
import java.util.Objects;

public class DevicesDataRunnable extends AsyncTask<Devices, Void, Void> {
    private WeakReference<RecyclerView> recyclerView;
    public DevicesDataRunnable(RecyclerView recyclerView){
        this.recyclerView = new WeakReference<>(recyclerView);
    }
    @Override
    protected Void doInBackground(Devices... devices) {
        InetAddress broadcast = null;
        InetAddress mySelf = null;
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
                    mySelf = interfaceAddress.getAddress();
                }
            }
        }catch(SocketException e){
            e.printStackTrace();
        }
        DatagramSocket client_socket = null;
        for(Devices device : devices)
            try {
                device.setConnected(false);
                client_socket = new DatagramSocket(3255);
                byte[] data = String.valueOf(device.getId()).getBytes();
                DatagramPacket send_packet = new DatagramPacket(data, data.length, broadcast, 3255);
                client_socket.send(send_packet);
                byte[] receivedData = new byte[2];
                DatagramPacket receivePacket = new DatagramPacket(receivedData, receivedData.length);
                client_socket.setSoTimeout(300);
                do {
                    client_socket.receive(receivePacket);
                }while(receivePacket.getAddress().equals(mySelf));

                if(new String(receivedData).equals("R.")) {
                    InetAddress address = receivePacket.getAddress();
                    device.setHostname(address.getHostName());
                    device.setIp(address);
                    device.setConnected(true);
                }

                //device.setConnected(address.isReachable(1000));

            } catch (IOException e) {
                Log.i("TouchFit", "No response from a compatible devices, ignoring...");
            }finally {
                assert client_socket != null;
                client_socket.close();
                recyclerView.get().post(() -> {
                    Objects.requireNonNull(recyclerView.get().getAdapter()).notifyItemChanged(device.getId());
                });
            }
        return null;
    }
}
