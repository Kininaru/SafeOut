package top.shiftregister.safeout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.LinkedList;

public class MyListAdapter extends BaseAdapter {
    private final LinkedList<ListViewItem> core = new LinkedList<>();
    Context context;

    MyListAdapter(Context c) {
        context = c;
    }

    public void addItem(ListViewItem newItem) {
        core.add(newItem);
    }

    @Override
    public int getCount() {
        return core.size();
    }

    @Override
    public Object getItem(int position) {
        return core.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint({"InflateParams", "ViewHolder"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout item;
        if (convertView != null) item = (LinearLayout) convertView;
        item = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.main_list_item, null);
        ListViewItem listViewItem = (ListViewItem) getItem(position);

        TextView machineId = item.findViewById(R.id.list_item_machine_id);
        TextView date = item.findViewById(R.id.list_item_date);
        TextView mode = item.findViewById(R.id.list_item_mode);

        machineId.setText(listViewItem.machineId);
        date.setText(listViewItem.date);
        if (listViewItem.granted) {
            mode.setText("正常");
            mode.setTextColor(Color.GREEN);
        } else {
            mode.setText("异常");
            mode.setTextColor(Color.RED);
        }

        return item;
    }
}
