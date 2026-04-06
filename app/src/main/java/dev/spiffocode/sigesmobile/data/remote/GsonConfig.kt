package dev.spiffocode.sigesmobile.data.remote

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


class DurationTypeAdapter : TypeAdapter<Duration>() {
    override fun write(out: JsonWriter, value: Duration?) {
        out.value(value?.toString())
    }

    override fun read(input: JsonReader): Duration? {
        return if (input.peek() == JsonToken.NULL) {
            input.nextNull()
            null
        } else {
            Duration.parse(input.nextString())
        }
    }
}

class LocalDateTypeAdapter : TypeAdapter<LocalDate>() {
    override fun write(out: JsonWriter, value: LocalDate?) {
        out.value(value?.toString())
    }

    override fun read(input: JsonReader): LocalDate? {
        return if (input.peek() == JsonToken.NULL) {
            input.nextNull()
            null
        } else {
            LocalDate.parse(input.nextString())
        }
    }
}

class LocalTimeTypeAdapter : TypeAdapter<LocalTime>() {
    override fun write(out: JsonWriter, value: LocalTime?) {
        out.value(value?.format(DateTimeFormatter.ofPattern("HH:mm")))
    }

    override fun read(input: JsonReader): LocalTime? {
        return if (input.peek() == JsonToken.NULL) {
            input.nextNull()
            null
        } else {
            LocalTime.parse(input.nextString())
        }
    }
}