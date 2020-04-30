package uqac.bigbrainstudio.touchfit.controllers.challenges;

import androidx.annotation.NonNull;
import com.google.firebase.database.Exclude;
import uqac.bigbrainstudio.touchfit.ui.game.Game;
import uqac.bigbrainstudio.touchfit.ui.game.GameActivity;
import uqac.bigbrainstudio.touchfit.ui.game.GameViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Challenge{
    private long date;
    private String key;
    private Game game;


    private List<Integer> gameParameters = new ArrayList<>();//just for serializing purposes

    private ChallengeType challengeType;
    private boolean success;
    private int tries;
    public Challenge(Date date, ChallengeType challengeType, int gameParameters1, int gameParmeters2){
        this.date = date.getTime();
        this.challengeType = challengeType;
            gameParameters.add(gameParameters1);
            gameParameters.add(gameParmeters2);
    }


    @SuppressWarnings("unused")
    public Challenge(){

    }

    public boolean isSuccess() {
        return success;
    }

    //Mandatory for firebase :/
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Integer> getGameParameters() {
        return gameParameters;
    }

    public void setGameParameters(List<Integer> gameParameters) {
        this.gameParameters = gameParameters;
    }

    public Date getDate() {
        return new Date(date);
    }

    @Exclude
    public Game getGame(GameViewModel gameViewModel, GameActivity gameActivity) {
        if(game == null){
            switch (challengeType){
                case NO_FAILS: return new NoFailGame(gameParameters.get(0), gameParameters.get(1), gameViewModel, gameActivity, this);
                case LIGHT_SECONDS: return new LightSecondsGame(gameParameters.get(1), gameParameters.get(0), gameViewModel, gameActivity, this);
            }
        }
        return game;
    }

    public ChallengeType getChallengeType() {
        return challengeType;
    }

    public void setChallengeType(ChallengeType challengeType) {
        this.challengeType = challengeType;
    }

    public int getTries() {
        return tries;
    }
    @Exclude
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void pass(){
        ChallengesManager.getInstance().addStreak();
        this.success = true;
    }

    public void fail(){
        this.tries++;
    }

    @NonNull
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Challenge{");
        sb.append("date=").append(date);
        sb.append(", key='").append(key).append('\'');
        sb.append(", success=").append(success);
        sb.append(", tries=").append(tries);
        sb.append('}');
        return sb.toString();
    }
}
