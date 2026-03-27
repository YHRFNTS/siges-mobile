package dev.spiffocode.sigesmobile.data.remote

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.time.Duration


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