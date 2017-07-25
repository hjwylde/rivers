package com.hjwylde.rivers.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hjwylde.rivers.R;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;
import static java.util.Objects.requireNonNull;

public final class SelectImageDialogFragment extends DialogFragment {
    private static final int REQUEST_CODE_PHOTO_TAKEN = 0;
    private static final int REQUEST_CODE_PHOTO_SELECTED = 1;

    private final Option[] mOptions = new Option[]{
            new Option(R.drawable.ic_camera, R.string.label_takePhoto, view -> {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_CODE_PHOTO_TAKEN);
                } else {
                    // TODO (hjw)
                }
            }),
            new Option(R.drawable.ic_image, R.string.label_selectPhoto, view -> {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");

                startActivityForResult(intent, REQUEST_CODE_PHOTO_SELECTED);
            }),
    };

    private ArrayAdapter<Option> mAdapter;

    private OnImageSelectedListener mOnImageSelectedListener;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        Bitmap bitmap = null;

        switch (requestCode) {
            case REQUEST_CODE_PHOTO_TAKEN:
                bitmap = data.getParcelableExtra("data");
                break;
            case REQUEST_CODE_PHOTO_SELECTED:
                Uri uri = data.getData();

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                } catch (IOException e) {
                    // TODO (hjw)
                }
        }

        if (bitmap != null) {
            mOnImageSelectedListener.onImageSelected(bitmap);
        }

        dismiss();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mAdapter = new SelectImageAdapter(getContext(), R.layout.item_select_image_option, mOptions);

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.title_dialog_selectImage)
                .setAdapter(mAdapter, null)
                .create();

        dialog.getListView().setOnItemClickListener((parent, view, position, id) -> {
            Option option = mAdapter.getItem(position);
            if (option != null) {
                option.mOnClickListener.onClick(view);
            }
        });

        return dialog;
    }

    public void setOnImageSelectedListener(@NonNull OnImageSelectedListener listener) {
        mOnImageSelectedListener = requireNonNull(listener);
    }

    public interface OnImageSelectedListener {
        void onImageSelected(@NonNull Bitmap bitmap);
    }

    private static final class Option {
        @DrawableRes
        final int mIconId;
        @StringRes
        final int mLabelId;

        View.OnClickListener mOnClickListener;

        public Option(@DrawableRes int iconId, @StringRes int labelId, @NonNull View.OnClickListener listener) {
            mIconId = iconId;
            mLabelId = labelId;

            mOnClickListener = requireNonNull(listener);
        }
    }

    private final class SelectImageAdapter extends ArrayAdapter<Option> {
        private LayoutInflater mInflater;
        @LayoutRes
        private int mResource;

        SelectImageAdapter(@NonNull Context context, int resource, @NonNull Option[] items) {
            super(context, resource, items);

            mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mResource = resource;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(mResource, parent, false);
            }

            Option option = getItem(position);

            ((ImageView) convertView.findViewById(R.id.icon)).setImageResource(option.mIconId);
            ((TextView) convertView.findViewById(R.id.label)).setText(option.mLabelId);

            return convertView;
        }
    }
}
