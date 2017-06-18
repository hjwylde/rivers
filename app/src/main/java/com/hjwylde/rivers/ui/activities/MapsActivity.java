package com.hjwylde.rivers.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.android.gms.maps.model.LatLng;
import com.hjwylde.rivers.R;
import com.hjwylde.rivers.RiversApplication;
import com.hjwylde.rivers.models.Image;
import com.hjwylde.rivers.models.Section;
import com.hjwylde.rivers.ui.contracts.MapsContract;
import com.hjwylde.rivers.ui.presenters.MapsPresenter;
import com.hjwylde.rivers.ui.util.CreateSectionMode;
import com.hjwylde.rivers.ui.util.SectionSuggestion;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

public final class MapsActivity extends BaseActivity implements MapsContract.View, View.OnClickListener {
    private static final String TAG = MapsActivity.class.getSimpleName();

    private static final int REQUEST_CODE_SECTION_CREATED = 0;
    private static final int REQUEST_CODE_SECTION_EDITED = 1;

    private static final String STATE_SEARCH_VIEW = "searchView";
    private static final String STATE_BOTTOM_SHEET = "bottomSheet";
    private static final String STATE_CREATE_SECTION_MODE_ACTIVE = "createSectionModeActive";
    private static final String STATE_SECTION_ID = "sectionId";

    private FloatingSearchView mSearchView;
    private BottomSheetBehavior<NestedScrollView> mBottomSheetBehavior;
    private CreateSectionMode mCreateSectionMode;

    private MapsContract.Presenter mPresenter;

    private List<? extends Section> mSections = new ArrayList<>();
    private Section mSection;
    private Image mImage;

    @Override
    public void clearSelection() {
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    @Override
    public void createSection(@NonNull LatLng putIn) {
        Intent intent = new Intent(MapsActivity.this, CreateSectionActivity.class);
        intent.putExtra(CreateSectionActivity.INTENT_PUT_IN, putIn);

        startActivityForResult(intent, REQUEST_CODE_SECTION_CREATED);
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
    public void onClick(@NonNull View view) {
        switch (view.getId()) {
            case R.id.fab:
                startCreateSectionMode();
                break;
        }
    }

    @Override
    public void onDeleteSectionFailure(@NonNull Throwable t) {
        Log.w(TAG, t.getMessage(), t);

        final Snackbar snackbar = Snackbar.make(findViewById(R.id.root_container), R.string.error_onDeleteSection, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.action_retryDeleteSection, view -> {
            if (snackbar.isShown()) {
                snackbar.dismiss();
            }

            onDeleteSectionClick();
        });

        snackbar.show();
    }

    @Override
    public void onDeleteSectionSuccess() {
        clearSelection();

        Snackbar snackbar = Snackbar.make(findViewById(R.id.root_container), R.string.info_onSectionDeleted, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    public void onGetImageFailure(@NonNull Throwable t) {
        Log.w(TAG, t.getMessage(), t);

        // TODO (#74)
    }

    @Override
    public void onGetSectionFailure(@NonNull Throwable t) {
        Log.w(TAG, t.getMessage(), t);

        // TODO (hjw)
    }

    @Override
    public void onGetSectionSuccess(@NonNull Section section) {
        setSection(section);

        refreshSection();
        refreshImage();

        if (mImage == null) {
            mPresenter.getImage(mSection.getImageId());
        }

        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    public void onGetSectionSuggestionsFailure(@NonNull Throwable t) {
        Log.w(TAG, t.getMessage(), t);

        mSearchView.clearSuggestions();
    }

    public void onTitleContainerClick(@NonNull View view) {
        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    @Override
    public void refreshImage() {
        ImageView imageView = findTById(R.id.image);

        if (mImage != null) {
            imageView.setImageBitmap(mImage.getBitmap());

            animateImageIn(imageView);
        } else {
            imageView.setImageBitmap(null);
        }
    }

    @Override
    public void refreshMap() {
        MapsFragment mapsFragment = getMapsFragment();
        mapsFragment.refreshMap(mSections);
    }

    @Override
    public void selectSection(@NonNull String id) {
        if (mSection != null && mSection.getId().equals(id)) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            return;
        }

        mPresenter.getSection(id);
    }

    @Override
    public void setImage(@NonNull Image image) {
        mImage = requireNonNull(image);
    }

    @Override
    public void setSectionSuggestions(@NonNull List<SectionSuggestion> sectionSuggestions) {
        mSearchView.swapSuggestions(sectionSuggestions);
    }

    @Override
    public void setSections(@NonNull List<? extends Section> sections) {
        mSections = requireNonNull(sections);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case REQUEST_CODE_SECTION_CREATED:
                onSectionCreated();
                break;
            case REQUEST_CODE_SECTION_EDITED:
                String id = data.getStringExtra(EditSectionActivity.RESULT_SECTION_ID);

                // TODO (hjw)
                // onSectionEdited(section);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);

        mSearchView = findTById(R.id.floating_search_view);
        mSearchView.setOnQueryChangeListener((oldQuery, newQuery) -> {
            if (newQuery.isEmpty()) {
                mSearchView.clearSuggestions();
                return;
            }

            mPresenter.getSectionSuggestions(newQuery);
        });
        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSearchAction(String currentQuery) {
            }

            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
                // TODO (hjw)
                // final String id = ((String) searchSuggestion).getSectionId();

                // CameraUpdate update = CameraUpdateFactory.newLatLng(section.getPutIn());
                // getMapsFragment().getMap().animateCamera(update, new GoogleMap.CancelableCallback() {
                //     @Override
                //     public void onCancel() {
                //     }

                //     @Override
                //     public void onFinish() {
                //         selectSection(section);
                //     }
                // });
            }
        });

        Toolbar sectionToolbar = findTById(R.id.sectionToolbar);
        sectionToolbar.inflateMenu(R.menu.menu_section);
        sectionToolbar.setNavigationIcon(R.drawable.ic_arrow_left);
        sectionToolbar.setNavigationOnClickListener(view -> {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        });
        sectionToolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_dots_vertical));
        sectionToolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.editSection:
                    onEditSectionClick();
                    return true;
                case R.id.deleteSection:
                    onDeleteSectionClick();
                    return true;
            }

            return false;
        });

        FloatingActionButton fab = findTById(R.id.fab);
        fab.setOnClickListener(this);

        NestedScrollView bottomSheet = findTById(R.id.bottomSheet);
        bottomSheet.setSmoothScrollingEnabled(true);

        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            private final FloatingActionButton mFab = findTById(R.id.fab);
            private final View mImageContainer = findViewById(R.id.image_container);
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

        final View titleContainer = findViewById(R.id.title_container);
        titleContainer.post(() -> {
            mBottomSheetBehavior.setPeekHeight(titleContainer.getHeight());
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

        mSearchView.onRestoreInstanceState(savedInstanceState.getParcelable(STATE_SEARCH_VIEW));

        NestedScrollView child = findTById(R.id.bottomSheet);
        CoordinatorLayout parent = (CoordinatorLayout) child.getParent();
        Parcelable bottomSheetParcelable = requireNonNull(savedInstanceState.getParcelable(STATE_BOTTOM_SHEET));
        mBottomSheetBehavior.onRestoreInstanceState(parent, child, bottomSheetParcelable);

        refreshFloatingActionButton();

        boolean createSectionModeActive = savedInstanceState.getBoolean(STATE_CREATE_SECTION_MODE_ACTIVE);
        if (createSectionModeActive) {
            startCreateSectionMode();
        }

        // TODO (hjw)
        // mSection = (Section) savedInstanceState.getSerializable(STATE_SECTION);
        // refreshSection();
        // refreshImageContainer();
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadImage();

        mPresenter.getSections();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Parcelable searchViewParcelable = mSearchView.onSaveInstanceState();
        outState.putParcelable(STATE_SEARCH_VIEW, searchViewParcelable);

        NestedScrollView child = findTById(R.id.bottomSheet);
        CoordinatorLayout parent = (CoordinatorLayout) child.getParent();
        Parcelable bottomSheetParcelable = mBottomSheetBehavior.onSaveInstanceState(parent, child);
        outState.putParcelable(STATE_BOTTOM_SHEET, bottomSheetParcelable);

        outState.putBoolean(STATE_CREATE_SECTION_MODE_ACTIVE, mCreateSectionMode != null && mCreateSectionMode.isActive());

        if (mSection != null) {
            outState.putString(STATE_SECTION_ID, mSection.getId());
        }
    }

    private void animateImageIn(@NonNull View imageView) {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_image_in);

        imageView.startAnimation(animation);
    }

    @NonNull
    private MapsFragment getMapsFragment() {
        return (MapsFragment) getSupportFragmentManager().findFragmentById(R.id.map);
    }

    private void loadImage() {
        if (mSection != null && mSection.getImageId() != null && mImage == null) {
            mPresenter.getImage(mSection.getImageId());
        }
    }

    private void onDeleteSectionClick() {
        mPresenter.deleteSection(mSection);
    }

    private void onEditSectionClick() {
        Intent intent = new Intent(this, EditSectionActivity.class);
        intent.putExtra(EditSectionActivity.INTENT_SECTION_BUILDER, Section.builder().copy(mSection));

        startActivityForResult(intent, REQUEST_CODE_SECTION_EDITED);
    }

    private void onSectionCreated() {
        mCreateSectionMode.finish();

        Snackbar snackbar = Snackbar.make(findViewById(R.id.root_container), R.string.info_onSectionCreated, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void onSectionEdited(@NonNull Section section) {
        setSection(section);
        refreshSection();

        loadImage();

        Snackbar snackbar = Snackbar.make(findViewById(R.id.root_container), R.string.info_onSectionEdited, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void refreshFloatingActionButton() {
        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            return;
        }

        FloatingActionButton fab = findTById(R.id.fab);
        fab.hide();
    }

    private void refreshImageContainer() {
        if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            return;
        }

        View imageContainer = findViewById(R.id.image_container);
        imageContainer.getLayoutParams().height = (int) getResources().getDimension(R.dimen.imageHeight);
        imageContainer.requestLayout();
    }

    private void refreshSection() {
        if (mSection == null) {
            return;
        }

        findTextViewById(R.id.title).setText(mSection.getTitle());
        findTextViewById(R.id.subtitle).setText(mSection.getSubtitle());

        if (mSection.getGrade() != null && !mSection.getGrade().isEmpty()) {
            findTextViewById(R.id.grade).setText(mSection.getGrade());
            findViewById(R.id.grade_container).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.grade_container).setVisibility(View.GONE);
        }
        if (mSection.getLength() != null && !mSection.getLength().isEmpty()) {
            findTextViewById(R.id.length).setText(mSection.getLength());
            findViewById(R.id.length_container).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.length_container).setVisibility(View.GONE);
        }
        if (mSection.getDuration() != null && !mSection.getDuration().isEmpty()) {
            findTextViewById(R.id.duration).setText(mSection.getDuration());
            findViewById(R.id.duration_container).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.duration_container).setVisibility(View.GONE);
        }

        findTextViewById(R.id.description).setText(mSection.getDescription());
    }

    private void setSection(@NonNull Section section) {
        mSection = requireNonNull(section);

        if (mImage != null && !mImage.getId().equals(mSection.getImageId())) {
            mImage = null;
        }
    }

    private void startCreateSectionMode() {
        if (mCreateSectionMode == null) {
            mCreateSectionMode = new CreateSectionMode(this, getMapsFragment());
        }

        startActionMode(mCreateSectionMode);
    }
}
