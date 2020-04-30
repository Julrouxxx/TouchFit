package uqac.bigbrainstudio.touchfit.controllers.stats;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * TouchFit
 * Created by Julrouxxx.
 */
public class StatisticsManager {
    public static StatisticsManager instance = new StatisticsManager();
    private FirebaseUser mUser;
    private DatabaseReference mData;
    private ArrayList<Statistic> statistics;
    public static StatisticsManager getInstance() {
        return instance;
    }

    public void setup(){
        this.mUser = FirebaseAuth.getInstance().getCurrentUser();
        this.mData = FirebaseDatabase.getInstance().getReference(mUser.getUid() + "/stats");
        this.statistics = new ArrayList<>();
        mData.orderByChild("date").limitToLast(100).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                statistics.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Statistic statistic = snapshot.getValue(Statistic.class);
                        assert statistic != null;
                        statistic.setKey(snapshot.getKey());
                        statistics.add(statistic);
                }
                Collections.sort(statistics);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if(databaseError.getCode() == -3){
                    mData.removeEventListener(this);
                    return;
                }
                Log.e("TouchFit", "Error on reading stats online error: " + databaseError.getCode());
            }
        });
    }

    public ArrayList<Statistic> getStatistics() {
        return statistics;
    }

    public Statistic getStaticsByKey(String key){
        return statistics.stream().anyMatch(s -> s.getKey().equals(key)) ? statistics.stream().filter(s -> s.getKey().equals(key)).findFirst().get() : null;
    }

    public void deleteStatistics(Statistic statistic){
        mData.child(statistic.getKey()).removeValue();
        statistics.remove(statistic);
    }

    public String addStatistics(Statistic statistic){
        DatabaseReference key = mData.push();
        /*
            private List<Long> average;
    private int lightLeft;
    private int switchSeconds;
    private int lightActivated;
    private int lightTotal;
    private long time;
         */
        Map<String, Object> childAdd = new HashMap<>();
        childAdd.put("date", statistic.getDate().getTime());
        childAdd.put("switchSeconds", statistic.getSwitchSeconds());
        childAdd.put("lightTotal", statistic.getLightTotal());
        childAdd.put("lightActivated", statistic.getLightActivated());
        childAdd.put("average", statistic.getAverageList());
        childAdd.put("time", statistic.getTime());
        key.setValue(childAdd);

        return key.getKey();
    }
}
