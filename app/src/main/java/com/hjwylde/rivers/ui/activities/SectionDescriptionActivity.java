package com.hjwylde.rivers.ui.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.actinarium.aligned.TextView;
import com.hjwylde.rivers.R;
import com.hjwylde.rivers.models.Section;
import com.hjwylde.rivers.ui.viewModels.SectionDescriptionViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

import static java.util.Objects.requireNonNull;

public final class SectionDescriptionActivity extends BaseActivity {
    public static final String INTENT_SECTION_ID = "sectionId";

    private static final String STATE_SECTION_ID = "sectionId";

    @BindView(R.id.title)
    TextView mTitleView;
    @BindView(R.id.subtitle)
    TextView mSubtitleView;
    @BindView(R.id.description)
    TextView mDescriptionView;

    private SectionDescriptionViewModel mViewModel;
    private Observer<Section> mOnGetSectionObserver = new OnGetSectionObserver();

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

        mViewModel.getSection(mSectionId).observe(this, mOnGetSectionObserver);
    }

    private final class OnGetSectionObserver implements Observer<Section> {
        @Override
        public void onChanged(@Nullable Section section) {
            if (section != null) {
                refreshSection(section);
            }
        }

        private void refreshSection(@NonNull Section section) {
            mTitleView.setText(section.getTitle());
            mSubtitleView.setText(section.getSubtitle());
            mDescriptionView.setText(section.getDescription());
        }
    }
}