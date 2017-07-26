package com.hjwylde.rivers.ui.activities.sectionDescription;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;

import com.actinarium.aligned.TextView;
import com.hjwylde.reactivex.observers.LifecycleBoundObserver;
import com.hjwylde.rivers.R;
import com.hjwylde.rivers.models.Section;
import com.hjwylde.rivers.ui.activities.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

import static java.util.Objects.requireNonNull;

@UiThread
public final class SectionDescriptionActivity extends BaseActivity {
    public static final String INTENT_SECTION_ID = "sectionId";

    private static final String TAG = SectionDescriptionActivity.class.getSimpleName();

    private static final String STATE_SECTION_ID = "sectionId";

    @BindView(R.id.title)
    TextView mTitleView;
    @BindView(R.id.subtitle)
    TextView mSubtitleView;
    @BindView(R.id.description)
    TextView mDescriptionView;

    private SectionDescriptionViewModel mViewModel;

    private String mSectionId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_section_description);

        ButterKnife.bind(this);

        mViewModel = ViewModelProviders.of(this).get(SectionDescriptionViewModel.class);

        if (savedInstanceState != null) {
            mSectionId = requireNonNull(savedInstanceState.getString(STATE_SECTION_ID));
        } else {
            mSectionId = requireNonNull(getIntent().getStringExtra(INTENT_SECTION_ID));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(STATE_SECTION_ID, mSectionId);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mViewModel.getSection(mSectionId)
                .subscribe(new OnGetSectionObserver());
    }

    @UiThread
    private final class OnGetSectionObserver extends LifecycleBoundObserver<Section> {
        OnGetSectionObserver() {
            super(SectionDescriptionActivity.this);
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
            refreshSection(section);
        }

        private void refreshSection(@NonNull Section section) {
            mTitleView.setText(section.getTitle());
            mSubtitleView.setText(section.getSubtitle());
            mDescriptionView.setText(section.getDescription());
        }
    }
}
