package au.csiro.ozatlas.login;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.widget.EditText;

import javax.inject.Inject;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.base.BaseActivity;
import au.csiro.ozatlas.rest.RestClient;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sad038 on 6/4/17.
 */

public class LoginActivity extends BaseActivity {
    @BindView(R.id.inputLayoutUsername)
    TextInputLayout inputLayoutUsername;
    @BindView(R.id.editUsername)
    EditText editUsername;
    @BindView(R.id.inputLayoutPassword)
    TextInputLayout inputLayoutPassword;
    @BindView(R.id.editPassword)
    EditText editPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.loginButton)
    void loginButton(){

    }

    @OnClick(R.id.registerLabel)
    void registerLabel(){

    }
}
