package ac.huji.agapps.mustsee;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ac.huji.agapps.mustsee.mustSeeApi.MovieGenresAPI;
import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.Genre;
import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.Genres;

public class PreferencesActivity extends AppCompatActivity implements  View.OnClickListener{

    private GoogleApiClient mGoogleApiClient;
    private SignInButton mGoogleButton; //google sign in button
    private static final int RC_SIGN_IN = 1;
    private FirebaseAuth mAuth;
    private static final String TAG = "MAIN_ACTIVITY";
    private ProgressDialog progressDialog;
    private Button mLogOutButton; // log out buton
    private TextView mStatusBar; //status of who's online


    //api genres:
    private ListView listView;
    private MovieGenresAPI genreAPI;
    private Genres genres;
    private GenresAdapter dataAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
//        todo :need to take from DB instead from API
//        todo: uncomment this, it didn't work for me for some reason so I manually put a list of genres later on
//        genreAPI = new MovieGenresAPI();
//        genres = genreAPI.getGenres();

        progressDialog = new ProgressDialog(this);
        mStatusBar = (TextView) findViewById(R.id.statusBar);
        mLogOutButton = (Button) findViewById(R.id.logOut);

        mLogOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        mAuth = FirebaseAuth.getInstance();



        mGoogleButton = (SignInButton) findViewById(R.id.google_button);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(PreferencesActivity.this, R.string.error_msg, Toast.LENGTH_LONG).show();
                    }

                }).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

        mGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
                //save genres in sharedPrefences
                saveGenres();
//
            }
        });

//      for testing purposes, reset sharedPreferences in comment below
//        resetFavoriteGenres();
        getFavGenres();
        displayListView();
//        checkLogOutIntent(getIntent());

    }


    /**
     * used to reset sharedPreferences for debugging
     */
    private void resetFavoriteGenres() {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.shared_pref_id),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPref.edit();
        edit.clear();

        edit.apply();
    }

    /**
     * tries to retrieve favorite genres in shared preferences
     */
    private void getFavGenres() {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.shared_pref_id),
                Context.MODE_PRIVATE);
        genres = new Gson().fromJson(sharedPref.getString(getString(R.string.userGenres), ""), Genres.class);

    }

    private void saveGenres() {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.shared_pref_id), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        Gson gson = new Gson();
        genres.setGenres(dataAdapter.getAllItems());
        String fav_genres_string = gson.toJson(genres);

        //stores the fav genres with a syntax of <genre.id>.<genre.name>,
        //for example: 123.horror,124.action, and so on.
        editor.putString(getString(R.string.userGenres), fav_genres_string);

        editor.apply();
    }

    private void saveUserName(String userName) {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.shared_pref_username), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.userName), userName);
        editor.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        updateUI(null);
                    }
                });
        Toast.makeText(PreferencesActivity.this, "logged out", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                updateUI(null);

                // Google Sign In failed, update UI appropriately
                // ...
            }
        } else {
            Toast.makeText(PreferencesActivity.this, R.string.error_msg, Toast.LENGTH_LONG).show();
        }

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();


                            saveUserName(user.getDisplayName());
                            startMain();

//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(PreferencesActivity.this, R.string.auth_fail,
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                        // ...
                    }
                });
    }

    private void startMain() {
        Intent myIntent = new Intent(PreferencesActivity.this, MainActivity.class);
        PreferencesActivity.this.startActivity(myIntent);
    }

    /**
     * update UI after logging in/disconnecting
     * @param user, the user connected, null if disconnected
     */
    private void updateUI(FirebaseUser user) {
        progressDialog.dismiss();
        if (user != null) {
            //user logged in, set log in button invisible
            mStatusBar.setText("user: " + user.getDisplayName());
            mGoogleButton.setVisibility(View.GONE);
            mLogOutButton.setVisibility(View.VISIBLE);
            mLogOutButton.setText(R.string.log_out);
        } else {
            //user logged out, set log out button invisible
            mStatusBar.setText("user: Disconnected");
            mGoogleButton.setVisibility(View.VISIBLE);
            mLogOutButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.google_button) {
            signIn();
        } else if (i == R.id.logOut) {
            signOut();
            //possibly need a case of "disconnect" as seen by google example, not sure what's
            //the difference as in both ifs they did the same thing
        }
    }



    /**
     * displays the listview of favorite genres
     */
    private void displayListView() {

        //create an ArrayAdaptar from the String Array
        if (genres == null) {

// todo: remove this later, was for testing (creating custom list of genres), also remove constructor in Genre
            List<Genre> my_list = new ArrayList<>();
            my_list.add(new Genre(123, "action"));
            my_list.add(new Genre(124, "comedy"));
            my_list.add(new Genre(125, "horror"));
            my_list.add(new Genre(126, "animated"));
            my_list.add(new Genre(127, "romance"));
            genres = new Genres();
            genres.setGenres(my_list);
            genres.setDate(new Date());
        }

        dataAdapter = new GenresAdapter(this, R.layout.genre_check_box, genres);
        listView = (ListView) findViewById(R.id.listView1);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//                // When clicked, show a toast with the TextView text
//                Genre genre = (Genre) parent.getItemAtPosition(position);
//                Toast.makeText(getApplicationContext(),
//                        "Clicked on Row: " + genre.getName(),
//                        Toast.LENGTH_LONG).show();
//            }
//        });
    }
}
