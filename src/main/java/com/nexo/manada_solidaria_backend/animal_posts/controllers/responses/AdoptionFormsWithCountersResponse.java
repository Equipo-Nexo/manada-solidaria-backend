package com.nexo.manada_solidaria_backend.animal_posts.controllers.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdoptionFormsWithCountersResponse {
    private AdoptionFormCountersResponse counters;
    private List<AdoptionFormItemResponse> forms;
}