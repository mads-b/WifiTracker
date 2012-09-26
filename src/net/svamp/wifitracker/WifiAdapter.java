package net.svamp.wifitracker;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import net.svamp.wifitracker.core.WifiItem;

import java.util.ArrayList;
import java.util.List;


public class WifiAdapter extends BaseAdapter  implements OnItemClickListener {
    private List<WifiItem> results = new ArrayList<WifiItem>();
    private final List<View> listItems = new ArrayList<View>();
    private final List<Boolean> clicked = new ArrayList<Boolean>();

    public WifiAdapter(Context context, List<WifiItem> results) {
        this.results = results;
        View convertView;
        for (WifiItem result : results) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.wifi_list_item, null);
            ImageView wifiLogo = (ImageView) convertView.findViewById(R.id.encryption_logo);
            wifiLogo.setImageResource(result.getDrawableId());
            TextView wifiName = (TextView) convertView.findViewById(R.id.wifi_name);
            wifiName.setText(result.ssid);
            clicked.add(false);
            listItems.add(convertView);
        }
    }

    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View view = listItems.get(position);
        if(clicked.get(position))
            view.setBackgroundColor(Color.YELLOW);
        else
            view.setBackgroundColor(Color.TRANSPARENT);
        return view;
    }


    public int getCount() {
        return results.size();
    }


    public Object getItem(int arg0) {
        return results.get(arg0);
    }


    public long getItemId(int arg0) {
        return 0;
    }


    public void onItemClick(AdapterView<?> parent, View curItem, int position, long id) {
        clicked.set(position, !clicked.get(position));
    }

    /*
      * @return A list of the selected wifi AP's.
      */
    public ArrayList<String> getClicked() {
        ArrayList<String> selected = new ArrayList<String>();
        for(int i=0;i<clicked.size();i++) {
            if(clicked.get(i))
                selected.add(results.get(i).bss);
        }
        return selected;
    }


}
 
