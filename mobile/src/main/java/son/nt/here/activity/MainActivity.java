package son.nt.here.activity;

import android.net.Uri;
import android.os.Bundle;
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
import son.nt.here.fragment.DetailFragment;
import son.nt.here.fragment.HomeFragment;

public class MainActivity extends AbsBaseActivity implements HomeFragment.OnFragmentInteractionListener, DetailFragment.OnFragmentInteractionListener {

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
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(AdapterView<?> adapterView, View view, int i, long l, IDrawerItem iDrawerItem) {
                        return false;
                    }
                })
                .build();
        leftDrawer.addItem(new PrimaryDrawerItem().withName("Favourites").withIcon(R.drawable.ic_fav_1));
        leftDrawer.addItem(new PrimaryDrawerItem().withName("Favourites").withIcon(R.drawable.ic_fav_1));
        leftDrawer.addItem(new PrimaryDrawerItem().withName("Favourites").withIcon(R.drawable.ic_fav_1));
        leftDrawer.addItem(new DividerDrawerItem());
        leftDrawer.addItem(new PrimaryDrawerItem().withName("Favourites").withIcon(R.drawable.ic_fav_1));
        leftDrawer.addItem(new PrimaryDrawerItem().withName("Favourites").withIcon(R.drawable.ic_fav_1));
        leftDrawer.addItem(new DividerDrawerItem());
        leftDrawer.addItem(new SecondaryDrawerItem().withName("Configuration"));
        leftDrawer.addItem(new PrimaryDrawerItem().withName("Favourites").withIcon(R.drawable.ic_fav_1));
    }

    @Override
    public void goDetail(MyPlaceDto location) {
        DetailFragment f = DetailFragment.newInstance(location, "");
        showFragment(f, true);
    }
}
