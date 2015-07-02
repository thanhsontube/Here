package son.nt.here.promo_app;

import android.content.Context;

import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sonnt on 5/29/15.
 */
public abstract class GiftCodeParseLoader extends ParseLoader<List<String>> {
    public GiftCodeParseLoader(Context context, String query) {
        super(context, query);
    }

    @Override
    protected List<String> handleData(List<ParseObject> list) {
        List<String> listCodes = new ArrayList<>();

        if (list == null || list.size() == 0) {
            return null;
        }
        for (ParseObject dto : list) {
            String code = dto.getString("code");
            listCodes.add(code);
        }
        return  listCodes;
    }
}
