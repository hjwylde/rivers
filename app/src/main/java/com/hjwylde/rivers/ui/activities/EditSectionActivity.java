package com.hjwylde.rivers.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.hjwylde.rivers.R;
import com.hjwylde.rivers.RiversApplication;
import com.hjwylde.rivers.models.Image;
import com.hjwylde.rivers.models.Section;
import com.hjwylde.rivers.ui.contracts.EditSectionContract;
import com.hjwylde.rivers.ui.dialogs.SelectImageDialog;
import com.hjwylde.rivers.ui.presenters.EditSectionPresenter;

import static com.hjwylde.rivers.ui.dialogs.SelectImageDialog.REQUEST_CODE_PHOTO_SELECTED;
import static com.hjwylde.rivers.ui.dialogs.SelectImageDialog.REQUEST_CODE_PHOTO_TAKEN;
import static com.hjwylde.rivers.util.Preconditions.checkNotNull;

public final class EditSectionActivity extends BaseActivity implements EditSectionContract.View {
    public static final String INTENT_SECTION = "section";

    private static final String TAG = EditSectionActivity.class.getSimpleName();

    private static final String STATE_SECTION = "section";

    private EditSectionContract.Presenter mPresenter;

    private Section mSection;
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
            case REQUEST_CODE_PHOTO_TAKEN:
                // TODO (#11)
                break;
            case REQUEST_CODE_PHOTO_SELECTED:
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

        mPresenter = new EditSectionPresenter(this, RiversApplication.getRiversService());

        mSection = (Section) getIntent().getSerializableExtra(INTENT_SECTION);
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

        mSection = (Section) savedInstanceState.getSerializable(STATE_SECTION);
        refreshSection();

        refreshFocus();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mImage == null) {
            mPresenter.getImage(mSection.getImageId());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(STATE_SECTION, buildSection());
    }

    private void animateImageIn(@NonNull View imageView) {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_image_in);

        imageView.startAnimation(animation);
    }

    @NonNull
    private Section buildSection() {
        Section.Builder builder = new Section.Builder(mSection);
        builder.title(getTitle_());
        builder.subtitle(getSubtitle());
        builder.grade(getGrade());
        builder.length(getLength());
        builder.duration(getDuration());
        builder.description(getDescription());

        return builder.build();
    }

    @NonNull
    private String getDescription() {
        return findTextViewById(R.id.description).getText().toString();
    }

    @NonNull
    private String getDuration() {
        return findTextViewById(R.id.duration).getText().toString();
    }

    @NonNull
    private String getGrade() {
        return findTextViewById(R.id.grade).getText().toString();
    }

    @NonNull
    private String getLength() {
        return findTextViewById(R.id.length).getText().toString();
    }

    @NonNull
    private String getSubtitle() {
        return findTextViewById(R.id.subtitle).getText().toString();
    }

    @NonNull
    private String getTitle_() {
        return findTextViewById(R.id.title).getText().toString();
    }

    private void refreshFocus() {
        View view = getCurrentFocus();
        if (view == null) {
            findViewById(R.id.title).requestFocus();
        } else if (view instanceof TextInputEditText) {
            TextInputEditText editText = (TextInputEditText) view;
            editText.setSelection(editText.getText().length());
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
        findTextViewById(R.id.title).setText(mSection.getTitle());
        findTextViewById(R.id.subtitle).setText(mSection.getSubtitle());
        findTextViewById(R.id.grade).setText(mSection.getGrade());
        findTextViewById(R.id.length).setText(mSection.getLength());
        findTextViewById(R.id.duration).setText(mSection.getDuration());
        findTextViewById(R.id.description).setText(mSection.getDescription());
    }
}