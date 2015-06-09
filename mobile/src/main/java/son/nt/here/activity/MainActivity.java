package son.nt.here.activity;

import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import son.nt.here.R;
import son.nt.here.base.AbsBaseActivity;
import son.nt.here.dto.MyPlaceDto;
import son.nt.here.fragment.AddFavFragment;
import son.nt.here.fragment.DetailFragment;
import son.nt.here.fragment.FavFragment;
import son.nt.here.fragment.HomeFragment;
import son.nt.here.fragment.SearchPlaceFragment;
import son.nt.here.promo_app.main.PromoAppFragment;

public class MainActivity extends AbsBaseActivity implements HomeFragment.OnFragmentInteractionListener,
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
    }

    private void initListener() {

    }

    private void initLayout() {
        viewAds = findViewById(R.id.main_ll_ads);

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
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
                .withDelayOnDrawerClose(1)
                .build();
        leftDrawer.addItem(new PrimaryDrawerItem().withName("HERE").withIcon(R.drawable.ic_fav_1));
        leftDrawer.addItem(new PrimaryDrawerItem().withName("Favourites").withIcon(R.drawable.ic_fav_1));
        leftDrawer.addItem(new PrimaryDrawerItem().withName("Search").withIcon(R.drawable.ic_fav_1));
        leftDrawer.addItem(new DividerDrawerItem());
        leftDrawer.addItem(new SecondaryDrawerItem().withName("Promotion").setEnabled(false));
        leftDrawer.addItem(new PrimaryDrawerItem().withName("Another Apps").withIcon(R.drawable.ic_fav_1));

        leftDrawer.setOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(AdapterView<?> adapterView, View view, int i, long l, IDrawerItem iDrawerItem) {
                Fragment f;
//                while ( stackFragmentTags.size() > 0) {
//                    getSafeFragmentManager().popBackStackImmediate();
//                }

                switch (i) {
                    case 0:
                        while (stackFragmentTags.size() > 0) {
                            getSafeFragmentManager().popBackStackImmediate();
                        }
                        break;
                    case 1:
                        f = FavFragment.newInstance("", "");
                        showFragment(f, true);
                        break;
                    case 2:
                        f = SearchPlaceFragment.newInstance("", "");
                        showFragment(f, true);
                        break;
                    case 5:
                        f = PromoAppFragment.newInstance("", "");
                        showFragment(f, true);
                        break;
                }
                return false;
            }
        });
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
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            leftDrawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
        }
    }
}
