package lib.lennken.utils;

import android.widget.CheckBox;

import java.util.List;

/**
 * Created by caprinet on 1/29/15.
 */
public interface OnCheckableChange<T>{

    public void onCheckedChange(CheckBox checkBox, int position, List<T> objects);
}
