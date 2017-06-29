package ac.huji.agapps.mustsee;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ac.huji.agapps.mustsee.mustSeeApi.MovieDetailsAPI;
import ac.huji.agapps.mustsee.mustSeeApi.MovieGenresAPI;
import ac.huji.agapps.mustsee.mustSeeApi.MovieSearchAPI;
import ac.huji.agapps.mustsee.mustSeeApi.TopMoviesAPI;
import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.DetailedMovie;
import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.Genre;
import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.Genres;
import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.MovieSearchResults;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("ac.huji.agapps.mustsee", appContext.getPackageName());
    }

    @Test
    public void SearchMovies_isCorrect() throws Exception {
        MovieSearchAPI search = new MovieSearchAPI("hello");
        int lastPage = 0;
        while (search.haveNext()) {
            MovieSearchResults results = search.searchNext();
            assertNotNull(results);
            Log.d("SEARCH RESULTS:", results.toString());
            assertEquals(++lastPage, results.getPage().intValue());
        }
    }

    @Test
    public void TopMovies_isCorrect() throws Exception {
        TopMoviesAPI top = new TopMoviesAPI();
        int lastPage = 0;
        while (top.haveNext()) {
            MovieSearchResults results = top.searchNext();
            assertNotNull(results);
            Log.d("SEARCH RESULTS:", results.toString());
            assertEquals(++lastPage, results.getPage().intValue());
        }
    }

    @Test
    public void Genres_isCorrect() throws Exception {
        MovieGenresAPI genresAPI = new MovieGenresAPI();
        Genres genres = genresAPI.getGenres();
        assertNotNull(genres);
        Log.d("GENRES:", genres.toString());
        Long genresExpected[] = {28L, 12L, 16L, 35L, 80L, 99L, 18L, 10751L, 14L, 36L, 27L,
                10402L, 9648L, 10749L, 878L, 10770L, 53L, 10752L, 37L};
        List<Long> expected = Arrays.asList(genresExpected);
        List<Long> actual = new ArrayList<>();
        for (Genre g: genres.getGenres()) {
            assertNotNull(g.getId());
            assertNotNull(g.getName());
            assertTrue("genres list doesn't contains " + g.toString(), expected.contains(g.getId()));
            actual.add(g.getId());
        }
        assertArrayEquals(genresExpected, actual.toArray());
    }

    @Test
    public void MovieDetails_isCorrect() throws Exception {
        MovieDetailsAPI detailsAPI = new MovieDetailsAPI();
        DetailedMovie movie = detailsAPI.getMovieDetails(18400);
        assertNotNull(movie);
        Log.d("MOVIE DETAILS:", movie.toString());
        assertNotNull(movie.getGenres());
        Long genresExpected[] = {18L, 35L, 10749L, 10769L};
        for (Genre g: movie.getGenres()) {
            assertTrue("Genre (" + g.getName() + ", " + g.getId() + ") wasn't in genres list",
                    Arrays.asList(genresExpected).contains(g.getId()));
        }
    }
}
