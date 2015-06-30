package son.nt.here.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;

import son.nt.here.R;

/**
 * Created by Sonnt on 6/30/15.
 */
public class GiftCodeDialog extends DialogFragment {

    public interface IDialogListener {
        void onSubmit (String code);
    }
    IDialogListener listener;
    public GiftCodeDialog()
    {
        // Empty constructor required for DialogFragment
    }

    public void onSetCallback (IDialogListener callback) {
        this.listener = callback;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.dialog_gift_code, container, false);
        final EditText edt = (EditText) view.findViewById(R.id.edt_gift);
        view.findViewById(R.id.confirm_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onSubmit(edt.getText().toString());
                    dismiss();
                }
            }
        });
        view.findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    dismiss();
            }
        });

        return view;
    }


}
