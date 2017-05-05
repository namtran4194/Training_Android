package com.namtran.lazada.view.trangchu;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.namtran.lazada.R;
import com.namtran.lazada.adapter.ExpandableLVAdapter;
import com.namtran.lazada.adapter.ViewPagerAdapterHome;
import com.namtran.lazada.model.dangnhap_dangky.ModelDangNhap;
import com.namtran.lazada.model.objectclass.LoaiSanPham;
import com.namtran.lazada.presenter.trangchu.xulymenu.PresenterXuLyMenu;
import com.namtran.lazada.view.dangnhap_dangky.DangNhapVaDangKyActivity;
import com.namtran.lazada.view.dangnhap_dangky.fragment.FragmentDangNhap;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class TrangChuActivity extends AppCompatActivity implements ViewXuLyMenu, GoogleApiClient.OnConnectionFailedListener, AppBarLayout.OnOffsetChangedListener {
    private static final String TAG = "TrangChuActivity";
    public static final String SERVER = "http://192.168.1.227:8000/lazada";
    private static final int REQUEST_CODE_LOGIN = 0;
    private Toolbar mToolbar;
    private TabLayout mTabs;
    private ViewPager mViewPager;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ExpandableListView mExpandableListView;
    private AccessToken mFbToken;
    private GoogleSignInResult mGgToken;
    private Menu mMenu;
    private MenuItem mMenuLogin;
    private PresenterXuLyMenu mXuLyMenu;
    private ModelDangNhap mModelDangNhap;
    private GoogleApiClient mGoogleApiClient;
    private CollapsingToolbarLayout mCollapsingToolbar;
    private LinearLayout mLayoutSearch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trang_chu_activity);

        init();

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mViewPager.setAdapter(new ViewPagerAdapterHome(getSupportFragmentManager()));
        mTabs.setupWithViewPager(mViewPager);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.openDescRes, R.string.closeDescRes);
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        mXuLyMenu = new PresenterXuLyMenu(this);
        mModelDangNhap = new ModelDangNhap();

        mXuLyMenu.layDanhSachMenu();
        mGoogleApiClient = mModelDangNhap.getGoogleApiClient(this, this);
    }

    private void init() {
        mToolbar = (Toolbar) findViewById(R.id.home_toolbar);
        mTabs = (TabLayout) findViewById(R.id.home_tabLayout);
        mViewPager = (ViewPager) findViewById(R.id.home_viewPager);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.home_drawerLayout);
        mExpandableListView = (ExpandableListView) findViewById(R.id.home_expandableLV);
        mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.home_collapsingToolbar);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.home_appBar);
        appBarLayout.addOnOffsetChangedListener(this);
        mLayoutSearch = (LinearLayout) findViewById(R.id.home_layout_search);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        mDrawerToggle.syncState();
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
            mDrawerLayout.closeDrawers();
        else
            super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.home_menu, menu);
        mMenuLogin = menu.findItem(R.id.menu_login);
        MenuItem menuLogout = menu.findItem(R.id.menu_logout);

        // lấy facebookToken đăng nhập fb
        mFbToken = mXuLyMenu.layTokenNguoiDungFB();
        mGgToken = mModelDangNhap.getGoogleSignInResult(mGoogleApiClient);

        // lấy thông tin người dùng từ facebookToken
        if (mFbToken != null) {
            GraphRequest request = GraphRequest.newMeRequest(mFbToken, new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    try {
                        String name = object.getString("name");
                        mMenuLogin.setTitle(String.valueOf("Hi, " + name));
                    } catch (JSONException e) {
                        Log.d(TAG, e.toString());
                    }
                }
            });

            // truyền các tham số cần lấy
            Bundle parameter = new Bundle();
            parameter.putString("fields", "name");
            request.setParameters(parameter);
            request.executeAsync();
        }

        // lấy thông tin người dùng từ googleToken
        if (mGgToken != null) {
            String name = mGgToken.getSignInAccount().getDisplayName();
            mMenuLogin.setTitle(String.valueOf("Hi, " + name));
        }

        if (mFbToken != null || mGgToken != null) {
            // hiển thị mMenu Đăng xuất
            menuLogout.setVisible(true);
        }

        // lưu mMenu hiện tại
        this.mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item))
            return true;

        int id = item.getItemId();
        switch (id) {
            case R.id.menu_login:
                if (mFbToken == null && mGgToken == null) { // chưa đăng nhập
                    Intent loginIntent = new Intent(this, DangNhapVaDangKyActivity.class);
                    startActivityForResult(loginIntent, REQUEST_CODE_LOGIN);
                }
                break;
            case R.id.menu_logout:
                // đăng xuất
                if (mFbToken != null) {
                    LoginManager.getInstance().logOut();
                } else {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                }
                this.onCreateOptionsMenu(mMenu);
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_LOGIN) {
            // resultCode từ FragmentDangNhap
            if (resultCode == FragmentDangNhap.RESULT_CODE_LOGIN) {
                this.onCreateOptionsMenu(mMenu);
            }
        }
    }

    @Override
    public void hienThiDanhSachMenu(List<LoaiSanPham> loaiSanPhams) {
        ExpandableLVAdapter adapter = new ExpandableLVAdapter(this, loaiSanPhams);
        mExpandableListView.setAdapter(adapter);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, connectionResult.toString());
    }

    // AppBarLayout gồm Toolbar, LinearLayout chứa thanh tìm kiếm và TabLayout
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        // ẩn LinearLayout chứa button tìm kiếm + camera khi scroll lên
        int height = mCollapsingToolbar.getHeight() + verticalOffset;
        Log.d(TAG, mCollapsingToolbar.getHeight() + "-" + verticalOffset + "-" + ViewCompat.getMinimumHeight(mCollapsingToolbar));

        if (height <= ViewCompat.getMinimumHeight(mCollapsingToolbar)) {
            mLayoutSearch.animate().alpha(0).setDuration(200);
            MenuItem menuSearch = mMenu.findItem(R.id.menu_seach);
            menuSearch.setVisible(true);
        } else { // lần đầu chạy app sẽ vào trường hợp này
            mLayoutSearch.animate().alpha(1).setDuration(200);
            // do ban đầu mMenu = null nên phải kiểm tra
            if (mMenu != null) {
                MenuItem menuSearch = mMenu.findItem(R.id.menu_seach);
                menuSearch.setVisible(false);
            }
        }
    }
}
