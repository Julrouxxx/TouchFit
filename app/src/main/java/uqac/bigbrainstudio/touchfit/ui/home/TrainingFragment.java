package uqac.bigbrainstudio.touchfit.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
        final TextView textView = root.findViewById(R.id.text_home);
        trainingViewModel.getText().observe(this, textView::setText);
        return root;
    }
}