package org.androidtown.dietapp.Auth;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.usermgmt.LoginButton;
import com.kakao.util.exception.KakaoException;

import org.androidtown.dietapp.R;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import static com.kakao.util.helper.Utility.getPackageInfo;

public class AuthMainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    /*
    TODO:
        구글 로그인 후 카카오톡 로그인이 안되는 오류는 발견
        그리고 못고칠듯 ㅋ 너무 깊숙함
     */
    GoogleApiClient mGoogleApiClient;
    FirebaseAuth mAuth;
    LoginButton loginButton;

    @VisibleForTesting
    public ProgressDialog mProgressDialog;

    private static final int RC_SIGN_IN = 9001;
    String TAG = "AuthMain";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_main);


        Log.d(TAG,"현재 토큰값 로그아웃을 하긴 함"+Session.getCurrentSession().getAccessToken());
        // [START KAKAO SIGN IN]
        loginButton = (LoginButton) findViewById(R.id.login_button);
        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();



        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.emailButton).setOnClickListener(this);

        Session.getCurrentSession().addCallback(new KakaoSessionCallback());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sign_in_button:
                showProgressDialog();
                Log.d(TAG,"fuck google혹시몰라 한국어로 인식장애 해결 찡긋_<");
                signIn();
                break;
            case R.id.emailButton:
                startActivity(new Intent(AuthMainActivity.this,EmailPasswordActivity.class));
                break;

        }
    }

    private void signIn(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // [START_EXCLUDE]
                // [END_EXCLUDE]
            }
        }
        Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), ""+connectionResult, Toast.LENGTH_SHORT).show();
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            hideProgressDialog();
                            goToUserInfo();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d(TAG, "signInWithCredential:fail");
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if(mAuth.getCurrentUser()!=null){
            super.onBackPressed();
        }
        getKeyHash(this);
    }

    ///카카오톡 메소드 시작
    /*



     */
    ///KAKAOTALK METHOD START
    private Task<String> getFirebaseJwt(final String kakaoAccessToken) {
        final TaskCompletionSource<String> source = new TaskCompletionSource<>();

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://salty-caverns-92125.herokuapp.com" + "/verifyToken";
        HashMap<String, String> validationObject = new HashMap<>();
        validationObject.put("token", kakaoAccessToken);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(validationObject), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String firebaseToken = response.getString("firebase_token");
                    source.setResult(firebaseToken);
                } catch (Exception e) {
                    source.setException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
                source.setException(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", kakaoAccessToken);
                return params;
            }
        };

        queue.add(request);
        return source.getTask();
    }

    /**
     * Session callback class for Kakao Login. OnSessionOpened() is called after successful login.
     */
    private class KakaoSessionCallback implements ISessionCallback {


        @Override
        public void onSessionOpened() {
            showProgressDialog();
            String accessToken = Session.getCurrentSession().getAccessToken();
            getFirebaseJwt(accessToken).continueWithTask(new Continuation<String, Task<AuthResult>>() {
                @Override
                public Task<AuthResult> then(@NonNull Task<String> task) throws Exception {
                    String firebaseToken = task.getResult();
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    return auth.signInWithCustomToken(firebaseToken);
                }
            }).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        onStop();
                        hideProgressDialog();
                        goToUserInfo();
                    } else {
                        Toast.makeText(getApplicationContext(), "Failed to create a Firebase user.", Toast.LENGTH_LONG).show();
                        if (task.getException() != null) {
                            Log.e(TAG, task.getException().toString());
                        }
                        onStop();
                    }
                }
            });
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if (exception != null) {
                Log.e(TAG, "카톡 세션이 안열림"+exception.toString());
            }
        }
    }

    public void getKeyHash(final Context context) {
        PackageInfo packageInfo = getPackageInfo(context, PackageManager.GET_SIGNATURES);
        if (packageInfo == null)
            return;

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (NoSuchAlgorithmException e) {
                Log.w(TAG, "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
        return;
    }
    /*




    */
    ///KAKAOTALK METHOD END




    //유저 정보창으로 이동
    public void goToUserInfo(){
        Intent UserInfoIntenet=new Intent(AuthMainActivity.this,UserInfoActivity.class);
        startActivity(UserInfoIntenet);
        finish();
    }

    /*
    진행되고 있는지 안되는 지를 알려주는 다이얼로그
                                             쓰실분은 쓰세영
     */
    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }
}