package com.hjwylde.rivers.ui.activities.editSection;

import android.arch.lifecycle.ViewModelProviders;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.couchbase.lite.util.IOUtils;
import com.hjwylde.reactivex.observers.LifecycleBoundMaybeObserver;
import com.hjwylde.reactivex.observers.LifecycleBoundSingleObserver;
import com.hjwylde.rivers.R;
import com.hjwylde.rivers.models.Image;
import com.hjwylde.rivers.models.Section;
import com.hjwylde.rivers.ui.activities.BaseActivity;
import com.hjwylde.rivers.ui.util.SoftInput;
import com.hjwylde.rivers.ui.widgets.selectImageDialog.SelectImageDialogFragment;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

@UiThread
public final class EditSectionActivity extends BaseActivity {
    public static final String INTENT_SECTION_ID = "INTENT_SECTION_ID";

    private static final String TAG = EditSectionActivity.class.getSimpleName();
    private static final String TAG_SELECT_IMAGE_DIALOG = "TAG_SELECT_IMAGE_DIALOG";

    private static final String STATE_SECTION_ID = "STATE_SECTION_ID";
    private static final String STATE_SECTION_BUILDER = "STATE_SECTION_BUILDER";

    @BindView(R.id.root_container)
    View mRootView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.image)
    ImageView mImageView;
    @BindView(R.id.title)
    @NotEmpty(messageResId = R.string.error_titleEmpty)
    TextInputEditText mTitleText;
    @BindView(R.id.title_layout)
    TextInputLayout mTitleLayout;
    @NotEmpty(messageResId = R.string.error_subtitleEmpty)
    @BindView(R.id.subtitle)
    TextInputEditText mSubtitleText;
    @BindView(R.id.subtitle_layout)
    TextInputLayout mSubtitleLayout;
    @BindView(R.id.grade)
    EditText mGradeText;
    @BindView(R.id.length)
    EditText mLengthText;
    @BindView(R.id.duration)
    EditText mDurationText;

    private Validator mValidator;

    private EditSectionViewModel mViewModel;

    private String mSectionId;
    private Section.DefaultBuilder mSectionBuilder = Section.builder();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit_section, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                return true;
            case R.id.updateSection:
                onUpdateSectionClick();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_section);

        ButterKnife.bind(this);

        mValidator = new Validator(this);
        mValidator.setValidationListener(new OnValidationListener());

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mViewModel = ViewModelProviders.of(this).get(EditSectionViewModel.class);

        if (savedInstanceState != null) {
            mSectionId = savedInstanceState.getString(STATE_SECTION_ID);
            mSectionBuilder = (Section.DefaultBuilder) savedInstanceState.getSerializable(STATE_SECTION_BUILDER);
        } else {
            mSectionId = getIntent().getStringExtra(INTENT_SECTION_ID);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(STATE_SECTION_ID, mSectionId);
        outState.putSerializable(STATE_SECTION_BUILDER, mSectionBuilder);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mSectionBuilder.id() == null) {
            mViewModel.getSection(mSectionId)
                    .subscribe(new OnGetSectionObserver());
        } else {
            refreshSection();

            if (mSectionBuilder.imageId() != null) {
                mViewModel.getImage(mSectionBuilder.imageId())
                        .subscribe(new OnGetImageObserver());
            }
        }
    }

    @OnClick(R.id.camera)
    void onCameraClick() {
        SelectImageDialogFragment dialog = new SelectImageDialogFragment();
        dialog.setOnImageSelectedListener(this::onImageSelected);

        dialog.show(getSupportFragmentManager(), TAG_SELECT_IMAGE_DIALOG);
    }

    @OnTextChanged(R.id.duration)
    void onDurationTextChanged(@NonNull CharSequence text) {
        mSectionBuilder.duration(text.toString());
    }

    @OnTextChanged(R.id.grade)
    void onGradeTextChanged(@NonNull CharSequence text) {
        mSectionBuilder.grade(text.toString());
    }

    @OnTextChanged(R.id.length)
    void onLengthTextChanged(@NonNull CharSequence text) {
        mSectionBuilder.length(text.toString());
    }

    @OnTextChanged(R.id.subtitle)
    void onSubtitleTextChanged(@NonNull CharSequence text) {
        mSubtitleLayout.setError(null);
        mSubtitleLayout.setErrorEnabled(false);

        mSectionBuilder.subtitle(text.toString());
    }

    @OnTextChanged(R.id.title)
    void onTitleTextChanged(@NonNull CharSequence text) {
        mTitleLayout.setError(null);
        mTitleLayout.setErrorEnabled(false);

        mSectionBuilder.title(text.toString());
    }

    private void onCreateImageFailure(@NonNull Throwable t) {
        Log.w(TAG, t.getMessage(), t);

        Snackbar snackbar = Snackbar.make(mRootView, R.string.error_onCreateImage, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void onImageSelected(@NonNull Uri uri) {
        AsyncTask.execute(() -> {
            try {
                InputStream in = getContentResolver().openInputStream(uri);
                byte[] bytes = IOUtils.toByteArray(in);

                Image.Builder builder = Image.builder();
                builder.decodedData(bytes);

                mViewModel.createImage(builder)
                        .subscribe(new OnCreateImageObserver());
            } catch (IOException e) {
                onCreateImageFailure(e);
            }
        });
    }

    private void onUpdateSectionClick() {
        SoftInput.hide(this);

        mValidator.validate(true);
    }

    private void refreshImage(@NonNull Image image) {
        Glide.with(EditSectionActivity.this)
                .asBitmap()
                .load(image.getDecodedData())
                .apply(RequestOptions.centerCropTransform())
                .into(mImageView);
    }

    private void refreshSection() {
        mTitleText.setText(mSectionBuilder.title());
        mSubtitleText.setText(mSectionBuilder.subtitle());
        mGradeText.setText(mSectionBuilder.grade());
        mLengthText.setText(mSectionBuilder.length());
        mDurationText.setText(mSectionBuilder.duration());
    }

    @UiThread
    private final class OnCreateImageObserver extends LifecycleBoundSingleObserver<Image> {
        OnCreateImageObserver() {
            super(EditSectionActivity.this);
        }

        @Override
        public void onError(@NonNull Throwable t) {
            onCreateImageFailure(t);
        }

        @Override
        public void onSuccess(@NonNull Image image) {
            mSectionBuilder.imageId(image.getId());

            refreshImage(image);
        }
    }

    @UiThread
    private final class OnGetImageObserver extends LifecycleBoundMaybeObserver<Image> {
        OnGetImageObserver() {
            super(EditSectionActivity.this);
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
    }

    @UiThread
    private final class OnGetSectionObserver extends LifecycleBoundMaybeObserver<Section> {
        OnGetSectionObserver() {
            super(EditSectionActivity.this);
        }

        @Override
        public void onComplete() {
            // TODO (hjw): close the activity
        }

        @Override
        public void onError(@NonNull Throwable t) {
            Log.w(TAG, t.getMessage(), t);

            // TODO (hjw): display a warning to the user and close the activity
        }

        @Override
        public void onSuccess(@NonNull Section section) {
            mSectionBuilder.copy(section);

            refreshSection();

            if (section.getImageId() != null) {
                mViewModel.getImage(section.getImageId())
                        .subscribe(new OnGetImageObserver());
            }
        }
    }

    @UiThread
    private final class OnUpdateSectionObserver extends LifecycleBoundSingleObserver<Section> {
        OnUpdateSectionObserver() {
            super(EditSectionActivity.this);
        }

        @Override
        public void onError(@NonNull Throwable t) {
            Log.w(TAG, t.getMessage(), t);

            final Snackbar snackbar = Snackbar.make(mRootView, R.string.error_onUpdateSection, Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.action_retryUpdateSection, view -> {
                if (snackbar.isShown()) {
                    snackbar.dismiss();
                }

                onUpdateSectionClick();
            });

            snackbar.show();
        }

        @Override
        public void onSuccess(@NonNull Section section) {
            setResult(RESULT_OK);
            finish();
        }
    }

    @UiThread
    private final class OnValidationListener implements Validator.ValidationListener {
        @Override
        public void onValidationFailed(@NonNull List<ValidationError> errors) {
            for (ValidationError error : errors) {
                TextInputEditText editText = (TextInputEditText) error.getView();
                TextInputLayout layout = (TextInputLayout) editText.getParent().getParent();

                String message = error.getCollatedErrorMessage(EditSectionActivity.this);

                layout.setError(message);
            }
        }

        @Override
        public void onValidationSucceeded() {
            mViewModel.updateSection(mSectionBuilder)
                    .subscribe(new OnUpdateSectionObserver());
        }
    }
}