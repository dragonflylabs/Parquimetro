package lib.lennken.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lib.lennken.R;

/**
 * Created by caprinet on 1/29/15.
 */
public class CheckableAdapter<T> extends ArrayAdapter<T> {

    private List<Integer> checkedPositions;
    private Context mContext;
    private boolean[] checked;
    List<T> mObjects;
    private OnCheckableChange callback;

    public CheckableAdapter(Context context, ArrayList<T> objects, OnCheckableChange callback) {
        super(context, R.layout.list_item_checkable , objects);
        initItems(context, objects, callback);
    }

    public List<T> getObjects(){
        return mObjects;
    }
    public CheckableAdapter(Context context, List<T> objects, ArrayList<Integer> checkedPositions, OnCheckableChange callback) {
        super(context, R.layout.list_item_checkable, objects);
        this.checkedPositions = checkedPositions;
        initItems(context, objects, callback);
    }

    private void initItems(Context context, List<T> objects, OnCheckableChange callback) {
        this.mObjects = objects;
        this.mContext = context;
        this.checked = new boolean[mObjects.size()];
        for(int i = 0; i < checked.length; i++)
            checked[i] = false;
        if(checkedPositions != null && checkedPositions.size() > 0){
            for (Integer i : checkedPositions){
                checked[i] = true;
            }
        }
        this.callback = callback;
    }

    public CheckableAdapter(Context context, List<T> objects, OnCheckableChange callback) {
        super(context, R.layout.list_item_checkable , objects);
        initItems(context, objects, callback);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_checkable, null);
        }
        TextView textView = (TextView) convertView.findViewById(R.id.__list_item_checkable_text);
        final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.__list_item_checkable_check);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean check) {
                checked[position] = check;
                if(callback != null){
                    callback.onCheckedChange((CheckBox)compoundButton, position, mObjects);
                }
            }
        });
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkBox.setChecked(!checkBox.isChecked());
            }
        });
        Object model = this.mObjects.get(position);
        textView.setText(model.toString());
        checkBox.setChecked(checked[position]);
        return convertView;
    }

    public ArrayList<T> getCheckedItems(){
        ArrayList<T> items = new ArrayList<T>();
        for(int i = 0; i < checked.length; i++){
            if(checked[i])
                items.add(mObjects.get(i));
        }
        return items;
    }

}
