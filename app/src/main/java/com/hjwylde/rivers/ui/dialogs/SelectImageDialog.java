package com.hjwylde.rivers.ui.dialogs;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import com.hjwylde.rivers.R;

import static java.util.Objects.requireNonNull;

public final class SelectImageDialog {
    public static final int REQUEST_CODE_PHOTO_TAKEN = 0;
    public static final int REQUEST_CODE_PHOTO_SELECTED = 1;

    private SelectImageDialog() {
    }

    public static final class Builder extends AlertDialog.Builder {
        public Builder(@NonNull Activity activity) {
            super(activity);

            setTitle(R.string.title_dialog_selectImage);
            setItems(R.array.options_dialog_selectImage, new DefaultOnClickListener(activity));
        }
    }

    private static final class DefaultOnClickListener implements DialogInterface.OnClickListener {
        private final Activity mActivity;

        public DefaultOnClickListener(@NonNull Activity activity) {
            mActivity = requireNonNull(activity);
        }

        @Override
        public void onClick(DialogInterface dialogInterface, int which) {
            switch (which) {
                case 0:
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    mActivity.startActivityForResult(intent, REQUEST_CODE_PHOTO_TAKEN);
                    break;
                case 1:
                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    mActivity.startActivityForResult(intent, REQUEST_CODE_PHOTO_SELECTED);
            }
        }
    }
}
