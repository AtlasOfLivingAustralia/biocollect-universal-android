package au.csiro.ozatlas.adapter;

import android.app.DatePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.manager.AtlasDateTimeUtils;
import au.csiro.ozatlas.model.post.SightingPhoto;

/**
 * Created by sad038 on 13/4/17.
 */

public class ImageUploadAdapter extends RecyclerView.Adapter<ImageViewHolders> {

    private List<Uri> imagePaths;
    private List<SightingPhoto> sightingPhotos;
    private static final String DATE_FORMAT = "dd MMMM, yyyy";
    private ArrayAdapter licenseAdapter;

    public ImageUploadAdapter(List<Uri> imagePaths, List<SightingPhoto> sightingPhotos, Context context) {
        this.imagePaths = imagePaths;
        this.sightingPhotos = sightingPhotos;
        this.licenseAdapter = ArrayAdapter.createFromResource(context,R.array.license_array, R.layout.item_textview);
    }

    public List<SightingPhoto> getSightingPhotos() {
        return sightingPhotos;
    }

    @Override
    public ImageViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_upload_image, null);
        return new ImageViewHolders(layoutView);
    }

    @Override
    public int getItemCount() {
        return imagePaths.size();
    }

    @Override
    public void onBindViewHolder(final ImageViewHolders holder, final int position) {
        holder.imageView.setImageBitmap(loadImage(getRealPathFromURI(holder.imageView.getContext(), imagePaths.get(position))));
        holder.crossButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePaths.remove(holder.getAdapterPosition());
                sightingPhotos.remove(holder.getAdapterPosition());
                ImageUploadAdapter.this.notifyDataSetChanged();
            }
        });
        holder.spinner.setAdapter(licenseAdapter);
        final Calendar calendar= Calendar.getInstance();
        if(sightingPhotos.get(position).dateTaken!=null) {
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

    private String getRealPathFromURI(Context context, Uri contentURI) {
        String result;
        Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
}

class ImageViewHolders extends RecyclerView.ViewHolder {
    ImageView imageView;
    ImageView crossButton;
    AppCompatSpinner spinner;
    TextView date;

    public ImageViewHolders(View itemView) {
        super(itemView);
        date = (TextView) itemView.findViewById(R.id.date);
        imageView = (ImageView) itemView.findViewById(R.id.imageView);
        crossButton = (ImageView) itemView.findViewById(R.id.crossButton);
        spinner = (AppCompatSpinner) itemView.findViewById(R.id.licenseSpinner);
    }
}

/*class LicenseAdapter extends ArrayAdapter<String>{

}*/
