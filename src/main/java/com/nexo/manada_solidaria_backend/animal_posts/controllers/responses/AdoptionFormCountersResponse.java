package com.nexo.manada_solidaria_backend.animal_posts.controllers.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdoptionFormCountersResponse {
    private long total;
    private long pending;
    private long reviewed;
}