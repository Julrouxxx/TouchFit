package uqac.bigbrainstudio.touchfit.ui.devices;

import java.io.*;
import java.net.Socket;

/**
 * TouchFit
 * Created by Julrouxxx.
 */
public class DeviceButtonThread implements Runnable {

    private final Socket socket;
    private final DeviceListener deviceListener;
    private final Devices device;

    public DeviceButtonThread(Socket socket, DeviceListener deviceListener, Devices device) {
        this.socket = socket;
        this.deviceListener = deviceListener;
        this.device = device;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        while(socket.isConnected())
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String read = input.readLine();
            if(read == null){
                return;
            }
            if(read.equals("b")){
                deviceListener.onButtonPush(device);
                continue;
            }
            if(read.equals("t")){
                deviceListener.onTimeOut(device);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
