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
import android.widget.Toast;

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
        public TextView txtUninstall;
        public TextView txtGooglePlay;
        public View viewIsInstall;
         PromoAppDto dto ;
        public Holder(View view) {

            super(view);
            imgCard = (ImageView) view.findViewWithTag("img");
            txtAppName = (TextView) view.findViewWithTag("txt");
            txtUninstall = (TextView) view.findViewById(R.id.promo_row_txt_uninstall);
            txtGooglePlay = (TextView) view.findViewById(R.id.promo_row_txt_google_play);
            viewIsInstall = view.findViewWithTag("is_install");
            txtUninstall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dto = mList.get(getAdapterPosition());
                    if (dto.isInstall) {
                        Uri packageUri = Uri.parse("package:" + dto.appLink);
                        Intent uninstallIntent =
                                new Intent(Intent.ACTION_DELETE, packageUri);
                        uninstallIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(uninstallIntent);
                    } else {
                        Toast.makeText(context, dto.appName + " is still not installed !", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            txtGooglePlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dto = mList.get(getAdapterPosition());
                    final String appPackageName = mList.get(getAdapterPosition()).appLink; // getPackageName() from Context or Activity object
                    try {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                }
            });
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dto = mList.get(getAdapterPosition());
                    if (dto.isInstall) {
                        //open
                        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(dto.appLink);
                        launchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(launchIntent);
                    } else {
                        final String appPackageName = mList.get(getAdapterPosition()).appLink; // getPackageName() from Context or Activity object
                        try {
                            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
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
        if (dto.isInstall) {
            holder.viewIsInstall.setVisibility(View.VISIBLE);
            holder.txtUninstall.setVisibility(View.VISIBLE);
        } else {
            holder.viewIsInstall.setVisibility(View.GONE);
            holder.txtUninstall.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
