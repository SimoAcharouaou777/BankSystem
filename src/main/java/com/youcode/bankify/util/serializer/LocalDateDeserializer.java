package com.youcode.bankify.util.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class LocalDateDeserializer extends JsonDeserializer<LocalDate> {

    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String date = p.getText();
        try {
            long timestamp = Long.parseLong(date);
            return Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate();
        } catch (NumberFormatException e) {
            DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                    .appendPattern("yyyy-MM-dd")
                    .optionalStart().appendPattern("[.SSS]").optionalEnd()
                    .optionalStart().appendOffset("+HH:mm", "Z").optionalEnd()
                    .toFormatter();

            return LocalDate.parse(date, formatter);
        }
    }
}