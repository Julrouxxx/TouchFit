package uqac.bigbrainstudio.touchfit.ui.home;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import uqac.bigbrainstudio.touchfit.R;
import uqac.bigbrainstudio.touchfit.controllers.challenges.Challenge;
import uqac.bigbrainstudio.touchfit.controllers.challenges.ChallengesManager;

import java.util.ArrayList;
import java.util.Collections;

public class StreaksActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streaks_list);
        RecyclerView recyclerView = findViewById(R.id.streakRecyclerView);
        recyclerView.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<Challenge> mChallenges = new ArrayList<>(ChallengesManager.getInstance().getChallenges());
        Collections.reverse(mChallenges);
        mChallenges.removeIf(c -> !c.isSuccess());
        recyclerView.setAdapter(new MyStreaksRecyclerViewAdapter(mChallenges, mChallenges.get(mChallenges.size() -1).getDate()));
    }
}
