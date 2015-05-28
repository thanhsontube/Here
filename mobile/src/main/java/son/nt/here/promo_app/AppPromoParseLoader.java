package son.nt.here.promo_app;

import android.content.Context;

import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sonnt on 5/29/15.
 */
public abstract class AppPromoParseLoader extends ParseLoader<AppPromoData> {
    public AppPromoParseLoader(Context context, String query) {
        super(context, query);
    }

    @Override
    protected AppPromoData handleData(List<ParseObject> list) {

        List<PromoAppDto> rList = new ArrayList<>();
        PromoAppDto dto;
        for (ParseObject o : list) {
            dto = new PromoAppDto();
            dto.appName = o.getString("appName");
            dto.appDescription = o.getString("appDescription");
            dto.appLink = o.getString("appLink");
            dto.appNo = o.getInt("appNo");
            rList.add(dto);
        }
        return new AppPromoData(rList);
    }
}
