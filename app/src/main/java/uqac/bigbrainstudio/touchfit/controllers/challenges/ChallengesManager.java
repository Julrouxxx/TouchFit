package uqac.bigbrainstudio.touchfit.controllers.challenges;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import uqac.bigbrainstudio.touchfit.ui.home.TrainingFragment;

import java.util.*;

/**
 * TouchFit
 * Created by Julrouxxx.
 */
public class ChallengesManager {
    public static ChallengesManager instance = new ChallengesManager();
    private DatabaseReference mData;
    private ArrayList<Challenge> challenges;
    private boolean first = true;
    public static ChallengesManager getInstance() {
        return instance;
    }

    public void  setup(TrainingFragment trainingFragment){
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        assert mUser != null;
        this.mData = FirebaseDatabase.getInstance().getReference(mUser.getUid() + "/challenges");
        this.challenges = new ArrayList<>();
        mData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                challenges.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Challenge challenge = snapshot.getValue(Challenge.class);
                    assert challenge != null;
                    challenge.setKey(snapshot.getKey());
                    challenges.add(challenge);
                }
                if(first) {
                    first = false;
                    trainingFragment.showDailyChallenge();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("TouchFit", "Error on reading challenges online error: " + databaseError.getCode());
            }
        });
    }


    public ArrayList<Challenge> getChallenges() {
        return challenges;
    }

    public Challenge addRandomChallenge(){
        Random rdm = new Random();
        ChallengeType challengeType = ChallengeType.values()[rdm.nextInt(ChallengeType.values().length)];
        //noinspection OptionalGetWithoutIsPresent
        Challenge challenge = new Challenge(new Date(), challengeType, rdm.ints(5, 40).findAny().getAsInt(), rdm.ints(5, 40).findAny().getAsInt());
        addChallenge(challenge);
        return challenge;
    }
    public void addChallenge(Challenge challenge){
        DatabaseReference key = mData.push();

        Map<String, Object> childAdd = new HashMap<>();
        childAdd.put("date", challenge.getDate().getTime());
        childAdd.put("challengeType", challenge.getChallengeType());
        childAdd.put("gameParameters", challenge.getGameParameters());
        childAdd.put("tries", challenge.getTries());
        childAdd.put("success", challenge.isSuccess());
        key.setValue(childAdd);
    }

    public Challenge getTodayChallenge(){
        for (Challenge challenge : challenges) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(challenge.getDate());
            if(isSameDay(calendar, Calendar.getInstance()))
                return challenge;
        }
        return addRandomChallenge();
    }

    private boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }

    public void update(Challenge challenge){
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(challenge.getKey() + "/success/", challenge.isSuccess());
        childUpdates.put(challenge.getKey() + "/tries/", challenge.getTries());

        mData.updateChildren(childUpdates);
    }
}
