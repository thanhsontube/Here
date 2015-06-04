package son.nt.here.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import son.nt.here.R;

/**
 * Created by Sonnt on 6/2/15.
 */
public class AddFavAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<String> mList;
    private LayoutInflater inflater;
    private Context context;

    public static final int TYPE_IMAGES = 0;
    public static final int TYPE_ADD = 1;


    public AddFavAdapter(Context context, List<String> list) {
        this.mList = list;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_IMAGES) {

            return new HolderImage(inflater.inflate(R.layout.row_add_img, parent, false));
        } else {
            return new HolderAdd(inflater.inflate(R.layout.row_add_more, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof  HolderImage) {
            HolderImage holderImage = (HolderImage) holder;
            Picasso.with(context).load(mList.get(position)).into(holderImage.imgAdded);
        } else {
            HolderAdd holderAdd = (HolderAdd) holder;
        }
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    static class HolderImage extends RecyclerView.ViewHolder {
        ImageView imgRemove;
        ImageView imgAdded;

        public HolderImage(View view) {
            super(view);
            imgRemove = (ImageView) view.findViewWithTag("remove");
            imgAdded = (ImageView) view.findViewWithTag("img");
        }
    }

    static class HolderAdd extends RecyclerView.ViewHolder {

        public HolderAdd(View view) {
            super(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v("", ">>>" + "add new Images");
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mList.size() - 1) {
            return TYPE_ADD;
        }
        return TYPE_IMAGES;
    }
}
