package com.elite.qel_medistore;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.webkit.JavascriptInterface;
import android.widget.EditText;

public class DialogHelper {
    private Context context;
    private Drawable appIcon;
    private WebAppInterface webAppInterface;

    public DialogHelper(Context context, WebAppInterface webAppInterface) {
        this.context = context;
        this.appIcon = context.getDrawable(R.drawable.app_icon);
        this.webAppInterface = webAppInterface;
    }

    @JavascriptInterface
    public void showAlert(String message) {
        new AlertDialog.Builder(context)
            .setTitle("Alert")
            .setMessage(message)
            .setIcon(appIcon)
            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
            .show();
    }

    @JavascriptInterface
    public void showConfirm(String message) {
        new AlertDialog.Builder(context)
            .setTitle("Confirm")
            .setMessage(message)
            .setIcon(appIcon)
            .setPositiveButton("Yes", (dialog, which) -> webAppInterface.onConfirmResult(true))
            .setNegativeButton("No", (dialog, which) -> webAppInterface.onConfirmResult(false))
            .show();
    }

    @JavascriptInterface
    public void showPrompt(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Prompt");
        builder.setMessage(message);
        builder.setIcon(appIcon);

        final EditText input = new EditText(context);
        builder.setView(input);

        builder.setPositiveButton("Submit", (dialog, which) -> {
            String userInput = input.getText().toString();
            webAppInterface.onPromptResult(userInput);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> webAppInterface.onPromptResult(""));
        builder.show();
    }
}
