package au.csiro.ozatlas.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import au.csiro.ozatlas.R;

/**
 * Created by sad038 on 13/4/17.
 */

public class ImageUploadAdapter extends RecyclerView.Adapter<ViewHolders> {

    private List<Uri> imagePaths;

    public ImageUploadAdapter(List<Uri> imagePaths) {
        this.imagePaths = imagePaths;
    }

    @Override
    public ViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_upload_image, null);
        return new ViewHolders(layoutView);
    }

    @Override
    public int getItemCount() {
        return imagePaths.size();
    }

    @Override
    public void onBindViewHolder(final ViewHolders holder, final int position) {
        holder.imageView.setImageBitmap(loadImage(getRealPathFromURI(holder.imageView.getContext(), imagePaths.get(position))));
        holder.crossButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePaths.remove(holder.getAdapterPosition());
                ImageUploadAdapter.this.notifyDataSetChanged();
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

class ViewHolders extends RecyclerView.ViewHolder {
    ImageView imageView;
    ImageView crossButton;

    public ViewHolders(View itemView) {
        super(itemView);
        imageView = (ImageView) itemView.findViewById(R.id.imageView);
        crossButton = (ImageView) itemView.findViewById(R.id.crossButton);
    }
}
