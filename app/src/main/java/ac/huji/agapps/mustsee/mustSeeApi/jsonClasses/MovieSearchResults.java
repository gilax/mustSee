
package ac.huji.agapps.mustsee.mustSeeApi.jsonClasses;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
public class MovieSearchResults {

    @SerializedName("page")
    private Long mPage;
    @SerializedName("results")
    private List<Result> mResults = new ArrayList<>();
    @SerializedName("total_pages")
    private Long mTotalPages;
    @SerializedName("total_results")
    private Long mTotalResults = 1l;

    public Long getPage() {
        return mPage;
    }

    public void setPage(Long page) {
        mPage = page;
    }

    public List<Result> getResults() {
        return mResults;
    }

    public void setResults(List<Result> results) {
        mResults = results;
    }

    public Long getTotalPages() {
        return mTotalPages;
    }

    public void setTotalPages(Long totalPages) {
        mTotalPages = totalPages;
    }

    public Long getTotalResults() {
        return mTotalResults;
    }

    public void setTotalResults(Long totalResults) {
        mTotalResults = totalResults;
    }

    @Override
    public String toString() {
        return "{\n" +
                "  \"page\": " + getPage() + ",\n" +
                "  \"total_results\": " + getTotalResults() + ",\n" +
                "  \"total_pages\": " + getTotalPages() + ",\n" +
                "  \"results\": " + getResults().toString() + "\n" +
                "}";
    }

    public boolean addResults(MovieSearchResults searchResults) {
        if (searchResults != null &&
                searchResults != this &&
                searchResults.getTotalPages().equals(getTotalPages()) &&
                searchResults.getTotalResults().equals(getTotalResults())) {
            mPage = searchResults.getPage();
            return mResults.addAll(searchResults.getResults());
        } else if (mResults.size() == 0) {
            this.mResults = searchResults.getResults();
            this.mPage = searchResults.getPage();
            this.mTotalPages = searchResults.getTotalPages();
            this.mTotalResults = searchResults.getTotalResults();
            return true;
        } else {
            return false;
        }
    }

    public boolean addNullToResults() {
        if (mResults.size() == 0 ||(mResults.size() > 0 && mResults.get(mResults.size() - 1) != null))
            return mResults.add(null);
        else
            return false;
    }

    public Result removeLastFromResults() {
        return mResults.remove(mResults.size() - 1);
    }

    public Result getLastResult() {
        return mResults.get(mResults.size() - 1);
    }

    public boolean isNull(int position) {
        try {
            return (mResults.get(position) == null);
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }
}
