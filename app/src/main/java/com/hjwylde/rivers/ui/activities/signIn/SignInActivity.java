package com.hjwylde.rivers.ui.activities.signIn;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.hjwylde.rivers.R;
import com.hjwylde.rivers.ui.activities.BaseActivity;
import com.hjwylde.rivers.ui.activities.home.HomeActivity;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@UiThread
public final class SignInActivity extends BaseActivity {
    private static final String TAG = SignInActivity.class.getSimpleName();

    private static final int REQUEST_CODE_SIGN_IN = 0;

    @BindView(R.id.root_container)
    View mRootView;
    @BindView(R.id.sign_in)
    SignInButton mSignInButton;

    @BindString(R.string.pref_key_google_account_email)
    String mSharedPrefGoogleAccountEmail;
    @BindString(R.string.pref_key_is_first_launch)
    String mSharedPrefIsFirstLaunch;

    private SharedPreferences mSharedPreferences;

    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(result);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_in);

        ButterKnife.bind(this);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        initGoogleApiClient();
    }

    @OnClick(R.id.sign_in)
    void onSignInClick() {
        OptionalPendingResult<GoogleSignInResult> pendingResult = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (pendingResult.isDone()) {
            handleSignInResult(pendingResult.get());
        } else {
            pendingResult.setResultCallback(result -> {
                if (result.isSuccess()) {
                    Log.i(TAG, "Silent sign in successful");

                    setAccount(result.getSignInAccount());

                    startHomeActivity();
                } else {
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN);
                }
            });
        }
    }

    @OnClick(R.id.skip)
    void onSkipClick() {
        startHomeActivity();
    }

    private void handleSignInResult(@NonNull GoogleSignInResult result) {
        Log.i(TAG, "Sign in successful: " + result.isSuccess());

        if (result.isSuccess()) {
            setAccount(result.getSignInAccount());

            startHomeActivity();
        } else if (result.getStatus().getStatusCode() != GoogleSignInStatusCodes.SIGN_IN_CANCELLED) {
            Log.w(TAG, "Sign in status: " + result.getStatus().getStatusCode());

            Snackbar snackbar = Snackbar.make(mRootView, R.string.error_onSignIn, Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    private void initGoogleApiClient() {
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, result -> {
                    Log.w(TAG, result.getErrorMessage());

                    mSignInButton.setEnabled(false);

                    Snackbar snackbar = Snackbar.make(mRootView, R.string.error_onGoogleApiConnectClient, Snackbar.LENGTH_LONG);
                    snackbar.show();
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                .build();
    }

    private void setAccount(GoogleSignInAccount account) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(mSharedPrefGoogleAccountEmail, account.getEmail());
        editor.apply();
    }

    private void setIsFirstLaunch() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(mSharedPrefIsFirstLaunch, false);
        editor.apply();
    }

    private void startHomeActivity() {
        setIsFirstLaunch();

        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);

        finish();
    }
}