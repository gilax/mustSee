package ac.huji.agapps.mustsee.mustSeeApi;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.ImageableAPIElement;

public class ImageAPI {
    public static void putPosterToView(Context context, ImageableAPIElement element, ImageView imageView) {
        loadImageToView(context, ImageableAPIElement.API_IMAGE_REQUEST + element.getPosterPath(), imageView);
    }

    public static void putBackdropToView(Context context, ImageableAPIElement element, ImageView imageView) {
        loadImageToView(context, ImageableAPIElement.API_IMAGE_REQUEST + element.getBackdropPath(), imageView);
    }

    private static void loadImageToView(Context context, String path, ImageView imageView) {
        Picasso.with(context)
                .setLoggingEnabled(true);
        Picasso.with(context)
                .load(path)
                .fit()
                .centerCrop()
                .error(android.R.drawable.ic_delete)
                .into(imageView);
    }
}
