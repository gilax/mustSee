package ac.huji.agapps.mustsee.mustSeeApi;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import ac.huji.agapps.mustsee.BuildConfig;

public class TmdbApiRequest {

    protected static final String TMDB_BASE_URL = "https://api.themoviedb.org/3/movie/550";

    protected static final String API_KEY_KEY = "api_key";
    protected static final String API_KEY_VALUE = BuildConfig.TMDB_API_KEY;

    private final String TAG = "TMDB API Request";

    protected static final String POST = "POST";
    protected static final String GET = "GET";

    protected String url;

    public TmdbApiRequest() {
        this.url = TMDB_BASE_URL;
    }

    protected String getApiKeyForURL() {
        return API_KEY_KEY + "=" + API_KEY_VALUE;
    }

    protected String getResponseForHTTPGetRequest() {
        URL url = null;
        HttpsURLConnection connection = null;
        String response = "";
        try {
            url = new URL(this.url);
            connection = (HttpsURLConnection) url.openConnection();
            InputStream in = connection.getInputStream();
            InputStreamReader isw = new InputStreamReader(in);

            int data = isw.read();
            while (data != -1){
                char current = (char) data;
                data = isw.read();
                response += current;
            }
        } catch (MalformedURLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } catch (IOException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        finally {
            if (connection != null)
                connection.disconnect();
        }
        return response;
    }
}
