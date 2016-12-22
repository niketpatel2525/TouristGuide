package com.project.touristguide;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.desai.vatsal.mydynamictoast.MyDynamicToast;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.project.touristguide.gps.GPS;

/**
 * Created by nbpat on 11/16/2016.
 */

public class GoogleLogIn extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;


    private Button btSignIn, btSignOut, btGuest;
    private RelativeLayout rlInvisibleLayout;

    private final String PREF_ACTIVITY = "GOOGLE_SIGN_IN";
    private final String PREF_SIGNIN = "SIGNIN";
    private final String PREF_GUEST = "GUEST";
    private final String PREF_USER = "USERNAME";
    private final String PREF_EMAIL = "EMAIL";
    Animation anim_down, anim_up, anim_zoom;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btSignIn = (Button) findViewById(R.id.btSignin);
        btSignOut = (Button) findViewById(R.id.btSignOut);
        btGuest = (Button) findViewById(R.id.btGuest);
        rlInvisibleLayout = (RelativeLayout) findViewById(R.id.invisibleLayout);
        anim_down = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        anim_up = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        updateUI();
        // Communication with Google API

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN.DEFAULT_SIGN_IN).requestEmail().build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        btSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isOnline()) {
//                    MyDynamicToast.errorMessage(GoogleLogIn.this, "error toast message..");
                    MyDynamicToast.informationMessage(GoogleLogIn.this, "Please connect to internet...");
                } else {
                    Intent signInInternt = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    startActivityForResult(signInInternt, RC_SIGN_IN);
                }

            }
        });

        btSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        rlInvisibleLayout.setVisibility(View.INVISIBLE);
                        rlInvisibleLayout.setAnimation(anim_up);


                        SharedPreferences pref = getApplicationContext().getSharedPreferences(PREF_ACTIVITY, MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putBoolean(PREF_SIGNIN, false);
                        editor.putString(PREF_USER, "Sign In");
                        editor.putBoolean(PREF_GUEST, false);
                        editor.commit();


                        btSignIn.setText("Sign In");
                    }
                });
            }
        });

        btGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                SharedPreferences pref = getApplicationContext().getSharedPreferences(PREF_ACTIVITY, MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean(PREF_SIGNIN, false);
                editor.putString(PREF_USER, "Sign In");
                editor.putBoolean(PREF_GUEST, true);
                editor.commit();


                if (!isOnline()) {
                    //MyDynamicToast.errorMessage(GoogleLogIn.this, "error toast message..");
                    MyDynamicToast.informationMessage(GoogleLogIn.this, "Please connect to internet...");
                } else {
                    Intent startMap = new Intent(getApplicationContext(), CurrentLocationMapFragement.class);
                    startActivity(startMap);
                    overridePendingTransition(R.anim.fade_in, R.anim.zoom_in);
                }
            }
        });

    }

    @Override
    protected void onPause() {
        updateUI();
        super.onPause();

    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void updateUI() {

        SharedPreferences pref = getApplicationContext().getSharedPreferences(PREF_ACTIVITY, getApplicationContext().MODE_PRIVATE);
        boolean isSignIn = pref.getBoolean(PREF_SIGNIN, false);
        String username = pref.getString(PREF_USER, "Sign In");

        if (!username.equals("Sign In"))
            btSignIn.setText("Continue as:" + username);
        else
            btSignIn.setText(username);


        if (isSignIn) {
            rlInvisibleLayout.setVisibility(View.VISIBLE);
            rlInvisibleLayout.setAnimation(anim_down);
        } else {
            rlInvisibleLayout.setVisibility(View.INVISIBLE);
            rlInvisibleLayout.setAnimation(anim_up);
        }

    }

 /*  *//* @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }*//*

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }*/

    final Handler handler = new Handler();

    private void handleSignInResult(GoogleSignInResult result) {

        if (result.isSuccess()) {

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    rlInvisibleLayout.setVisibility(View.VISIBLE);
                    rlInvisibleLayout.setAnimation(anim_down);
                }
            }, 10);


            GoogleSignInAccount acct = result.getSignInAccount();
            String username = acct.getDisplayName();


            SharedPreferences pref = getApplicationContext().getSharedPreferences(PREF_ACTIVITY, MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean(PREF_SIGNIN, true);
            editor.putString(PREF_USER, username);
            editor.putString(PREF_EMAIL, acct.getEmail());
            editor.putBoolean(PREF_GUEST, false);
            editor.commit();

            GPS gps = new GPS(getApplicationContext());
            if (!gps.isLocationEnabled()) {
                MyDynamicToast.informationMessage(GoogleLogIn.this, "Please turn on GPS...");
                showDialogGPS();

            } else {
                Intent startMap = new Intent(getApplicationContext(), CurrentLocationMapFragement.class);
                startActivity(startMap);
                overridePendingTransition(R.anim.fade_in, R.anim.zoom_in);
            }


        } else {
            // Alert Box -- Please Sign in to continue
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned  from launching  the Intent  from GoogleSignInAPI
        if (requestCode == RC_SIGN_IN) {
            try {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(result);
            } catch (Exception e) {

            }

        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(GoogleLogIn.this);
        builder.setTitle("Confirm");
        builder.setMessage("Are you sure you want to exit");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();


    }

    private void showDialogGPS() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Enable GPS");
        builder.setMessage("Please enable GPS");
        builder.setInverseBackgroundForced(true);
        builder.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                startActivity(
                        new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
//        builder.setNegativeButton("Ignore", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
        android.app.AlertDialog alert = builder.create();
        alert.show();
    }

}
