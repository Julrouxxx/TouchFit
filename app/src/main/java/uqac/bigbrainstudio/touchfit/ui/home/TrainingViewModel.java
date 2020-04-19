package uqac.bigbrainstudio.touchfit.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TrainingViewModel extends ViewModel {

    private MutableLiveData<Integer> mSeconds;
    private MutableLiveData<Integer> mLights;

    public TrainingViewModel() {
        mSeconds = new MutableLiveData<>();
        mLights = new MutableLiveData<>();
    }

    public LiveData<Integer> getLights() {
        return mLights;
    }

    public LiveData<Integer> getSeconds() {
        return mSeconds;
    }
}