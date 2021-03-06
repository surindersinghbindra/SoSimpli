package com.think360.sosimply.ui.activities.login;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.hbb20.CountryCodeDialog;
import com.hbb20.CountryCodePicker;

import com.think360.sosimply.AppController;
import com.think360.sosimply.R;

import com.think360.sosimply.databinding.ActivityLoginBinding;
import com.think360.sosimply.manager.ApiService;
import com.think360.sosimply.presenter.LoginPresenter;
import com.think360.sosimply.ui.activities.HomeActivity;
import com.think360.sosimply.AppConstants;
import com.think360.sosimply.utils.KeyboardUtil;

import javax.inject.Inject;


import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class LoginActivity extends AppCompatActivity implements LoginPresenter.View, View.OnClickListener {


    @Inject
    ApiService apiService;

    private ActivityLoginBinding activityMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppController) getApplication()).getComponent().inject(this);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        activityMainBinding.btnSignIn.setOnClickListener(this);
        activityMainBinding.tvForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
            }
        });
    }

    @Override
    public void loginSuccessful(String firstName, int workerId) {
        AppController.getSharedPrefEditor().putBoolean(AppConstants.IS_REMEMBER_TAPPED, activityMainBinding.switchGprs.isChecked()).apply();
        AppController.getSharedPrefEditor().putBoolean(AppConstants.IS_LOGIN, true).apply();
        callMainActivity();
    }


    @Override
    public void loginFailed(Throwable t) {
        Snackbar.make(activityMainBinding.loginMainLayout, t.getMessage() + "", Snackbar.LENGTH_SHORT).show();

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private boolean isValidMobile(String phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSignIn:

                if (TextUtils.isEmpty(activityMainBinding.etEmail.getText().toString().trim()) || TextUtils.isEmpty(activityMainBinding.etPassword.getText().toString().trim())) {
                    if (!TextUtils.isEmpty(activityMainBinding.etEmail.getText().toString().trim())) {
                        Toast.makeText(LoginActivity.this, "Email can't be empty", Toast.LENGTH_SHORT).show();

                    } else {
                        Snackbar.make(activityMainBinding.loginMainLayout,"Password can't be empty",Snackbar.LENGTH_SHORT).show();

                    }
                } else {

                    if (isValidMobile(activityMainBinding.etEmail.getText().toString().trim())) {
                        Log.d("ISVALID", "true");

                        CountryCodeDialog.openCountryCodeDialog(activityMainBinding.ccp);
                        activityMainBinding.ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
                            @Override
                            public void onCountrySelected() {
                                new LoginPresenter(LoginActivity.this, apiService, activityMainBinding.ccp.getSelectedCountryCodeWithPlus() + activityMainBinding.etEmail.getText().toString().trim(), activityMainBinding.etPassword.getText().toString().trim());
                                Log.d("COUNTRY", activityMainBinding.ccp.getSelectedCountryCodeWithPlus());
                            }
                        });

                    } else if (KeyboardUtil.isValidEmail(activityMainBinding.etEmail.getText().toString().trim())) {
                        new LoginPresenter(LoginActivity.this, apiService, activityMainBinding.etEmail.getText().toString().trim(), activityMainBinding.etPassword.getText().toString().trim());

                    } else {
                        Snackbar.make(activityMainBinding.loginMainLayout, "Please fill valid email or mobile number", Snackbar.LENGTH_SHORT).show();
                    }

                }
                break;
        }
    }

    private void callMainActivity() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
        overridePendingTransition(R.anim.zoom_exit, 0);
    }

}
