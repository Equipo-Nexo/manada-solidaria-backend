package com.nexo.manada_solidaria_backend.guards.services.implementations;

import com.nexo.manada_solidaria_backend.guards.components.HolidayCalendar;
import com.nexo.manada_solidaria_backend.guards.controllers.responses.GuardStatusResponse;
import com.nexo.manada_solidaria_backend.guards.services.interfaces.GuardService;
import com.nexo.manada_solidaria_backend.guards.utils.GuardSchedule;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class GuardServiceImpl implements GuardService {

    private final HolidayCalendar holidayCalendar;
    private final Clock clock;

    @Override
    public GuardStatusResponse getStatus() {
        LocalDateTime now = LocalDateTime.now(clock);
        return new GuardStatusResponse(isActive(now));
    }

    private boolean isActive(LocalDateTime moment) {
        return GuardSchedule.isActiveAt(moment) || holidayCalendar.isHoliday(moment.toLocalDate());
    }
}
