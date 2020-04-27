package uqac.bigbrainstudio.touchfit.controllers.devices;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * TouchFit
 * Created by Julrouxxx.
 */
public class DeviceButtonThread implements Runnable {

    private final Socket socket;
    private final DeviceListener deviceListener;
    private final Device device;

    public DeviceButtonThread(Socket socket, DeviceListener deviceListener, Device device) {
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
        while(!socket.isClosed())
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String read = input.readLine();
            if(read == null){
                return;
            }
            if(read.equals("b")){
                device.setOn(false);
                deviceListener.onButtonPush(device);
                continue;
            }
            if(read.equals("t")){
                device.setOn(false);
                deviceListener.onTimeOut(device);

            }
        } catch (IOException e) {
            return;
        }
    }
}
