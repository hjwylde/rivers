package com.hjwylde.rivers.models.serialisation;

import android.support.annotation.NonNull;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.hjwylde.rivers.models.SerializableLatLng;

import java.lang.reflect.Type;

public final class LatLngDeserializer implements JsonDeserializer<SerializableLatLng> {
    @Override
    public SerializableLatLng deserialize(@NonNull JsonElement json, @NonNull Type typeOfT, @NonNull JsonDeserializationContext context) throws JsonParseException {
        try {
            JsonObject jobj = json.getAsJsonObject();

            double lat = jobj.get("lat").getAsDouble();
            double lng = jobj.get("lng").getAsDouble();

            return new SerializableLatLng(lat, lng);
        } catch (NullPointerException | IllegalStateException e) {
            throw new JsonParseException(e);
        }
    }
}
