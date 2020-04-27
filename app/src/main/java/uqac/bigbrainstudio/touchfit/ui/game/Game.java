package uqac.bigbrainstudio.touchfit.ui.game;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import uqac.bigbrainstudio.touchfit.R;
import uqac.bigbrainstudio.touchfit.controllers.Device;
import uqac.bigbrainstudio.touchfit.controllers.DeviceListener;
import uqac.bigbrainstudio.touchfit.controllers.DevicesManager;

import java.util.*;
import java.util.stream.LongStream;

/**
 * TouchFit
 * Created by Julrouxxx.
 */
public class Game implements DeviceListener {

    public static final int DEFAULT_COLOR = Color.parseColor("#0099cc");
    private final MediaPlayer goodSound;
    private final MediaPlayer wrongSound;
    private final Handler timerHandler = new Handler();
    private long timerPerLight;
    private List<Long> average;
    private int lightLeft;
    private int switchSeconds;
    private int lightActivated;
    private int lightTotal;
    private ArrayList<Device> devices;
    private GameViewModel viewModel;
    private long start = 0;
    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long timing = System.currentTimeMillis() - start;
            int millis = (int) (timing % 1000)/10;
            int seconds = (int) (timing / 1000);
            int minutes = seconds/ 60;
            seconds = seconds % 60;

            viewModel.getTimer().setValue(String.format(Locale.US, "%02d:%02d:%02d", minutes, seconds, millis));
            timerHandler.postDelayed(this, 50);
        }
    };
    public Game(int lightLeft, int switchSeconds, GameViewModel viewModel, Context context) {
        this.lightLeft = lightLeft;
        this.switchSeconds = switchSeconds;
        this.viewModel = viewModel;
        this.goodSound = MediaPlayer.create(context, R.raw.correct);
        this.wrongSound = MediaPlayer.create(context, R.raw.wrong);
        this.average = new ArrayList<>();
        viewModel.getLightLeft().setValue(lightLeft);
        devices = DevicesManager.getInstance().getDevicesConnected();
        shuffleAndList();
    }

    public int getLightActivated() {
        return lightActivated;
    }

    public ArrayList<Device> getDevices() {
        return devices;
    }

    public Handler getTimerHandler() {
        return timerHandler;
    }

    public Runnable getTimerRunnable() {
        return timerRunnable;
    }

    public int getSwitchSeconds() {
        return switchSeconds;
    }

    public int getLightLeft() {
        return lightLeft;
    }

    public void start(){
        start = System.currentTimeMillis();
        timerHandler.post(timerRunnable);
        devices.forEach(d -> d.startListening(this));

        lightOn();
    }

    @Override
    public void onButtonPush(Device devices) {
        timerPerLight = System.currentTimeMillis() - timerPerLight;
        viewModel.getBackgroundColor().postValue(Color.GREEN);
        goodSound.start();
        getTimerHandler().postDelayed(() -> viewModel.getBackgroundColor().setValue(DEFAULT_COLOR), 1000);
        viewModel.getLightActived().postValue(++lightActivated);
        checkState();
        average.add(timerPerLight);
        long[] longs = new long[average.size()];
        for(int i = 0; i < average.size(); i++) longs[i] = average.get(i);
        double avg = LongStream.of(longs).average().getAsDouble();

        viewModel.getAverage().postValue(avg/1000);
    }

    @Override
    public void onTimeOut(Device devices) {
        wrongSound.start();
        viewModel.getBackgroundColor().postValue(Color.RED);
        getTimerHandler().postDelayed(() -> viewModel.getBackgroundColor().setValue(DEFAULT_COLOR), 1000);
        checkState();
    }

    private void shuffleAndList(){
        boolean add = devices.size() < lightLeft;
        Random rdm = new Random();
        while(devices.size() != lightLeft){
            if(add){
                devices.add(devices.get(rdm.nextInt(DevicesManager.getInstance().getDevices().size() -1)));
            }else{
                devices.remove(rdm.nextInt(DevicesManager.getInstance().getDevices().size() -1));
            }
        }
        Collections.shuffle(devices);
    }

    private Device lightOn(){
        Device device = devices.remove(0);
        device.turnOn(switchSeconds);
        timerPerLight = System.currentTimeMillis();
        viewModel.getLightLeft().postValue(this.devices.size());
        return device;
    }

    private void checkState(){
        lightTotal++;
        viewModel.getAccuracy().postValue(Math.floorDiv(lightActivated * 100, lightTotal));

        if(devices.isEmpty()){
            stop();
            return;
        }
        timerHandler.postDelayed(this::lightOn, 1000);
    }

    private void stop(){
        //TODO: Finish Intent goes here
        timerHandler.removeCallbacks(timerRunnable);
        DevicesManager.getInstance().getDevices().forEach(Device::stopListening);

    }
}
