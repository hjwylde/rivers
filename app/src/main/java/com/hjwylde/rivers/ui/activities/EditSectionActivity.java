package com.hjwylde.rivers.ui.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;

import com.hjwylde.reactivex.observers.LifecycleBoundMaybeObserver;
import com.hjwylde.reactivex.observers.LifecycleBoundSingleObserver;
import com.hjwylde.rivers.R;
import com.hjwylde.rivers.models.Image;
import com.hjwylde.rivers.models.Section;
import com.hjwylde.rivers.ui.dialogs.SelectImageDialog;
import com.hjwylde.rivers.ui.util.NullTextWatcher;
import com.hjwylde.rivers.ui.util.SoftInput;
import com.hjwylde.rivers.ui.viewModels.EditSectionViewModel;

import java.io.IOException;

public final class EditSectionActivity extends BaseActivity {
    public static final String INTENT_SECTION_BUILDER = "sectionBuilder";
    public static final String RESULT_SECTION_ID = "sectionId";

    private static final String TAG = EditSectionActivity.class.getSimpleName();

    private static final String STATE_SECTION_BUILDER = "sectionBuilder";

    private EditSectionViewModel mViewModel;

    private Section.DefaultBuilder mSectionBuilder;

    public void onCameraClick(@NonNull View view) {
        new SelectImageDialog.Builder(this).create().show();
    }

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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_section);

        Toolbar toolbar = findTById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mViewModel = ViewModelProviders.of(this).get(EditSectionViewModel.class);

        findEditTextById(R.id.title).addTextChangedListener(new NullTextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                mSectionBuilder.title(editable.toString());
            }
        });
        findEditTextById(R.id.subtitle).addTextChangedListener(new NullTextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                mSectionBuilder.subtitle(editable.toString());
            }
        });
        findEditTextById(R.id.grade).addTextChangedListener(new NullTextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                mSectionBuilder.grade(editable.toString());
            }
        });
        findEditTextById(R.id.length).addTextChangedListener(new NullTextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                mSectionBuilder.length(editable.toString());
            }
        });
        findEditTextById(R.id.duration).addTextChangedListener(new NullTextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                mSectionBuilder.duration(editable.toString());
            }
        });
        findEditTextById(R.id.description).addTextChangedListener(new NullTextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                mSectionBuilder.description(editable.toString());
            }
        });

        mSectionBuilder = (Section.DefaultBuilder) getIntent().getSerializableExtra(INTENT_SECTION_BUILDER);
        refreshSection();

        refreshFocus();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mSectionBuilder = (Section.DefaultBuilder) savedInstanceState.getSerializable(STATE_SECTION_BUILDER);
        refreshSection();

        refreshFocus();
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
            mViewModel.getImage(mSectionBuilder.imageId())
                    .subscribe(new OnGetImageObserver());
        }
    }

    private void animateImageIn(@NonNull View imageView) {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_image_in);

        imageView.startAnimation(animation);
    }

    private void onImageSelected(Bitmap bitmap) {
        Image.Builder builder = Image.builder();
        builder.bitmap(bitmap);

        mViewModel.createImage(builder)
                .subscribe(new OnCreateImageObserver());
    }

    private void onUpdateSectionClick() {
        SoftInput.hide(this);

        mViewModel.updateSection(mSectionBuilder)
                .subscribe(new OnUpdateSectionObserver());
    }

    private void refreshFocus() {
        View view = getCurrentFocus();
        if (view instanceof EditText) {
            EditText editText = (EditText) view;
            editText.setSelection(editText.getText().length());
        }
    }

    private void refreshImage(@NonNull Image image) {
        ImageView imageView = findTById(R.id.image);
        imageView.setImageBitmap(image.getBitmap());

        animateImageIn(imageView);
    }

    private void refreshSection() {
        findTextViewById(R.id.title).setText(mSectionBuilder.title());
        findTextViewById(R.id.subtitle).setText(mSectionBuilder.subtitle());
        findTextViewById(R.id.grade).setText(mSectionBuilder.grade());
        findTextViewById(R.id.length).setText(mSectionBuilder.length());
        findTextViewById(R.id.duration).setText(mSectionBuilder.duration());
        findTextViewById(R.id.description).setText(mSectionBuilder.description());
    }

    private final class OnCreateImageObserver extends LifecycleBoundSingleObserver<Image> {
        OnCreateImageObserver() {
            super(EditSectionActivity.this);
        }

        @Override
        public void onError(Throwable t) {
            Log.w(TAG, t.getMessage(), t);

            Snackbar snackbar = Snackbar.make(findViewById(R.id.root_container), R.string.error_onCreateImage, Snackbar.LENGTH_LONG);
            snackbar.show();
        }

        @Override
        public void onSuccess(Image image) {
            mSectionBuilder.imageId(image.getId());

            refreshImage(image);
        }
    }

    private final class OnGetImageObserver extends LifecycleBoundMaybeObserver<Image> {
        OnGetImageObserver() {
            super(EditSectionActivity.this);
        }

        @Override
        public void onComplete() {
            // Do nothing
        }

        @Override
        public void onError(Throwable t) {
            // TODO (hjw)
        }

        @Override
        public void onSuccess(Image image) {
            refreshImage(image);
        }
    }

    private final class OnUpdateSectionObserver extends LifecycleBoundSingleObserver<Section> {
        OnUpdateSectionObserver() {
            super(EditSectionActivity.this);
        }

        @Override
        public void onError(Throwable t) {
            Log.w(TAG, t.getMessage(), t);

            final Snackbar snackbar = Snackbar.make(findViewById(R.id.root_container), R.string.error_onUpdateSection, Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.action_retryUpdateSection, view -> {
                if (snackbar.isShown()) {
                    snackbar.dismiss();
                }

                onUpdateSectionClick();
            });

            snackbar.show();
        }

        @Override
        public void onSuccess(Section section) {
            Intent data = new Intent();
            data.putExtra(RESULT_SECTION_ID, section.getId());

            setResult(RESULT_OK, data);
            finish();
        }
    }
}