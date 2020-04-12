package uqac.bigbrainstudio.touchfit.ui.devices;

import android.app.Activity;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class DevicesUDPReceive extends Thread{

    private Activity activity;
    public DevicesUDPReceive(Activity activity){
        this.activity = activity;
    }
    @Override
    public void run() {
        try {

            DatagramSocket client_socket = new DatagramSocket(3250);
            byte[] receiveData = new byte[12];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            client_socket.receive(receivePacket);
            String modifiedSentence = new String(receivePacket.getData());
            int id = DevicesManager.getInstance().getDevicesByIp(InetAddress.getByName(modifiedSentence)).getId();
            activity.runOnUiThread(() -> Toast.makeText(activity, "ID: " + id, Toast.LENGTH_LONG).show());
            client_socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }    }

}
