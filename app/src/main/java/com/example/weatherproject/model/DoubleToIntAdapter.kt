package com.example.weatherproject.model

import com.google.gson.JsonSyntaxException
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.io.IOException

class DoubleToIntAdapter : TypeAdapter<Int>() {
    @Throws(IOException::class)
    override fun write(out: JsonWriter, value: Int?) {
        out.value(value)
    }

    @Throws(IOException::class)
    override fun read(`in`: JsonReader): Int? {
        if (`in`.peek() == JsonToken.NULL) {
            `in`.nextNull()
            return null
        }
        try {
            val value = `in`.nextDouble()
            return value.toInt()
        } catch (e: NumberFormatException) {
            throw JsonSyntaxException(e)
        }
    }
}
