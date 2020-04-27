package uqac.bigbrainstudio.touchfit.controllers.stats;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;

import java.util.Date;
import java.util.List;
import java.util.stream.LongStream;

/**
 * TouchFit
 * Created by Julrouxxx.
 */
public class Statistic implements Comparable<Statistic> {
    private List<Long> average;

    private int switchSeconds;
    private int lightActivated;
    private int lightTotal;
    private long time;
    private long date;
    private String key;

    @SuppressWarnings("unused")
    public Statistic(){

    }

    public Statistic(int switchSeconds, int lightActivated, int lightTotal, List<Long> average, long time){
        this.date = new Date().getTime();
        this.time = time;
        this.switchSeconds = switchSeconds;
        this.lightTotal = lightTotal;
        this.lightActivated = lightActivated;
        this.average = average;
    }
    @PropertyName("average")
    public List<Long> getAverageList() {
        return average;
    }


    public Double getAverageSeconds(){
        LongStream.Builder builder = LongStream.builder();
        average.forEach(builder::accept);
        LongStream longStream = builder.build();
        return longStream.average().getAsDouble() / 1000;
    }


    public int getSwitchSeconds() {
        return switchSeconds;
    }

    public int getLightActivated() {
        return lightActivated;
    }

    public int getLightTotal() {
        return lightTotal;
    }
    public int getAccuracy(){
        return Math.floorDiv(lightActivated * 100, lightTotal);
    }
    public Date getDate() {
        return new Date(date);
    }

    @Exclude
    public String getKey(){
       return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * <p>The implementor must ensure <tt>sgn(x.compareTo(y)) ==
     * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
     * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
     * <tt>y.compareTo(x)</tt> throws an exception.)
     *
     * <p>The implementor must also ensure that the relation is transitive:
     * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
     * <tt>x.compareTo(z)&gt;0</tt>.
     *
     * <p>Finally, the implementor must ensure that <tt>x.compareTo(y)==0</tt>
     * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
     * all <tt>z</tt>.
     *
     * <p>It is strongly recommended, but <i>not</i> strictly required that
     * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
     * class that implements the <tt>Comparable</tt> interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     *
     * <p>In the foregoing description, the notation
     * <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
     * <i>signum</i> function, which is defined to return one of <tt>-1</tt>,
     * <tt>0</tt>, or <tt>1</tt> according to whether the value of
     * <i>expression</i> is negative, zero or positive.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     */
    @Override
    public int compareTo(Statistic o) {
        return o.getDate().compareTo(getDate());
    }

    public long getTime() {
        return time;
    }
}
