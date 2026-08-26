package com.nexo.manada_solidaria_backend.common.integrations.base;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MapDispatcher extends Dispatcher {

    private final ConcurrentHashMap<String, Queue<MockResponse>> responsesByPath = new ConcurrentHashMap<>();

    public void enqueue(String path, MockResponse response) {
        responsesByPath
                .computeIfAbsent(normalize(path), ignored -> new ConcurrentLinkedQueue<>())
                .add(response);
    }

    public void clear() {
        responsesByPath.clear();
    }

    @Override
    public MockResponse dispatch(RecordedRequest request) {
        String path = request.getRequestUrl() != null
                ? request.getRequestUrl().encodedPath()
                : request.getPath();
        Queue<MockResponse> responses = responsesByPath.get(normalize(path));
        MockResponse response = responses == null ? null : responses.poll();

        if (responses != null && responses.isEmpty()) {
            responsesByPath.remove(normalize(path), responses);
        }

        return response != null
                ? response
                : new MockResponse().setResponseCode(404);
    }

    private String normalize(String path) {
        if (path == null || path.isBlank()) {
            return "/";
        }
        String normalized = path.startsWith("/") ? path : "/" + path;
        int queryStart = normalized.indexOf('?');
        return queryStart >= 0 ? normalized.substring(0, queryStart) : normalized;
    }
}
