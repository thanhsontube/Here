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
import son.nt.here.dto.MyPlaceDto;
import son.nt.here.utils.TsDateUtils;

/**
 * Created by Sonnt on 6/2/15.
 */
public class FavAdapter extends RecyclerView.Adapter<FavAdapter.Holder> {
    private List<MyPlaceDto> mList;
    private LayoutInflater inflater;


    public FavAdapter(Context context, List<MyPlaceDto> list, IAdapterCallback callback) {
        this.listener = callback;
        this.mList = list;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(inflater.inflate(R.layout.row_fav, parent, false), new Holder.IHolderListener() {
            @Override
            public void onClick(int position) {
                if (listener != null) {
                    listener.onRowClick(mList.get(position));
                }
            }
        });
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        MyPlaceDto dto = mList.get(position);
        setText(holder.txtTitle, dto.favTitle);
        setText(holder.txtAddress, dto.formatted_address);
        setText(holder.txtNotes, dto.favNotes);
        setText(holder.txtDate, TsDateUtils.getStringDate(dto.favUpdateTime));
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtAddress, txtNotes, txtDate;
        LinearLayout viewTags;
        public Holder(View view, IHolderListener callback) {
            super(view);
            this.listener = callback;
            txtTitle = (TextView) view.findViewWithTag("title");
            txtAddress = (TextView) view.findViewWithTag("address");
            txtNotes = (TextView) view.findViewWithTag("notes");
            viewTags = (LinearLayout) view.findViewWithTag("tags");
            txtDate = (TextView) view.findViewWithTag("date");
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onClick(getAdapterPosition());
                    }

                }
            });


        }
        IHolderListener listener;
        public interface IHolderListener {
            void onClick (int position);
        }
    }

    private void setText(TextView textView, String text) {
        textView.setText(TextUtils.isEmpty(text) ? "No Data ..." : text);
        if(TextUtils.isEmpty(text)) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.VISIBLE);
        }
    }
    IAdapterCallback listener;

    public interface IAdapterCallback {
        void onRowClick (MyPlaceDto dto);
    }
}
