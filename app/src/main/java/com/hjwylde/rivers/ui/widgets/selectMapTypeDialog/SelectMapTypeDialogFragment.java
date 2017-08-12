package com.hjwylde.rivers.ui.widgets.selectMapTypeDialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.ArrayAdapter;

import com.google.android.gms.maps.GoogleMap;
import com.hjwylde.rivers.R;

import static java.util.Objects.requireNonNull;

public final class SelectMapTypeDialogFragment extends DialogFragment {
    private ArrayAdapter<Option> mAdapter;

    private OnMapTypeSelectedListener mOnMapTypeSelectedListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Option[] options = new Option[]{
                new NormalOption(),
                new HybridOption(),
                new TerrainOption(),
        };

        mAdapter = new SelectMapTypeAdapter(getContext(), R.layout.item_select_map_type_option, options);

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.title_dialog_selectMapType)
                .setAdapter(mAdapter, (dialog, which) -> {
                    Option option = mAdapter.getItem(which);
                    if (option != null && mOnMapTypeSelectedListener != null) {
                        mOnMapTypeSelectedListener.onMapTypeSelected(option.getMapType());
                    }
                })
                .create();
    }

    public void setOnMapTypeSelectedListener(@NonNull OnMapTypeSelectedListener listener) {
        mOnMapTypeSelectedListener = requireNonNull(listener);
    }

    public interface OnMapTypeSelectedListener {
        void onMapTypeSelected(int mapType);
    }

    private final class HybridOption extends Option {
        public HybridOption() {
            super(R.drawable.ic_satellite, R.string.label_selectHybrid, GoogleMap.MAP_TYPE_HYBRID);
        }
    }

    private final class NormalOption extends Option {
        public NormalOption() {
            super(R.drawable.ic_map, R.string.label_selectNormal, GoogleMap.MAP_TYPE_NORMAL);
        }
    }

    private final class TerrainOption extends Option {
        public TerrainOption() {
            super(R.drawable.ic_terrain, R.string.label_selectTerrain, GoogleMap.MAP_TYPE_TERRAIN);
        }
    }
}