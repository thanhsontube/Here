package son.nt.here.utils;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

/**
 * Useful functions to control the android soft keyboard 
 */
public class KeyboardUtils
{
    /**
     * Hides the soft keyaboard input from given {@code activity}. Uses the element that is current focused to dismiss the keyboard.
     */
    public static void hideKeyboard(Activity activity)
    {
        if (activity == null || activity.getCurrentFocus() == null)
        {
            return;
        }

        ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(activity
                        .getCurrentFocus().getWindowToken(), 0);
    }
}
