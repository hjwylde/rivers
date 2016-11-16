package com.hjwylde.rivers.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.hjwylde.rivers.R;
import com.hjwylde.rivers.RiversApplication;
import com.hjwylde.rivers.models.Action;
import com.hjwylde.rivers.models.Image;
import com.hjwylde.rivers.models.Section;
import com.hjwylde.rivers.ui.contracts.SectionContract;
import com.hjwylde.rivers.ui.presenters.SectionPresenter;

import static com.google.common.base.Preconditions.checkNotNull;

public final class SectionActivity extends BaseActivity implements SectionContract.View {
    public static final String INTENT_SECTION = "section";

    private static final String TAG = SectionActivity.class.getSimpleName();

    private static final String STATE_SECTION = "section";
    private static final String STATE_IMAGE = "image";
    private static final String STATE_BOTTOM_SHEET = "bottomSheet";

    private BottomSheetBehavior<NestedScrollView> mBottomSheetBehavior;

    private SectionContract.Presenter mPresenter;

    private Section mSection;
    private Image mImage;

    public void onActivityClick(View view) {
        finish();
    }

    public void onTitleContainerClick(View view) {
        if (mBottomSheetBehavior == null) {
            return;
        }

        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.editSection:
                startEditSectionActivity();
                return true;
            case R.id.deleteSection:
                mPresenter.deleteSection(buildAction());
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_section, menu);

        return true;
    }

    @Override
    public void setImage(@NonNull Image image) {
        mImage = checkNotNull(image);
    }

    @Override
    public void refreshImage() {
        refreshImage(true);
    }

    @Override
    public void onGetImageFailure(@NonNull Throwable t) {
        Log.w(TAG, t.getMessage(), t);
    }

    @Override
    public void onDeleteSectionSuccess(@NonNull Action action) {
        // TODO (hjw)
    }

    @Override
    public void onDeleteSectionFailure(@NonNull Throwable t) {
        Log.w(TAG, t.getMessage(), t);

        final Snackbar snackbar = Snackbar.make(findViewById(R.id.root_container), R.string.error_onDeleteSection, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.action_retry, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.deleteSection(buildAction());
            }
        });

        snackbar.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_section);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NestedScrollView bottomSheet = ((NestedScrollView) findViewById(R.id.bottomSheet));
        bottomSheet.setSmoothScrollingEnabled(true);

        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            private final View mImage = findViewById(R.id.image);
            private final float mExpandedHeight = getResources().getDimension(R.dimen.imageHeight);

            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    finish();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                float collapsedY = getResources().getDisplayMetrics().heightPixels - mBottomSheetBehavior.getPeekHeight();
                float expandedRatio = (collapsedY - bottomSheet.getY()) / collapsedY;

                int imageHeight = (int) Math.max(mExpandedHeight * expandedRatio, 0);

                mImage.getLayoutParams().height = (int) imageHeight;
                mImage.requestLayout();
            }
        });

        mPresenter = new SectionPresenter(this, RiversApplication.getRiversService());

        mSection = (Section) getIntent().getSerializableExtra(INTENT_SECTION);
        refreshSection();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mImage == null) {
            mPresenter.getImage(mSection.getImageId());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        NestedScrollView child = (NestedScrollView) findViewById(R.id.bottomSheet);
        CoordinatorLayout parent = (CoordinatorLayout) child.getParent();
        Parcelable bottomSheetParcelable = mBottomSheetBehavior.onSaveInstanceState(parent, child);
        outState.putParcelable(STATE_BOTTOM_SHEET, bottomSheetParcelable);

        outState.putSerializable(STATE_SECTION, mSection);
        outState.putSerializable(STATE_IMAGE, mImage);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        NestedScrollView child = (NestedScrollView) findViewById(R.id.bottomSheet);
        CoordinatorLayout parent = (CoordinatorLayout) child.getParent();
        Parcelable bottomSheetParcelable = checkNotNull(savedInstanceState.getParcelable(STATE_BOTTOM_SHEET));
        mBottomSheetBehavior.onRestoreInstanceState(parent, child, bottomSheetParcelable);

        mSection = (Section) savedInstanceState.getSerializable(STATE_SECTION);
        refreshSection();

        mImage = (Image) savedInstanceState.getSerializable(STATE_IMAGE);
        refreshImage(false);

        refreshImageView();
    }

    @Override
    protected void onPause() {
        mPresenter.unsubscribe();

        super.onPause();
    }

    private void refreshImage(boolean animate) {
        if (mImage == null) {
            return;
        }

        ImageView imageView = (ImageView) findViewById(R.id.image);
        imageView.setImageBitmap(mImage.getBitmap());

        if (animate) {
            animateImageIn(imageView);
        }
    }

    private void animateImageIn(View imageView) {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_image_in);

        imageView.startAnimation(animation);
    }

    private void refreshImageView() {
        View imageView = findViewById(R.id.image);

        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            imageView.getLayoutParams().height = (int) getResources().getDimension(R.dimen.imageHeight);
            imageView.requestLayout();
        }
    }

    private void refreshSection() {
        ((TextView) findViewById(R.id.title)).setText(mSection.getTitle());
        ((TextView) findViewById(R.id.subtitle)).setText(mSection.getSubtitle());

        if (mSection.getGrade() != null && !mSection.getGrade().isEmpty()) {
            ((TextView) findViewById(R.id.grade)).setText(mSection.getGrade());
        } else {
            findViewById(R.id.grade_container).setVisibility(View.GONE);
        }
        if (mSection.getLength() != null && !mSection.getLength().isEmpty()) {
            ((TextView) findViewById(R.id.length)).setText(mSection.getLength());
        } else {
            findViewById(R.id.length_container).setVisibility(View.GONE);
        }
        if (mSection.getDuration() != null && !mSection.getDuration().isEmpty()) {
            ((TextView) findViewById(R.id.duration)).setText(mSection.getDuration());
        } else {
            findViewById(R.id.duration_container).setVisibility(View.GONE);
        }

        ((TextView) findViewById(R.id.description)).setText(mSection.getDescription());
    }

    private void startEditSectionActivity() {
        Intent intent = new Intent(this, EditSectionActivity.class);
        intent.putExtra(EditSectionActivity.INTENT_SECTION, mSection);

        startActivity(intent);
    }

    private Action buildAction() {
        Action.Builder builder = new Action.Builder();
        builder.action(Action.ACTION_REMOVE);
        builder.targetId(mSection.getId());
        builder.targetCollection(Section.getCollection());

        return builder.build();
    }
}