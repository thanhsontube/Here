package son.nt.here.base;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import java.util.Collection;
import java.util.Stack;

import son.nt.here.utils.Logger;

/**
 * Created by Sonnt on 4/17/15.
 */
public abstract class AbsBaseActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener {
    public static final String TAG = "AbsBaseActivity";
    protected static final String KEY_MAIN_FRAGMENT = "main";
    protected static final String KEY_SAVE_STACK = "tag_stack";
    protected Stack<String> stackFragmentTags = new Stack<>();

    protected abstract int getLayoutMain ();
    protected abstract Fragment getMainFragment(Bundle saveInstanceState);
    protected FragmentManager fragmentManager;
    protected ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getSafeFragmentManager()
                    .beginTransaction()
                    .add(getLayoutMain(), getMainFragment(savedInstanceState), KEY_MAIN_FRAGMENT)
                    .setTransition(FragmentTransaction.TRANSIT_NONE)
                    .commit();
        } else {
            Collection<String> saveCollection = (Collection<String>) savedInstanceState.getSerializable(KEY_SAVE_STACK);
            stackFragmentTags.addAll(saveCollection);
            restoreListFragment();
        }

        getSafeFragmentManager().addOnBackStackChangedListener(this);
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        restoreListFragment();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_SAVE_STACK, stackFragmentTags);
    }

    protected void restoreListFragment () {
        FragmentTransaction ft = getSafeFragmentManager().beginTransaction();
        if (stackFragmentTags.size() == 0) {
            ft.show(getSafeFragmentManager().findFragmentByTag(KEY_MAIN_FRAGMENT));
        } else {
            ft.hide(getSafeFragmentManager().findFragmentByTag(KEY_MAIN_FRAGMENT));
            for (int i = 0; i < stackFragmentTags.size(); i ++) {
                String tag = stackFragmentTags.get(i);
                Fragment f = getSafeFragmentManager().findFragmentByTag(tag);
                if (i + 1 < stackFragmentTags.size()) {
                    ft.hide(f);
                } else {
                    ft.show(f);
                }
            }
        }
        ft.commit();

    }

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (this.isDestroyed()) {
                return false;
            }
        }
        if (this.isFinishing()) {
            return false;
        }
        return true;
    }

    public void showFragment (Fragment f, boolean isTransit) {
        FragmentTransaction ft = getSafeFragmentManager().beginTransaction();
        if (stackFragmentTags.size() == 0) {
            Fragment mainF = getSafeFragmentManager().findFragmentByTag(KEY_MAIN_FRAGMENT);
            ft.hide(mainF);
        } else {
            Fragment fCurrent = getSafeFragmentManager().findFragmentByTag(stackFragmentTags.peek());
            ft.hide(fCurrent);
        }

        String tag = String.format("%s:%d", getClass().getName(), stackFragmentTags.size());
        if (getSafeFragmentManager().findFragmentByTag(tag) == null) {
            ft.add(getLayoutMain(), f, tag);
        } else {
            ft.replace(getLayoutMain(), f, tag);
        }
        ft.addToBackStack(null);

        if (isTransit) {
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        }
        ft.commit();
        stackFragmentTags.add(tag);
    }

    @Override
    public void onBackStackChanged() {
        Logger.debug(TAG, ">>>" + "onBackStackChanged");
        if (getSafeFragmentManager().getBackStackEntryCount() == stackFragmentTags.size()) {
            Logger.debug(TAG, ">>>" + "onBackStackChanged same size");
            return;
        }
        if (stackFragmentTags.size() > 0) {
            String tag = stackFragmentTags.pop();
            FragmentTransaction ft = getSafeFragmentManager().beginTransaction();
            Fragment f = getSafeFragmentManager().findFragmentByTag(tag);
            if (f != null) {
                ft.remove(f);
            }
            ft.commit();
        }
    }

    @Override
    public void onBackPressed() {
        Fragment fCurrent;
        if (stackFragmentTags.size() == 0) {
            fCurrent = getSafeFragmentManager().findFragmentByTag(KEY_MAIN_FRAGMENT);
        } else {
            fCurrent = getSafeFragmentManager().findFragmentByTag(stackFragmentTags.peek());
        }

        if (fCurrent instanceof IFragmentOnBackPressed)  {
            if (((IFragmentOnBackPressed)fCurrent).onFragmentBackPressed()) {
                return;
            }
        }
        super.onBackPressed();
    }

    public interface IFragmentOnBackPressed {
        boolean onFragmentBackPressed();
    }
}
