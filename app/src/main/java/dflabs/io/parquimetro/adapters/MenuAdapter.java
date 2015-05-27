package dflabs.io.parquimetro.adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import dflabs.io.parquimetro.R;
import dflabs.io.parquimetro.models.MenuItem;

/**
 * Created by danielgarcia on 5/26/15.
 */
public class MenuAdapter extends ArrayAdapter<MenuItem> {

    static String[] titles;
    static TypedArray icons;
    public MenuAdapter(Context context, int resId) {
        super(context, resId);
        titles = context.getResources().getStringArray(R.array.menu_titles);
        icons = context.getResources().obtainTypedArray(R.array.menu_icons);
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_menu, null);
        }
        TextView titleTextView = (TextView) convertView.findViewById(R.id.item_menu_title);
        ImageView iconImageView = (ImageView) convertView.findViewById(R.id.item_menu_icon);
        titleTextView.setText(titles[position]);
        iconImageView.setImageResource(icons.getResourceId(position, 0));
        return convertView;
    }

}
