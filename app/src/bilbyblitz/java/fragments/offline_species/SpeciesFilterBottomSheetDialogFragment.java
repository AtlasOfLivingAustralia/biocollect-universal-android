package fragments.offline_species;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import org.parceler.Parcels;

import activity.BilbyBlitzActivityListener;
import activity.BilbyBlitzBaseActivity;
import au.csiro.ozatlas.R;
import au.csiro.ozatlas.manager.Language;
import au.csiro.ozatlas.model.SpeciesFilter;

/**
 * Created by sad038 on 5/6/17.
 */

public class SpeciesFilterBottomSheetDialogFragment extends BottomSheetDialogFragment implements BilbyBlitzActivityListener {
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
    protected BilbyBlitzActivityListener bilbyBlitzActivityListener;
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
        CheckBox checkBoxSpikes = (CheckBox) contentView.findViewById(R.id.checkBoxSpikes);
        CheckBox checkBoxScales = (CheckBox) contentView.findViewById(R.id.checkBoxScales);
        CheckBox checkBoxFeather = (CheckBox) contentView.findViewById(R.id.checkBoxFeathers);
        Button done = contentView.findViewById(R.id.done);
        done.setOnClickListener(v -> {
            speciesFilter.isBodyCoverSpikes = checkBoxSpikes.isChecked();
            speciesFilter.isBodyCoverScales = checkBoxScales.isChecked();
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

        checkBoxSpikes.setChecked(speciesFilter.isBodyCoverSpikes);
        checkBoxSpikes.setText(localisedString("spikes", R.string.spikes));
        checkBoxScales.setChecked(speciesFilter.isBodyCoverScales);
        checkBoxScales.setText(localisedString("scales", R.string.scales));
        checkBoxFeather.setChecked(speciesFilter.isBodyCoverFeather);
        checkBoxFeather.setText(localisedString("feather", R.string.feathers));
        checkBoxFur.setChecked(speciesFilter.isBodyCoverFur);
        checkBoxFur.setText(localisedString("fur", R.string.fur));
        checkBoxLarge.setChecked(speciesFilter.isSizeLarge);
        checkBoxLarge.setText(localisedString("large", R.string.large));
        checkBoxMedium.setChecked(speciesFilter.isSizeMedium);
        checkBoxMedium.setText(localisedString("medium", R.string.medium));
        checkBoxSmall.setChecked(speciesFilter.isSizeSmall);
        checkBoxSmall.setText(localisedString("small", R.string.small));

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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BilbyBlitzBaseActivity) {
            bilbyBlitzActivityListener = (BilbyBlitzBaseActivity) context;
        }
    }

    /**
     * localised a string if a translation is being chosen
     *
     * @param key
     * @param defaultRes
     * @return
     */
    @Override
    public String localisedString(String key, int defaultRes) {
        if (bilbyBlitzActivityListener != null)
            return bilbyBlitzActivityListener.localisedString(key, defaultRes);
        return null;
    }

    /**
     * localised a string if a translation is being chosen
     *
     * @param key
     * @return
     */
    @Override
    public String localisedString(String key) {
        if (bilbyBlitzActivityListener != null)
            return bilbyBlitzActivityListener.localisedString(key);
        return null;
    }

    /**
     * loading the language file
     *
     * @param fileName
     */
    @Override
    public void loadLanguageFile(String fileName, Language language) {
        if (bilbyBlitzActivityListener != null)
            bilbyBlitzActivityListener.loadLanguageFile(fileName, language);
    }
}
