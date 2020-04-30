package uqac.bigbrainstudio.touchfit.controllers.challenges;

import android.app.Activity;
import android.graphics.Color;
import uqac.bigbrainstudio.touchfit.controllers.devices.Device;
import uqac.bigbrainstudio.touchfit.controllers.devices.DevicesManager;
import uqac.bigbrainstudio.touchfit.ui.game.Game;
import uqac.bigbrainstudio.touchfit.ui.game.GameViewModel;

/**
 * TouchFit
 * Created by Julrouxxx.
 */
public class NoFailGame extends Game implements ChallengeGame{
    private Challenge challenge;
    public NoFailGame(int lightLeft, int switchSeconds, GameViewModel viewModel, Activity context, Challenge challenge) {
        super(switchSeconds, lightLeft, viewModel, context);
        this.challenge = challenge;
    }

    @Override
    public void onTimeOut(Device devices) {
        timerPerLight = System.currentTimeMillis() - timerPerLight;
        wrongSound.start();
        viewModel.getBackgroundColor().postValue(Color.RED);
        getTimerHandler().postDelayed(() -> viewModel.getBackgroundColor().setValue(DEFAULT_COLOR), 1000);
        stop();
    }


    @Override
    protected void stop() {
        timerHandler.removeCallbacks(timerRunnable);
        DevicesManager.getInstance().getDevices().forEach(Device::stopListening);
        if(lightActivated != lightLeft) {
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
