package com.hjwylde.rivers.activities;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hjwylde.rivers.R;
import com.hjwylde.rivers.RiversApplication;
import com.hjwylde.rivers.models.Image;
import com.hjwylde.rivers.models.Section;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class SectionActivity extends AppCompatActivity implements View.OnClickListener, AppBarLayout.OnOffsetChangedListener {
    public static final String ID = "id";
    public static final String SECTION = "section";
    private static final String LOG_TAG = "SectionActivity";

    private Subscription mSectionSubscription;
    private Subscription mImageSubscription;
    private Section mSection;
    private Image mImage;

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
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        View detailsView = findViewById(R.id.details);

        int expandedPadding = (int) getResources().getDimension(R.dimen.details_card_view_padding_top);
        int collapsedPadding = (int) getResources().getDimension(R.dimen.text_margin);
        float ratio = verticalOffset / (float) appBarLayout.getTotalScrollRange();

        int topPadding = (int) (expandedPadding + ratio * (expandedPadding - collapsedPadding));

        detailsView.setPadding(0, topPadding, 0, collapsedPadding);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_section);

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        NestedScrollView view = ((NestedScrollView) findViewById(R.id.scroll_view));
        view.setSmoothScrollingEnabled(true);

        String id = getIntent().getStringExtra(ID);
        if (id != null) {
            subscribeToSection(id);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(SECTION, mSection);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mSection = (Section) savedInstanceState.getSerializable(SECTION);
        loadSection();
    }

    @Override
    protected void onDestroy() {
        // TODO (hjw): should this be in onPause?
        if (mSectionSubscription != null) {
            mSectionSubscription.unsubscribe();
        }
        if (mImageSubscription != null) {
            mImageSubscription.unsubscribe();
        }

        super.onDestroy();
    }

    private void loadSection() {
        setTitle(mSection.getName());
        ((TextView) findViewById(R.id.grade)).setText(mSection.getGrade());
        ((TextView) findViewById(R.id.length)).setText(mSection.getLength());
        ((TextView) findViewById(R.id.duration)).setText(mSection.getDuration());
        ((TextView) findViewById(R.id.description)).setText(mSection.getDescription());

        findViewById(R.id.content).setVisibility(View.VISIBLE);

        subscribeToImage(mSection.getImageId());
    }

    private void loadImage() {
        ((ImageView) findViewById(R.id.image)).setImageBitmap(mImage.getBitmap());
    }

    private void subscribeToSection(String id) {
        this.mSectionSubscription = RiversApplication.getRiversApi()
                .getSection(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Section>() {
                    @Override
                    public void onCompleted() {
                        loadSection();
                    }

                    @Override
                    public void onError(Throwable e) {
                        // TODO (hjw): handle error
                        Log.e(LOG_TAG, e.getMessage(), e);
                    }

                    @Override
                    public void onNext(Section section) {
                        mSection = section;
                    }
                });
    }

    private void subscribeToImage(String id) {
        this.mImageSubscription = RiversApplication.getRiversApi()
                .getImage(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Image>() {
                    @Override
                    public void onCompleted() {
                        loadImage();
                    }

                    @Override
                    public void onError(Throwable e) {
                        // TODO (hjw): handle error
                        Log.e(LOG_TAG, e.getMessage(), e);
                    }

                    @Override
                    public void onNext(Image image) {
                        mImage = image;
                    }
                });
    }
}