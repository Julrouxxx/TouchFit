package uqac.bigbrainstudio.touchfit.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.transition.TransitionManager;
import uqac.bigbrainstudio.touchfit.R;
import uqac.bigbrainstudio.touchfit.controllers.Devices;
import uqac.bigbrainstudio.touchfit.controllers.DevicesDataRunnable;
import uqac.bigbrainstudio.touchfit.controllers.DevicesManager;
import uqac.bigbrainstudio.touchfit.ui.game.GameActivity;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class TrainingFragment extends Fragment {

    private TrainingViewModel trainingViewModel;
    ProgressBar progressBar;
    LinearLayout linearLayout;
    CardView cardTraining;
    CardView cardChallenge;
    HandlerThread handlerThread;
    ViewGroup.LayoutParams layoutParams;
    EditText lightSeconds;
    EditText numberLight;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        trainingViewModel =
                ViewModelProviders.of(this).get(TrainingViewModel.class);
        View root = inflater.inflate(R.layout.fragment_training, container, false);
        lightSeconds = root.findViewById(R.id.numberSecondsLight);
        numberLight = root.findViewById(R.id.numberEachLight);
        progressBar = root.findViewById(R.id.progressBar);
        linearLayout = root.findViewById(R.id.layout_training);
        cardTraining = root.findViewById(R.id.cardTraining);
        cardChallenge = root.findViewById(R.id.cardChallenge);
        Button button = root.findViewById(R.id.start_button);
        lightSeconds.setFilters(new InputFilter[]{new InputFilterMinMax(1, 30)});
        numberLight.setFilters(new InputFilter[]{new InputFilterMinMax(1, 20)});
        trainingViewModel.getSeconds().observe(getViewLifecycleOwner(), l -> lightSeconds.setText(String.valueOf(l)));
        trainingViewModel.getLights().observe(getViewLifecycleOwner(), l -> numberLight.setText(String.valueOf(l)));
        button.setOnClickListener(this::onClick);
        return root;
    }

    private void onClick(View l) {
        boolean error = false;
        if(lightSeconds.getText().toString().isEmpty()){
            lightSeconds.setError(getString(R.string.please_provide));
            error =true;
        }
        if(numberLight.getText().toString().isEmpty()) {
            numberLight.setError(getString(R.string.please_provide));
            error =true;
        }
        if(error)
            return;
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(Objects.requireNonNull(requireActivity().getCurrentFocus()).getWindowToken(), 0);
        TransitionManager.beginDelayedTransition(linearLayout);

        progressBar.setVisibility(View.VISIBLE);
        cardChallenge.setVisibility(View.GONE);
        linearLayout.setGravity(Gravity.CENTER);

        Intent intent = new Intent(getContext(), GameActivity.class);
        intent.putExtra("seconds", trainingViewModel.getSeconds().getValue());
        intent.putExtra("lights", trainingViewModel.getLights().getValue());
        handlerThread = new HandlerThread("OnCheckBeforeGame");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());

        handler.post(() -> {
            try {
                int connected = new DevicesDataRunnable(progressBar).execute(DevicesManager.getInstance().getDevices().toArray(new Devices[0])).get();
                if(connected == 0){
                    requireActivity().runOnUiThread(this::onStop);
                    Toast.makeText(getContext(), R.string.no_device, Toast.LENGTH_LONG).show();

                    return;
                }
                requireActivity().runOnUiThread(() -> {
                    TransitionManager.beginDelayedTransition(linearLayout);
                    layoutParams = cardTraining.getLayoutParams();
                    cardTraining.setLayoutParams(new LinearLayout.LayoutParams(5000, 5000));
                });
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }


            startActivity(intent);

        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if(layoutParams != null)
        cardTraining.setLayoutParams(layoutParams);
        TransitionManager.beginDelayedTransition(linearLayout);
        progressBar.setVisibility(View.GONE);
        cardChallenge.setVisibility(View.VISIBLE);
        linearLayout.setGravity(Gravity.NO_GRAVITY);
        progressBar.setProgress(0);

    }



    private static class InputFilterMinMax implements InputFilter {
        private final int min;
        private final int max;

        public InputFilterMinMax(int min, int max) {
            this.min = min;
            this.max = max;
        }
        private boolean isInRange(int a, int b, int c) {
            return b > a ? c >= a && c <= b : c >= b && c <= a;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            try {
                int input = Integer.parseInt(dest.toString() + source.toString());
                if (isInRange(min, max, input))
                    return null;
            } catch (NumberFormatException ignored) { }
            return "";
        }
    }
}