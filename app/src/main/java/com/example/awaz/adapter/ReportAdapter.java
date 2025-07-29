package com.example.awaz.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.awaz.R;

import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    private final List<Report> reportList;

    public ReportAdapter(List<Report> reportList) {
        this.reportList = reportList;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_item, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        Report report = reportList.get(position);
        holder.textReportTitle.setText(report.getTitle());
        holder.textReportDate.setText(report.getDate());
        holder.textReportStatus.setText(report.getStatus());
        System.out.println("Binding report at position " + position + ": " + report.getTitle()); // Debug log
    }

    @Override
    public int getItemCount() {
        return reportList != null ? reportList.size() : 0;
    }

    public static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView textReportTitle, textReportDate, textReportStatus;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            textReportTitle = itemView.findViewById(R.id.textReportTitle);
            textReportDate = itemView.findViewById(R.id.textReportDate);
            textReportStatus = itemView.findViewById(R.id.textReportStatus);
        }
    }

    public static class Report {
        private String title, date, status;

        public Report(String title, String date, String status) {
            this.title = title;
            this.date = date;
            this.status = status;
        }

        public String getTitle() { return title; }
        public String getDate() { return date; }
        public String getStatus() { return status; }
    }
}