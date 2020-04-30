package uqac.bigbrainstudio.touchfit.ui.game;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import uqac.bigbrainstudio.touchfit.R;
import uqac.bigbrainstudio.touchfit.controllers.challenges.ChallengesManager;
import uqac.bigbrainstudio.touchfit.controllers.devices.Device;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class GameActivity extends AppCompatActivity {

    /**
     * Time before game starts in milliseconds
     */
    private final int DELAY_BEFORE = 5000;

    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private GameViewModel viewModelProvider;
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    @SuppressLint("ClickableViewAccessibility")
    private final View.OnTouchListener mDelayHideTouchListener = (view, motionEvent) -> {
        if (AUTO_HIDE) {
            delayedHide(AUTO_HIDE_DELAY_MILLIS);
        }
        return false;
    };
    private TextView mContentDecompte;
    private ConstraintLayout mGameContent;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mGameContent.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private Game game;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = this::hide;
    private final CountDownTimer mCountdownRunnable = new CountDownTimer(DELAY_BEFORE, 1000) {
        /**
         * Callback fired on regular interval.
         *
         * @param millisUntilFinished The amount of time until finished.
         */
        @Override
        public void onTick(long millisUntilFinished) {
            mContentDecompte.setText(String.valueOf(millisUntilFinished/1000));
        }

        /**
         * Callback fired when the time is up.
         */
        @Override
        public void onFinish() {
            mContentDecompte.setVisibility(View.GONE);
            mGameContent.setVisibility(View.VISIBLE);
            game.start();
            /*start = System.currentTimeMillis();
            timerHandler.post(timerRunnable);*/
        }


    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        viewModelProvider = new ViewModelProvider(this).get(GameViewModel.class);
        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentDecompte = findViewById(R.id.fullscreen_decompte);
        mGameContent = findViewById(R.id.gameLayout);
        FrameLayout frameLayout = findViewById(R.id.frameLayoutGame);
        mCountdownRunnable.start();
        // Set up the user interaction to manually show or hide the system UI.
        mGameContent.setOnClickListener(view -> toggle());



        TextView lightLeft = mGameContent.findViewById(R.id.lightDevicesLeft);
        TextView lightActivated = mGameContent.findViewById(R.id.gameLightDevicesActivated);
        TextView average = mGameContent.findViewById(R.id.averageScore);
        TextView timer = mGameContent.findViewById(R.id.timerTextView);
        TextView accuracy = mGameContent.findViewById(R.id.accuracy);
        viewModelProvider.getAverage().observe(this, l -> average.setText(getString(R.string.averageData, l)));
        viewModelProvider.getLightActived().observe(this, l -> lightActivated.setText(String.valueOf(l)));
        viewModelProvider.getLightLeft().observe(this, l -> lightLeft.setText(String.valueOf(l)));
        viewModelProvider.getTimer().observe(this, timer::setText);
        viewModelProvider.getBackgroundColor().observe(this, frameLayout::setBackgroundColor);
        viewModelProvider.getAccuracy().observe(this, l -> accuracy.setText(getString(R.string.accuracy, l)));
        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.

        Intent intent = getIntent();
        if(intent.getBooleanExtra("challenge", false)) game = ChallengesManager.getInstance().getTodayChallenge().getGame(viewModelProvider, this);
        else game = new Game(intent.getIntExtra("lights", 10), intent.getIntExtra("seconds", 5), viewModelProvider, this);

        findViewById(R.id.stop_button).setOnTouchListener(mDelayHideTouchListener);
        findViewById(R.id.stop_button).setOnClickListener(v -> finish());
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(DELAY_BEFORE);
    }

    @Override
    public void onBackPressed() {

    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        game.getTimerHandler().removeCallbacks(game.getTimerRunnable());
        game.getDevices().forEach(Device::stopListening);
        mCountdownRunnable.cancel();
    }
    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mGameContent.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
