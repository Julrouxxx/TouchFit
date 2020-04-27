package uqac.bigbrainstudio.touchfit.ui.game;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import uqac.bigbrainstudio.touchfit.R;
import uqac.bigbrainstudio.touchfit.controllers.stats.Statistic;
import uqac.bigbrainstudio.touchfit.controllers.stats.StatisticsManager;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class FinishActivity extends AppCompatActivity {
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_finish);
        Statistic statistic = StatisticsManager.getInstance().getStaticsByKey(getIntent().getStringExtra("stats"));
        if(getIntent().getBooleanExtra("review", false)){

            TextView gameFinished = findViewById(R.id.game_finshed);
            gameFinished.setText(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(statistic.getDate()));
        }
        TextView lightActivated = findViewById(R.id.gameLightDevicesActivated);
        TextView average = findViewById(R.id.averageScore);
        TextView timer = findViewById(R.id.lightDevicesLeft);
        TextView accuracy = findViewById(R.id.accuracy);
        long timing = statistic.getTime();
        int millis = (int) (timing % 1000)/10;
        int seconds = (int) (timing / 1000);
        int minutes = seconds/ 60;
        seconds = seconds % 60;
        timer.setText(String.format(Locale.US, "%02d:%02d:%02d", minutes, seconds, millis));
        average.setText(getString(R.string.averageData, statistic.getAverageSeconds()));
        accuracy.setText(getString(R.string.accuracy, statistic.getAccuracy()));
        lightActivated.setText(getString(R.string.light_activated_stats, statistic.getLightActivated(), statistic.getLightTotal()));


        BarChart barChart = findViewById(R.id.endChart);
        barChart.getDescription().setEnabled(false);
        barChart.setDrawGridBackground(false);
        //barChart.setTouchEnabled(false);
        barChart.setDragEnabled(true);
        barChart.setDoubleTapToZoomEnabled(false);
        barChart.setHighlightPerDragEnabled(false);
        barChart.setHighlightPerTapEnabled(false);
        barChart.setScaleEnabled(false);
        barChart.getViewPortHandler().setMaximumScaleX(4f);
        barChart.setScaleXEnabled(true);

        //barChart.setVisibleYRange(0, statistic.getSwitchSeconds(), YAxis.AxisDependency.RIGHT);
        DefaultValueFormatter xAxisValueFormatter = new DefaultValueFormatter(0);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(Typeface.DEFAULT_BOLD);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(statistic.getLightTotal());
        xAxis.setValueFormatter(xAxisValueFormatter);

        ValueFormatter yAxisValueFormatter = new DefaultAxisValueFormatter(0);
        barChart.getAxisRight().setEnabled(false);
        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(statistic.getSwitchSeconds());
        yAxis.setLabelCount(statistic.getSwitchSeconds() * (statistic.getSwitchSeconds() / 2), true);

        yAxis.setValueFormatter(yAxisValueFormatter);
        barChart.getLegend().setEnabled(false);
        ArrayList<BarEntry> values = new ArrayList<>();
        float i = 1f;
        for (Long l : statistic.getAverageList()) {
            float f = l/1000f;
            BarEntry barEntry = new BarEntry(i, f);
            values.add(barEntry);
            i++;
        }
        BarDataStats barDataSet = new BarDataStats(values, statistic.getSwitchSeconds());
        barDataSet.setColors(Color.GREEN,Color.YELLOW,Color.RED);
        barDataSet.setDrawValues(false);
        BarData barData = new BarData(barDataSet);

        barChart.setData(barData);
    }

    public static class BarDataStats extends BarDataSet{
        int switchSeconds;
        public BarDataStats(ArrayList<BarEntry> values, int switchSeconds) {
            super(values, "Time");
            this.switchSeconds = switchSeconds;

        }

        @Override
        public int getColor(int index) {
            if(getEntryForIndex(index).getY() > switchSeconds){
                return mColors.get(2);
            }
            if(getEntryForIndex(index).getY() > switchSeconds - 0.5)
                return mColors.get(1);
            return mColors.get(0);
        }
    }
}
