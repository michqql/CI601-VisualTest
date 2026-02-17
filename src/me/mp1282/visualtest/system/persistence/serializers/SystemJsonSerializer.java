package me.mp1282.visualtest.system.persistence.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import me.mp1282.visualtest.system.VisualTestSystem;

import java.lang.reflect.Type;

public class SystemJsonSerializer implements JsonSerializer<VisualTestSystem> {

    @Override
    public JsonElement serialize(VisualTestSystem sys, Type type, JsonSerializationContext ctx) {
        JsonObject root = new JsonObject();
        root.add("meta", ctx.serialize(sys.getProjectInformation()));
        return root;
    }
}
