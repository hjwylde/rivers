package com.hjwylde.rivers.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.hjwylde.rivers.R;
import com.hjwylde.rivers.RiversApplication;
import com.hjwylde.rivers.models.Image;
import com.hjwylde.rivers.models.Section;
import com.hjwylde.rivers.ui.contracts.MapsContract;
import com.hjwylde.rivers.ui.presenters.MapsPresenter;
import com.hjwylde.rivers.ui.util.CreateSectionMode;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public final class MapsActivity extends BaseActivity implements MapsContract.View, View.OnClickListener {
    private static final String TAG = MapsActivity.class.getSimpleName();

    private static final String STATE_BOTTOM_SHEET = "bottomSheet";
    private static final String STATE_CREATE_SECTION_MODE_ACTIVE = "createSectionModeActive";
    private static final String STATE_SECTION = "section";
    private static final String STATE_SECTIONS = "sections";

    private BottomSheetBehavior<NestedScrollView> mBottomSheetBehavior;
    private CreateSectionMode mCreateSectionMode;

    private MapsContract.Presenter mPresenter;

    private Image mImage;
    private Section mSection;
    private List<Section> mSections = new ArrayList<>();

    @Override
    public void setSections(@NonNull List<Section> sections) {
        mSections = checkNotNull(sections);
    }

    @Override
    public void setImage(@NonNull Image image) {
        mImage = checkNotNull(image);
    }

    @Override
    public void refreshImage() {
        ImageView imageView = (ImageView) findViewById(R.id.image);

        if (mImage != null) {
            imageView.setImageBitmap(mImage.getBitmap());

            animateImageIn(imageView);
        } else {
            imageView.setImageBitmap(null);
        }
    }

    @Override
    public void onGetImageFailure(@NonNull Throwable t) {
        Log.w(TAG, t.getMessage(), t);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                startCreateSectionMode();
                break;
        }
    }

    @Override
    public void onSectionClick(@NonNull Section section) {
        setSection(section);

        refreshSection();
        refreshImage();

        if (mImage == null) {
            mPresenter.getImage(mSection.getImageId());
        }

        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    public void onCreateSectionClick() {
        MapsFragment mapsFragment = getMapsFragment();
        mapsFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Intent intent = new Intent(MapsActivity.this, CreateSectionActivity.class);
                intent.putExtra(CreateSectionActivity.INTENT_PUT_IN, googleMap.getCameraPosition().target);

                startActivity(intent);
            }
        });
    }

    public void refreshMap() {
        MapsFragment mapsFragment = getMapsFragment();
        mapsFragment.refreshMap(mSections);
    }

    @Override
    public void onGetSectionsFailure(@NonNull Throwable t) {
        Log.w(TAG, t.getMessage(), t);

        // TODO (#23)
    }

    public void onTitleContainerClick(View view) {
        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        NestedScrollView child = (NestedScrollView) findViewById(R.id.bottomSheet);
        CoordinatorLayout parent = (CoordinatorLayout) child.getParent();
        Parcelable bottomSheetParcelable = mBottomSheetBehavior.onSaveInstanceState(parent, child);
        outState.putParcelable(STATE_BOTTOM_SHEET, bottomSheetParcelable);

        outState.putBoolean(STATE_CREATE_SECTION_MODE_ACTIVE, mCreateSectionMode != null && mCreateSectionMode.isActive());
        outState.putSerializable(STATE_SECTION, mSection);
        outState.putSerializable(STATE_SECTIONS, new ArrayList<>(mSections));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        NestedScrollView child = (NestedScrollView) findViewById(R.id.bottomSheet);
        CoordinatorLayout parent = (CoordinatorLayout) child.getParent();
        Parcelable bottomSheetParcelable = checkNotNull(savedInstanceState.getParcelable(STATE_BOTTOM_SHEET));
        mBottomSheetBehavior.onRestoreInstanceState(parent, child, bottomSheetParcelable);
        mBottomSheetBehavior.setState(mBottomSheetBehavior.getState());

        boolean createSectionModeActive = savedInstanceState.getBoolean(STATE_CREATE_SECTION_MODE_ACTIVE);
        if (createSectionModeActive) {
            startCreateSectionMode();
        }

        mSection = (Section) savedInstanceState.getSerializable(STATE_SECTION);
        refreshSection();
        refreshImageView();

        mSections = (List<Section>) savedInstanceState.getSerializable(STATE_SECTIONS);
        refreshMap();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Rivers_Dark_NoActionBar);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Toolbar sectionToolbar = (Toolbar) findViewById(R.id.sectionToolbar);
        sectionToolbar.inflateMenu(R.menu.menu_section);
        sectionToolbar.setNavigationIcon(R.drawable.ic_arrow_left);
        sectionToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
        sectionToolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_dots_vertical));
        sectionToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.editSection:
                        onEditSectionClick();
                        return true;
                    case R.id.deleteSection:
                        // TODO (#14)
                        // mPresenter.deleteSection(buildAction());
                        return true;
                }

                return false;
            }
        });

        FloatingActionButton fab = getFloatingActionButton();
        fab.setOnClickListener(this);

        NestedScrollView bottomSheet = (NestedScrollView) findViewById(R.id.bottomSheet);
        bottomSheet.setSmoothScrollingEnabled(true);

        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            private final View mImageContainer = findViewById(R.id.image_container);
            private final float mExpandedHeight = getResources().getDimension(R.dimen.imageHeight);

            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        fab.show();
                        bottomSheet.setElevation(getResources().getDimension(R.dimen.fab_elevation));
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        fab.hide();
                        bottomSheet.setElevation(getResources().getDimension(R.dimen.toolbar_elevation));
                        break;
                    default:
                        fab.hide();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                float collapsedY = bottomSheet.getHeight() - mBottomSheetBehavior.getPeekHeight();
                float expandedRatio = (collapsedY - bottomSheet.getY()) / collapsedY;

                int imageHeight = (int) Math.max(mExpandedHeight * expandedRatio, 0);

                mImageContainer.getLayoutParams().height = (int) imageHeight;
                mImageContainer.requestLayout();
            }
        });

        mPresenter = new MapsPresenter(this, RiversApplication.getRiversService());
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mSection != null && mImage == null) {
            mPresenter.getImage(mSection.getImageId());
        }

        if (mSections.isEmpty()) {
            mPresenter.getSections();
        }
    }

    @Override
    protected void onPause() {
        mPresenter.unsubscribe();

        super.onPause();
    }

    private void animateImageIn(View imageView) {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_image_in);

        imageView.startAnimation(animation);
    }

    private void setSection(@NonNull Section section) {
        mSection = checkNotNull(section);

        if (mImage != null && !mSection.getImageId().equals(mImage.getId())) {
            mImage = null;
        }
    }

    private void refreshSection() {
        if (mSection == null) {
            return;
        }

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

    private void refreshImageView() {
        View imageContainer = findViewById(R.id.image_container);

        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            imageContainer.getLayoutParams().height = (int) getResources().getDimension(R.dimen.imageHeight);
            imageContainer.requestLayout();
        }
    }

    private void onEditSectionClick() {
        Intent intent = new Intent(this, EditSectionActivity.class);
        intent.putExtra(EditSectionActivity.INTENT_SECTION, mSection);

        startActivity(intent);
    }

    private void startCreateSectionMode() {
        if (mCreateSectionMode == null) {
            FloatingActionButton fab = getFloatingActionButton();
            View centerMarker = findViewById(R.id.center_marker);
            MapsFragment mapsFragment = getMapsFragment();

            mCreateSectionMode = new CreateSectionMode(this, fab, centerMarker, mapsFragment, this);
        }

        startActionMode(mCreateSectionMode);
    }

    private FloatingActionButton getFloatingActionButton() {
        return (FloatingActionButton) findViewById(R.id.fab);
    }

    private MapsFragment getMapsFragment() {
        return (MapsFragment) getSupportFragmentManager().findFragmentById(R.id.map);
    }
}
