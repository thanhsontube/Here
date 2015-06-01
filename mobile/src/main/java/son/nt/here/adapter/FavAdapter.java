package son.nt.here.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import son.nt.here.R;
import son.nt.here.dto.FavDto;

/**
 * Created by Sonnt on 6/2/15.
 */
public class FavAdapter extends RecyclerView.Adapter<FavAdapter.Holder> {
    private List<FavDto> mList;
    private LayoutInflater inflater;


    public FavAdapter(Context context, List<FavDto> list) {
        this.mList = list;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(inflater.inflate(R.layout.row_fav, parent, false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        FavDto dto = mList.get(position);
        setText(holder.txtTitle, dto.favTitle);
        setText(holder.txtAddress, dto.formatted_address);
        setText(holder.txtNotes, dto.favNotes);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtAddress, txtNotes;
        LinearLayout viewTags;
        public Holder(View view) {
            super(view);
            txtTitle = (TextView) view.findViewWithTag("title");
            txtAddress = (TextView) view.findViewWithTag("address");
            txtNotes = (TextView) view.findViewWithTag("notes");
            viewTags = (LinearLayout) view.findViewWithTag("tags");
        }
    }

    private void setText(TextView textView, String text) {
        textView.setText(TextUtils.isEmpty(text) ? "No Data ..." : text);
    }
}