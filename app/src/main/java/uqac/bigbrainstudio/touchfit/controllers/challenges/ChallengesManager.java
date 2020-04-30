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
    private int streaks;
    public static ChallengesManager getInstance() {
        return instance;
    }

    public void  setup(TrainingFragment trainingFragment){
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        assert mUser != null;
        this.mData = FirebaseDatabase.getInstance().getReference(mUser.getUid() + "/challenges");
        first = true;
        this.challenges = new ArrayList<>();
        mData.child("streaks").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue() == null)
                        streaks = 0;
                    else
                        streaks = dataSnapshot.getValue(Integer.class);
                    if(first)
                mData.orderByChild("date").endAt(new Date().getTime(), "date").limitToLast(streaks+2).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        challenges.clear();
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            if(!snapshot.getKey().equals("streaks")) {
                                Challenge challenge = snapshot.getValue(Challenge.class);
                                assert challenge != null;
                                challenge.setKey(snapshot.getKey());
                                challenges.add(challenge);
                                //Log.i("test", "onDataChange: " + challenge.toString());

                            }
                        }
                        if(first) {
                            first = false;
                            trainingFragment.showDailyChallenge();
                            trainingFragment.checkForStreaks();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        if(databaseError.getCode() == -3) {
                            mData.removeEventListener(this);
                            return;
                        }
                        Log.e("TouchFit", "Error on reading challenges online error: " + databaseError.getCode());
                    }
                });
                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if(databaseError.getCode() == -3){
                    mData.removeEventListener(this);
                    return;
                }
                Log.e("TouchFit", "Error on reading streaks error: " + databaseError.getCode());
            }
        });

    }

    public void zeroStreak() {
        streaks = 0;
        mData.child("streaks").setValue(0, 2);
    }


    public ArrayList<Challenge> getChallenges() {
        return challenges;
    }

    public Challenge addRandomChallenge(){
        Random rdm = new Random();
        ChallengeType challengeType = ChallengeType.values()[rdm.nextInt(ChallengeType.values().length)];
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
    public int getStreaks(){
        return streaks;
    }
    public void addStreak(){
        mData.child("streaks").setValue(streaks+1, 2);
    }

}
