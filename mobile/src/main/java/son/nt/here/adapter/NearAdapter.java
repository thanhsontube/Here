package son.nt.here.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import son.nt.here.R;
import son.nt.here.dto.MyPlaceDto;

/**
 * Created by Sonnt on 6/26/15.
 */
public class NearAdapter extends ArrayAdapter<MyPlaceDto> {
    private List<MyPlaceDto> list;
    private Context context;
    public NearAdapter (Context context, List<MyPlaceDto> list) {
        super(context, 0, list);
        this.context = context;
        this.list = list;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        Holder holder = null;
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.row_near_snipper, parent, false);
            holder = new Holder();
            holder.txtAddress = (TextView) view.findViewById(R.id.row_near_txt_address);
            view.setTag(holder);
        } else {
            holder  = (Holder) view.getTag();
        }

        holder.txtAddress.setText(list.get(position).getName());
        return view;
    }

    static class Holder {
        TextView txtAddress;
    }

    @Override
    public int getViewTypeCount() {
        return super.getViewTypeCount();
    }
}
