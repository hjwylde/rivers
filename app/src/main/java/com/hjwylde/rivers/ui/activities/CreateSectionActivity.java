package com.hjwylde.rivers.ui.activities;

import android.content.Intent;
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

import com.google.android.gms.maps.model.LatLng;
import com.hjwylde.rivers.R;
import com.hjwylde.rivers.RiversApplication;
import com.hjwylde.rivers.models.Section;
import com.hjwylde.rivers.ui.contracts.CreateSectionContract;
import com.hjwylde.rivers.ui.dialogs.SelectImageDialog;
import com.hjwylde.rivers.ui.presenters.CreateSectionPresenter;

import static com.hjwylde.rivers.ui.dialogs.SelectImageDialog.REQUEST_CODE_PHOTO_SELECTED;
import static com.hjwylde.rivers.ui.dialogs.SelectImageDialog.REQUEST_CODE_PHOTO_TAKEN;

public final class CreateSectionActivity extends BaseActivity implements CreateSectionContract.View {
    public static final String INTENT_PUT_IN = "putIn";

    private static final String TAG = CreateSectionActivity.class.getSimpleName();

    private static final String STATE_SECTION_BUILDER = "sectionBuilder";

    private CreateSectionContract.Presenter mPresenter;

    private Section.Builder mSectionBuilder = new Section.Builder();

    public void onCameraClick(View view) {
        new SelectImageDialog.Builder(this).create().show();
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
        snackbar.setAction(R.string.action_retry, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO (#61)
            }
        });

        snackbar.show();
    }

    @Override
    public void onCreateSectionSuccess() {
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
                // TODO (#61)
                // mPresenter.createSection(buildAction());
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
            case REQUEST_CODE_PHOTO_TAKEN:
                // TODO (hjw)
                break;
            case REQUEST_CODE_PHOTO_SELECTED:
                // TODO (hjw)
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_section);

        Toolbar toolbar = findToolbarById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mPresenter = new CreateSectionPresenter(this, RiversApplication.getRiversService());

        LatLng putIn = getIntent().getParcelableExtra(INTENT_PUT_IN);
        mSectionBuilder.putIn(putIn);
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(STATE_SECTION_BUILDER, buildSectionBuilder());
    }

    private Section.Builder buildSectionBuilder() {
        Section.Builder builder = new Section.Builder(mSectionBuilder);
        builder.title(getTitle_());
        builder.subtitle(getSubtitle());
        builder.grade(getGrade());
        builder.length(getLength());
        builder.duration(getDuration());
        builder.description(getDescription());

        return builder;
    }

    private String getDescription() {
        return findTextViewById(R.id.description).getText().toString();
    }

    private String getDuration() {
        return findTextViewById(R.id.duration).getText().toString();
    }

    private String getGrade() {
        return findTextViewById(R.id.grade).getText().toString();
    }

    private String getLength() {
        return findTextViewById(R.id.length).getText().toString();
    }

    private String getSubtitle() {
        return findTextViewById(R.id.subtitle).getText().toString();
    }

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

    private void refreshSection() {
        findTextViewById(R.id.title).setText(mSectionBuilder.title());
        findTextViewById(R.id.subtitle).setText(mSectionBuilder.subtitle());
        findTextViewById(R.id.grade).setText(mSectionBuilder.grade());
        findTextViewById(R.id.length).setText(mSectionBuilder.length());
        findTextViewById(R.id.duration).setText(mSectionBuilder.duration());
        findTextViewById(R.id.description).setText(mSectionBuilder.description());
    }
}