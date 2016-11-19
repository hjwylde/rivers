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
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import static com.hjwylde.rivers.util.Preconditions.checkNotNull;

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
    public void clearSelection() {
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    @Override
    public void createSection() {
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

    @Override
    public void onBackPressed() {
        switch (mBottomSheetBehavior.getState()) {
            case BottomSheetBehavior.STATE_COLLAPSED:
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                return;
            case BottomSheetBehavior.STATE_DRAGGING:
                return;
            case BottomSheetBehavior.STATE_EXPANDED:
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                return;
            case BottomSheetBehavior.STATE_SETTLING:
                int y = (int) findViewById(R.id.bottomSheet).getY();
                if (y < mBottomSheetBehavior.getPeekHeight()) {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                } else {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }

                return;
        }

        super.onBackPressed();
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
    public void onGetImageFailure(@NonNull Throwable t) {
        Log.w(TAG, t.getMessage(), t);
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
    public void refreshImage() {
        ImageView imageView = getImage();

        if (mImage != null) {
            imageView.setImageBitmap(mImage.getBitmap());

            animateImageIn(imageView);
        } else {
            imageView.setImageBitmap(null);
        }
    }

    public void refreshMap() {
        MapsFragment mapsFragment = getMapsFragment();
        mapsFragment.refreshMap(mSections);
    }

    @Override
    public void selectSection(@NonNull Section section) {
        if (mSection != null && mSection.getId().equals(section.getId())) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            return;
        }

        setSection(section);

        refreshSection();
        refreshImage();

        if (mImage == null) {
            mPresenter.getImage(mSection.getImageId());
        }

        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    public void setSections(@NonNull List<Section> sections) {
        mSections = checkNotNull(sections);
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
            private final FloatingActionButton mFab = getFloatingActionButton();
            private final View mImageContainer = getImageContainer();
            private final float mExpandedHeight = getResources().getDimension(R.dimen.imageHeight);

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                float halfHeight = mFab.getHeight() / 2;
                float margin = ((ViewGroup.MarginLayoutParams) mFab.getLayoutParams()).bottomMargin;
                if (bottomSheet.getY() <= bottomSheet.getHeight() - halfHeight - margin) {
                    mFab.setY(bottomSheet.getY() - halfHeight);
                }

                float collapsedY = bottomSheet.getHeight() - mBottomSheetBehavior.getPeekHeight();
                float expandedRatio = (collapsedY - bottomSheet.getY()) / collapsedY;

                int imageHeight = (int) Math.max(mExpandedHeight * expandedRatio, 0);

                mImageContainer.getLayoutParams().height = (int) imageHeight;
                mImageContainer.requestLayout();
            }

            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        mSection = null;
                        mImage = null;

                        float height = mFab.getHeight();
                        float margin = ((ViewGroup.MarginLayoutParams) mFab.getLayoutParams()).bottomMargin;
                        mFab.setY(bottomSheet.getHeight() - height - margin);

                        mFab.show();
                        break;
                    default:
                        mFab.hide();
                }
            }
        });

        getTitleContainer().post(new Runnable() {
            @Override
            public void run() {
                mBottomSheetBehavior.setPeekHeight(getTitleContainer().getHeight());
            }
        });

        mPresenter = new MapsPresenter(this, RiversApplication.getRiversService());
    }

    @Override
    protected void onPause() {
        mPresenter.unsubscribe();

        super.onPause();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        NestedScrollView child = (NestedScrollView) findViewById(R.id.bottomSheet);
        CoordinatorLayout parent = (CoordinatorLayout) child.getParent();
        Parcelable bottomSheetParcelable = checkNotNull(savedInstanceState.getParcelable(STATE_BOTTOM_SHEET));
        mBottomSheetBehavior.onRestoreInstanceState(parent, child, bottomSheetParcelable);

        refreshFloatingActionButton();

        boolean createSectionModeActive = savedInstanceState.getBoolean(STATE_CREATE_SECTION_MODE_ACTIVE);
        if (createSectionModeActive) {
            startCreateSectionMode();
        }

        mSection = (Section) savedInstanceState.getSerializable(STATE_SECTION);
        refreshSection();
        refreshImageContainer();

        mSections = (List<Section>) savedInstanceState.getSerializable(STATE_SECTIONS);
        refreshMap();
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

    private void animateImageIn(View imageView) {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_image_in);

        imageView.startAnimation(animation);
    }

    private ImageView getCenterMarker() {
        return (ImageView) findViewById(R.id.center_marker);
    }

    private FloatingActionButton getFloatingActionButton() {
        return (FloatingActionButton) findViewById(R.id.fab);
    }

    private ImageView getImage() {
        return (ImageView) findViewById(R.id.image);
    }

    @Override
    public void setImage(@NonNull Image image) {
        mImage = checkNotNull(image);
    }

    private View getImageContainer() {
        return findViewById(R.id.image_container);
    }

    private MapsFragment getMapsFragment() {
        return (MapsFragment) getSupportFragmentManager().findFragmentById(R.id.map);
    }

    private LinearLayout getTitleContainer() {
        return (LinearLayout) findViewById(R.id.title_container);
    }

    private void onEditSectionClick() {
        Intent intent = new Intent(this, EditSectionActivity.class);
        intent.putExtra(EditSectionActivity.INTENT_SECTION, mSection);

        startActivity(intent);
    }

    private void refreshFloatingActionButton() {
        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            return;
        }

        FloatingActionButton fab = getFloatingActionButton();
        fab.hide();
    }

    private void refreshImageContainer() {
        if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            return;
        }

        View imageContainer = getImageContainer();
        imageContainer.getLayoutParams().height = (int) getResources().getDimension(R.dimen.imageHeight);
        imageContainer.requestLayout();
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

    private void setSection(@NonNull Section section) {
        mSection = checkNotNull(section);

        if (mImage != null && !mSection.getImageId().equals(mImage.getId())) {
            mImage = null;
        }
    }

    private void startCreateSectionMode() {
        if (mCreateSectionMode == null) {
            FloatingActionButton fab = getFloatingActionButton();
            ImageView centerMarker = getCenterMarker();
            MapsFragment mapsFragment = getMapsFragment();

            mCreateSectionMode = new CreateSectionMode(this, fab, centerMarker, mapsFragment, this);
        }

        startActionMode(mCreateSectionMode);
    }
}
