package com.example.awaz.view;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.awaz.R;
import com.example.awaz.model.SystemNotification;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class SystemNotificationDetailActivity extends AppCompatActivity {

    private TextView authorDetail, timestampDetail, titleDetail, messageDetail,
            districtDetail, wardDetail, areaNameDetail, issueHeadingDetail, issueStatusDetail;
    private ImageView imageDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_notification_detail);

        authorDetail = findViewById(R.id.authorDetail);
        timestampDetail = findViewById(R.id.timestampDetail);
        titleDetail = findViewById(R.id.titleDetail);
        imageDetail = findViewById(R.id.imageDetail);
        messageDetail = findViewById(R.id.messageDetail);
        districtDetail = findViewById(R.id.districtDetail);
        wardDetail = findViewById(R.id.wardDetail);
        areaNameDetail = findViewById(R.id.areaNameDetail);
        issueHeadingDetail = findViewById(R.id.issueHeadingDetail);
        issueStatusDetail = findViewById(R.id.issueStatusDetail);

        SystemNotification notification = (SystemNotification) getIntent().getSerializableExtra("notification");

        if (notification == null) {
            Toast.makeText(this, "Error: Notification data not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        authorDetail.setText("Nepal Government");
        timestampDetail.setText(getRelativeTime(notification.getCreatedAt()));
        titleDetail.setText(notification.getTitle() != null ? notification.getTitle() : "");
        messageDetail.setText(notification.getMessage() != null ? notification.getMessage() : "");
        districtDetail.setText(notification.getDistrict() != null ? "District: " + notification.getDistrict() : "");
        wardDetail.setText(notification.getWard() != null ? "Ward: " + notification.getWard() : "");
        areaNameDetail.setText(notification.getAreaName() != null ? "Area: " + notification.getAreaName() : "");

        if (notification.getIssue() != null) {
            issueHeadingDetail.setText(notification.getIssue().getHeading() != null ? "Issue Heading: " + notification.getIssue().getHeading() : "");
            issueStatusDetail.setText(notification.getIssue().getStatus() != null ? "Issue Status: " + notification.getIssue().getStatus() : "");
        } else {
            issueHeadingDetail.setVisibility(View.GONE);
            issueStatusDetail.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(notification.getImage())) {
            imageDetail.setVisibility(View.VISIBLE);
            Glide.with(this).load(notification.getImage()).into(imageDetail);
        } else {
            imageDetail.setVisibility(View.GONE);
        }
    }

    private String getRelativeTime(String createdAt) {
        if (createdAt == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            long time = sdf.parse(createdAt).getTime();
            return DateUtils.getRelativeTimeSpanString(time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }
}