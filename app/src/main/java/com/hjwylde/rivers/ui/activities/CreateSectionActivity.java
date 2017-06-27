package com.hjwylde.rivers.ui.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;
import com.hjwylde.lifecycle.CompletableResult;
import com.hjwylde.rivers.R;
import com.hjwylde.rivers.models.Image;
import com.hjwylde.rivers.models.Section;
import com.hjwylde.rivers.ui.dialogs.SelectImageDialog;
import com.hjwylde.rivers.ui.util.SoftInput;
import com.hjwylde.rivers.ui.viewModels.CreateSectionViewModel;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public final class CreateSectionActivity extends BaseActivity {
    public static final String INTENT_PUT_IN = "putIn";

    private static final String TAG = CreateSectionActivity.class.getSimpleName();

    private static final String STATE_SECTION_BUILDER = "sectionBuilder";

    @BindView(R.id.root_container)
    View mRootView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.image)
    ImageView mImageView;
    @BindView(R.id.title)
    EditText mTitleView;
    @BindView(R.id.subtitle)
    EditText mSubtitleView;
    @BindView(R.id.grade)
    EditText mGradeView;
    @BindView(R.id.length)
    EditText mLengthView;
    @BindView(R.id.duration)
    EditText mDurationView;
    @BindView(R.id.description)
    EditText mDescriptionView;
    Animation mFadeImageInAnimation;

    private Section.DefaultBuilder mSectionBuilder = Section.builder();

    private CreateSectionViewModel mViewModel;
    private Observer<CompletableResult<Image>> mOnCreateImageObserver = result -> {
        switch (result.code()) {
            case OK:
                Image image = result.getResult();

                mSectionBuilder.imageId(image.getId());

                refreshImage(image);
                break;
            case ERROR:
                onCreateImageFailure(result.getThrowable());
        }
    };
    private Observer<CompletableResult<Section>> mOnCreateSectionObserver = result -> {
        switch (result.code()) {
            case OK:
                setResult(RESULT_OK);
                finish();
                break;
            case ERROR:
                onCreateSectionFailure(result.getThrowable());
        }
    };
    private Observer<Image> mOnGetImageObserver = image -> {
        if (image != null) {
            refreshImage(image);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_create_section, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                return true;
            case R.id.createSection:
                onCreateSectionClick();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case SelectImageDialog.REQUEST_CODE_PHOTO_TAKEN:
                Bitmap bitmap = data.getParcelableExtra("data");

                onImageSelected(bitmap);
                break;
            case SelectImageDialog.REQUEST_CODE_PHOTO_SELECTED:
                Uri uri = data.getData();

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                    onImageSelected(bitmap);
                } catch (IOException e) {
                    onCreateImageFailure(e);
                }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_section);

        ButterKnife.bind(this);
        mFadeImageInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_image_in);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mViewModel = ViewModelProviders.of(this).get(CreateSectionViewModel.class);

        if (savedInstanceState != null) {
            mSectionBuilder = (Section.DefaultBuilder) savedInstanceState.getSerializable(STATE_SECTION_BUILDER);
            refreshSection();

            refreshFocus();
        } else {
            LatLng putIn = getIntent().getParcelableExtra(INTENT_PUT_IN);
            mSectionBuilder.putIn(putIn);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(STATE_SECTION_BUILDER, mSectionBuilder);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mSectionBuilder.imageId() != null) {
            mViewModel.getImage(mSectionBuilder.imageId()).observe(this, mOnGetImageObserver);
        }
    }

    @OnClick(R.id.camera)
    void onCameraClick() {
        new SelectImageDialog.Builder(this).create().show();
    }

    @OnTextChanged(R.id.description)
    void onDescriptionTextChanged(@NonNull CharSequence text) {
        mSectionBuilder.description(text.toString());
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
        mSectionBuilder.subtitle(text.toString());
    }

    @OnTextChanged(R.id.title)
    void onTitleTextChanged(@NonNull CharSequence text) {
        mSectionBuilder.title(text.toString());
    }

    private void onCreateImageFailure(@NonNull Throwable t) {
        Log.w(TAG, t.getMessage(), t);

        Snackbar snackbar = Snackbar.make(mRootView, R.string.error_onCreateImage, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void onCreateSectionClick() {
        SoftInput.hide(this);

        mViewModel.createSection(mSectionBuilder).observe(this, mOnCreateSectionObserver);
    }

    private void onCreateSectionFailure(@NonNull Throwable t) {
        Log.w(TAG, t.getMessage(), t);

        final Snackbar snackbar = Snackbar.make(mRootView, R.string.error_onCreateSection, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.action_retryCreateSection, view -> {
            if (snackbar.isShown()) {
                snackbar.dismiss();
            }

            onCreateSectionClick();
        });

        snackbar.show();
    }

    private void onImageSelected(@NonNull Bitmap bitmap) {
        Image.Builder builder = Image.builder();
        builder.bitmap(bitmap);

        mViewModel.createImage(builder).observe(this, mOnCreateImageObserver);
    }

    private void refreshFocus() {
        View view = getCurrentFocus();
        if (view instanceof EditText) {
            EditText editText = (EditText) view;
            editText.setSelection(editText.getText().length());
        }
    }

    private void refreshImage(@NonNull Image image) {
        mImageView.setImageBitmap(image.getBitmap());

        mImageView.startAnimation(mFadeImageInAnimation);
    }

    private void refreshSection() {
        mTitleView.setText(mSectionBuilder.title());
        mSubtitleView.setText(mSectionBuilder.subtitle());
        mGradeView.setText(mSectionBuilder.grade());
        mLengthView.setText(mSectionBuilder.length());
        mDurationView.setText(mSectionBuilder.duration());
        mDescriptionView.setText(mSectionBuilder.description());
    }
}