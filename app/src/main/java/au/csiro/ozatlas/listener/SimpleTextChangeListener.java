package au.csiro.ozatlas.listener;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by sad038 on 28/4/17.
 */

public class SimpleTextChangeListener implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
    }
}
