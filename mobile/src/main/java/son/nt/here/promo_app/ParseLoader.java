package son.nt.here.promo_app;

import android.content.Context;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Created by Sonnt on 5/29/15.
 */
public abstract class ParseLoader<T> {

    private Context context;
    private String mQuery;
    private boolean isCancel = false;
    private boolean isCache = true;

    public abstract void onSuccess (T result);
    public abstract void onFail (Throwable e);

    public ParseLoader (Context context, String query) {
        this.context = context;
        this.mQuery = query;

    }
    public ParseLoader (Context context, String query, boolean isCache) {
        this.context = context;
        this.isCache = isCache;
        this.mQuery = query;

    }

    public void setCache (boolean isCache) {
        this.isCache = isCache;
    }

    void execute(ParseManager pm) {
        this.query();
    }
    void cancel () {
        isCancel = true;
    }

    private void query() {
        if (isCache) {

        }
        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery(mQuery);
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if(isCancel) {
                    onFail(new Throwable("Cancel was called"));
                    return;
                }
                if (e != null) {
                    onFail(new Throwable(e));
                    return;
                }
                if (isCache) {

                }
                onSuccess(handleData(list));
            }
        });

    }
    protected abstract T handleData (List<ParseObject> list);

}
