package com.example.ejercicioenclaserestfulwebservices;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ejercicioenclaserestfulwebservices.model.Report;

import java.util.ArrayList;
import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {

    private final List<Report> reports = new ArrayList<>();

    public void setReports(List<Report> newReports) {
        reports.clear();
        reports.addAll(newReports);
        notifyDataSetChanged();
    }

    public void addReportOnTop(Report report) {
        reports.add(0, report);
        notifyItemInserted(0);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_report, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Report r = reports.get(position);
        holder.tvMunicipality.setText(r.getMunicipality());
        holder.tvSummary.setText(r.getSymptomSummary());
        holder.tvRisk.setText("Risk: " + r.getRiskLevel());

        // Accessibility: lectura completa para TalkBack
        holder.itemView.setContentDescription(
                "Report " + r.getId() + ", municipality " + r.getMunicipality()
                        + ", risk level " + r.getRiskLevel()
                        + ". " + r.getSymptomSummary());
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvMunicipality, tvSummary, tvRisk;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMunicipality = itemView.findViewById(R.id.tvMunicipality);
            tvSummary = itemView.findViewById(R.id.tvSummary);
            tvRisk = itemView.findViewById(R.id.tvRisk);
        }
    }
}
