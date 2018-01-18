package fragments.offline_species;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import org.parceler.Parcel;
import org.parceler.Parcels;

import au.csiro.ozatlas.R;

/**
 * Created by sad038 on 5/6/17.
 */

public class SpeciesFilterBottomSheetDialogFragment extends BottomSheetDialogFragment {
    private SpeciesFilter speciesFilter;
    private SpeciesFilterBottomSheetListener bottomSheetListener;
    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    private SpeciesFilter getSpeciesFilter() {
        return speciesFilter;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        //noinspection RestrictedApi
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.dialog_species_filter, null);
        dialog.setContentView(contentView);

        CheckBox checkBoxLarge = (CheckBox) contentView.findViewById(R.id.checkBoxLarge);
        CheckBox checkBoxMedium = (CheckBox) contentView.findViewById(R.id.checkBoxMedium);
        CheckBox checkBoxSmall = (CheckBox) contentView.findViewById(R.id.checkBoxSmall);
        CheckBox checkBoxFur = (CheckBox) contentView.findViewById(R.id.checkBoxFur);
        CheckBox checkBoxFeather = (CheckBox) contentView.findViewById(R.id.checkBoxFeathers);
        Button done = contentView.findViewById(R.id.done);
        done.setOnClickListener(v -> {
            speciesFilter.isBodyCoverFeather = checkBoxFeather.isChecked();
            speciesFilter.isBodyCoverFur = checkBoxFur.isChecked();
            speciesFilter.isSizeLarge = checkBoxLarge.isChecked();
            speciesFilter.isSizeMedium = checkBoxMedium.isChecked();
            speciesFilter.isSizeSmall = checkBoxSmall.isChecked();
            if (bottomSheetListener != null)
                bottomSheetListener.onDoneFiltering(speciesFilter);
            dismiss();
        });

        Bundle bundle = getArguments();
        if (bundle != null) {
            speciesFilter = Parcels.unwrap(bundle.getParcelable(getString(R.string.species_filter_parameter)));
        }
        if (speciesFilter == null || bundle == null) {
            speciesFilter = new SpeciesFilter();
        }

        checkBoxFeather.setChecked(speciesFilter.isBodyCoverFeather);
        checkBoxFur.setChecked(speciesFilter.isBodyCoverFur);
        checkBoxLarge.setChecked(speciesFilter.isSizeLarge);
        checkBoxMedium.setChecked(speciesFilter.isSizeMedium);
        checkBoxSmall.setChecked(speciesFilter.isSizeSmall);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
    }

    public void setBottomSheetListener(SpeciesFilterBottomSheetListener bottomSheetListener) {
        this.bottomSheetListener = bottomSheetListener;
    }

    public interface SpeciesFilterBottomSheetListener {
        void onDoneFiltering(SpeciesFilter speciesFilter);
    }

    @Parcel
    public static class SpeciesFilter {
        public boolean isSizeLarge = true;
        public boolean isSizeMedium = true;
        public boolean isSizeSmall = true;
        public boolean isBodyCoverFur = true;
        public boolean isBodyCoverFeather = true;

    }
}
