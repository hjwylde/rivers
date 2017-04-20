package com.hjwylde.rivers.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.hjwylde.rivers.R;
import com.hjwylde.rivers.RiversApplication;
import com.hjwylde.rivers.models.Image;
import com.hjwylde.rivers.models.Section;
import com.hjwylde.rivers.ui.contracts.EditSectionContract;
import com.hjwylde.rivers.ui.dialogs.SelectImageDialog;
import com.hjwylde.rivers.ui.presenters.EditSectionPresenter;
import com.hjwylde.rivers.ui.util.NullTextWatcher;

import static com.hjwylde.rivers.util.Preconditions.checkNotNull;

public final class EditSectionActivity extends BaseActivity implements EditSectionContract.View {
    public static final String INTENT_SECTION = "section";

    private static final String TAG = EditSectionActivity.class.getSimpleName();

    private static final String STATE_SECTION_BUILDER = "sectionBuilder";

    private EditSectionContract.Presenter mPresenter;

    private Section.Builder mSectionBuilder;
    private Image mImage;

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
    public void onGetImageFailure(@NonNull Throwable t) {
        Log.w(TAG, t.getMessage(), t);

        // TODO (#74)
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                return true;
            case R.id.saveSection:
                // TODO (#13)
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void refreshImage() {
        refreshImage(true);
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
                // TODO (#11)
                break;
            case SelectImageDialog.REQUEST_CODE_PHOTO_SELECTED:
                // TODO (#11)
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_section);

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

        mPresenter = new EditSectionPresenter(this, RiversApplication.getRiversService());

        Section section = (Section) getIntent().getSerializableExtra(INTENT_SECTION);
        mSectionBuilder = new Section.Builder(section);
        refreshSection();

        refreshFocus();
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

        if (mSectionBuilder.imageId() != null && mImage == null) {
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

    private void refreshFocus() {
        View view = getCurrentFocus();
        if (view instanceof EditText) {
            EditText editText = (EditText) view;
            editText.setSelection(editText.getText().length());
        } else {
            findViewById(R.id.title).requestFocus();
        }
    }

    private void refreshImage(boolean animate) {
        if (mImage == null) {
            return;
        }

        ImageView imageView = findTById(R.id.image);
        imageView.setImageBitmap(mImage.getBitmap());

        if (animate) {
            animateImageIn(imageView);
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