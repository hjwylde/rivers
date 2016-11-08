package com.hjwylde.rivers.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.hjwylde.rivers.R;
import com.hjwylde.rivers.RiversApplication;
import com.hjwylde.rivers.models.Action;
import com.hjwylde.rivers.models.Image;
import com.hjwylde.rivers.models.Section;
import com.hjwylde.rivers.ui.contracts.CreateSectionContract;
import com.hjwylde.rivers.ui.presenters.CreateSectionPresenter;

public final class CreateSectionActivity extends BaseActivity implements CreateSectionContract.View {
    public static final String INTENT_PUT_IN = "putIn";

    private static final String TAG = CreateSectionActivity.class.getSimpleName();

    private static final String STATE_SECTION_BUILDER = "sectionBuilder";
    private static final String STATE_IMAGE = "image";

    private static final int REQUEST_CODE_PHOTO_TAKEN = 0;
    private static final int REQUEST_CODE_PHOTO_SELECTED = 1;

    private CreateSectionContract.Presenter mPresenter;

    private Section.Builder mSectionBuilder = new Section.Builder();
    private Image mImage;

    public void onCameraClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.title_dialog_selectImage));
        builder.setItems(getResources().getStringArray(R.array.options_dialog_selectImage), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(intent, REQUEST_CODE_PHOTO_TAKEN);
                                break;
                            case 1:
                                intent = new Intent(Intent.ACTION_GET_CONTENT);
                                intent.setType("image/*");
                                startActivityForResult(intent, REQUEST_CODE_PHOTO_SELECTED);
                        }
                    }
                });

        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case REQUEST_CODE_PHOTO_TAKEN:
                break;
            case REQUEST_CODE_PHOTO_SELECTED:
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                return true;
            case R.id.createSection:
                mPresenter.createSection(buildAction());
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_create_section, menu);

        return true;
    }

    @Override
    public void onCreateSectionSuccess(@NonNull Action action) {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onCreateSectionFailure(@NonNull Throwable t) {
        Log.w(TAG, t.getMessage(), t);

        final Snackbar snackbar = Snackbar.make(findViewById(R.id.root_container), R.string.error_onCreateSection, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.action_retry, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.createSection(buildAction());

            }
        });

        snackbar.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_section);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mPresenter = new CreateSectionPresenter(this, RiversApplication.getRiversService());

        LatLng putIn = getIntent().getParcelableExtra(INTENT_PUT_IN);
        mSectionBuilder.putIn(putIn);
        refreshSection();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(STATE_SECTION_BUILDER, buildSectionBuilder());
        outState.putSerializable(STATE_IMAGE, buildImage());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mSectionBuilder = (Section.Builder) savedInstanceState.getSerializable(STATE_SECTION_BUILDER);
        refreshSection();

        mImage = (Image) savedInstanceState.getSerializable(STATE_IMAGE);
    }

    @Override
    protected void onPause() {
        mPresenter.unsubscribe();

        super.onPause();
    }

    private void refreshSection() {
        getTitleTextView().setText(mSectionBuilder.title());
        getSubtitleTextView().setText(mSectionBuilder.subtitle());
        getGradeTextView().setText(mSectionBuilder.grade());
        getLengthTextView().setText(mSectionBuilder.length());
        getDurationTextView().setText(mSectionBuilder.duration());
        getDescriptionTextView().setText(mSectionBuilder.description());

        View view = getCurrentFocus();
        if (view == null) {
            findViewById(R.id.title).requestFocus();
        } else if (view instanceof TextInputEditText) {
            TextInputEditText editText = (TextInputEditText) view;
            editText.setSelection(editText.getText().length());
        }
    }

    private Section.Builder buildSectionBuilder() {
        Section.Builder builder = new Section.Builder(mSectionBuilder);
        builder.title(getTitleString());
        builder.subtitle(getSubtitleString());
        builder.grade(getGradeString());
        builder.length(getLengthString());
        builder.duration(getDurationString());
        builder.description(getDescriptionString());

        return builder;
    }

    private Image buildImage() {
        return mImage;
    }

    private Action buildAction() {
        Action.Builder builder = new Action.Builder();
        builder.action(Action.ACTION_INSERT);
        builder.targetCollection(Section.getCollection());

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