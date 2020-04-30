package uqac.bigbrainstudio.touchfit.ui.home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.transition.TransitionManager;
import uqac.bigbrainstudio.touchfit.R;
import uqac.bigbrainstudio.touchfit.controllers.challenges.Challenge;
import uqac.bigbrainstudio.touchfit.controllers.challenges.ChallengeType;
import uqac.bigbrainstudio.touchfit.controllers.challenges.ChallengesManager;
import uqac.bigbrainstudio.touchfit.controllers.devices.Device;
import uqac.bigbrainstudio.touchfit.controllers.devices.DevicesDataRunnable;
import uqac.bigbrainstudio.touchfit.controllers.devices.DevicesManager;
import uqac.bigbrainstudio.touchfit.ui.game.GameActivity;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class TrainingFragment extends Fragment {

    ProgressBar progressBar;
    LinearLayout linearLayout;
    CardView cardTraining;
    CardView cardChallenge;
    HandlerThread handlerThread;
    ViewGroup.LayoutParams layoutParams;
    EditText lightSeconds;
    EditText numberLight;
    TextView challengesToday;
    TextView successOrTries;
    Button goChallenge;
    Challenge challenge;
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        ChallengesManager.getInstance().setup(this);
        super.onActivityCreated(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_training, container, false);



        //TRAINING
        lightSeconds = root.findViewById(R.id.numberSecondsLight);
        numberLight = root.findViewById(R.id.numberEachLight);
        progressBar = root.findViewById(R.id.progressBar);
        linearLayout = root.findViewById(R.id.layout_training);
        cardTraining = root.findViewById(R.id.cardTraining);
        cardChallenge = root.findViewById(R.id.cardChallenge);
        Button button = root.findViewById(R.id.start_button);
        lightSeconds.setFilters(new InputFilter[]{new InputFilterMinMax(1, 59)});
        numberLight.setFilters(new InputFilter[]{new InputFilterMinMax(1, 99)});
        button.setOnClickListener(this::onClickTraining);


        //CHALLENGES
        challengesToday= root.findViewById(R.id.text_challenge);
        successOrTries = root.findViewById(R.id.text_challengeTriesOrSucces);
        goChallenge = root.findViewById(R.id.start_c_button);
        if(ChallengesManager.getInstance().getChallenges() != null && !ChallengesManager.getInstance().getChallenges().isEmpty())
            showDailyChallenge();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(ChallengesManager.getInstance().getChallenges() != null && !ChallengesManager.getInstance().getChallenges().isEmpty())
            showDailyChallenge();
    }

    private void onClickChallenge(View l){
        if(challenge == null) return;
        requireActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        TransitionManager.beginDelayedTransition(linearLayout);

        progressBar.setVisibility(View.VISIBLE);
        cardTraining.setVisibility(View.GONE);
        linearLayout.setGravity(Gravity.CENTER);
        handlerThread = new HandlerThread("OnCheckBeforeGame");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());

        handler.post(() -> {
            if (fadeCard(cardChallenge)) return;


            Intent intent = new Intent(getContext(), GameActivity.class);
            intent.putExtra("challenge", true);
            startActivity(intent);

        });

    }

    private boolean fadeCard(CardView card) {
        try {
            int connected = new DevicesDataRunnable(progressBar).execute(DevicesManager.getInstance().getDevices().toArray(new Device[0])).get();
            if (connected == 0) {
                requireActivity().runOnUiThread(this::onStop);
                Toast.makeText(getContext(), R.string.no_device, Toast.LENGTH_LONG).show();

                return true;
            }
            requireActivity().runOnUiThread(() -> {
                TransitionManager.beginDelayedTransition(linearLayout);
                layoutParams = card.getLayoutParams();
                card.setLayoutParams(new LinearLayout.LayoutParams(5000, 5000));
            });
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void onClickTraining(View l) {
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
        requireActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        InputMethodManager imm = requireActivity().getSystemService(InputMethodManager.class);
        assert imm != null;
        if(requireActivity().getCurrentFocus() != null)
        imm.hideSoftInputFromWindow(Objects.requireNonNull(requireActivity().getCurrentFocus()).getWindowToken(), 0);
        TransitionManager.beginDelayedTransition(linearLayout);

        progressBar.setVisibility(View.VISIBLE);
        cardChallenge.setVisibility(View.GONE);
        linearLayout.setGravity(Gravity.CENTER);

        Intent intent = new Intent(getContext(), GameActivity.class);
        intent.putExtra("seconds", Integer.parseInt(lightSeconds.getText().toString()));
        intent.putExtra("lights",  Integer.parseInt(numberLight.getText().toString()));
        handlerThread = new HandlerThread("OnCheckBeforeGame");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());

        handler.post(() -> {
            if (fadeCard(cardTraining)) return;


            startActivity(intent);

        });
    }

    @Override
    public void onStop() {
        super.onStop();
        requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        if(layoutParams != null) {
            cardTraining.setLayoutParams(layoutParams);
            cardChallenge.setLayoutParams(layoutParams);
        }
        layoutParams = null;
        TransitionManager.beginDelayedTransition(linearLayout);
        progressBar.setVisibility(View.GONE);
        cardChallenge.setVisibility(View.VISIBLE);
        cardTraining.setVisibility(View.VISIBLE);
        linearLayout.setGravity(Gravity.NO_GRAVITY);
        progressBar.setProgress(0);

    }


    public void showDailyChallenge() {
        challenge = ChallengesManager.getInstance().getTodayChallenge();
        if(challenge.getChallengeType() == ChallengeType.NO_FAILS){
            challengesToday.setText(getString(R.string.no_fails_challenge, challenge.getGameParameters().get(1), challenge.getGameParameters().get(0)));
        }else if(challenge.getChallengeType() == ChallengeType.LIGHT_SECONDS){
            challengesToday.setText(getString(R.string.lights_seconds_challenge, challenge.getGameParameters().get(0), challenge.getGameParameters().get(1)));
        }
        if(challenge.isSuccess()){
            successOrTries.setText(R.string.success);
            successOrTries.setTextColor(Color.GREEN);
            goChallenge.setEnabled(false);
        }else{
            if(challenge.getTries() > 0){
                successOrTries.setText(getString(R.string.Tries_so_far, challenge.getTries()));
                successOrTries.setTextColor(Color.RED);
            }else{
                successOrTries.setText(R.string.never_tester);
                successOrTries.setTextColor(Color.GRAY);
            }
        }
        goChallenge.setOnClickListener(this::onClickChallenge);
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