package fragments.animal;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import activity.SingleFragmentActivity;
import au.csiro.ozatlas.R;
import base.BaseMainActivityFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import fragments.ValidationCheck;

import static android.app.Activity.RESULT_OK;

/**
 * Created by sad038 on 9/10/17.
 */

public class AnimalFragment extends BaseMainActivityFragment implements ValidationCheck {
    private final int ADD_ANIMAL_REQUEST_CODE = 1;

    @BindView(R.id.listView)
    ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_animal, container, false);
        //setTitle(getString(R.string.setting));
        ButterKnife.bind(this, view);
        setFloatingButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(getString(R.string.fragment_type_parameter), SingleFragmentActivity.FragmentType.ADD_ANIMAL);
                Intent intent = new Intent(getActivity(), SingleFragmentActivity.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, ADD_ANIMAL_REQUEST_CODE);
            }
        });

        //set the localized labels
        setLanguageValues();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ADD_ANIMAL_REQUEST_CODE:
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
}
