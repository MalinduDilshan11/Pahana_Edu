package Utils;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    @Override
    public void write(JsonWriter out, LocalDateTime value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value.format(FORMATTER));
        }
    }

    @Override
    public LocalDateTime read(JsonReader in) throws IOException {
        if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        String dateTimeStr = in.nextString();
        try {
            // Parse as ZonedDateTime to handle 'Z' or other offsets
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateTimeStr, FORMATTER);
            // Convert to LocalDateTime
            return zonedDateTime.toLocalDateTime();
        } catch (Exception e) {
            throw new IOException("Failed to parse LocalDateTime from: " + dateTimeStr, e);
        }
    }
}