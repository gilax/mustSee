
package ac.huji.agapps.mustsee.mustSeeApi.jsonClasses;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import ac.huji.agapps.mustsee.BuildConfig;
import ac.huji.agapps.mustsee.mustSeeApi.MovieTrailerAPI;
import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.videosClasses.MovieVideoResults;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class DetailedMovie implements ImageableAPIElement {

    private static final String TAG = "DETAILED MOVIE TMDB API";

    @Expose
    @SerializedName("adult")
    private Boolean mAdult;
    @Expose
    @SerializedName("backdrop_path")
    private String mBackdropPath;
    @Expose
    @SerializedName("belongs_to_collection")
    private Object mBelongsToCollection;
    @Expose
    @SerializedName("budget")
    private Long mBudget;
    @Expose
    @SerializedName("genres")
    private List<Genre> mGenres;
    @Expose
    @SerializedName("homepage")
    private String mHomepage;
    @Expose
    @SerializedName("id")
    private Long mId;
    @Expose
    @SerializedName("imdb_id")
    private String mImdbId;
    @Expose
    @SerializedName("original_language")
    private String mOriginalLanguage;
    @Expose
    @SerializedName("original_title")
    private String mOriginalTitle;
    @Expose
    @SerializedName("overview")
    private String mOverview;
    @Expose
    @SerializedName("popularity")
    private Double mPopularity;
    @Expose
    @SerializedName("poster_path")
    private String mPosterPath;
    @Expose
    @SerializedName("production_companies")
    private List<ProductionCompany> mProductionCompanies;
    @Expose
    @SerializedName("production_countries")
    private List<ProductionCountry> mProductionCountries;
    @Expose
    @SerializedName("release_date")
    private String mReleaseDate;
    @Expose
    @SerializedName("revenue")
    private Long mRevenue;
    @Expose
    @SerializedName("runtime")
    private Long mRuntime;
    @Expose
    @SerializedName("spoken_languages")
    private List<SpokenLanguage> mSpokenLanguages;
    @Expose
    @SerializedName("status")
    private String mStatus;
    @Expose
    @SerializedName("tagline")
    private String mTagline;
    @Expose
    @SerializedName("title")
    private String mTitle;
    @Expose
    @SerializedName("video")
    private Boolean mVideo;
    @Expose
    @SerializedName("vote_average")
    private Double mVoteAverage;
    @Expose
    @SerializedName("vote_count")
    private Long mVoteCount;
    @SerializedName("youtube_id")
    private String mYouTubeId = "";

    public Boolean getAdult() {
        return mAdult;
    }

    public void setAdult(Boolean adult) {
        mAdult = adult;
    }

    @Override
    public String getBackdropPath() {
        return mBackdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        mBackdropPath = backdropPath;
    }

    public Object getBelongsToCollection() {
        return mBelongsToCollection;
    }

    public void setBelongsToCollection(Object belongsToCollection) {
        mBelongsToCollection = belongsToCollection;
    }

    public Long getBudget() {
        return mBudget;
    }

    public void setBudget(Long budget) {
        mBudget = budget;
    }

    public List<Genre> getGenres() {
        return mGenres;
    }

    public void setGenres(List<Genre> genres) {
        mGenres = genres;
    }

    public String getHomepage() {
        return mHomepage;
    }

    public void setHomepage(String homepage) {
        mHomepage = homepage;
    }

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }

    public String getImdbId() {
        return mImdbId;
    }

    public void setImdbId(String imdbId) {
        mImdbId = imdbId;
    }

    public String getOriginalLanguage() {
        return mOriginalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        mOriginalLanguage = originalLanguage;
    }

    public String getOriginalTitle() {
        return mOriginalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        mOriginalTitle = originalTitle;
    }

    public String getOverview() {
        return mOverview;
    }

    public void setOverview(String overview) {
        mOverview = overview;
    }

    public Double getPopularity() {
        return mPopularity;
    }

    public void setPopularity(Double popularity) {
        mPopularity = popularity;
    }

    @Override
    public String getPosterPath() {
        return mPosterPath;
    }

    public void setPosterPath(String posterPath) {
        mPosterPath = posterPath;
    }

    public List<ProductionCompany> getProductionCompanies() {
        return mProductionCompanies;
    }

    public void setProductionCompanies(List<ProductionCompany> productionCompanies) {
        mProductionCompanies = productionCompanies;
    }

    public List<ProductionCountry> getProductionCountries() {
        return mProductionCountries;
    }

    public void setProductionCountries(List<ProductionCountry> productionCountries) {
        mProductionCountries = productionCountries;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        mReleaseDate = releaseDate;
    }

    public Long getRevenue() {
        return mRevenue;
    }

    public void setRevenue(Long revenue) {
        mRevenue = revenue;
    }

    public Long getRuntime() {
        return mRuntime;
    }

    public void setRuntime(Long runtime) {
        mRuntime = runtime;
    }

    public List<SpokenLanguage> getSpokenLanguages() {
        return mSpokenLanguages;
    }

    public void setSpokenLanguages(List<SpokenLanguage> spokenLanguages) {
        mSpokenLanguages = spokenLanguages;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

    public String getTagline() {
        return mTagline;
    }

    public void setTagline(String tagline) {
        mTagline = tagline;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Boolean getVideo() {
        return mVideo;
    }

    public void setVideo(Boolean video) {
        mVideo = video;
    }

    public Double getVoteAverage() {
        return mVoteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        mVoteAverage = voteAverage;
    }

    public Long getVoteCount() {
        return mVoteCount;
    }

    public void setVoteCount(Long voteCount) {
        mVoteCount = voteCount;
    }

    public String getYouTubeId() {
        return mYouTubeId;
    }

    public void setYouTubeId(String youTubeId) {
        this.mYouTubeId = youTubeId;
    }

    @Override
    public String toString() {
        return "{\n" +
                "  \"adult\": " + getAdult() + ",\n" +
                "  \"backdrop_path\": \"" + getBackdropPath() + "\",\n" +
                "  \"belongs_to_collection\": " + getBelongsToCollection() + ",\n" +
                "  \"budget\": " + getBudget() + ",\n" +
                "  \"genres\": " + getGenres() + ",\n" +
                "  \"homepage\": \"" + getHomepage() + "\",\n" +
                "  \"id\": " + getId() + ",\n" +
                "  \"imdb_id\": \"" + getImdbId() + "\",\n" +
                "  \"original_language\": \"" + getOriginalLanguage() + "\",\n" +
                "  \"original_title\": \"" + getOriginalTitle() + "\",\n" +
                "  \"overview\": \"" + getOverview() + "\",\n" +
                "  \"popularity\": " + getPopularity() + ",\n" +
                "  \"poster_path\": \"" + getPosterPath() + "\",\n" +
                "  \"production_companies\": " + getProductionCompanies() + ",\n" +
                "  \"production_countries\": " + getProductionCountries() + ",\n" +
                "  \"release_date\": \"" + getReleaseDate() + "\",\n" +
                "  \"revenue\": " + getRevenue() + ",\n" +
                "  \"runtime\": " + getRuntime() + ",\n" +
                "  \"spoken_languages\": " + getSpokenLanguages() + ",\n" +
                "  \"status\": \"" + getStatus() + "\",\n" +
                "  \"tagline\": \"" + getTagline() + "\",\n" +
                "  \"title\": \"" + getTitle() + "\",\n" +
                "  \"video\": " + getVideo() + ",\n" +
                "  \"vote_average\": " + getVoteAverage() + ",\n" +
                "  \"vote_count\": " + getVoteCount() + ",\n" +
                "  \"youtube_id\": " + getYouTubeId() + "\n" +
                "}";
    }

    public Result reduceToResult() {
        Result reduced = new Result();
        reduced.setAdult(mAdult);
        reduced.setBackdropPath(mBackdropPath);
        List<Long> genreIds = new ArrayList<>(mGenres.size());
        for (Genre genre : mGenres) {
            genreIds.add(genre.getId());
        }
        reduced.setGenreIds(genreIds);
        reduced.setId(mId);
        reduced.setOriginalLanguage(mOriginalLanguage);
        reduced.setOriginalTitle(mOriginalTitle);
        reduced.setOverview(mOverview);
        reduced.setPopularity(mPopularity);
        reduced.setPosterPath(mPosterPath);
        reduced.setReleaseDate(mReleaseDate);
        reduced.setTitle(mTitle);
        reduced.setVideo(mVideo);
        reduced.setVoteAverage(mVoteAverage);
        reduced.setVoteCount(mVoteCount);

        return reduced;
    }

    public void attachTrailerPlayer(Fragment parentFragment, FrameLayout frameLayout, int frameLayoutResource) {
        if (mYouTubeId.length() == 0) {
            new TrailerAsyncTask(parentFragment, frameLayout, frameLayoutResource).execute(this);
        } else {
            setYouTubeFragmentToFrame(parentFragment, frameLayout, frameLayoutResource);
        }
    }

    private void setYouTubeFragmentToFrame(Fragment parentFragment, final FrameLayout frameLayout, int frameLayoutResource) {
        YouTubePlayerSupportFragment youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();

        FragmentManager fragmentManager = parentFragment.getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(frameLayoutResource, youTubePlayerFragment).commit();

        youTubePlayerFragment.initialize(BuildConfig.YOU_TUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                frameLayout.setVisibility(View.VISIBLE);
                player.setPlayerStateChangeListener(new TrailerEventListener());
                player.setPlaybackEventListener(new TrailerEventListener());
                if (!wasRestored) {
                    // get the YouTube trailer key from the first result
                    player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                    player.cueVideo(mYouTubeId);
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Log.e(TAG, "Failured to Initialize!");
            }
        });
    }

    private class TrailerEventListener implements YouTubePlayer.PlaybackEventListener, YouTubePlayer.PlayerStateChangeListener {
        @Override
        public void onPlaying() {

        }

        @Override
        public void onPaused() {

        }

        @Override
        public void onStopped() {

        }

        @Override
        public void onBuffering(boolean b) {

        }

        @Override
        public void onSeekTo(int i) {

        }

        @Override
        public void onLoading() {

        }

        @Override
        public void onLoaded(String s) {

        }

        @Override
        public void onAdStarted() {

        }

        @Override
        public void onVideoStarted() {

        }

        @Override
        public void onVideoEnded() {

        }

        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason) {

        }
    }

    private class TrailerAsyncTask extends AsyncTask<DetailedMovie, Void, MovieVideoResults> {

        private static final String YOUTUBE = "YouTube";

        private final Fragment mParentFragment;
        private final FrameLayout mFrameLayout;
        private final int mFrameLayoutResource;

        public TrailerAsyncTask(Fragment parentFragment, FrameLayout frameLayout, int frameLayoutResource) {
            super();
            this.mParentFragment = parentFragment;
            this.mFrameLayout = frameLayout;
            this.mFrameLayoutResource = frameLayoutResource;
        }

        @Override
        protected MovieVideoResults doInBackground(DetailedMovie... params) {
            MovieVideoResults results = (new MovieTrailerAPI()).getMovieVideos(params[0].getId().intValue());
            return results;
        }

        @Override
        protected void onPostExecute(final MovieVideoResults results) {
            if (results != null && results.getResults().size() > 0 && results.getResults().get(0).getSite().equals(YOUTUBE)) {
                mYouTubeId = results.getResults().get(0).getKey();
                setYouTubeFragmentToFrame(mParentFragment, mFrameLayout, mFrameLayoutResource);
            }
        }
    }
}
