package son.nt.here.promo_app.main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import son.nt.here.R;
import son.nt.here.promo_app.PromoAppDto;

/**
 * Created by Sonnt on 5/29/15.
 */
public class PromoAppAdapter extends RecyclerView.Adapter<PromoAppAdapter.Holder> {
    private static final String TAG = "PromoAppAdapter";
    private List<PromoAppDto> mList;
    private LayoutInflater inflater;
    private Context context;

    public PromoAppAdapter (Context context, List<PromoAppDto> mList) {
        this.mList = mList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }

    public class Holder extends RecyclerView.ViewHolder {
        public ImageView imgCard;
        public TextView txtAppName;
        public Holder(View view) {
            super(view);
            imgCard = (ImageView) view.findViewWithTag("img");
            txtAppName = (TextView) view.findViewWithTag("txt");
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String appPackageName = mList.get(getAdapterPosition()).appLink; // getPackageName() from Context or Activity object
                    try {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                }
            });
        }
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.row_promo_app, viewGroup, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int i) {
        PromoAppDto dto = mList.get(i);
        holder.txtAppName.setText(dto.appName);
        Picasso.with(context).load(dto.appImage).into(holder.imgCard);

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
