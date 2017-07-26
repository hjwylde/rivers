package com.hjwylde.rivers.ui.widgets.selectImageDialog;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import com.hjwylde.rivers.BuildConfig;
import com.hjwylde.rivers.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;
import static java.util.Objects.requireNonNull;

public final class SelectImageDialogFragment extends DialogFragment {
    private static final String TAG = SelectImageDialogFragment.class.getSimpleName();

    private static final String FILE_PROVIDER_AUTHORITY = BuildConfig.APPLICATION_ID + ".fileProvider";

    private static final int REQUEST_CODE_PHOTO_TAKEN = 0;
    private static final int REQUEST_CODE_PHOTO_SELECTED = 1;

    private Option[] mOptions;
    private ArrayAdapter<Option> mAdapter;

    private OnImageSelectedListener mOnImageSelectedListener;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            getTakePhotoOption().cleanImageFile();
            return;
        }

        Uri uri = null;

        switch (requestCode) {
            case REQUEST_CODE_PHOTO_TAKEN:
                uri = getTakePhotoOption().getImageUri();
                break;
            case REQUEST_CODE_PHOTO_SELECTED:
                uri = data.getData();
        }

        if (uri != null) {
            mOnImageSelectedListener.onImageSelected(uri);
        }

        dismiss();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mOptions = new Option[]{
                new TakePhotoOption(),
                new SelectPhotoOption(),
        };

        mAdapter = new SelectImageAdapter(getContext(), R.layout.item_select_image_option, mOptions);

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.title_dialog_selectImage)
                .setAdapter(mAdapter, null)
                .create();

        dialog.getListView().setOnItemClickListener((parent, view, position, id) -> {
            Option option = mAdapter.getItem(position);
            if (option != null) {
                option.onClick(view);
            }
        });

        return dialog;
    }

    public void setOnImageSelectedListener(@NonNull OnImageSelectedListener listener) {
        mOnImageSelectedListener = requireNonNull(listener);
    }

    @NonNull
    private TakePhotoOption getTakePhotoOption() {
        for (Option option : mOptions) {
            if (option instanceof TakePhotoOption) {
                return (TakePhotoOption) option;
            }
        }

        throw new IllegalStateException();
    }


    public interface OnImageSelectedListener {
        void onImageSelected(@NonNull Uri uri);
    }

    private final class SelectPhotoOption extends Option {
        SelectPhotoOption() {
            super(R.drawable.ic_image, R.string.label_selectPhoto);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");

            startActivityForResult(intent, REQUEST_CODE_PHOTO_SELECTED);
        }
    }

    private final class TakePhotoOption extends Option {
        private File mImageFile;

        TakePhotoOption() {
            super(R.drawable.ic_camera, R.string.label_takePhoto);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                createImageFile();

                if (mImageFile != null) {
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, getImageUri());

                    startActivityForResult(intent, REQUEST_CODE_PHOTO_TAKEN);
                } else {
                    // TODO (hjw)
                }
            } else {
                // TODO (hjw)
            }
        }

        void cleanImageFile() {
            if (mImageFile == null) {
                return;
            }

            mImageFile.delete();
            mImageFile = null;
        }

        @NonNull
        Uri getImageUri() {
            return FileProvider.getUriForFile(getContext(), FILE_PROVIDER_AUTHORITY, mImageFile);
        }

        private void createImageFile() {
            File dir = new File(getContext().getExternalCacheDir(), Environment.DIRECTORY_PICTURES);
            String prefix = SimpleDateFormat.getDateTimeInstance().format(new Date());

            try {
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                mImageFile = File.createTempFile(prefix, ".jpg", dir);
            } catch (IOException e) {
                Log.w(TAG, e.getMessage(), e);
            }
        }
    }
}
