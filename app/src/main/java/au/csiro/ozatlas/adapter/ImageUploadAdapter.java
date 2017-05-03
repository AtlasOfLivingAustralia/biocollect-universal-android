package au.csiro.ozatlas.adapter;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.listener.SimpleTextChangeListener;
import au.csiro.ozatlas.manager.AtlasDateTimeUtils;
import au.csiro.ozatlas.model.SightingPhoto;
import io.realm.RealmList;

/**
 * Created by sad038 on 13/4/17.
 */

public class ImageUploadAdapter extends RecyclerView.Adapter<ImageViewHolders> {

    private RealmList<SightingPhoto> sightingPhotos;
    private String[] attributionMapStrings;
    private static final String DATE_FORMAT = "dd MMMM, yyyy";
    private ArrayAdapter licenseAdapter;

    public ImageUploadAdapter(RealmList<SightingPhoto> sightingPhotos, Context context) {
        this.sightingPhotos = sightingPhotos;
        this.licenseAdapter = ArrayAdapter.createFromResource(context, R.array.license_array, R.layout.item_textview);
        this.attributionMapStrings = context.getResources().getStringArray(R.array.license_map_array);
    }

    public RealmList<SightingPhoto> getSightingPhotos() {
        return sightingPhotos;
    }

    @Override
    public ImageViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_upload_image, null);
        return new ImageViewHolders(layoutView);
    }

    @Override
    public int getItemCount() {
        return sightingPhotos.size();
    }

    @Override
    public void onBindViewHolder(final ImageViewHolders holder, final int position) {
        holder.imageView.setImageBitmap(loadImage(sightingPhotos.get(position).filePath));
        holder.crossButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sightingPhotos.remove(holder.getAdapterPosition());
                ImageUploadAdapter.this.notifyDataSetChanged();
            }
        });

        holder.spinner.setAdapter(licenseAdapter);
        holder.spinner.setSelection(sightingPhotos.get(position).licensePosition, false);
        holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int p, long id) {
                sightingPhotos.get(position).licence = attributionMapStrings[p];
                sightingPhotos.get(position).licensePosition = p;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final Calendar calendar = Calendar.getInstance();
        if (sightingPhotos.get(position).dateTaken != null) {
            calendar.setTime(AtlasDateTimeUtils.getDateFromString(sightingPhotos.get(position).dateTaken));
        }
        holder.date.setText(AtlasDateTimeUtils.getStringFromDate(calendar.getTime(), DATE_FORMAT));
        holder.date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                (new DatePickerDialog(holder.date.getContext(), R.style.DateTimeDialogTheme, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        sightingPhotos.get(position).dateTaken = AtlasDateTimeUtils.getStringFromDate(calendar.getTime());
                        holder.date.setText(AtlasDateTimeUtils.getStringFromDate(calendar.getTime(), DATE_FORMAT));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))).show();
            }
        });

        if (sightingPhotos.get(position).attribution == null) {
            sightingPhotos.get(position).attribution = "";
        }
        holder.attributionEditText.setText(sightingPhotos.get(position).attribution);
        holder.attributionEditText.addTextChangedListener(new SimpleTextChangeListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sightingPhotos.get(position).attribution = s.toString();
            }
        });

        if (sightingPhotos.get(position).notes == null) {
            sightingPhotos.get(position).notes = "";
        }
        holder.noteEditText.setText(sightingPhotos.get(position).notes);
        holder.noteEditText.addTextChangedListener(new SimpleTextChangeListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sightingPhotos.get(position).notes = s.toString();
            }
        });

        if (sightingPhotos.get(position).name == null) {
            sightingPhotos.get(position).name = "";
        }
        holder.titleEditText.setText(sightingPhotos.get(position).name);
        holder.titleEditText.addTextChangedListener(new SimpleTextChangeListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sightingPhotos.get(position).name = s.toString();
            }
        });
    }

    private Bitmap loadImage(String imgPath) {
        BitmapFactory.Options options;
        try {
            options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            return BitmapFactory.decodeFile(imgPath, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

class ImageViewHolders extends RecyclerView.ViewHolder {
    ImageView imageView;
    ImageView crossButton;
    AppCompatSpinner spinner;
    TextView date;
    EditText noteEditText, titleEditText, attributionEditText;

    public ImageViewHolders(View itemView) {
        super(itemView);
        date = (TextView) itemView.findViewById(R.id.date);
        imageView = (ImageView) itemView.findViewById(R.id.imageView);
        crossButton = (ImageView) itemView.findViewById(R.id.crossButton);
        spinner = (AppCompatSpinner) itemView.findViewById(R.id.licenseSpinner);
        attributionEditText = (EditText) itemView.findViewById(R.id.attributionEditText);
        titleEditText = (EditText) itemView.findViewById(R.id.titleEditText);
        noteEditText = (EditText) itemView.findViewById(R.id.noteEditText);
    }
}