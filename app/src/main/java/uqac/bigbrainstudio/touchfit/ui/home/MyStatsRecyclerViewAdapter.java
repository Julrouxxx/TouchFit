package uqac.bigbrainstudio.touchfit.ui.home;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import uqac.bigbrainstudio.touchfit.R;
import uqac.bigbrainstudio.touchfit.controllers.stats.Statistic;
import uqac.bigbrainstudio.touchfit.ui.game.FinishActivity;
import uqac.bigbrainstudio.touchfit.ui.home.StatsFragment.OnListFragmentInteractionListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MyStatsRecyclerViewAdapter extends RecyclerView.Adapter<MyStatsRecyclerViewAdapter.ViewHolder> {

    private final List<Statistic> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyStatsRecyclerViewAdapter(List<Statistic> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_stats, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mDate.setText(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(holder.mItem.getDate()));

        holder.mAverage.setText(holder.mView.getContext().getString(R.string.averageData, holder.mItem.getAverageSeconds()));
        holder.mTime.setText(new SimpleDateFormat("mm:ss:SS", Locale.US).format(holder.mItem.getTime()));



        holder.mBarChart.getDescription().setEnabled(false);
        holder.mBarChart.setDrawGridBackground(false);
        holder.mBarChart.setTouchEnabled(false);

        //barChart.setVisibleYRange(0, statistic.getSwitchSeconds(), YAxis.AxisDependency.RIGHT);
        DefaultValueFormatter xAxisValueFormatter = new DefaultValueFormatter(0);
        XAxis xAxis = holder.mBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(Typeface.DEFAULT_BOLD);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(holder.mItem.getLightTotal());
        xAxis.setValueFormatter(xAxisValueFormatter);

        ValueFormatter yAxisValueFormatter = new DefaultAxisValueFormatter(0);
        holder.mBarChart.getAxisRight().setEnabled(false);
        YAxis yAxis = holder.mBarChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(holder.mItem.getSwitchSeconds());
        yAxis.setLabelCount(holder.mItem.getSwitchSeconds() * (holder.mItem.getSwitchSeconds() / 2), true);

        yAxis.setValueFormatter(yAxisValueFormatter);
        holder.mBarChart.getLegend().setEnabled(false);
        ArrayList<BarEntry> values = new ArrayList<>();
        float i = 1f;
        for (Long l : holder.mItem.getAverageList()) {
            float f = l/1000f;
            BarEntry barEntry = new BarEntry(i, f);
            values.add(barEntry);
            i++;
        }
        FinishActivity.BarDataStats barDataSet = new FinishActivity.BarDataStats(values, holder.mItem.getSwitchSeconds());
        barDataSet.setColors(Color.GREEN,Color.YELLOW,Color.RED);
        barDataSet.setDrawValues(false);
        BarData barData = new BarData(barDataSet);

        holder.mBarChart.setData(barData);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final BarChart mBarChart;
        public final TextView mDate;
        public final TextView mTime;
        public final TextView mAverage;
        public Statistic mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mDate = view.findViewById(R.id.dateStats);
            mTime = view.findViewById(R.id.time);
            mAverage = view.findViewById(R.id.average);
            mBarChart = view.findViewById(R.id.endChart);
        }

    }
}
