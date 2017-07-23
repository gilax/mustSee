package ac.huji.agapps.mustsee.mustSeeApi.jsonClasses;

public interface ImageableAPIElement {
    public static final String API_IMAGE_REQUEST = "https://image.tmdb.org/t/p/w780";

    public String getBackdropPath();
    public String getPosterPath();
}
