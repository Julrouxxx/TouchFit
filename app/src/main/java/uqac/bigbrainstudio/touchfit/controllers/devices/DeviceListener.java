package uqac.bigbrainstudio.touchfit.controllers.devices;

/**
 * TouchFit
 * Created by Julrouxxx.
 */
public interface DeviceListener {
    void onButtonPush(Device devices);
    void onTimeOut(Device devices);
}
