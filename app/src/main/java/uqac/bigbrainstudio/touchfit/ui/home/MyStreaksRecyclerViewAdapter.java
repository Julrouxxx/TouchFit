package uqac.bigbrainstudio.touchfit.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import uqac.bigbrainstudio.touchfit.R;
import uqac.bigbrainstudio.touchfit.controllers.challenges.Challenge;
import uqac.bigbrainstudio.touchfit.controllers.challenges.ChallengeType;

import java.text.DateFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;

/**
 * TouchFit
 * Created by Julrouxxx.
 */
public class MyStreaksRecyclerViewAdapter extends RecyclerView.Adapter<MyStreaksRecyclerViewAdapter.ViewHolder> {
    ArrayList<Challenge> mChallenges;
    Date dateStarded;
    public MyStreaksRecyclerViewAdapter(ArrayList<Challenge> mChallenges, Date dateStarded) {
        this.mChallenges = mChallenges;
        this.dateStarded = dateStarded;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_streaks, parent, false);

        return new MyStreaksRecyclerViewAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mIdView.setText(holder.mView.getContext().getString(R.string.day_nbr,
                ChronoUnit.DAYS.between(dateStarded.toInstant(), mChallenges.get(position).getDate().toInstant()) + 1));
        holder.mDateView.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(mChallenges.get(position).getDate()));
        if(mChallenges.get(position).getChallengeType() == ChallengeType.NO_FAILS)
            holder.mDescView.setText(holder.mView.getContext().getString(R.string.no_fails_challenge, mChallenges.get(position).getGameParameters().get(1), mChallenges.get(position).getGameParameters().get(0)));
        else
            holder.mDescView.setText(holder.mView.getContext().getString(R.string.lights_seconds_challenge, mChallenges.get(position).getGameParameters().get(0), mChallenges.get(position).getGameParameters().get(1)));
    }


    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return mChallenges.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mDateView;
        public final TextView mDescView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.nbrStreak);
            mDateView = view.findViewById(R.id.dateStreak);
            mDescView = view.findViewById(R.id.descChallenge);
        }
    }
}
