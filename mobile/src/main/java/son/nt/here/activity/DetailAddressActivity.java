package son.nt.here.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;

import son.nt.here.R;
import son.nt.here.base.BaseActivity;
import son.nt.here.dto.MyPlaceDto;
import son.nt.here.fragment.DetailAddressFragment;
import son.nt.here.fragment.DetailFragment;

public class DetailAddressActivity extends BaseActivity implements DetailAddressFragment.OnFragmentInteractionListener, DetailFragment.OnFragmentInteractionListener{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_address);
        MyPlaceDto myPlaceDto = (MyPlaceDto) getIntent().getSerializableExtra("data");
        DetailAddressFragment detailAddressFragment = (DetailAddressFragment) getSafeFragmentManager().findFragmentByTag("detail");
        DetailFragment f = DetailFragment.newInstance(myPlaceDto, "");
        if (detailAddressFragment == null) {
            FragmentTransaction ft = getSafeFragmentManager().beginTransaction();
            detailAddressFragment = DetailAddressFragment.newInstance(myPlaceDto,"");
            ft.add(R.id.detail_ll_main, f, "detail");
            ft.commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail_address, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
