package uqac.bigbrainstudio.touchfit.ui.devices;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import uqac.bigbrainstudio.touchfit.R;
import uqac.bigbrainstudio.touchfit.ui.devices.DevicesFragment.OnListFragmentInteractionListener;

import java.util.List;

public class MyDevicesRecyclerViewAdapter extends RecyclerView.Adapter<MyDevicesRecyclerViewAdapter.ViewHolder> {

    private final List<Devices> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyDevicesRecyclerViewAdapter(List<Devices> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_devices, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(String.valueOf(mValues.get(position).getId() + 1));
        holder.mContentView.setText(mValues.get(position).getName());
        holder.mContentIp.setText(mValues.get(position).getHostname());


        if (mValues.get(position).isConnected()) {
            holder.mContentStatus.setText(R.string.online_status);
            holder.mContentStatus.setTextColor(Color.GREEN);
        } else {
            holder.mContentStatus.setText(R.string.offline_status);
            holder.mContentStatus.setTextColor(Color.RED);
        }
      /*  holder.mView.setOnLongClickListener(v ->{
            if(null != mListener) {
                mListener.onLongClickFragment(holder.mItem);
                return true;
            }
            return false;
        });*/
      holder.mView.setOnCreateContextMenuListener((contextMenu, view, contextMenuInfo) -> {
          contextMenu.setHeaderTitle(holder.mItem.getName());
          if(holder.mItem.isConnected())
              contextMenu.add(0, view.getId(), position, R.string.delete_device);
          contextMenu.add(0, view.getId(), position, R.string.rename_device);
      });

        holder.mView.setOnClickListener(v -> {
            if (null != mListener) {
                mListener.onListFragmentInteraction(holder.mItem);

            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final TextView mContentIp;
        public final TextView mContentStatus;
        public Devices mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.item_number);
            mContentView = view.findViewById(R.id.content_name);
            mContentIp = view.findViewById(R.id.content_ip);
            mContentStatus = view.findViewById(R.id.content_status);
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
