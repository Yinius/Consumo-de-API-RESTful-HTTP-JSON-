package com.example.ejercicioenclaserestfulwebservices.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Singleton: una sola instancia de Retrofit para toda la app.
 */
public class RetrofitClient {

    // IMPORTANTE: debe terminar en "/".
    // Backend: MockAPI (recurso "reports")
    private static final String BASE_URL = "https://6ad165b8addc5b5cb142fc44.mockapi.io/api/v1/";

    private static Retrofit retrofit = null;
    private static ApiService apiService = null;

    private RetrofitClient() { }

    public static ApiService getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        if (apiService == null) {
            apiService = retrofit.create(ApiService.class);
        }
        return apiService;
    }
}
