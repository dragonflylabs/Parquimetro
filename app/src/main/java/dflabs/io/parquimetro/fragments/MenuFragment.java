package dflabs.io.parquimetro.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import dflabs.io.parquimetro.MainActivity;
import dflabs.io.parquimetro.R;
import dflabs.io.parquimetro.adapters.MenuAdapter;

/**
 * Created by danielgarcia on 5/26/15.
 */
public class MenuFragment extends Fragment {

    @InjectView(R.id.fr_menu_list) ListView menuListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_menu, container, false);
        ButterKnife.inject(this, v);
        menuListView.setAdapter(new MenuAdapter(getActivity(), R.layout.item_menu));
        return v;
    }

    @OnItemClick(R.id.fr_menu_list) void onMenuItemClick(int position){
        MainActivity activity = (MainActivity) getActivity();
        switch (position){
            case 0: activity.switchContent(new MapsFragment());
            case 1: activity.switchContent(new PaysFragment());
            case 2: activity.switchContent(new CalendarFragment());
            case 3: activity.switchContent(new SettingsFragment());
        }
    }

    @Override
    public void onDestroyView() {
        ButterKnife.reset(this);
        super.onDestroyView();
    }
}
