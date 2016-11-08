package com.hjwylde.rivers.ui.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hjwylde.rivers.R;
import com.hjwylde.rivers.RiversApplication;
import com.hjwylde.rivers.models.Action;
import com.hjwylde.rivers.models.Image;
import com.hjwylde.rivers.models.Section;
import com.hjwylde.rivers.ui.contracts.EditSectionContract;
import com.hjwylde.rivers.ui.presenters.EditSectionPresenter;

import static com.google.common.base.Preconditions.checkNotNull;

public final class EditSectionActivity extends BaseActivity implements EditSectionContract.View {
    public static final String INTENT_SECTION = "section";

    private static final String TAG = EditSectionActivity.class.getSimpleName();

    private static final String STATE_SECTION = "section";
    private static final String STATE_IMAGE = "image";

    private EditSectionContract.Presenter mPresenter;

    private Section mSection;
    private Image mImage;

    public void onCameraClick(View view) {
        // TODO (hjw)
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                return true;
            case R.id.saveSection:
                mPresenter.updateSection(buildAction());
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit_section, menu);

        return true;
    }

    @Override
    public void setImage(@NonNull Image image) {
        mImage = checkNotNull(image);
    }

    @Override
    public void refreshImage() {
        if (mImage == null) {
            return;
        }

        getImageView().setImageBitmap(mImage.getBitmap());
    }

    @Override
    public void onGetImageFailure(@NonNull Throwable t) {
        Log.w(TAG, t.getMessage(), t);
    }

    @Override
    public void onUpdateSectionSuccess(@NonNull Action action) {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onUpdateSectionFailure(@NonNull Throwable t) {
        Log.w(TAG, t.getMessage(), t);

        final Snackbar snackbar = Snackbar.make(findViewById(R.id.root_container), R.string.error_onUpdateSection, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.action_retry, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.updateSection(buildAction());

            }
        });

        snackbar.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_section);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mPresenter = new EditSectionPresenter(this, RiversApplication.getRiversService());

        mSection = (Section) getIntent().getSerializableExtra(INTENT_SECTION);
        refreshSection();
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
        outState.putSerializable(STATE_IMAGE, buildImage());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mSection = (Section) savedInstanceState.getSerializable(STATE_SECTION);
        refreshSection();

        mImage = (Image) savedInstanceState.getSerializable(STATE_IMAGE);
        refreshImage();
    }

    @Override
    protected void onPause() {
        mPresenter.unsubscribe();

        super.onPause();
    }

    private void refreshSection() {
        getTitleTextView().setText(mSection.getTitle());
        getSubtitleTextView().setText(mSection.getSubtitle());
        getGradeTextView().setText(mSection.getGrade());
        getLengthTextView().setText(mSection.getLength());
        getDurationTextView().setText(mSection.getDuration());
        getDescriptionTextView().setText(mSection.getDescription());

        View view = getCurrentFocus();
        if (view == null) {
            findViewById(R.id.title).requestFocus();
        } else if (view instanceof TextInputEditText) {
            TextInputEditText editText = (TextInputEditText) view;
            editText.setSelection(editText.getText().length());
        }
    }

    private Section buildSection() {
        Section.Builder builder = new Section.Builder(mSection);
        builder.title(getTitleString());
        builder.subtitle(getSubtitleString());
        builder.grade(getGradeString());
        builder.length(getLengthString());
        builder.duration(getDurationString());
        builder.description(getDescriptionString());

        return builder.build();
    }

    private Image buildImage() {
        return mImage;
    }

    private Action buildAction() {
        Action.Builder builder = new Action.Builder();
        builder.action(Action.ACTION_UPDATE);
        builder.targetId(mSection.getId());
        builder.targetCollection(Section.getCollection());

        // TODO (hjw): only update the property if it's changed
        builder.datum(Section.PROPERTY_TITLE, getTitleString());
        builder.datum(Section.PROPERTY_SUBTITLE, getSubtitleString());
        builder.datum(Section.PROPERTY_GRADE, getGradeString());
        builder.datum(Section.PROPERTY_LENGTH, getLengthString());
        builder.datum(Section.PROPERTY_DURATION, getDurationString());
        builder.datum(Section.PROPERTY_DESCRIPTION, getDescriptionString());

        return builder.build();
    }

    private String getTitleString() {
        return getTitleTextView().getText().toString();
    }

    private String getSubtitleString() {
        return getSubtitleTextView().getText().toString();
    }

    private String getGradeString() {
        return getGradeTextView().getText().toString();
    }

    private String getLengthString() {
        return getLengthTextView().getText().toString();
    }

    private String getDurationString() {
        return getDurationTextView().getText().toString();
    }

    private String getDescriptionString() {
        return getDescriptionTextView().getText().toString();
    }

    private ImageView getImageView() {
        return (ImageView) findViewById(R.id.image);
    }

    private TextView getTitleTextView() {
        return (TextView) findViewById(R.id.title);
    }

    private TextView getSubtitleTextView() {
        return (TextView) findViewById(R.id.subtitle);
    }

    private TextView getGradeTextView() {
        return (TextView) findViewById(R.id.grade);
    }

    private TextView getLengthTextView() {
        return (TextView) findViewById(R.id.length);
    }

    private TextView getDurationTextView() {
        return (TextView) findViewById(R.id.duration);
    }

    private TextView getDescriptionTextView() {
        return (TextView) findViewById(R.id.description);
    }
}