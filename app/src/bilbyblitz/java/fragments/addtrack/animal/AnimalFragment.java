package fragments.addtrack.animal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.parceler.Parcels;

import activity.SingleFragmentActivity;
import au.csiro.ozatlas.R;
import au.csiro.ozatlas.manager.AtlasDialogManager;
import au.csiro.ozatlas.manager.FileUtils;
import au.csiro.ozatlas.manager.Language;
import base.BaseMainActivityFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import fragments.addtrack.AddTrackFragment;
import fragments.addtrack.BilbyDataManager;
import fragments.addtrack.ValidationCheck;
import io.realm.RealmList;
import model.track.BilbyBlitzData;
import model.track.SightingEvidenceTable;

import static android.app.Activity.RESULT_OK;

/**
 * Created by sad038 on 9/10/17.
 */

public class AnimalFragment extends BaseMainActivityFragment implements ValidationCheck, BilbyDataManager {
    private final int ADD_ANIMAL_REQUEST_CODE = 1;
    private final int EDIT_ANIMAL_REQUEST_CODE = 2;

    @BindView(R.id.listView)
    ListView listView;
    @BindView(R.id.total)
    TextView total;

    private RealmList<SightingEvidenceTable> sightingEvidenceTables = new RealmList<>();
    private SightingEvidenceTableAdapter sightingEvidenceTableAdapter;
    private int editRequestPosition = -1;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_animal, container, false);
        ButterKnife.bind(this, view);

        if (getParentFragment() instanceof AddTrackFragment) {
            BilbyBlitzData bilbyBlitzData = ((AddTrackFragment) getParentFragment()).getBilbyBlitzData();
            if (bilbyBlitzData.sightingEvidenceTable != null)
                sightingEvidenceTables = bilbyBlitzData.sightingEvidenceTable;
            else
                bilbyBlitzData.sightingEvidenceTable = sightingEvidenceTables;
        }

        setFloatingButtonClickListener(v -> {
            startAddAnimalActivity(new Bundle(), ADD_ANIMAL_REQUEST_CODE);
        });

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            editRequestPosition = position;
            Bundle bundle = new Bundle();
            bundle.putParcelable(getString(R.string.add_animal_parameter), Parcels.wrap(sightingEvidenceTables.get(position)));
            startAddAnimalActivity(bundle, EDIT_ANIMAL_REQUEST_CODE);
        });

        listView.setOnItemLongClickListener((parent, view12, position, id) -> {
            AtlasDialogManager.alertBox(getActivity(), getString(R.string.animal_delete), getString(R.string.animal_delete_title), getString(R.string.delete), (dialog, which) -> {
                sightingEvidenceTables.remove(position);
                sightingEvidenceTableAdapter.notifyDataSetChanged();
                showHeaderMessage();
            });
            return true;
        });

        sightingEvidenceTableAdapter = new SightingEvidenceTableAdapter();
        listView.setAdapter(sightingEvidenceTableAdapter);

        //set the localized labels
        setLanguageValues(sharedPreferences.getLanguageEnumLanguage());

        return view;
    }


    private void startAddAnimalActivity(Bundle bundle, int requestCode) {
        bundle.putSerializable(getString(R.string.fragment_type_parameter), SingleFragmentActivity.FragmentType.ADD_ANIMAL);
        Intent intent = new Intent(getActivity(), SingleFragmentActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("editRequestPosition", editRequestPosition);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            editRequestPosition = savedInstanceState.getInt("editRequestPosition");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ADD_ANIMAL_REQUEST_CODE:
                    if (data != null) {
                        SightingEvidenceTable sightingEvidenceTable = Parcels.unwrap(data.getParcelableExtra(getString(R.string.add_animal_parameter)));
                        if (sightingEvidenceTable != null) {
                            sightingEvidenceTables.add(sightingEvidenceTable);
                            sightingEvidenceTableAdapter.notifyDataSetChanged();
                        }
                    }
                    break;
                case EDIT_ANIMAL_REQUEST_CODE:
                    if (data != null) {
                        SightingEvidenceTable sightingEvidenceTable = Parcels.unwrap(data.getParcelableExtra(getString(R.string.add_animal_parameter)));
                        if (sightingEvidenceTable != null) {
                            sightingEvidenceTables.remove(editRequestPosition);
                            sightingEvidenceTables.add(editRequestPosition, sightingEvidenceTable);
                            sightingEvidenceTableAdapter.notifyDataSetChanged();
                        }
                    }
                    break;
            }
            showHeaderMessage();
        }
    }

    private void showHeaderMessage() {
        if (sightingEvidenceTables.size() > 0) {
            total.setVisibility(View.GONE);
        } else {
            total.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void setLanguageValues(Language language) {
        total.setText(localisedString("no_animal_record", R.string.no_animal_record));
    }

    @Override
    public String getValidationMessage() {
        StringBuilder stringBuilder = new StringBuilder();
        if (sightingEvidenceTables != null && sightingEvidenceTables.size() == 0) {
            stringBuilder.append(localisedString("species_select", R.string.species_select));
        }
        return stringBuilder.toString().trim();
    }

    @Override
    public void prepareData() {

    }

    @Override
    public void setBilbyBlitzData() {

    }

    private class SightingEvidenceTableAdapter extends ArrayAdapter<SightingEvidenceTable> {

        SightingEvidenceTableAdapter() {
            super(getActivity(), 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            // reuse views
            if (rowView == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                rowView = inflater.inflate(R.layout.item_animal, parent, false);
                // configure view holder
                SightingEvidenceTableViewHolder viewHolder = new SightingEvidenceTableViewHolder();
                viewHolder.imageView = (ImageView) rowView.findViewById(R.id.image);
                viewHolder.name = (TextView) rowView.findViewById(R.id.name);
                viewHolder.whatSeen = (TextView) rowView.findViewById(R.id.whatSeen);
                viewHolder.recent = (TextView) rowView.findViewById(R.id.recent);
                rowView.setTag(viewHolder);
            }

            // fill data
            SightingEvidenceTableViewHolder holder = (SightingEvidenceTableViewHolder) rowView.getTag();
            final SightingEvidenceTable sightingEvidenceTable = sightingEvidenceTables.get(position);
            if (sightingEvidenceTable.mPhotoPath != null) {
                Bitmap bitmap = FileUtils.getSmallThumbnailBitmapFromFilePath(sightingEvidenceTable.mPhotoPath);
                if (bitmap != null) {
                    holder.imageView.clearColorFilter();
                    holder.imageView.setImageBitmap(bitmap);
                }else {
                    holder.imageView.setImageResource(R.drawable.ic_camera_alt_black_48dp);
                    holder.imageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.red));
                }
            } else {
                holder.imageView.setImageResource(R.drawable.ic_camera_alt_black_48dp);
                holder.imageView.setColorFilter(Color.WHITE);
            }

            if (sightingEvidenceTable.species != null)
                if (sightingEvidenceTable.species.vernacularName != null)
                    holder.name.setText(sightingEvidenceTable.species.vernacularName);
                else
                    holder.name.setText(sightingEvidenceTable.species.name);

            holder.whatSeen.setText(sightingEvidenceTable.typeOfSign);
            holder.recent.setText(sightingEvidenceTable.evidenceAgeClass);

            return rowView;
        }

        @Override
        public int getCount() {
            return sightingEvidenceTables.size();
        }

        class SightingEvidenceTableViewHolder {
            ImageView imageView;
            TextView name, whatSeen, recent;
        }
    }
}
