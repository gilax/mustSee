
package ac.huji.agapps.mustsee.mustSeeApi.jsonClasses;

import javax.annotation.Generated;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Genre {

    //todo remove constructor later:
    public Genre(long id, String name)
    {
        this.mId = id;
        this.mName = name;
    }

    @Expose
    @SerializedName("id")
    private Long mId;
    @Expose
    @SerializedName("name")
    private String mName;
    @SerializedName("preferred")
    private Boolean mPreferred = false;

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Boolean getPreferred() {
        return mPreferred;
    }

    public void setPreferred(Boolean preferred) {
        this.mPreferred = preferred;
    }

    @Override
    public String toString() {
        return " {\n" +
                "  \"id\": " + getId() + ",\n" +
                "  \"name\": \"" + getName() + "\",\n" +
                "  \"preferred\": " + getPreferred() + "\n" +
                "}";
    }
}
