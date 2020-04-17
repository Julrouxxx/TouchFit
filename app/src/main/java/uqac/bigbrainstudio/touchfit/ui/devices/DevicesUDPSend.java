package uqac.bigbrainstudio.touchfit.ui.devices;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class DevicesUDPSend extends Thread {
    private Devices device;
    private String request;

    public DevicesUDPSend(Devices device, String request) {
        this.device = device;
        this.request = request;
    }

    @Override
    public void run() {
        try {
            DatagramSocket client_socket = new DatagramSocket(3245);
            byte[] data = request.getBytes();
            DatagramPacket send_packet = new DatagramPacket(data, data.length, device.getIp(), 3245);
            client_socket.send(send_packet);

            client_socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
