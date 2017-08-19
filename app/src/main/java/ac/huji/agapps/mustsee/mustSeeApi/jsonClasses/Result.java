package ac.huji.agapps.mustsee.mustSeeApi.jsonClasses;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Result implements ImageableAPIElement, Serializable, Parcelable {

    public Result() { }

    @SerializedName("adult")
    private Boolean mAdult;
    @SerializedName("backdrop_path")
    private String mBackdropPath;
    @SerializedName("genre_ids")
    private List<Long> mGenreIds;
    @SerializedName("id")
    private Long mId;
    @SerializedName("original_language")
    private String mOriginalLanguage;
    @SerializedName("original_title")
    private String mOriginalTitle;
    @SerializedName("overview")
    private String mOverview;
    @SerializedName("popularity")
    private Double mPopularity;
    @SerializedName("poster_path")
    private String mPosterPath;
    @SerializedName("release_date")
    private String mReleaseDate;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("video")
    private Boolean mVideo;
    @SerializedName("vote_average")
    private Double mVoteAverage;
    @SerializedName("vote_count")
    private Long mVoteCount;

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

    public List<Long> getGenreIds() {
        return mGenreIds;
    }

    public void setGenreIds(List<Long> genreIds) {
        mGenreIds = genreIds;
    }

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
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

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        mReleaseDate = releaseDate;
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

    @Override
    public String toString() {
        return "{\n" +
                "  \"vote_count\": " + getVoteCount() + ",\n" +
                "  \"id\": " + getId() + ",\n" +
                "  \"video\": " + getVideo() + ",\n" +
                "  \"vote_average\": " + getVoteAverage() + ",\n" +
                "  \"title\": \"" + getTitle() + "\",\n" +
                "  \"popularity\": " + getPopularity() + ",\n" +
                "  \"poster_path\": \"" + getPosterPath() + "\",\n" +
                "  \"original_language\": \"" + getOriginalLanguage() + "\",\n" +
                "  \"original_title\": \"" + getOriginalTitle() + "\",\n" +
                "  \"genre_ids\": " + getGenreIds().toString() + ",\n" +
                "  \"backdrop_path\": \"" + getBackdropPath() + "\",\n" +
                "  \"adult\": " + getAdult() + ",\n" +
                "  \"overview\": \"" + getOverview() + "\",\n" +
                "  \"release_date\": \"" + getReleaseDate() + "\"\n" +
                "}";
    }

    protected Result(Parcel in) {
        byte mAdultVal = in.readByte();
        mAdult = mAdultVal == 0x02 ? null : mAdultVal != 0x00;
        mBackdropPath = in.readString();
        if (in.readByte() == 0x01) {
            mGenreIds = new ArrayList<Long>();
            in.readList(mGenreIds, Long.class.getClassLoader());
        } else {
            mGenreIds = null;
        }
        mId = in.readByte() == 0x00 ? null : in.readLong();
        mOriginalLanguage = in.readString();
        mOriginalTitle = in.readString();
        mOverview = in.readString();
        mPopularity = in.readByte() == 0x00 ? null : in.readDouble();
        mPosterPath = in.readString();
        mReleaseDate = in.readString();
        mTitle = in.readString();
        byte mVideoVal = in.readByte();
        mVideo = mVideoVal == 0x02 ? null : mVideoVal != 0x00;
        mVoteAverage = in.readByte() == 0x00 ? null : in.readDouble();
        mVoteCount = in.readByte() == 0x00 ? null : in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (mAdult == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (mAdult ? 0x01 : 0x00));
        }
        dest.writeString(mBackdropPath);
        if (mGenreIds == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mGenreIds);
        }
        if (mId == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(mId);
        }
        dest.writeString(mOriginalLanguage);
        dest.writeString(mOriginalTitle);
        dest.writeString(mOverview);
        if (mPopularity == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeDouble(mPopularity);
        }
        dest.writeString(mPosterPath);
        dest.writeString(mReleaseDate);
        dest.writeString(mTitle);
        if (mVideo == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (mVideo ? 0x01 : 0x00));
        }
        if (mVoteAverage == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeDouble(mVoteAverage);
        }
        if (mVoteCount == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(mVoteCount);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Result> CREATOR = new Parcelable.Creator<Result>() {
        @Override
        public Result createFromParcel(Parcel in) {
            return new Result(in);
        }

        @Override
        public Result[] newArray(int size) {
            return new Result[size];
        }
    };
}