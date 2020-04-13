package uqac.bigbrainstudio.touchfit.ui.devices;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import uqac.bigbrainstudio.touchfit.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class DevicesFragment extends Fragment {

    // TODO: Customize parameter argument names
    // TODO: Customize parameters
    private OnListFragmentInteractionListener mListener;
    public static List<Devices> devicesList;

    public static RecyclerView recyclerView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DevicesFragment() {
    }

    // TODO: Customize parameter initialization
 /*   @SuppressWarnings("unused")
    public static DevicesFragment newInstance(int columnCount) {
        DevicesFragment fragment = new DevicesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }
*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        new DevicesDataRunnable(recyclerView).execute(devicesList.toArray(new Devices[0]));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_devices_list, container, false);
        // Set the adapter
        SwipeRefreshLayout mSwipeRefreshLayout = view.findViewById(R.id.refreshDevices);
        mSwipeRefreshLayout.setOnRefreshListener(() -> {

            devicesList.clear();
            devicesList.addAll(DevicesManager.getInstance().getDevices());

            new DevicesDataRunnable(recyclerView, mSwipeRefreshLayout).execute(devicesList.toArray(new Devices[0]));
        });


        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(v -> startActivity(new Intent(getContext(), AddDevicesActivity.class)));
        if (view.findViewById(R.id.list) instanceof RecyclerView) {
            recyclerView = view.findViewById(R.id.list);
            Context context = recyclerView.getContext();
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
            recyclerView.addItemDecoration(dividerItemDecoration);
            devicesList = new ArrayList<>();
            MyDevicesRecyclerViewAdapter adapter = new MyDevicesRecyclerViewAdapter(devicesList, mListener);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(adapter);

            devicesList.addAll(DevicesManager.getInstance().getDevices());
            //new DevicesDataRunnable(recyclerView).execute(devicesList.toArray(new Devices[0]));

        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;

        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Devices item);

        //void onContextInteraction(Devices mItem, View view);
        //void onLongClickFragment(Devices item);

    }

}
