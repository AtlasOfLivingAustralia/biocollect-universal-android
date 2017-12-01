package fragments.animal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import activity.SingleFragmentActivity;
import au.csiro.ozatlas.R;
import au.csiro.ozatlas.manager.AtlasDialogManager;
import au.csiro.ozatlas.manager.FileUtils;
import base.BaseMainActivityFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import fragments.AddTrackFragment;
import fragments.ValidationCheck;
import model.track.BilbyBlitzData;
import model.track.SightingEvidenceTable;

import static android.app.Activity.RESULT_OK;

/**
 * Created by sad038 on 9/10/17.
 */

public class AnimalFragment extends BaseMainActivityFragment implements ValidationCheck {
    private final int ADD_ANIMAL_REQUEST_CODE = 1;
    private final int EDIT_ANIMAL_REQUEST_CODE = 2;

    private List<SightingEvidenceTable> sightingEvidenceTables = new ArrayList<>();
    private SightingEvidenceTableAdapter sightingEvidenceTableAdapter;
    private int editRequestPosition = -1;
    private BilbyBlitzData bilbyBlitzData;

    @BindView(R.id.listView)
    ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_animal, container, false);
        //setTitle(getString(R.string.setting));
        ButterKnife.bind(this, view);

        if(getParentFragment() instanceof AddTrackFragment){
            bilbyBlitzData = ((AddTrackFragment)getParentFragment()).getBilbyBlitzData();
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
            });
            return false;
        });

        sightingEvidenceTableAdapter = new SightingEvidenceTableAdapter();
        listView.setAdapter(sightingEvidenceTableAdapter);

        //set the localized labels
        setLanguageValues();

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
        //outState.putString("TrackMapFragmentGPSButton", startGPSButton.getText().toString());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            //String string = savedInstanceState.getString("TrackMapFragmentGPSButton");
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
        }
    }

    @Override
    protected void setLanguageValues() {

    }

    @Override
    public String getValidationMessage() {
        return null;
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
                holder.imageView.setImageBitmap(FileUtils.getBitmapFromFilePath(sightingEvidenceTable.mPhotoPath));
            }
            holder.name.setText(sightingEvidenceTable.species == null ? "" : sightingEvidenceTable.species.name);
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
