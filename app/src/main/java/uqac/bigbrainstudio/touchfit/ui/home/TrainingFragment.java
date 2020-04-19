package uqac.bigbrainstudio.touchfit.ui.home;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import uqac.bigbrainstudio.touchfit.R;

public class TrainingFragment extends Fragment {

    private TrainingViewModel trainingViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        trainingViewModel =
                ViewModelProviders.of(this).get(TrainingViewModel.class);
        View root = inflater.inflate(R.layout.fragment_training, container, false);
        EditText lightSeconds = root.findViewById(R.id.numberSecondsLight);
        EditText numberLight = root.findViewById(R.id.numberEachLight);
        lightSeconds.setFilters(new InputFilter[]{new InputFilterMinMax(1, 30)});
        numberLight.setFilters(new InputFilter[]{new InputFilterMinMax(1, 20)});
        trainingViewModel.getSeconds().observe(this, l -> lightSeconds.setText(String.valueOf(l)));
        trainingViewModel.getLights().observe(this, l -> numberLight.setText(String.valueOf(l)));
        return root;
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