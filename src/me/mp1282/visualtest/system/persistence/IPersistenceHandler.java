package me.mp1282.visualtest.system.persistence;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

public interface IPersistenceHandler<T> extends JsonSerializer<T>, JsonDeserializer<T> {
}
