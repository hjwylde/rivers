package com.hjwylde.rivers.ui.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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

import com.google.android.gms.maps.model.LatLng;
import com.hjwylde.rivers.R;
import com.hjwylde.rivers.RiversApplication;
import com.hjwylde.rivers.models.Image;
import com.hjwylde.rivers.models.Section;
import com.hjwylde.rivers.ui.contracts.CreateSectionContract;
import com.hjwylde.rivers.ui.dialogs.SelectImageDialog;
import com.hjwylde.rivers.ui.presenters.CreateSectionPresenter;
import com.hjwylde.rivers.ui.util.NullTextWatcher;
import com.hjwylde.rivers.ui.util.SoftInput;

import java.io.IOException;

import static com.hjwylde.rivers.util.Preconditions.checkNotNull;

public final class CreateSectionActivity extends BaseActivity implements CreateSectionContract.View {
    public static final String INTENT_PUT_IN = "putIn";

    private static final String TAG = MapsActivity.class.getSimpleName();

    private static final String STATE_SECTION_BUILDER = "sectionBuilder";

    private CreateSectionContract.Presenter mPresenter;

    private Section.Builder mSectionBuilder = new Section.Builder();
    private Image mImage;

    public void onCameraClick(@NonNull View view) {
        new SelectImageDialog.Builder(this).create().show();
    }

    @Override
    public void onCreateImageFailure(@NonNull Throwable t) {
        Log.w(TAG, t.getMessage(), t);

        Snackbar snackbar = Snackbar.make(findViewById(R.id.root_container), R.string.error_onCreateImage, Snackbar.LENGTH_LONG);
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

        final Snackbar snackbar = Snackbar.make(findViewById(R.id.root_container), R.string.error_onCreateSection, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.action_retryCreateSection, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (snackbar.isShown()) {
                    snackbar.dismiss();
                }

                onCreateSectionClick();
            }
        });

        snackbar.show();
    }

    @Override
    public void onCreateSectionSuccess(@NonNull Section section) {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onGetImageFailure(@NonNull Throwable t) {
        Log.w(TAG, t.getMessage(), t);

        // TODO (hjw): report/retry
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
        ImageView imageView = findTById(R.id.image);

        if (mImage != null) {
            imageView.setImageBitmap(mImage.getBitmap());

            animateImageIn(imageView);
        } else {
            imageView.setImageResource(R.drawable.bm_create_section);
        }
    }

    @Override
    public void setImage(@NonNull Image image) {
        mImage = checkNotNull(image);
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

        Toolbar toolbar = findTById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        mPresenter = new CreateSectionPresenter(this, RiversApplication.getRiversService());

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

        mSectionBuilder = (Section.Builder) savedInstanceState.getSerializable(STATE_SECTION_BUILDER);
        refreshSection();

        refreshFocus();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mSectionBuilder != null && mSectionBuilder.imageId() != null && mImage == null) {
            mPresenter.getImage(mSectionBuilder.imageId());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(STATE_SECTION_BUILDER, mSectionBuilder);
    }

    private void animateImageIn(@NonNull View imageView) {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_image_in);

        imageView.startAnimation(animation);
    }

    private void onCreateSectionClick() {
        SoftInput.hide(this);

        mPresenter.createSection(mSectionBuilder);
    }

    private void onImageSelected(Bitmap bitmap) {
        Image.Builder builder = new Image.Builder();
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
        findTextViewById(R.id.title).setText(mSectionBuilder.title());
        findTextViewById(R.id.subtitle).setText(mSectionBuilder.subtitle());
        findTextViewById(R.id.grade).setText(mSectionBuilder.grade());
        findTextViewById(R.id.length).setText(mSectionBuilder.length());
        findTextViewById(R.id.duration).setText(mSectionBuilder.duration());
        findTextViewById(R.id.description).setText(mSectionBuilder.description());
    }
}