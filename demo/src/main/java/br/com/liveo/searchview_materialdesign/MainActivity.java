package br.com.liveo.searchview_materialdesign;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;

import br.com.liveo.searchliveo.SearchLiveo;
import br.com.liveo.searchview_materialdesign.base.BaseActivity;
import br.com.liveo.searchview_materialdesign.databinding.ActivityMainBinding;
import br.com.liveo.searchview_materialdesign.model.Company;

public class MainActivity extends BaseActivity implements SearchLiveo.OnSearchListener {

    private MainAdapter mAdapter;
    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.onInitView();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        this.fetchCompanies();
    }

    private void onInitView() {
        mBinding = (ActivityMainBinding) this.bindView(R.layout.activity_main);
        this.onInitToolbar(mBinding.toolbar, R.string.app_name);

        mBinding.searchLiveo.
                with(this).
                removeMinToSearch().
                removeSearchDelay().
                build();

        if (mBinding.includeMain != null) {
            mBinding.includeMain.recyclerView.setHasFixedSize(true);
            mBinding.includeMain.recyclerView.setLayoutManager(new LinearLayoutManager(this));

            mBinding.includeMain.swipeContainer.setEnabled(false);
            mBinding.includeMain.swipeContainer.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent,
                    R.color.colorPrimary, R.color.colorAccent);
        }
    }

    private void fetchCompanies() {
        mAdapter = new MainAdapter(Company.getCompanies());
        mBinding.includeMain.recyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            mBinding.searchLiveo.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void changedSearch(CharSequence text) {
        if (mAdapter != null) {
            mAdapter.searchCompanyes(text);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == SearchLiveo.REQUEST_CODE_SPEECH_INPUT) {
                mBinding.searchLiveo.resultVoice(requestCode, resultCode, data);
            }
        }
    }
}
