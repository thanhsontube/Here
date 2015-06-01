package son.nt.here.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import son.nt.here.R;
import son.nt.here.dto.DisplayDto;

/**
 * Created by Sonnt on 6/1/15.
 */
public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.Holder> {

    private static final String TAG = "DetailAdapter";
    private List<DisplayDto> mList;
    private LayoutInflater inflater;
    private Context context;

    public DetailAdapter(Context context, List<DisplayDto> mList) {
        this.mList = mList;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(inflater.inflate(R.layout.row_address, parent, false), new Holder.IHolderListener() {
            @Override
            public void onCopy(int position) {
                Toast.makeText(context, "Copy:" + mList.get(position).content, Toast.LENGTH_LONG).show();
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(mList.get(position).title, mList.get(position).content);
                clipboard.setPrimaryClip(clip);
            }
        });
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        DisplayDto dto = mList.get(position);
        holder.txtTitle.setText(dto.title);
        holder.txtContent.setText(TextUtils.isEmpty(dto.content) ? "No Data ..." : dto.content);

    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {

        public TextView txtTitle;
        public TextView txtContent;
        public ImageView imgCopy;

        public Holder(View itemView, IHolderListener callback) {
            super(itemView);
            this.listener = callback;

            txtTitle = (TextView) itemView.findViewWithTag("title");
            txtContent = (TextView) itemView.findViewWithTag("content");
            imgCopy = (ImageView) itemView.findViewById(R.id.row_img_copy);
            imgCopy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if( listener != null) {
                        listener.onCopy(getAdapterPosition());
                    }
                }
            });
        }
        IHolderListener listener;
        public interface IHolderListener {
            void onCopy (int position);
        }
    }
}
