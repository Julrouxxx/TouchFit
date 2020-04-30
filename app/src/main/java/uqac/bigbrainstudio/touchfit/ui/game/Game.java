package uqac.bigbrainstudio.touchfit.ui.game;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import uqac.bigbrainstudio.touchfit.R;
import uqac.bigbrainstudio.touchfit.controllers.devices.Device;
import uqac.bigbrainstudio.touchfit.controllers.devices.DeviceListener;
import uqac.bigbrainstudio.touchfit.controllers.devices.DevicesManager;
import uqac.bigbrainstudio.touchfit.controllers.stats.Statistic;
import uqac.bigbrainstudio.touchfit.controllers.stats.StatisticsManager;

import java.util.*;
import java.util.stream.LongStream;

/**
 * TouchFit
 * Created by Julrouxxx.
 */
public class Game implements DeviceListener {

    public static final int DEFAULT_COLOR = Color.parseColor("#0099cc");
    protected final MediaPlayer goodSound;
    protected final MediaPlayer wrongSound;
    protected final Activity context;
    protected final Handler timerHandler = new Handler();
    protected long timerPerLight;
    protected List<Long> average;
    protected int lightLeft;
    protected int switchSeconds;
    protected int lightActivated;
    protected int lightTotal;
    protected ArrayList<Device> devices;
    protected GameViewModel viewModel;
    protected long start = 0;
    protected long timing;

    protected final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            timing = System.currentTimeMillis() - start;
            int millis = (int) (timing % 1000)/10;
            int seconds = (int) (timing / 1000);
            int minutes = seconds/ 60;
            seconds = seconds % 60;
            viewModel.getTimer().setValue(String.format(Locale.US, "%02d:%02d:%02d", minutes, seconds, millis));
            timerHandler.postDelayed(this, 50);
        }
    };
    public Game(int lightLeft, int switchSeconds, GameViewModel viewModel, Activity context) {
        this.lightLeft = lightLeft;
        this.switchSeconds = switchSeconds;
        this.viewModel = viewModel;
        this.goodSound = MediaPlayer.create(context, R.raw.correct);
        this.wrongSound = MediaPlayer.create(context, R.raw.wrong);
        this.context = context;
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

    }

    @Override
    public void onTimeOut(Device devices) {
        timerPerLight = System.currentTimeMillis() - timerPerLight;
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
                if(DevicesManager.getInstance().getDevicesConnected().size() -1 == 0){
                    devices.add(DevicesManager.getInstance().getDevicesConnected().get(0));
                    continue;
                }
                devices.add(devices.get(rdm.nextInt(DevicesManager.getInstance().getDevices().size() -1)));
            }else{
                devices.remove(rdm.nextInt(DevicesManager.getInstance().getDevices().size() -1));
            }
        }
        Collections.shuffle(devices);
    }

    protected Device lightOn(){
        Device device = devices.remove(0);
        device.turnOn(switchSeconds);
        timerPerLight = System.currentTimeMillis();
        viewModel.getLightLeft().postValue(this.devices.size());
        return device;
    }

    private void checkState(){
        average.add(timerPerLight);
        long[] longs = new long[average.size()];
        for(int i = 0; i < average.size(); i++) longs[i] = average.get(i);
        //noinspection OptionalGetWithoutIsPresent
        double avg = LongStream.of(longs).average().getAsDouble();

        viewModel.getAverage().postValue(avg/1000);
        lightTotal++;
        viewModel.getAccuracy().postValue(Math.floorDiv(lightActivated * 100, lightTotal));

        if(devices.isEmpty()){
            stop();
            return;
        }
        timerHandler.postDelayed(this::lightOn, 1000);
    }

    protected void stop(){
        timerHandler.removeCallbacks(timerRunnable);
        DevicesManager.getInstance().getDevices().forEach(Device::stopListening);
        Statistic statistic = new Statistic(switchSeconds, lightActivated, lightTotal, average, timing);
        String key = StatisticsManager.getInstance().addStatistics(statistic);
        timerHandler.postDelayed(()->{
            Intent intent = new Intent(context, FinishActivity.class);
            intent.putExtra("stats", key);
            context.startActivity(intent);
            context.finish();
        }, 3000);
    }
}
