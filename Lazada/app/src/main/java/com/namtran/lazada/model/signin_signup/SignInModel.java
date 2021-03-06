package com.namtran.lazada.model.signin_signup;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.namtran.lazada.connection.internet.DownloadJson;
import com.namtran.lazada.view.home.HomeActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by namtr on 02/05/2017.
 */

public class SignInModel {
    private AccessToken accessToken;
    private AccessTokenTracker tracker;

    // lấy token đăng nhập fb
    public AccessToken layAccessTokenFB() {
        tracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                accessToken = currentAccessToken;
            }
        };
        accessToken = AccessToken.getCurrentAccessToken();
        destroyTracker();
        return accessToken;
    }

    public GoogleApiClient getApi(FragmentActivity activity, GoogleApiClient.OnConnectionFailedListener listener) {
        GoogleApiClient googleApiClient;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(activity)
                .enableAutoManage(activity, listener)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        return googleApiClient;
    }

    public GoogleSignInResult getResult(GoogleApiClient googleApiClient) {
        OptionalPendingResult<GoogleSignInResult> result = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (result.isDone()) {
            return result.get();
        }
        return null;
    }

    // dừng theo dõi access token hiện tại
    private void destroyTracker() {
        tracker.stopTracking();
    }

    // kiểm tra đăng nhập, nếu đăng nhập thành công thì lưu cache tên người dùng
    public boolean checkingLogin(Context context, String username, String password) {
        List<HashMap<String, String>> attrs = new ArrayList<>();
        String url = HomeActivity.SERVER_NAME + "dangnhap.php";

        HashMap<String, String> attrUser = new HashMap<>();
        attrUser.put("username", username);
        attrs.add(attrUser);

        HashMap<String, String> attrPass = new HashMap<>();
        attrPass.put("password", password);
        attrs.add(attrPass);

        DownloadJson downloadJson = new DownloadJson(url, attrs);
        downloadJson.execute();

        try {
            String json = downloadJson.get();
            if (json != null) {
                JSONObject object = new JSONObject(json);
                boolean isSuccessed = object.getBoolean("addCommentResult");
                if (isSuccessed) {
                    String tenNV = object.getString("tenNV");
                    updateLoginCache(context, tenNV);
                    return true;
                }
            }
        } catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    // trả về tên người dùng được lưu trong cache
    public String getLoginCache(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("login", Context.MODE_PRIVATE);
        return preferences.getString("tenNV", "");
    }

    // cập nhật lại tên người dùng
    public void updateLoginCache(Context context, String tenNV) {
        SharedPreferences preferences = context.getSharedPreferences("login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("tenNV", tenNV);
        editor.apply();
    }
}
