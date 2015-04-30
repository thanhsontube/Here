package son.nt.here.base;

import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Sonnt on 4/17/15.
 */
public class BaseActivity extends AppCompatActivity {
    protected FragmentManager fragmentManager;
    protected ActionBar actionBar;

    protected FragmentManager getSafeFragmentManager() {
        if (fragmentManager == null) {
            fragmentManager = getSupportFragmentManager();
        }
        return fragmentManager;
    }

    protected ActionBar getSafeActionBar() {
        if (actionBar == null) {
            actionBar = this.getSupportActionBar();
        }
        return actionBar;
    }

    protected boolean isSafe() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (this.isDestroyed()) {
                return false;
            }
        }
        if (this.isFinishing()) {
            return false;
        }
        return true;
    }

}
