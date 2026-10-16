package com.example.ejercicioenclaserestfulwebservices;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ejercicioenclaserestfulwebservices.api.ApiService;
import com.example.ejercicioenclaserestfulwebservices.api.RetrofitClient;
import com.example.ejercicioenclaserestfulwebservices.model.Report;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ApiService apiService;
    private ReportAdapter adapter;

    private ProgressBar progressBar;
    private EditText etMunicipality, etSymptoms;
    private Spinner spRisk;
    private Button btnSend, btnRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Views
        progressBar = findViewById(R.id.progressBar);
        etMunicipality = findViewById(R.id.etMunicipality);
        etSymptoms = findViewById(R.id.etSymptoms);
        spRisk = findViewById(R.id.spRisk);
        btnSend = findViewById(R.id.btnSend);
        btnRefresh = findViewById(R.id.btnRefresh);

        spRisk.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Low", "Medium", "High"}));

        RecyclerView recyclerView = findViewById(R.id.recyclerReports);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReportAdapter();
        recyclerView.setAdapter(adapter);

        // Initialize API service instance (Singleton)
        apiService = RetrofitClient.getApiService();

        btnRefresh.setOnClickListener(v -> fetchReportsFromServer());
        btnSend.setOnClickListener(v -> sendNewReport());

        // Perform GET request
        fetchReportsFromServer();
    }

    // ---------------------------------------------------------------- GET
    private void fetchReportsFromServer() {
        setLoading(true);
        Call<List<Report>> call = apiService.getAllReports();

        // enqueue() ejecuta la petición en un hilo secundario
        call.enqueue(new Callback<List<Report>>() {
            @Override
            public void onResponse(Call<List<Report>> call, Response<List<Report>> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<Report> reports = response.body();
                    adapter.setReports(reports);
                    Log.d("API_SUCCESS", "Retrieved " + reports.size() + " records.");
                    Toast.makeText(MainActivity.this,
                            "Successfully loaded " + reports.size() + " items.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("API_ERROR", "Server returned HTTP status: " + response.code());
                    showHttpError(response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Report>> call, Throwable t) {
                setLoading(false);
                handleFailure(t);
            }
        });
    }

    // --------------------------------------------------------------- POST
    private void sendNewReport() {
        String municipality = etMunicipality.getText().toString().trim();
        String symptoms = etSymptoms.getText().toString().trim();
        String risk = spRisk.getSelectedItem().toString();

        if (municipality.isEmpty()) {
            etMunicipality.setError("Municipality is required");
            return;
        }
        if (symptoms.isEmpty()) {
            etSymptoms.setError("Symptom summary is required");
            return;
        }

        setLoading(true);
        Report newReport = new Report(municipality, symptoms, risk);

        apiService.createReport(newReport).enqueue(new Callback<Report>() {
            @Override
            public void onResponse(Call<Report> call, Response<Report> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    Report created = response.body();
                    Log.d("API_POST", "Created report with ID: " + created.getId());
                    Toast.makeText(MainActivity.this,
                            "Report created with ID: " + created.getId(),
                            Toast.LENGTH_LONG).show();

                    // Si el servidor no regresa los campos, mostramos lo que enviamos
                    adapter.addReportOnTop(created.getMunicipality() != null
                            ? created : newReport);

                    etMunicipality.setText("");
                    etSymptoms.setText("");
                } else {
                    Log.e("API_ERROR", "POST failed with HTTP status: " + response.code());
                    showHttpError(response.code());
                }
            }

            @Override
            public void onFailure(Call<Report> call, Throwable t) {
                setLoading(false);
                handleFailure(t);
            }
        });
    }

    // ----------------------------------------------------- Error handling
    private void showHttpError(int code) {
        String message;
        if (code == 404) {
            message = "The requested resource was not found (404). Check the server URL.";
        } else if (code >= 500) {
            message = "The server had a problem (" + code + "). Please try again later.";
        } else if (code >= 400) {
            message = "The request could not be completed (" + code + ").";
        } else {
            message = "Unexpected server response (" + code + ").";
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void handleFailure(Throwable t) {
        Log.e("NETWORK_FAILURE", "Connection failed: " + t.getMessage());
        String message;
        if (t instanceof UnknownHostException) {
            message = "Network failure. Please check your internet connection.";
        } else if (t instanceof SocketTimeoutException) {
            message = "The server took too long to respond. Please try again.";
        } else if (t instanceof IOException) {
            message = "Network failure. Please check your internet connection.";
        } else {
            message = "Unexpected error while reading the server data.";
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    // -------------------------------------------------------- UI helpers
    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnSend.setEnabled(!loading);
        btnRefresh.setEnabled(!loading);
    }
}
