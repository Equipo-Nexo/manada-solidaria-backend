package com.nexo.manada_solidaria_backend.maps.utils;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static com.nexo.manada_solidaria_backend.common.utils.MockBaseDataUtils.INVALID_ACCESS_TOKEN;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class MockMapDataUtils {

    public static Stream<Arguments> provideMapFieldCases() {
        return Stream.of(
                Arguments.of("Solo el perdido activo va a lostAnimals", "$.lostAnimals[*].name", contains("Milo")),
                Arguments.of("Los en la calle activos, del mas nuevo al mas viejo", "$.inStreetAnimals[*].name", contains("Toby", "Luna")),
                Arguments.of("Todas las veterinarias, ordenadas por nombre y sin repetir", "$.vets[*].name", contains("Abre a la tarde", "Dr. Seba Veterinaria", "Sin horario")),

                Arguments.of("El perdido trae su id", "$.lostAnimals[0].id", is("ee000000-0000-0000-0000-0000000000c1")),
                Arguments.of("El perdido trae su imagen", "$.lostAnimals[0].imageUrl", is("/fotoMilo")),
                Arguments.of("El perdido tiene estado LOST", "$.lostAnimals[0].status", is("LOST")),
                Arguments.of("El en la calle tiene estado IN_STREET", "$.inStreetAnimals[*].status", contains("IN_STREET", "IN_STREET")),
                Arguments.of("La primera linea del animal es un reloj", "$.lostAnimals[0].firstLineDescription.iconName", is("Clock")),
                Arguments.of("Publicado hace 2 dias", "$.lostAnimals[0].firstLineDescription.text", is("2")),
                Arguments.of("Publicado hace 1 dia", "$.inStreetAnimals[1].firstLineDescription.text", is("1")),
                Arguments.of("Publicado hoy son 0 dias", "$.inStreetAnimals[0].firstLineDescription.text", is("0")),

                Arguments.of("Location con direccion, numero y nombre", "$.lostAnimals[0].location", is("Constancio Vigil 1821, San Justo")),
                Arguments.of("Location solo con nombre", "$.inStreetAnimals[1].location", is("Centro")),
                Arguments.of("Location sin datos devuelve null", "$.inStreetAnimals[0].location", nullValue()),
                Arguments.of("Trae la latitud", "$.lostAnimals[0].latitude", is(-32.4102)),
                Arguments.of("Trae la longitud", "$.lostAnimals[0].longitude", is(-63.2411)),

                Arguments.of("Abierta en su turno de la mañana", "$.vets[1].status", is("OPEN")),
                Arguments.of("Cerrada porque hoy abre a la tarde", "$.vets[0].status", is("CLOSED")),
                Arguments.of("Cerrada porque no tiene horario", "$.vets[2].status", is("CLOSED")),
                Arguments.of("La primera linea de la veterinaria es el telefono", "$.vets[1].firstLineDescription.iconName", is("Phone")),
                Arguments.of("El telefono con codigo de area", "$.vets[1].firstLineDescription.text", is("353-4010369")),
                Arguments.of("La veterinaria trae su imagen", "$.vets[1].imageUrl", is("/fotoVeterinaria")),
                Arguments.of("La veterinaria trae su location", "$.vets[1].location", is("San Martin 540, Villa Maria")),
                Arguments.of("Cada item trae solo los campos del contrato", "$.vets[1].keys()", hasSize(8))
        );
    }

    public static Stream<Arguments> provideUnauthorizedCases() {
        return Stream.of(
                Arguments.of("Sin token", null),
                Arguments.of("Con token invalido", INVALID_ACCESS_TOKEN)
        );
    }
}
