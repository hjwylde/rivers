package com.hjwylde.rivers.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.hjwylde.rivers.models.SerializableLatLng;

import java.lang.reflect.Type;

public final class LatLngDeserialiser implements JsonDeserializer<SerializableLatLng> {
    @Override
    public SerializableLatLng deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            JsonObject jobj = json.getAsJsonObject();

            double lat = jobj.get("lat").getAsDouble();
            double lng = jobj.get("lng").getAsDouble();

            return new SerializableLatLng(lat, lng);
        } catch (IllegalStateException e) {
            throw new JsonParseException(e);
        }
    }
}
