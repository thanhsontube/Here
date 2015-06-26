package son.nt.here.utils;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;


/**
 * Created by Sonnt on 4/18/15.
 */
public class EventBus {
    public static final String TAG = "EventBus";
    public static final Bus EVENT_BUS = new Bus(ThreadEnforcer.ANY);
    public static final Handler HANDLER_MAIN = new Handler(Looper.getMainLooper());
    public static void post (Object event) {
        Looper myLooper = Looper.myLooper();
        Looper mainLooper = Looper.getMainLooper();
        if (myLooper != null && myLooper.equals(mainLooper)) {
            EVENT_BUS.post(event);
        } else {
            HANDLER_MAIN.post(new EventRunnable(event));
        }
    }
    public static final class EventRunnable implements Runnable {
        final Object event;
        public EventRunnable(Object event) {
            this.event = event;
        }

        @Override
        public void run() {
            EVENT_BUS.post(this.event);
        }
    }
    public static void register (Object obj) {
        EVENT_BUS.register(obj);
    }

    public static void unregister(Object obj) {
        EVENT_BUS.unregister(obj);
    }
}
