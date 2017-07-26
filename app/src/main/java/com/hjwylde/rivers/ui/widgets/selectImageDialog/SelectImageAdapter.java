package com.hjwylde.rivers.ui.widgets.selectImageDialog;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hjwylde.rivers.R;

final class SelectImageAdapter extends ArrayAdapter<Option> {
    private LayoutInflater mInflater;
    @LayoutRes
    private int mResource;

    SelectImageAdapter(@NonNull Context context, int resource, @NonNull Option[] items) {
        super(context, resource, items);

        mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mResource = resource;
    }

    @NonNull
    @UiThread
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(mResource, parent, false);
        }

        Option option = getItem(position);
        if (option != null) {
            ((ImageView) convertView.findViewById(R.id.icon)).setImageResource(option.mIconId);
            ((TextView) convertView.findViewById(R.id.label)).setText(option.mLabelId);
        }

        return convertView;
    }
}