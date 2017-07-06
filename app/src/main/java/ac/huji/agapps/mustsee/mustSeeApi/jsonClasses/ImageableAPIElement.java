package ac.huji.agapps.mustsee.mustSeeApi.jsonClasses;

public interface ImageableAPIElement {
    public static final String API_IMAGE_REQUEST = "https://image.tmdb.org/t/p/original";

    public String getBackdropPath();
    public String getPosterPath();
}
