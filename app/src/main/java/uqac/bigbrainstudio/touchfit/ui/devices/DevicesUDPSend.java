package uqac.bigbrainstudio.touchfit.ui.devices;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class DevicesUDPSend extends Thread {
    private Devices device;
    public DevicesUDPSend(Devices device){
    this.device = device;

    }
    @Override
    public void run() {
        try {
            DatagramSocket client_socket = new DatagramSocket(3245);
            byte[] data = {1};
            DatagramPacket send_packet = new DatagramPacket(data, 1, device.getIp(), 3245);
            client_socket.send(send_packet);

            client_socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
