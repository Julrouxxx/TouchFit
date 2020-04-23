package uqac.bigbrainstudio.touchfit.controllers;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * TouchFit
 * Created by Julrouxxx.
 */
public class MultiplexDevices implements Runnable {

    private  ServerSocket serverSocket;
    public MultiplexDevices(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
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

        try {
            Socket client;
            while((client = serverSocket.accept()) != null){
                Devices device = DevicesManager.getInstance().getDevicesByIp(client.getInetAddress());
                if(device == null)
                    continue;

                device.setSocket(client);
                Log.i("TouchFit", "run: " + device.getIp() + " connected!");
            }
            serverSocket.close();
        } catch (IOException ignored) {

        }

    }
}
