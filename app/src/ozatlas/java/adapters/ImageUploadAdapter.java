package adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.listener.SimpleTextWatcher;
import au.csiro.ozatlas.model.SightingPhoto;
import io.realm.RealmList;

/**
 * Created by sad038 on 13/4/17.
 */

public class ImageUploadAdapter extends RecyclerView.Adapter<ImageViewHolders> {

    public ButtonVisibilityListener buttonVisibilityListener;
    private RealmList<SightingPhoto> sightingPhotos;
    private String[] attributionMapStrings;
    private ArrayAdapter licenseAdapter;
    private String userDisplay;

    public ImageUploadAdapter(RealmList<SightingPhoto> sightingPhotos, Context context, String displayUser) {
        this.sightingPhotos = sightingPhotos;
        this.userDisplay = displayUser;
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
                if (buttonVisibilityListener != null)
                    buttonVisibilityListener.update();
            }
        });

        holder.spinner.setAdapter(licenseAdapter);
        holder.spinner.setSelection(sightingPhotos.get(position).licensePosition, false);
        holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int p, long id) {
                sightingPhotos.get(holder.getAdapterPosition()).licence = attributionMapStrings[p];
                sightingPhotos.get(holder.getAdapterPosition()).licensePosition = p;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (sightingPhotos.get(position).attribution == null) {
            sightingPhotos.get(position).attribution = userDisplay;
        }
        holder.attributionEditText.setText(sightingPhotos.get(position).attribution);
        holder.attributionEditText.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sightingPhotos.get(holder.getAdapterPosition()).attribution = s.toString();
            }
        });
    }

    /**
     * This method makes a bitmap of the given image file into
     * a half-sized of the original one
     *
     * @param imgPath
     * @return
     */
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

    public interface ButtonVisibilityListener {
        void update();
    }
}

/**
 * View Holders
 */
class ImageViewHolders extends RecyclerView.ViewHolder {
    ImageView imageView;
    ImageView crossButton;
    AppCompatSpinner spinner;
    EditText attributionEditText;

    public ImageViewHolders(View itemView) {
        super(itemView);
        imageView = (ImageView) itemView.findViewById(R.id.imageView);
        crossButton = (ImageView) itemView.findViewById(R.id.crossButton);
        spinner = (AppCompatSpinner) itemView.findViewById(R.id.licenseSpinner);
        attributionEditText = (EditText) itemView.findViewById(R.id.attributionEditText);
    }
}