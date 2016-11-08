package com.hjwylde.rivers.ui.dialogs;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import com.hjwylde.rivers.R;

import static com.google.common.base.Preconditions.checkNotNull;

public final class SelectImageDialog {
    public static final int REQUEST_CODE_PHOTO_TAKEN = 0;
    public static final int REQUEST_CODE_PHOTO_SELECTED = 1;

    private SelectImageDialog() {}

    public static final class Builder extends AlertDialog.Builder {
        private final Activity mActivity;

        public Builder(@NonNull Activity activity) {
            super(activity);

            mActivity = checkNotNull(activity);
            setTitle(R.string.title_dialog_selectImage);
            setItems(R.array.options_dialog_selectImage, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
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
            });
        }
    }
}
