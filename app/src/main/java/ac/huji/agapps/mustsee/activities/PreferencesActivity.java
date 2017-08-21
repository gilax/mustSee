package ac.huji.agapps.mustsee.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import ac.huji.agapps.mustsee.R;

public class PreferencesActivity extends AppCompatActivity implements  View.OnClickListener{

    private GoogleApiClient mGoogleApiClient;
    private SignInButton mGoogleButton; //google sign in button
    private static final int RC_SIGN_IN = 1;
    private FirebaseAuth mAuth;
    private static final String TAG = "MAIN_ACTIVITY";
    private ProgressDialog progressDialog;
    private Button mLogOutButton; // log out buton
    private TextView mStatusBar; //status of who's online
    private FirebaseUser user;
    private RadioGroup radioGroup;

    public enum SortBy {
        TITLE("original_title.desc"),
        POPULARITY("popularity.desc"),
        VOTE_AVERAGE("vote_average.desc");

        private String sortType;

        SortBy(String sortType) {
            this.sortType = sortType;
        }

        public String sortType() {
            return sortType;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.appNameInPreferencesPage).setVisibility(View.INVISIBLE);
        findViewById(R.id.statusBar).setVisibility(View.INVISIBLE);

        //FirebaseUser, contains unique id, name, photo, etc about the user.
        user = FirebaseAuth.getInstance().getCurrentUser();

        radioGroup = (RadioGroup) findViewById(R.id.radio_group);


        //if we're logged in and this is the first time running PreferencesActivity, go to Main
        //first if check if we got here from main, if not, don't show 'back' button in toolbar
        if(getIntent().getStringExtra(getString(R.string.disable_auto_transfer)) != null)
        {
            if(getSupportActionBar() != null)
            {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }
        }
        else if(user != null)
            startMain();

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
                saveSortPick();
            }
        });

        getSortPick();

        initRadio();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                saveSortPick();
            }
        });
//        checkLogOutIntent(getIntent());
    }


    private void initRadio() {
        RadioButton title_rad = (RadioButton) findViewById(R.id.title_radio);
        RadioButton pop_rad = (RadioButton) findViewById(R.id.popularity_radio);
        RadioButton vote_rad = (RadioButton) findViewById(R.id.vote_average_radio);

        title_rad.setText(SortBy.TITLE.sortType());
        pop_rad.setText(SortBy.POPULARITY.sortType());
        vote_rad.setText(SortBy.VOTE_AVERAGE.sortType());
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    /**
     * tries to retrieve user's sorting pick in shared preferences
     */
    private void getSortPick() {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.shared_pref_id),
                Context.MODE_PRIVATE);


        String type = new Gson().fromJson(sharedPref.getString(getString(R.string.userSortPick), ""), String.class);
//        SortBy type = new Gson().fromJson(sharedPref.getString(getString(R.string.userSortPick), ""), SortBy.class);

        if(type != null && type.length() != 0)
        {
            if(type.equals(SortBy.POPULARITY.sortType()))
                radioGroup.check(R.id.popularity_radio);
            else if(type.equals(SortBy.TITLE.sortType()))
                radioGroup.check(R.id.title_radio);
            else
                radioGroup.check(R.id.vote_average_radio);

        }
//        String sortPick = sharedPref.getString(getString(R.string.userSortPick), "");

    }

    private void saveSortPick() {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.shared_pref_id), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        int id = radioGroup.getCheckedRadioButtonId();
        if(id != -1)
        {
            RadioButton button = (RadioButton)findViewById(id);
            String sortType = (String) button.getText();
//            SortBy sortType = SortBy.valueOf((String) button.getText());

            Gson gson = new Gson();
            editor.putString(getString(R.string.userSortPick), gson.toJson(sortType) );
            editor.apply();
        }
    }

    private void saveUserName(String userName) {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.shared_pref_username), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.userName), userName);
        editor.apply();
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
        Toast.makeText(PreferencesActivity.this, R.string.logged_out, Toast.LENGTH_SHORT).show();
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
        progressDialog.setMessage(getString(R.string.register_wait));
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

    /**
     * Start main activity
     */
    private void startMain() {
        Intent myIntent = new Intent(PreferencesActivity.this, MainActivity.class);
        PreferencesActivity.this.startActivity(myIntent);

        //remove this activity from the activity stack, we don't wanna be able to press 'back' into it
        finish();
    }

    /**
     * update UI after logging in/disconnecting
     * @param user, the user connected, null if disconnected
     */
    private void updateUI(FirebaseUser user) {
        progressDialog.dismiss();
        if (user != null) {
            //user logged in, set log in button invisible
            mStatusBar.setText(getString(R.string.user_status_bar) + user.getDisplayName());
            mGoogleButton.setVisibility(View.GONE);
            mLogOutButton.setVisibility(View.VISIBLE);
            mLogOutButton.setText(R.string.log_out);
        } else {
            //user logged out, set log out button invisible
            mStatusBar.setText(getString(R.string.user_disconnected_status_bar));
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.pref_menu, menu);
        // Associate searchable configuration with the SearchView

        if(user!=null)
            menu.findItem(R.id.pref_title).setTitle(user.getDisplayName());

        menu.findItem(R.id.pref_title).setEnabled(false);

        return true;
    }
}
