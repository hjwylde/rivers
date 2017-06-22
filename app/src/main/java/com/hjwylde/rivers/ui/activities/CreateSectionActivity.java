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
import com.hjwylde.rivers.R;
import com.hjwylde.rivers.RiversApplication;
import com.hjwylde.rivers.models.Image;
import com.hjwylde.rivers.models.Section;
import com.hjwylde.rivers.ui.contracts.CreateSectionContract;
import com.hjwylde.rivers.ui.dialogs.SelectImageDialog;
import com.hjwylde.rivers.ui.presenters.CreateSectionPresenter;
import com.hjwylde.rivers.ui.util.SoftInput;
import com.hjwylde.rivers.ui.viewModels.CreateSectionViewModel;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

import static java.util.Objects.requireNonNull;

public final class CreateSectionActivity extends BaseActivity implements CreateSectionContract.View {
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

    private CreateSectionViewModel mViewModel;
    private CreateSectionContract.Presenter mPresenter;

    private Section.DefaultBuilder mSectionBuilder = Section.builder();
    private Image mImage;

    @Override
    public void onCreateImageFailure(@NonNull Throwable t) {
        Log.w(TAG, t.getMessage(), t);

        // Should I assume that this can never occur?

        Snackbar snackbar = Snackbar.make(mRootView, R.string.error_onCreateImage, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    public void onCreateImageSuccess(@NonNull Image image) {
        mSectionBuilder.imageId(image.getId());

        setImage(image);
        refreshImage();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_create_section, menu);

        return true;
    }

    @Override
    public void onCreateSectionFailure(@NonNull Throwable t) {
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

    @Override
    public void onCreateSectionSuccess(@NonNull Section section) {
        setResult(RESULT_OK);
        finish();
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
    public void refreshImage() {
        if (mImage != null) {
            mImageView.setImageBitmap(mImage.getBitmap());

            animateImageIn();
        } else {
            mImageView.setImageResource(R.drawable.bm_create_section);
        }
    }

    @Override
    public void setImage(@NonNull Image image) {
        mImage = requireNonNull(image);
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
                    Log.w(TAG, e.getMessage(), e);

                    // TODO (hjw): report
                }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_section);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mViewModel = ViewModelProviders.of(this).get(CreateSectionViewModel.class);
        mViewModel.getImage().observe(this, onCreateImageObserver());

        mPresenter = new CreateSectionPresenter(this, RiversApplication.getRepository());

        LatLng putIn = getIntent().getParcelableExtra(INTENT_PUT_IN);
        mSectionBuilder.putIn(putIn);
    }

    @Override
    protected void onPause() {
        mPresenter.unsubscribe();

        super.onPause();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mSectionBuilder = (Section.DefaultBuilder) savedInstanceState.getSerializable(STATE_SECTION_BUILDER);
        refreshSection();

        if (mSectionBuilder.imageId() != null) {
            mViewModel.getImage(mSectionBuilder.imageId());
        }

        refreshFocus();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(STATE_SECTION_BUILDER, mSectionBuilder);
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

    private void animateImageIn() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_image_in);

        mImageView.startAnimation(animation);
    }

    @NonNull
    private Observer<Image> onCreateImageObserver() {
        return image -> {
            if (image == null) {
                // TODO (hjw): is this necessary?
                // mImageView.setImageResource(R.drawable.bm_create_section);
                return;
            }

            mImageView.setImageBitmap(image.getBitmap());

            Animation animation = AnimationUtils.loadAnimation(CreateSectionActivity.this, R.anim.fade_image_in);
            mImageView.startAnimation(animation);
        };
    }

    private void onCreateSectionClick() {
        SoftInput.hide(this);

        mPresenter.createSection(mSectionBuilder);
    }

    private void onImageSelected(@NonNull Bitmap bitmap) {
        Image.Builder builder = Image.builder();
        builder.bitmap(bitmap);

        mPresenter.createImage(builder);
    }

    private void refreshFocus() {
        View view = getCurrentFocus();
        if (view instanceof EditText) {
            EditText editText = (EditText) view;
            editText.setSelection(editText.getText().length());
        }
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