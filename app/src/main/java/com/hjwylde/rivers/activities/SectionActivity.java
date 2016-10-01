package com.hjwylde.rivers.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hjwylde.rivers.R;
import com.hjwylde.rivers.models.Section;
import com.hjwylde.rivers.models.Sections;

public class SectionActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String ID = "id";

    private Section mSection;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        Snackbar.make(view, R.string.edit_action_coming_soon, Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_section);

        int id = getIntent().getIntExtra(ID, -1);
        if (id != -1) {
            loadSection(id);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        NestedScrollView view = ((NestedScrollView) findViewById(R.id.scroll_view));
        view.setSmoothScrollingEnabled(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(ID, mSection.getId());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        loadSection(savedInstanceState.getInt(ID));
    }

    private void loadSection(int id) {
        // TODO (hjw): what if it's not there?
        mSection = Sections.find(id).get();

        setTitle(mSection.getName());
        ((ImageView) findViewById(R.id.image)).setImageResource(mSection.getImage());
        ((TextView) findViewById(R.id.description)).setText(mSection.getDescription());
    }
}