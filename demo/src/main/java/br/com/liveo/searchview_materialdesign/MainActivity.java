package br.com.liveo.searchview_materialdesign;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import br.com.liveo.searchliveo.OnSearchListener;
import br.com.liveo.searchliveo.SearchLiveo;

public class MainActivity extends AppCompatActivity implements OnSearchListener {

    private SearchLiveo mSearchLiveo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        mSearchLiveo = findViewById(R.id.search_liveo);
        mSearchLiveo.with(this).
                statusBarHideColor(R.color.colorPrimaryDark).
                statusBarShowColor(R.color.action_mode_primary_Dark).
                build();
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
            if (mSearchLiveo != null) {
                mSearchLiveo.show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void changedSearch(CharSequence text) {

    }

    @Override
    public void hideSearch() {
        if (mSearchLiveo != null){
            mSearchLiveo.hide();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == SearchLiveo.REQUEST_CODE_SPEECH_INPUT) {
                if (mSearchLiveo != null) {
                    mSearchLiveo.resultVoice(requestCode, resultCode, data);
                }
            }
        }
    }
}
