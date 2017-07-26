package com.hjwylde.rivers.ui.activities.maps;

import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Dimension;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.actinarium.aligned.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hjwylde.reactivex.observers.LifecycleBoundCompletableObserver;
import com.hjwylde.reactivex.observers.LifecycleBoundMaybeObserver;
import com.hjwylde.reactivex.observers.LifecycleBoundObserver;
import com.hjwylde.rivers.R;
import com.hjwylde.rivers.models.Image;
import com.hjwylde.rivers.models.Section;
import com.hjwylde.rivers.ui.activities.editSection.EditSectionActivity;
import com.hjwylde.rivers.ui.activities.sectionDescription.SectionDescriptionActivity;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static java.util.Objects.requireNonNull;

@UiThread
public final class SectionFragment extends LifecycleFragment implements Toolbar.OnMenuItemClickListener {
    private static final String TAG = SectionFragment.class.getSimpleName();

    private static final String STATE_SECTION_ID = "sectionId";
    private static final String STATE_BOTTOM_SHEET_BEHAVIOR = "bottomSheet";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.bottomSheet)
    NestedScrollView mBottomSheet;
    @BindView(R.id.image)
    ImageView mImageView;
    @BindView(R.id.image_container)
    ViewGroup mImageGroup;
    @BindView(R.id.title)
    TextView mTitleView;
    @BindView(R.id.title_container)
    ViewGroup mTitleGroup;
    @BindView(R.id.subtitle)
    TextView mSubtitleView;
    @BindView(R.id.description)
    TextView mDescriptionView;
    @BindView(R.id.description_container)
    ViewGroup mDescriptionGroup;
    @BindView(R.id.grade)
    TextView mGradeView;
    @BindView(R.id.grade_container)
    ViewGroup mGradeGroup;
    @BindView(R.id.length)
    TextView mLengthView;
    @BindView(R.id.length_container)
    ViewGroup mLengthGroup;
    @BindView(R.id.duration)
    TextView mDurationView;
    @BindView(R.id.duration_container)
    ViewGroup mDurationGroup;

    @BindDimen(R.dimen.imageHeight)
    @Dimension
    int mImageHeight;

    private BottomSheetBehavior<NestedScrollView> mBottomSheetBehavior;
    private BottomSheetBehavior.BottomSheetCallback mBottomSheetCallback;

    private SectionViewModel mViewModel;

    private String mSectionId;
    // TODO (hjw): get rid of this, need to update the services to just take an id and not a full document
    private Section mSection;

    @NonNull
    public BottomSheetBehavior<NestedScrollView> getBottomSheetBehavior() {
        // TODO (hjw): remove this method

        return mBottomSheetBehavior;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(SectionViewModel.class);

        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_SECTION_ID)) {
            mSectionId = requireNonNull(savedInstanceState.getString(STATE_SECTION_ID));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_section, container, false);

        ButterKnife.bind(this, view);

        initToolbar();

        initBottomSheet(savedInstanceState);

        refreshImageContainer();

        return view;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editSection:
                onEditSectionClick();
                return true;
            case R.id.deleteSection:
                onDeleteSectionClick();
                return true;
        }

        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mSectionId != null) {
            outState.putString(STATE_SECTION_ID, mSectionId);
        }

        CoordinatorLayout parent = (CoordinatorLayout) mBottomSheet.getParent();
        Parcelable bottomSheetBehaviorParcelable = mBottomSheetBehavior.onSaveInstanceState(parent, mBottomSheet);
        outState.putParcelable(STATE_BOTTOM_SHEET_BEHAVIOR, bottomSheetBehaviorParcelable);
    }

    @Override
    public void onStart() {
        super.onStart();

        mViewModel.getSection()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new OnGetSectionObserver());

        if (mSectionId != null) {
            mViewModel.getSection(mSectionId);
        }
    }

    public void setBottomSheetCallback(@NonNull BottomSheetBehavior.BottomSheetCallback bottomSheetCallback) {
        mBottomSheetCallback = requireNonNull(bottomSheetCallback);
    }

    public void setSectionId(@NonNull String sectionId) {
        if (sectionId.equals(mSectionId)) {
            if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            } else {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }

            return;
        }

        mSectionId = sectionId;

        mViewModel.getSection(mSectionId);

        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @OnClick(R.id.description_container)
    void onDescriptionContainerClick() {
        Intent intent = new Intent(getContext(), SectionDescriptionActivity.class);
        intent.putExtra(SectionDescriptionActivity.INTENT_SECTION_ID, mSectionId);

        startActivity(intent);
    }

    @OnClick(R.id.title_container)
    void onTitleContainerClick() {
        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    private void initBottomSheet(Bundle savedInstanceState) {
        mBottomSheet.setSmoothScrollingEnabled(true);

        mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheet);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                float collapsedY = bottomSheet.getHeight() - mBottomSheetBehavior.getPeekHeight();
                float expandedRatio = (collapsedY - bottomSheet.getY()) / collapsedY;

                mImageGroup.getLayoutParams().height = (int) Math.max(mImageHeight * expandedRatio, 0);
                mImageGroup.requestLayout();

                if (mBottomSheetCallback != null) {
                    mBottomSheetCallback.onSlide(bottomSheet, slideOffset);
                }
            }

            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (mBottomSheetCallback != null) {
                    mBottomSheetCallback.onStateChanged(bottomSheet, newState);
                }
            }
        });

        if (savedInstanceState != null) {
            CoordinatorLayout parent = (CoordinatorLayout) mBottomSheet.getParent();
            Parcelable bottomSheetParcelable = requireNonNull(savedInstanceState.getParcelable(STATE_BOTTOM_SHEET_BEHAVIOR));
            mBottomSheetBehavior.onRestoreInstanceState(parent, mBottomSheet, bottomSheetParcelable);
        } else {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }

        mTitleGroup.post(() -> mBottomSheetBehavior.setPeekHeight(mTitleGroup.getHeight()));
    }

    private void initToolbar() {
        mToolbar.inflateMenu(R.menu.menu_section);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_left);
        mToolbar.setNavigationOnClickListener(view -> mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN));
        mToolbar.setOverflowIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_dots_vertical));
        mToolbar.setOnMenuItemClickListener(this);
    }

    private void onDeleteSectionClick() {
        mViewModel.deleteSection(mSection)
                .subscribe(new OnDeleteSectionObserver());
    }

    private void onEditSectionClick() {
        Intent intent = new Intent(getContext(), EditSectionActivity.class);
        intent.putExtra(EditSectionActivity.INTENT_SECTION_BUILDER, Section.builder().copy(mSection));

        startActivityForResult(intent, MapsActivity.REQUEST_CODE_SECTION_EDITED);
    }

    private void refreshImageContainer() {
        if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            return;
        }

        mImageGroup.getLayoutParams().height = mImageHeight;
        mImageGroup.requestLayout();
    }

    @UiThread
    private final class OnDeleteSectionObserver extends LifecycleBoundCompletableObserver {
        OnDeleteSectionObserver() {
            super(SectionFragment.this);
        }

        @Override
        public void onComplete() {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

            Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.root_container), R.string.info_onSectionDeleted, Snackbar.LENGTH_LONG);
            snackbar.show();
        }

        @Override
        public void onError(@NonNull Throwable t) {
            Log.w(TAG, t.getMessage(), t);

            final Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.root_container), R.string.error_onDeleteSection, Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.action_retryDeleteSection, view -> {
                if (snackbar.isShown()) {
                    snackbar.dismiss();
                }

                onDeleteSectionClick();
            });

            snackbar.show();
        }
    }

    @UiThread
    private final class OnGetImageObserver extends LifecycleBoundMaybeObserver<Image> {
        private final int mImageWidth = mImageView.getWidth();

        OnGetImageObserver() {
            super(SectionFragment.this);
        }

        @Override
        public void onComplete() {
            // Do nothing
        }

        @Override
        public void onError(@NonNull Throwable t) {
            Log.w(TAG, t.getMessage(), t);

            // TODO (hjw): display a warning to the user
        }

        @Override
        public void onSuccess(@NonNull Image image) {
            refreshImage(image);
        }

        private void refreshImage(@NonNull Image image) {
            Glide.with(SectionFragment.this)
                    .asBitmap()
                    .load(image.getDecodedData())
                    .apply(
                            RequestOptions
                                    .centerCropTransform()
                                    .override(mImageWidth, mImageHeight)
                    ).into(mImageView);
        }
    }

    @UiThread
    private final class OnGetSectionObserver extends LifecycleBoundObserver<Section> {
        OnGetSectionObserver() {
            super(SectionFragment.this);
        }

        @Override
        public void onComplete() {
            // Do nothing
        }

        @Override
        public void onError(@NonNull Throwable t) {
            // This should never happen
            throw new RuntimeException(t);
        }

        @Override
        public void onNext(@NonNull Section section) {
            mSection = requireNonNull(section);

            refreshSection(section);

            if (section.getImageId() != null) {
                mViewModel.getImage(section.getImageId())
                        .subscribe(new OnGetImageObserver());
            } else {
                clearImage();
            }
        }

        private void clearImage() {
            mImageView.setImageBitmap(null);
        }

        private void refreshSection(@NonNull Section section) {
            mTitleView.setText(section.getTitle());
            mSubtitleView.setText(section.getSubtitle());

            if (section.getDescription() != null && !section.getDescription().isEmpty()) {
                mDescriptionView.setText(section.getDescription());
                mDescriptionGroup.setVisibility(View.VISIBLE);
            } else {
                mDescriptionGroup.setVisibility(View.GONE);
            }

            if (section.getGrade() != null && !section.getGrade().isEmpty()) {
                mGradeView.setText(section.getGrade());
                mGradeGroup.setVisibility(View.VISIBLE);
            } else {
                mGradeGroup.setVisibility(View.GONE);
            }
            if (section.getLength() != null && !section.getLength().isEmpty()) {
                mLengthView.setText(section.getLength());
                mLengthGroup.setVisibility(View.VISIBLE);
            } else {
                mLengthGroup.setVisibility(View.GONE);
            }
            if (section.getDuration() != null && !section.getDuration().isEmpty()) {
                mDurationView.setText(section.getDuration());
                mDurationGroup.setVisibility(View.VISIBLE);
            } else {
                mDurationGroup.setVisibility(View.GONE);
            }
        }
    }
}
