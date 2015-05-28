package son.nt.here.promo_app;

/**
 * Created by Sonnt on 5/29/15.
 */
public class ParseManager {
    public void load (ParseLoader<?> loader) {
        loader.execute(this);
    }

    public void cancel (ParseLoader<?> loader) {
        loader.cancel();
    }
}
