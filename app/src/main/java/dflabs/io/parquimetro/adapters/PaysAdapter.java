package dflabs.io.parquimetro.adapters;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import java.util.ArrayList;

import butterknife.ButterKnife;
import dflabs.io.parquimetro.R;
import dflabs.io.parquimetro.models.Pay;

/**
 * Created by danielgarcia on 5/27/15.
 */
public class PaysAdapter extends ArrayAdapter<Pay>{

    public PaysAdapter(Context context, int resId, ArrayList<Pay> pays) {
        super(context, resId, pays);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_pay, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        return convertView;
    }

    static class ViewHolder{


        public ViewHolder(View v){
            ButterKnife.inject(this, v);
        }
    }
}
