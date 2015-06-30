package son.nt.here.activity;

import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import son.nt.here.MsConst;
import son.nt.here.R;
import son.nt.here.base.AbsBaseActivity;
import son.nt.here.dialog.GiftCodeDialog;
import son.nt.here.dto.MyPlaceDto;
import son.nt.here.fragment.AddFavFragment;
import son.nt.here.fragment.DetailFragment;
import son.nt.here.fragment.FavFragment;
import son.nt.here.fragment.HomeFragment;
import son.nt.here.fragment.SearchPlaceFragment;
import son.nt.here.promo_app.GiftCodeParseLoader;
import son.nt.here.promo_app.ParseManager;
import son.nt.here.promo_app.main.PromoAppFragment;
import son.nt.here.utils.KeyboardUtils;
import son.nt.here.utils.Logger;
import son.nt.here.utils.PreferenceUtil;
import son.nt.here.utils.TsFeedback;
import son.nt.here.utils.TsGaTools;

public class HomeActivity extends AbsBaseActivity implements HomeFragment.OnFragmentInteractionListener,
        DetailFragment.OnFragmentInteractionListener, SearchPlaceFragment.OnFragmentInteractionListener,
        FavFragment.OnFragmentInteractionListener, AddFavFragment.OnFragmentInteractionListener,
        PromoAppFragment.OnFragmentInteractionListener {

    private Toolbar toolbar;
    private View viewAds;
    private Drawer leftDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolBar();
        initLeftDrawer(savedInstanceState);
        initLayout();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData(PreferenceUtil.getPreference(getApplicationContext(), MsConst.KEY_GIFT_CODE, "test"), false);
    }

    private void initListener() {

    }

    private void initLayout() {
        viewAds = findViewById(R.id.ll_ads);
//        viewAds.setVisibility(View.GONE);
        adMob();

    }

    @Override
    protected int getLayoutMain() {
        return R.id.ll_main;
    }

    @Override
    protected Fragment getMainFragment(Bundle saveInstanceState) {
        return HomeFragment.newInstance("", "");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            if (viewAds.getVisibility() == View.GONE) {
                viewAds.setVisibility(View.VISIBLE);
            } else {
                viewAds.setVisibility(View.GONE);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.home_toolbar);
        toolbar.setTitle("HERE");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        getDelegate().setSupportActionBar(toolbar);

    }

    private void initLeftDrawer(Bundle savedInstanceState) {
        leftDrawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withDisplayBelowToolbar(true)
                .withActionBarDrawerToggleAnimated(true)
                .withDrawerGravity(Gravity.LEFT)
                .withSavedInstance(savedInstanceState)
                .withSelectedItem(0)
                .withOnDrawerNavigationListener(new Drawer.OnDrawerNavigationListener() {
                    @Override
                    public boolean onNavigationClickListener(View view) {
                        getSafeFragmentManager().popBackStackImmediate();
                        return true;
                    }
                })
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View view) {

                    }

                    @Override
                    public void onDrawerClosed(View view) {
                        switch (leftDrawer.getCurrentSelection()) {

                            case 0:
                                toolbar.setTitle("HERE");
                                break;
                            case 1:
                                toolbar.setTitle("FAVOURITE");
                                break;
                            case 2:
                                break;
                            case 5:
                                toolbar.setTitle("ANOTHER APP");
                                break;
                        }

                    }

                    @Override
                    public void onDrawerSlide(View view, float v) {

                    }
                })
                .withDelayOnDrawerClose(1)
                .build();
        leftDrawer.addItem(new PrimaryDrawerItem().withName("HERE").withIcon(R.drawable.ic_fav_1));
        leftDrawer.addItem(new PrimaryDrawerItem().withName("Favourites").withIcon(R.drawable.ic_fav_1));
        leftDrawer.addItem(new PrimaryDrawerItem().withName("Search").withIcon(R.drawable.ic_fav_1));
        leftDrawer.addItem(new DividerDrawerItem());
        leftDrawer.addItem(new SecondaryDrawerItem().withName("").setEnabled(false));
        leftDrawer.addItem(new PrimaryDrawerItem().withName("Another Apps").withIcon(R.drawable.ic_fav_1));
        leftDrawer.addItem(new PrimaryDrawerItem().withName("Rating").withIcon(R.drawable.ic_fav_1));
        leftDrawer.addItem(new PrimaryDrawerItem().withName("Feedback").withIcon(R.drawable.ic_fav_1));
        leftDrawer.addItem(new DividerDrawerItem());
        leftDrawer.addItem(new PrimaryDrawerItem().withName("Gift Code").withIcon(R.drawable.ic_fav_1));

        leftDrawer.setOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(AdapterView<?> adapterView, View view, int i, long l, IDrawerItem iDrawerItem) {
                KeyboardUtils.hideKeyboard(HomeActivity.this);
                Fragment f;
                switch (i) {
                    case 0:
                        TsGaTools.trackPages("/home");
                        while (stackFragmentTags.size() > 0) {
                            getSafeFragmentManager().popBackStackImmediate();
                        }
                        break;
                    case 1:
                        TsGaTools.trackPages("/fav");
                        f = FavFragment.newInstance("", "");
                        showFragment(f, true);
                        break;
                    case 2:
                        TsGaTools.trackPages("/search");
                        f = SearchPlaceFragment.newInstance("", "");
                        showFragment(f, true);
                        break;
                    case 5:
                        TsGaTools.trackPages("/promo");
                        f = PromoAppFragment.newInstance("", "");
                        showFragment(f, true);
                        break;
                    case 6:
                        TsGaTools.trackPages("/rating");
                        TsFeedback.rating(getApplicationContext());
                        break;
                    case 7:
                        TsGaTools.trackPages("/feedback");
                        TsFeedback.sendEmailbyGmail(getApplicationContext(), "thanhsontube@gmail.com", getString(R.string.app_name), "I'd like to say that: ");
                        break;
                    case 9:
                        TsGaTools.trackPages("/giftCode");
                        showGiftCodeDialog();
                        break;
                }
                return false;
            }
        });
    }
    private void getData(final String code, final boolean isConfirm) {
        ParseManager parseManager = new ParseManager();
        parseManager.load(new GiftCodeParseLoader(this, "PromoCode") {
            @Override
            public void onSuccess(String result) {
                Logger.debug(TAG, ">>>" + "onSuccess:" + result);
                if (!isSafe()) {
                    return;
                }
                if (result.equalsIgnoreCase(code)) {
                    if (viewAds != null) {
                        if(isConfirm) {

                            Toast.makeText(getApplicationContext(),"Congratulations", Toast.LENGTH_SHORT).show();
                        }
                        PreferenceUtil.setPreference(getApplicationContext(), MsConst.KEY_GIFT_CODE, code);
                        viewAds.setVisibility(View.GONE);
                    } else {
                        if (isConfirm) {

                            Toast.makeText(getApplicationContext(),"Are you kidding me :v", Toast.LENGTH_SHORT).show();
                        }
                        viewAds.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFail(Throwable e) {
                Toast.makeText(getApplicationContext(),"There is something wrong with network :(", Toast.LENGTH_SHORT).show();
                Logger.error(TAG, ">>>" + "e:" + e.toString());

            }
        });
    }

    private void showGiftCodeDialog() {
        GiftCodeDialog dialog = (GiftCodeDialog) getSafeFragmentManager().findFragmentByTag("gift");
        FragmentTransaction ft = getSafeFragmentManager().beginTransaction();
        if (dialog != null) {
            ft.remove(dialog);
        }
        dialog = new GiftCodeDialog();
        dialog.onSetCallback(new GiftCodeDialog.IDialogListener() {
            @Override
            public void onSubmit(String code) {
                //
                getData(code, true);

            }
        });
        ft.commit();
        dialog.show(getSafeFragmentManager(), "gift");
    }

    @Override
    public void goDetail(MyPlaceDto location) {
        DetailFragment f = DetailFragment.newInstance(location, "");
        showFragment(f, true);
    }

    @Override
    public void onAddFav(MyPlaceDto myPlaceDto) {
        AddFavFragment f = AddFavFragment.newInstance(myPlaceDto, "");
        showFragment(f, true);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        leftDrawer.getActionBarDrawerToggle().syncState();
    }

    @Override
    public void onBackStackChanged() {
        super.onBackStackChanged();
        if (stackFragmentTags.size() > 0) {
            Fragment f = getSafeFragmentManager().findFragmentByTag(stackFragmentTags.peek());
            if (f instanceof FavFragment || f instanceof SearchPlaceFragment
                    || f instanceof PromoAppFragment) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                leftDrawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
            } else {
                leftDrawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(false);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }


        } else {
            toolbar.setTitle("HERE");
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            leftDrawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
        }
    }

    @Override
    public void onFavRowClick(MyPlaceDto dto) {
        DetailFragment f = DetailFragment.newInstance(dto, "");
        showFragment(f, true);
    }

    @Override
    public void onSelected(MyPlaceDto myPlaceDto) {
        TsGaTools.trackPages("search:" + myPlaceDto.formatted_address);
        leftDrawer.setSelection(0);
        KeyboardUtils.hideKeyboard(HomeActivity.this);
        getSafeFragmentManager().popBackStackImmediate();
        HomeFragment f = (HomeFragment) getSafeFragmentManager().findFragmentByTag(AbsBaseActivity.KEY_MAIN_FRAGMENT);
        if (f != null) {
            f.addPins(myPlaceDto);
        }
    }

    @Override
    public void onBack() {
        getSafeFragmentManager().popBackStackImmediate();
    }

    private void adMob()
    {
        //ad mob
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                                .addTestDevice("C5C5650D2E6510CF583E2D3D94B69B57")
                //                .addTestDevice("224370EA280CB464C7C922F369F77C69")
                .build();

        //my s3
        mAdView.loadAd(adRequest);
    }
}
