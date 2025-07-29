package com.example.awaz.handler;

import android.content.Context;
import android.content.Intent;

import com.example.awaz.view.ItemPostDetailActivity;

public class PostClickHandler {
    private final Context context;

    public PostClickHandler(Context context) {
        this.context = context;
    }

    public void onPostClick() {
        context.startActivity(new Intent(context, ItemPostDetailActivity.class));
    }
}