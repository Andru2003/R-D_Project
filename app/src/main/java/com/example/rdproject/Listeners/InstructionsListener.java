package com.example.rdproject.Listeners;

import com.example.rdproject.Models.InstructionsResponse;

import java.util.List;

public interface InstructionsListener {
    void didFetch(List<InstructionsResponse> response, String message);
    void didError(String message);
}
