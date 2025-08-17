package com.ninegroup.weather.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.ninegroup.weather.R;
import com.ninegroup.weather.api.client.TokenClient;
import com.ninegroup.weather.databinding.FragmentLoginBinding;
import com.ninegroup.weather.network.WebViewClient;

public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;
    private Handler handler;
    private Runnable runnable;
    private Runnable timedOut;
    private String username;
    private String password;
    private com.ninegroup.weather.network.WebViewClient webViewClient;

    private void initVariables() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                Log.d("Login Runnable","Login Runnable is running");
                //Boolean remember = WelcomeActivity.dataStoreHelper.getBooleanValue("remember_login");

                if (!TokenClient.isTokenRunning) {
                    if (TokenClient.accessToken != null && TokenClient.isSuccess) {
                        Log.d("WebView","is loading (else): " + WebViewClient.isRunning);

                        WelcomeActivity.dataStoreHelper.putStringValue("access_token", TokenClient.accessToken);

                        Intent i = new Intent(getContext(), MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getActivity().finish();
                        startActivity(i);
                        Toast.makeText(getContext(), "Login successful",
                                Toast.LENGTH_SHORT).show();

                        Log.d("Login Runnable","Login Runnable is stopped");
                        handler.removeCallbacks(timedOut);
                        handler.removeCallbacks(runnable);
                    }
                    else {
                        binding.signInButton.setText(R.string.sign_in);
                        binding.signInButton.setEnabled(true);
                        binding.progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getContext(), "Login failed! Account does not exist",
                                Toast.LENGTH_SHORT).show();
                        handler.removeCallbacks(timedOut);
                        handler.removeCallbacks(runnable);
                    }
                }
                else {
                    Log.d("Login Runnable","Login Runnable is running again");
                    handler.postDelayed(runnable, 1000);
                }
            }
        };

        timedOut = new Runnable() {
            @Override
            public void run() {
                handler.removeCallbacks(runnable);
            }
        };
    }

    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initVariables();
        ((WelcomeActivity) getActivity()).checkConnection();

        binding.rememberCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    WelcomeActivity.dataStoreHelper.putStringValue("remember_login", "1");
                else
                    WelcomeActivity.dataStoreHelper.putStringValue("remember_login", "0");
                String isRemember = WelcomeActivity.dataStoreHelper.getStringValue("remember_login");
                Log.i("Remember Login", isRemember);
            }
        });

        binding.signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((WelcomeActivity) getActivity()).checkConnection();
                if (WelcomeActivity.isConnected) {
                    username = binding.usernameEditText.getText().toString();
                    password = binding.passwordEditText.getText().toString();

                    binding.signInButton.setText("");
                    binding.signInButton.setEnabled(false);
                    binding.progressBar.setVisibility(View.VISIBLE);

                    WebStorage.getInstance().deleteAllData();
                    CookieManager.getInstance().removeAllCookies(null);
                    CookieManager.getInstance().flush();

                    WebView webView = binding.webView;
                    webViewClient = new WebViewClient(username, null, password, null, 2);
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.setWebViewClient(webViewClient);
                    webView.loadUrl("https://uiot.ixxc.dev/");

                    TokenClient tokenClient = new TokenClient();
                    tokenClient.getToken(username, password);

                    handler.postDelayed(runnable, 1000);
                    handler.postDelayed(timedOut, 30000);
                }
                else
                    Toast.makeText(getContext(), "No network connection available! Please connect to a network with Internet access!",
                            Toast.LENGTH_SHORT).show();
            }
        });

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).popBackStack();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
