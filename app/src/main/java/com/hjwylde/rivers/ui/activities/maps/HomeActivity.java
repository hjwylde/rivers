package com.hjwylde.rivers.ui.activities.maps;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.hjwylde.rivers.R;
import com.hjwylde.rivers.RiversApplication;
import com.hjwylde.rivers.ui.activities.BaseActivity;
import com.hjwylde.rivers.ui.activities.createSection.CreateSectionActivity;
import com.hjwylde.rivers.ui.activities.settings.SettingsActivity;

import java.util.List;

import butterknife.ButterKnife;

@UiThread
public final class HomeActivity extends BaseActivity implements HomeContract.View, View.OnClickListener {
    static final int REQUEST_CODE_SECTION_CREATED = 0;
    static final int REQUEST_CODE_SECTION_EDITED = 1;

    private static final String TAG = HomeActivity.class.getSimpleName();

    private static final String STATE_SEARCH_VIEW = "searchView";
    private static final String STATE_CREATE_SECTION_MODE_ACTIVE = "createSectionModeActive";

    private FloatingSearchView mSearchView;
    private MapFragment mMapFragment;
    private SectionFragment mSectionFragment;
    private CreateSectionMode mCreateSectionMode;

    private HomeContract.Presenter mPresenter;

    @Override
    public void createSection(@NonNull LatLng putIn) {
        Intent intent = new Intent(this, CreateSectionActivity.class);
        intent.putExtra(CreateSectionActivity.INTENT_PUT_IN, putIn);

        startActivityForResult(intent, REQUEST_CODE_SECTION_CREATED);
    }

    @Override
    public void onBackPressed() {
        // TODO (hjw): do this via the back history
        switch (mSectionFragment.getBottomSheetBehavior().getState()) {
            case BottomSheetBehavior.STATE_COLLAPSED:
                mSectionFragment.getBottomSheetBehavior().setState(BottomSheetBehavior.STATE_HIDDEN);
                return;
            case BottomSheetBehavior.STATE_DRAGGING:
                return;
            case BottomSheetBehavior.STATE_EXPANDED:
                mSectionFragment.getBottomSheetBehavior().setState(BottomSheetBehavior.STATE_COLLAPSED);
                return;
            case BottomSheetBehavior.STATE_SETTLING:
                int y = (int) findViewById(R.id.bottomSheet).getY();
                if (y < mSectionFragment.getBottomSheetBehavior().getPeekHeight()) {
                    mSectionFragment.getBottomSheetBehavior().setState(BottomSheetBehavior.STATE_COLLAPSED);
                } else {
                    mSectionFragment.getBottomSheetBehavior().setState(BottomSheetBehavior.STATE_HIDDEN);
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
    public void onGetSectionSuggestionsFailure(@NonNull Throwable t) {
        Log.w(TAG, t.getMessage(), t);

        mSearchView.clearSuggestions();
    }

    @Override
    public void selectSection(@NonNull String id) {
        mSectionFragment.setSectionId(id);
    }

    @Override
    public void setSectionSuggestions(@NonNull List<SectionSuggestion> sectionSuggestions) {
        mSearchView.swapSuggestions(sectionSuggestions);
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
                onSectionEdited();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);

        mSearchView = findTById(R.id.floating_search_view);
        mSearchView.setOnQueryChangeListener((oldQuery, newQuery) -> mPresenter.getSectionSuggestions(newQuery));
        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSearchAction(String currentQuery) {
            }

            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
                String sectionId = ((SectionSuggestion) searchSuggestion).getSectionId();

                mMapFragment.animateCameraToSection(sectionId, new GoogleMap.CancelableCallback() {
                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onFinish() {
                        selectSection(sectionId);
                    }
                });
            }
        });
        mSearchView.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.openSettings:
                    onOpenSettingsClick();
                    break;
                case R.id.sendFeedback:
                    onSendFeedbackClick();
            }
        });

        FloatingActionButton fab = findTById(R.id.fab);
        fab.setOnClickListener(this);

        initMapFragment();

        initSectionFragment();

        mPresenter = new HomePresenter(this, RiversApplication.getRepository());
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

        refreshFloatingActionButton();

        boolean createSectionModeActive = savedInstanceState.getBoolean(STATE_CREATE_SECTION_MODE_ACTIVE);
        if (createSectionModeActive) {
            startCreateSectionMode();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Parcelable searchViewParcelable = mSearchView.onSaveInstanceState();
        outState.putParcelable(STATE_SEARCH_VIEW, searchViewParcelable);

        outState.putBoolean(STATE_CREATE_SECTION_MODE_ACTIVE, mCreateSectionMode != null && mCreateSectionMode.isActive());
    }

    private void clearSelection() {
        mSectionFragment.getBottomSheetBehavior().setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    private void initMapFragment() {
        mMapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.setOnMapClickListener(position -> clearSelection());
        mMapFragment.setOnMarkerClickListener(sectionMarker -> {
            selectSection(sectionMarker.getId());
            return true;
        });
    }

    private void initSectionFragment() {
        mSectionFragment = (SectionFragment) getSupportFragmentManager().findFragmentById(R.id.section_fragment);
        mSectionFragment.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            private final FloatingActionButton mFab = findTById(R.id.fab);

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                float halfHeight = mFab.getHeight() / 2;
                float margin = ((ViewGroup.MarginLayoutParams) mFab.getLayoutParams()).bottomMargin;
                if (bottomSheet.getY() <= bottomSheet.getHeight() - halfHeight - margin) {
                    mFab.setY(bottomSheet.getY() - halfHeight);
                }
            }

            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
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
    }

    private void onOpenSettingsClick() {
        Intent intent = new Intent(this, SettingsActivity.class);

        startActivity(intent);
    }

    private void onSectionCreated() {
        mCreateSectionMode.finish();

        Snackbar snackbar = Snackbar.make(findViewById(R.id.root_container), R.string.info_onSectionCreated, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void onSectionEdited() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.root_container), R.string.info_onSectionEdited, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void onSendFeedbackClick() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.info_feedbackEmail)});
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.info_feedbackSubject));

        startActivity(intent);
    }

    private void refreshFloatingActionButton() {
        if (mSectionFragment.getBottomSheetBehavior().getState() == BottomSheetBehavior.STATE_HIDDEN) {
            return;
        }

        FloatingActionButton fab = findTById(R.id.fab);
        fab.hide();
    }

    private void startCreateSectionMode() {
        if (mCreateSectionMode == null) {
            mCreateSectionMode = new CreateSectionMode(this, mMapFragment);
        }

        startActionMode(mCreateSectionMode);
    }
}
