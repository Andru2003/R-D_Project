package com.example.rdproject.Listeners;

import com.example.rdproject.Models.RandomRApiResponse;

public interface RandomRecipiResponseListener {
    void didFetch(RandomRApiResponse response, String message);
    void didError(String message);
}
