package com.nexo.manada_solidaria_backend.common.utils;

import com.nexo.manada_solidaria_backend.common.data.models.StatusHistory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

public class StatusHistoryUtils {

    public static <T extends StatusHistory<?>> T getCurrentStatus(
            List<T> statusHistory
    ) {
        return statusHistory.stream()
                .filter(status -> status.getFinishedAt() == null)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "Error getting current status"
                        )
                );
    }
}