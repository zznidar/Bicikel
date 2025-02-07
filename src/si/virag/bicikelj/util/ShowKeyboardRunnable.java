package si.virag.bicikelj.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public final class ShowKeyboardRunnable implements Runnable {
    private final View view;
    private final Context context;

    public ShowKeyboardRunnable(Context context, View view) {
        this.context = context;
        this.view = view;
    }

    @Override
    public void run() {
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }
}
