
package ac.huji.agapps.mustsee.mustSeeApi.jsonClasses;

import java.util.Date;
import java.util.List;
import javax.annotation.Generated;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Genres {
    @Expose
    @SerializedName("genres")
    private List<Genre> mGenres;

    @SerializedName("date")
    private Date mDate;

    public List<Genre> getGenres() {
        return mGenres;
    }

    public void setGenres(List<Genre> genres) {
        mGenres = genres;
    }

    public void setDate(Date date) {
        this.mDate = date;
    }

    public Date getDate() {
        return mDate;
    }

    @Override
    public String toString() {
        return "{\n" +
                "  \"genres\": " + getGenres().toString() + "\n" +
                "}";
    }
}
