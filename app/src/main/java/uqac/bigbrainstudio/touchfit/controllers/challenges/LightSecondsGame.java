package uqac.bigbrainstudio.touchfit.controllers.challenges;

import android.app.Activity;
import uqac.bigbrainstudio.touchfit.controllers.devices.Device;
import uqac.bigbrainstudio.touchfit.controllers.devices.DevicesManager;
import uqac.bigbrainstudio.touchfit.ui.game.Game;
import uqac.bigbrainstudio.touchfit.ui.game.GameViewModel;

import java.util.Locale;

/**
 * TouchFit
 * Created by Julrouxxx.
 */
public class LightSecondsGame extends Game implements ChallengeGame {
    private int timeMax;
    private long stop;
    private Challenge challenge;
    private Runnable timerRunnable = new Runnable(){

        @Override
        public void run() {

            timing = System.currentTimeMillis() - stop;
            if(timing >= 0) {
                stop();
                return;
            }
            timing = Math.abs(timing);
            int millis = (int) (timing % 1000)/10;
            int seconds = (int) (timing / 1000);
            int minutes = seconds/ 60;
            seconds = seconds % 60;
            viewModel.getTimer().setValue(String.format(Locale.US, "%02d:%02d:%02d", minutes, seconds, millis));
            timerHandler.postDelayed(this, 50);
        }
    };

    public LightSecondsGame(int timeMax, int lights, GameViewModel viewModel, Activity context, Challenge challenge) {
        super(lights, timeMax, viewModel, context);
        this.timeMax =timeMax;
        this.challenge = challenge;
    }

    public int getTimeMax() {
        return timeMax;
    }

    @Override
    public void start() {
        start = System.currentTimeMillis();
        stop = start + timeMax * 1000;
        timerHandler.post(timerRunnable);
        devices.forEach(d -> d.startListening(this));

        lightOn();
    }

    @Override
    protected void stop() {
        timerHandler.removeCallbacks(timerRunnable);
        DevicesManager.getInstance().getDevices().forEach(Device::stopListening);
        if(lightActivated != lightLeft){
            challenge.fail();
            ChallengesManager.getInstance().update(challenge);
            timerHandler.postDelayed(context::finish, 1000);

            return;
        }
        challenge.pass();
        ChallengesManager.getInstance().update(challenge);
        timerHandler.postDelayed(context::finish, 1000);

    }

    @Override
    public Challenge getChallenge() {
        return challenge;
    }
}
