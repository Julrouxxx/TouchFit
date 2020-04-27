package uqac.bigbrainstudio.touchfit.ui.game;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Locale;

/**
 * TouchFit
 * Created by Julrouxxx.
 */
public class GameViewModel extends ViewModel {
    private MutableLiveData<Integer> lightActived;
    private MutableLiveData<Double> average;
    private MutableLiveData<Integer> accuracy;
    private MutableLiveData<Integer> lightLeft;
    private MutableLiveData<String> timer;
    private MutableLiveData<Integer> backgroundColor;

    public GameViewModel(){
        this.timer = new MutableLiveData<>(String.format(Locale.US, "%02d:%02d:%02d", 0, 0, 0));
        this.lightActived = new MutableLiveData<>(0);
        this.average = new MutableLiveData<>(0.0);
        this.lightLeft = new MutableLiveData<>(0);
        this.accuracy = new MutableLiveData<>(0);
        this.backgroundColor = new MutableLiveData<>(Game.DEFAULT_COLOR);
    }

    public MutableLiveData<Double> getAverage() {
        return average;
    }

    public MutableLiveData<Integer> getLightActived() {
        return lightActived;
    }

    public MutableLiveData<Integer> getLightLeft() {
        return lightLeft;
    }

    public MutableLiveData<Integer> getBackgroundColor() {
        return backgroundColor;
    }

    public MutableLiveData<String> getTimer() {
        return timer;
    }

    public MutableLiveData<Integer> getAccuracy() {
        return accuracy;
    }
}
