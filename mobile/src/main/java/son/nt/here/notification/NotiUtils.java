package son.nt.here.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;

import son.nt.here.MsConst;
import son.nt.here.R;
import son.nt.here.activity.HomeActivity;
import son.nt.here.dto.MyPlaceDto;

/**
 * Created by Sonnt on 6/22/15.
 */
public class NotiUtils {
    public static void showNotification (Context context, MyPlaceDto dto) {
        dto.isFav = true;

        Intent intent = new Intent(context, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        String title = "WHERE AM I";
        if (!TextUtils.isEmpty(dto.favTitle)) {
            title = dto.favTitle;
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //WEAR

        //when user clicks favourite a place on wear, it sends a broadcast with data MyPlaceDto to Service via intentWear.
        //Service adds this place to DB after receiving this broadcast
        Intent intentWear = new Intent();
        intentWear.putExtra(MsConst.WEAR_FAV_EXTRA, dto);
        intentWear.setAction(MsConst.WEAR_FAV_EXTRA);
        PendingIntent wearPendingIntent = PendingIntent.getBroadcast(context, 0, intentWear, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action wearAction = new NotificationCompat.Action(R.drawable.ic_fav_0, "Favourite", wearPendingIntent);
        NotificationCompat.WearableExtender wearableExtender = new NotificationCompat.WearableExtender().addAction(wearAction);

        //bigStyle
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle().bigText(dto.formatted_address)
                .setBigContentTitle(title);

        Notification notification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_search_address)
                .setContentTitle(title)
                .setContentText(dto.formatted_address)
                .setContentIntent(pendingIntent)
                .setStyle(bigTextStyle)
                .setTicker("HERE>>> " + dto.formatted_address)
                .extend(wearableExtender)
                .build();

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(19, notification);
    }
}
