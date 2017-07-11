
package ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.videosClasses;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class MovieVideoResults {

    @SerializedName("id")
    private Long mId;
    @SerializedName("results")
    private List<Result> mResults;

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }

    public List<Result> getResults() {
        return mResults;
    }

    public void setResults(List<Result> results) {
        mResults = results;
    }

    @Override
    public String toString() {
        return "{\n" +
                "  \"id\": " + getId() + ",\n" +
                "  \"results\": " + getResults() + "\n" +
                "}";
    }
}
