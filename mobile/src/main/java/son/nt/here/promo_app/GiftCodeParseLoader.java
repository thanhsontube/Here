package son.nt.here.promo_app;

import android.content.Context;

import com.parse.ParseObject;

import java.util.List;

/**
 * Created by Sonnt on 5/29/15.
 */
public abstract class GiftCodeParseLoader extends ParseLoader<String> {
    public GiftCodeParseLoader(Context context, String query) {
        super(context, query);
    }

    @Override
    protected String handleData(List<ParseObject> list) {

        if (list == null || list.size() == 0) {
            return null;
        }
        ParseObject dto = list.get(0);
        String code = dto.getString("code");
        return code;
    }
}
