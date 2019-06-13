package com.example.profile;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.microsoft.identity.client.*;
import com.microsoft.identity.client.exception.*;

public class AuthActivity extends AppCompatActivity {

    final static String SCOPES [] = {"https://graph.microsoft.com/User.Read"};
    final static String MSGRAPH_URL = "https://graph.microsoft.com/v1.0/me";

    /* UI & Debugging Variables */
    private static final String TAG = "AUTH_MS";

    private TextView tvAuthRefresh;
    private TextView tvAuthError;

    /* Azure AD Variables */
    private static PublicClientApplication sampleApp;
    private IAuthenticationResult authResult;

    private JSONObject studentInfo;

    public static PublicClientApplication getSampleApp() {
        return sampleApp;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        getSupportActionBar().setTitle("Авторизация");

        /* Configure your sample app and save state for this activity */
        sampleApp = new PublicClientApplication(this.getApplicationContext(), R.raw.auth_config);

        /* Attempt to get a user and acquireTokenSilent */
        sampleApp.getAccounts(new PublicClientApplication.AccountsLoadedCallback() {
            @Override
            public void onAccountsLoaded(final List<IAccount> accounts) {
                if (!accounts.isEmpty()) {
                    /* This sample doesn't support multi-account scenarios, use the first account */
                    sampleApp.acquireTokenSilentAsync(SCOPES, accounts.get(0), getAuthSilentCallback());
                } else {
                    /* No accounts */
                    sampleApp.acquireToken(getActivity(), SCOPES, getAuthInteractiveCallback());
                }
            }
        });

        tvAuthRefresh = findViewById(R.id.tvAuthRefresh);
        tvAuthError = findViewById(R.id.tvAuthError);
    }

    /* Set the UI for successful token acquisition data */
    private void updateSuccessUI() {
         Intent intent = new Intent(this, MainActivity.class);
         intent.putExtra("studentInfoStr", studentInfo.toString());
         startActivity(intent);
    }

    public Activity getActivity() {
        return this;
    }

    /* Callback used in for silent acquireToken calls.
     * Looks if tokens are in the cache (refreshes if necessary and if we don't forceRefresh)
     * else errors that we need to do an interactive request.
     */
    private AuthenticationCallback getAuthSilentCallback() {
        return new AuthenticationCallback() {

            @Override
            public void onSuccess(IAuthenticationResult authenticationResult) {
                /* Successfully got a token, call graph now */
                Log.d(TAG, "Successfully authenticated");

                /* Store the authResult */
                authResult = authenticationResult;

                /* call graph */
                callGraphAPI();
            }

            @Override
            public void onError(MsalException exception) {
                /* Failed to acquireToken */
                Log.d(TAG, "Authentication failed: " + exception.toString());

                if (exception instanceof MsalClientException) {
                    /* Exception inside MSAL, more info inside the exception */
                    Log.d(TAG, "1");
                    tvAuthRefresh.setVisibility(TextView.VISIBLE);
                    tvAuthError.setVisibility(TextView.VISIBLE);
                    new CountDownTimer(10000, 1000) {

                        @Override
                        public void onTick(long millisUntilFinished) {
                            tvAuthRefresh.setText("Попытка переподключения через " + millisUntilFinished / 1000 + "...");
                        }

                        @Override
                        public void onFinish() {
                            sampleApp.acquireToken(getActivity(), SCOPES, getAuthInteractiveCallback());
                            tvAuthRefresh.setVisibility(TextView.INVISIBLE);
                            tvAuthError.setVisibility(TextView.INVISIBLE);
                        }
                    }.start();
                } else if (exception instanceof MsalServiceException) {
                    /* Exception when communicating with the STS, likely config issue */
                    Log.d(TAG, "2");
                } else if (exception instanceof MsalUiRequiredException) {
                    /* Tokens expired or no session, retry with interactive */
                    Log.d(TAG, "3");
                }
            }

            @Override
            public void onCancel() {
                /* User cancelled the authentication */
                Log.d(TAG, "User cancelled login.");
            }
        };
    }

    /* Callback used for interactive request.  If succeeds we use the access
     * token to call the Microsoft Graph. Does not check cache
     */
    private AuthenticationCallback getAuthInteractiveCallback() {
        return new AuthenticationCallback() {

            @Override
            public void onSuccess(IAuthenticationResult authenticationResult) {
                /* Successfully got a token, call graph now */
                Log.d(TAG, "Successfully authenticated");
                Log.d(TAG, "ID Token: " + authenticationResult.getIdToken());

                /* Store the auth result */
                authResult = authenticationResult;

                /* call graph */
                callGraphAPI();
            }

            @Override
            public void onError(MsalException exception) {
                /* Failed to acquireToken */
                Log.d(TAG, "Authentication failed: " + exception.toString());

                if (exception instanceof MsalClientException) {
                    /* Exception inside MSAL, more info inside the exception */
                    Log.d(TAG, "4");
                    tvAuthRefresh.setVisibility(TextView.VISIBLE);
                    tvAuthError.setVisibility(TextView.VISIBLE);
                    new CountDownTimer(10000, 1000) {

                        @Override
                        public void onTick(long millisUntilFinished) {
                            tvAuthRefresh.setText("Попытка переподключения через " + millisUntilFinished / 1000 + "...");
                        }

                        @Override
                        public void onFinish() {
                            sampleApp.acquireToken(getActivity(), SCOPES, getAuthInteractiveCallback());
                            tvAuthRefresh.setVisibility(TextView.INVISIBLE);
                            tvAuthError.setVisibility(TextView.INVISIBLE);
                        }
                    }.start();
                } else if (exception instanceof MsalServiceException) {
                    /* Exception when communicating with the STS, likely config issue */
                    Log.d(TAG, "5");
                }
            }

            @Override
            public void onCancel() {
                /* User cancelled the authentication */
                Log.d(TAG, "User cancelled login.");
                sampleApp.acquireToken(getActivity(), SCOPES, getAuthInteractiveCallback());
            }
        };
    }

    /* Use Volley to make an HTTP request to the /me endpoint from MS Graph using an access token */
    private void callGraphAPI() {
        Log.d(TAG, "Starting volley request to graph");

        /* Make sure we have a token to send to graph */
        if (authResult.getAccessToken() == null) {return;}

        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject parameters = new JSONObject();

        try {
            parameters.put("key", "value");
        } catch (Exception e) {
            Log.d(TAG, "Failed to put parameters: " + e.toString());
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, MSGRAPH_URL,
                parameters,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                /* Successfully called graph, process data and send to UI */
                Log.d(TAG, "Response: " + response.toString());
                studentInfo = response;

                /* update the UI to post call graph state */
                updateSuccessUI();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error: " + error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + authResult.getAccessToken());
                return headers;
            }
        };

        Log.d(TAG, "Adding HTTP GET to Queue, Request: " + request.toString());

        request.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }
}
