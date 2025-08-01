package com.livraria.integrador.util;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZonedDateTime;

/**
 * Adaptador para o Gson serializar e desserializar LocalDateTime
 * no formato padrão ISO_LOCAL_DATE_TIME, que é compatível com o Jackson do Spring Boot.
 */
public class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public JsonElement serialize(LocalDateTime localDateTime, Type srcType, JsonSerializationContext context) {
        return new JsonPrimitive(formatter.format(localDateTime));
    }

    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        String dateString = json.getAsString();
        try {
            return ZonedDateTime.parse(dateString).toLocalDateTime();
        } catch (Exception e) {
            return LocalDateTime.parse(dateString, formatter);
        }
    }
}