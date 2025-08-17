package com.ninegroup.weather.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.ninegroup.weather.R;
import com.ninegroup.weather.api.client.TokenClient;
import com.ninegroup.weather.databinding.FragmentResetpwdBinding;
import com.ninegroup.weather.network.WebViewClient;

public class ResetPasswordFragment extends Fragment {
    private FragmentResetpwdBinding binding;
    private com.ninegroup.weather.network.WebViewClient webViewClient;
    private Handler handler;
    private Runnable runnable;
    private Runnable tokenGetter;
    private Runnable timedOut;
    private View fragmentView;
    private String username;
    private String old_password;
    private String new_password;
    private String confirm_password;

    private void initVariables() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                Log.d("Reset Password Runnable","Reset Password Runnable is running");
                Log.d("WebView","WebView is loading: " + WebViewClient.isRunning);

                if (!WebViewClient.isRunning) {
                    TokenClient tokenClient = new TokenClient();
                    tokenClient.getToken(username, new_password);

                    Log.d("Reset Password Runnable","Reset Password Runnable is stopped");
                    handler.postDelayed(tokenGetter, 1000);
                    handler.removeCallbacks(runnable);
                }
                else {
                    Log.d("Reset Password Runnable","Reset Password Runnable is running again");
                    handler.postDelayed(runnable, 1000);
                }
            }
        };

        tokenGetter = new Runnable() {
            @Override
            public void run() {
                Log.d("Reset Password TokenGetter","Reset Password TokenGetter is running");
                Log.d("WebView","WebView is loading: " + WebViewClient.isRunning);
                Log.d("TokenClient","TokenClient is running: " + TokenClient.isTokenRunning);

                if (!TokenClient.isTokenRunning) {
                    if (TokenClient.accessToken != null && TokenClient.isSuccess) {
                        Intent i = new Intent(getContext(), MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        Toast.makeText(getContext(), "Reset password successfully!",
                                Toast.LENGTH_SHORT).show();

                        Log.d("Reset Password TokenGetter","Reset Password TokenGetter is stopped. Token is not null");
                        //Navigation.findNavController(fragmentView).navigate(R.id.action_resetPasswordFragment_to_loginFragment);
                        handler.removeCallbacks(tokenGetter);
                    }
                    else {
                        binding.resetPasswordButton.setText(R.string.reset);
                        binding.resetPasswordButton.setEnabled(true);
                        binding.progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getContext(), "Reset password failed! Please double check your password and try again later!",
                                Toast.LENGTH_SHORT).show();
                        Log.d("Reset Password TokenGetter","Reset Password TokenGetter is stopped. Token is null");
                        handler.removeCallbacks(tokenGetter);
                    }
                }
                else {
                    Log.d("Reset Password TokenGetter","Reset Password TokenGetter is running again");
                    handler.postDelayed(tokenGetter, 1000);
                }
            }
        };
    }

    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentResetpwdBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initVariables();
        ((MainActivity) getActivity()).checkConnection();
        fragmentView = (View) view;

        binding.resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).checkConnection();
                if (MainActivity.isConnected) {
                    username = binding.usernameEditText.getText().toString();
                    old_password = binding.oldPasswordEditText.getText().toString();
                    new_password = binding.newPasswordEditText.getText().toString();
                    confirm_password = binding.confirmPasswordEditText.getText().toString();

                    binding.resetPasswordButton.setText("");
                    binding.resetPasswordButton.setEnabled(false);
                    binding.progressBar.setVisibility(View.VISIBLE);

                    WebView webView = binding.webView;
                    //webView.setVisibility(View.VISIBLE);
                    webViewClient = new WebViewClient(username, null, old_password, new_password, 3);
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.3");
                    webView.setWebViewClient(webViewClient);
                    webView.loadUrl("https://uiot.ixxc.dev/auth/realms/master/account/password");

                    handler.postDelayed(runnable, 1000);
                }
                else
                    Toast.makeText(getContext(), "No network connection available! Please connect to a network with Internet access!",
                            Toast.LENGTH_SHORT).show();

                //Navigation.findNavController(view).navigate(R.id.action_resetPasswordFragment_to_loginFragment);
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
