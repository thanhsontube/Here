package son.nt.here.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

/**
 * Created by Sonnt on 4/17/15.
 */
public abstract class BaseFragment extends Fragment {
    protected FragmentManager fragmentManager;

    protected FragmentManager getSafeFragmentManager() {
        if (fragmentManager == null) {
            fragmentManager = getFragmentManager();
        }
        return fragmentManager;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        initLayout(view);
        initListener();
    }

    public abstract void initData();
    public abstract void initLayout(View view);
    public abstract void initListener();

    protected boolean isSafe() {
        if (this.isRemoving() || this.getActivity() == null || !this.isAdded() || this.isDetached() || this.getView() == null) {
            return false;
        }
        return true;

    }
}
