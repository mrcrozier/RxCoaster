package com.example.laptor.rxcoaster.data.remote;

/**
 * Created by laptor on 10/31/17.
 */

public class ApiUtils {

    private ApiUtils() {}

    public static final String BASE_URL = "http://clevercoasters.duckdns.org:4000/";

    public static APIService getAPIService() {

        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }
}
