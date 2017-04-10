package au.csiro.ozatlas.base;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import javax.inject.Inject;

import au.csiro.ozatlas.OzAtlasApplication;
import au.csiro.ozatlas.R;
import au.csiro.ozatlas.activity.LoginActivity;
import au.csiro.ozatlas.manager.AtlasManager;
import au.csiro.ozatlas.manager.AtlasSharedPreferenceManager;
import au.csiro.ozatlas.rest.RestClient;

/**
 * Created by sad038 on 5/4/17.
 */

public class BaseActivity extends AppCompatActivity implements BaseActivityFragmentListener{
    @Inject
    protected AtlasSharedPreferenceManager sharedPreferences;

    @Inject
    protected RestClient restClient;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OzAtlasApplication.component().inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        AtlasManager.eventBus.register(this);
        if(!(this instanceof LoginActivity) && sharedPreferences.getAuthKey()==null){
            launchLoginActivity();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        AtlasManager.eventBus.unregister(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * show the spinner dialog
     */
    @Override
    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog = new ProgressDialog(this, R.style.OSSProgressBarTheme);
            mProgressDialog.getWindow().setDimAmount(0.2f);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(false);
        }

        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    /**
     * hide the spinner
     */
    @Override
    public void hideProgressDialog(){
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    /**
     *
     * @param editText to check the empty content
     * @return
     */
    @Override
    public boolean validate(EditText editText){
        return editText.getText().toString().length()>0;
    }

    /**
     *
     * @param coordinatorLayout
     * @param string message to show
     */
    @Override
    public void showSnackBarMessage(CoordinatorLayout coordinatorLayout, String string) {
        Snackbar.make(coordinatorLayout, string, Snackbar.LENGTH_LONG).show();
    }

    /**
     * launch Login Activity from anywhere
     */
    @Override
    public void launchLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
