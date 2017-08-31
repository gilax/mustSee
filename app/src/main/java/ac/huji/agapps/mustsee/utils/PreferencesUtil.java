package ac.huji.agapps.mustsee.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ac.huji.agapps.mustsee.R;
import ac.huji.agapps.mustsee.activities.MainActivity;

public class PreferencesUtil {
    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "Preferences Util";

    public static GoogleApiClient mGoogleApiClient;

    private enum SortBy {
        TITLE("original_title.asc", "Title"),
        POPULARITY("popularity.desc", "Popularity"),
        VOTE_AVERAGE("vote_average.desc", "Rating");

        private String sortByValue;
        private String sortByText;

        SortBy(String sortByValue, String sortByText) {
            this.sortByValue = sortByValue;
            this.sortByText = sortByText;
        }

        public String getValue() {
            return sortByValue;
        }

        public String getText() {
            return sortByText;
        }
    }

    public static void initGoogleApiClient(final AppCompatActivity activity) {
        if (mGoogleApiClient == null) {
            // Configure Google Sign In
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(activity.getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            mGoogleApiClient = new GoogleApiClient.Builder(activity.getApplicationContext())
                    .enableAutoManage(activity, new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                            Toast.makeText(activity, R.string.error_msg, Toast.LENGTH_LONG).show();
                        }
                    }).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
        }
    }

    @NonNull
    public static AlertDialog createSignInDialog(final AppCompatActivity activity) {
        initGoogleApiClient(activity);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(R.string.welcome_to_mustsee);
        builder.setTitle(R.string.welcome_title);
        builder.setCancelable(false);
        builder.setNeutralButton(R.string.sign_in, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                activity.startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
        return builder.create();
    }

    public static void onActivityResult(final MainActivity activity, int requestCode, int resultCode,
                                        Intent data, final OnSignedInListener onSignedInListener) {
        GoogleSignInAccount account = null;

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN && resultCode != Activity.RESULT_CANCELED) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                account = result.getSignInAccount();
            }
        } else {
            AlertDialog signInDialog = createSignInDialog(activity);
            signInDialog.show();
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(activity.getString(R.string.register_wait));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (account != null) {
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithCredential:success");
                                FirebaseUser user = mAuth.getCurrentUser();

                                if (user == null)
                                    saveUserName(activity, null);
                                else
                                    saveUserName(activity, user.getDisplayName());

                                onSignedInListener.onSignedIn();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithCredential:failure", task.getException());
                                Toast.makeText(activity, R.string.auth_fail,
                                        Toast.LENGTH_SHORT).show();
                            }
                            progressDialog.dismiss();
                        }
                    });
        } else
            progressDialog.dismiss();
    }

    private static void saveUserName(AppCompatActivity activity, @Nullable String userName) {
        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getString(R.string.shared_pref_username),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(activity.getString(R.string.userName), userName);
        editor.apply();
    }

    @NonNull
    public static AlertDialog createSortByDialog(final AppCompatActivity activity, final OnChooseSortByListener onChooseSortByListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Choose how to sort your Top Movies:");
        List<String> sortByTexts = new ArrayList<>();
        List<String> sortByValues = new ArrayList<>();
        for (SortBy sortBy : SortBy.values()) {
            sortByTexts.add(sortBy.getText());
            sortByValues.add(sortBy.getValue());
        }
        String currentSortBy = getSortBy(activity);
        int checkedItem;
        if (currentSortBy.length() != 0)
            checkedItem = sortByValues.indexOf(currentSortBy);
        else
            checkedItem = sortByValues.indexOf(SortBy.POPULARITY.getValue());
        builder.setSingleChoiceItems(sortByTexts.toArray(new String[sortByTexts.size()]), checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SortBy chosenSortBy = Arrays.asList(SortBy.values()).get(which);
                setSortBy(activity, chosenSortBy);
                onChooseSortByListener.onChooseSortBy(chosenSortBy.getText());
                dialog.dismiss();
            }
        });

        return builder.create();
    }

    public static String getSortBy(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.shared_pref_id),
                                                                        Context.MODE_PRIVATE);

        return sharedPref.getString(context.getString(R.string.userSortPick), "");
    }

    public static void setSortBy(Context context, @Nullable SortBy sortBy) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.shared_pref_id),
                                                                        Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        String sortType;
        if (sortBy != null) {
            sortType = sortBy.getValue();
        } else {
            // Default SortBy
            sortType = SortBy.POPULARITY.getValue();
        }
        editor.putString(context.getString(R.string.userSortPick), sortType);
        editor.apply();
    }

    public interface OnSignedInListener {
        void onSignedIn();
    }

    public interface OnChooseSortByListener {
        void onChooseSortBy(String chosenSortPick);
    }
}
