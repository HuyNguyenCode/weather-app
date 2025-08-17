package com.ninegroup.weather.network;

import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebView;

public class WebViewClient extends android.webkit.WebViewClient {
    private String username;
    private String email;
    private String password;
    private String newPassword;
    private Integer type; // 1 = Sign up, 2 = Sign in, 3 = Reset password, 4 = Change user info
    public static Boolean isRunning = true;

    public WebViewClient(String username, String email, String password,
                         String newPassword, Integer type){
        this.username = username;
        this.email = email;
        this.password = password;
        this.newPassword = newPassword;
        this.type = type;
    }

    @Override
    public void onPageFinished(WebView webView, String url) {
        super.onPageFinished(webView, url);
        CookieManager.getInstance().setAcceptCookie(true);

        switch (type) {
            case 1:
                if (url.contains("/registration")) {
                    System.out.println("register");
                    fillSignUpForm(webView);
                }
                else {
                    String signUpButton = "document.querySelector(\"#wrapper > div > div > div:nth-child(1) > div.col.s12 > form > div:nth-child(2) > a\").click()";
                    webView.evaluateJavascript(signUpButton,null);
                    fillSignUpForm(webView);
                }
                break;
            case 2:
                fillSignInForm(webView);
                break;
            case 3:
                fillResetPasswordForm(webView);
                break;
        }
    }

    public void fillSignUpForm(WebView webView) {
        Log.i("WebViewClient", "Signing up");

        String usernameScript = "document.getElementById('username').value='" + username + "';";
        String emailScript = "document.getElementById('email').value='" + email + "';";
        String passwordScript = "document.getElementById('password').value='" + password + "';";
        String confirmPasswordScript = "document.getElementById('password-confirm').value='" + password  + "';";
        //String submitScript = "document.forms['kc-register-form'].submit();";
        String submitScript = "document.querySelector('button[name=\"register\"]').click()";

        webView.evaluateJavascript(usernameScript,null);
        webView.evaluateJavascript(emailScript,null);
        webView.evaluateJavascript(passwordScript,null);
        webView.evaluateJavascript(confirmPasswordScript,null);
        webView.evaluateJavascript(submitScript,null);

        isRunning = false;
    }

    public void fillSignInForm(WebView webView) {
        Log.i("WebViewClient", "Signing in");

        String usernameScript = "document.getElementById('username').value='" + username + "';";
        String passwordScript = "document.getElementById('password').value='" + password + "';";
        String submitScript = "document.querySelector('button[name=\"login\"]').click()";

        webView.evaluateJavascript(usernameScript,null);
        webView.evaluateJavascript(passwordScript,null);
        webView.evaluateJavascript(submitScript,null);

        isRunning = false;
    }

    public void fillResetPasswordForm(WebView webView) {
        Log.i("WebViewClient", "Resetting password");

        String oldPasswordScript = "document.getElementById('password').value='" + password + "';";
        String newPasswordScript = "document.getElementById('password-new').value='" + newPassword + "';";
        String confirmPasswordScript = "document.getElementById('password-confirm').value='" + newPassword + "';";
        String submitScript = "document.querySelector('button').click()";

        webView.evaluateJavascript(oldPasswordScript,null);
        webView.evaluateJavascript(newPasswordScript,null);
        webView.evaluateJavascript(confirmPasswordScript,null);
        webView.evaluateJavascript(submitScript,null);

        isRunning = false;
    }
}
