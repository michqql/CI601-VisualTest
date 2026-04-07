package me.mp1282.visualtest.system.persistence.handlers;

import com.google.gson.*;
import me.mp1282.visualtest.system.VisualTestSystem;
import me.mp1282.visualtest.system.executable.Executable;
import me.mp1282.visualtest.system.executable.IExecutableTypeHolder;
import me.mp1282.visualtest.system.persistence.IPersistenceHandler;
import me.mp1282.visualtest.util.GsonUtil;


import java.lang.reflect.Type;

public class ExecutablePersistenceHandler implements IPersistenceHandler<Executable> {

    private static final String TYPE_KEY           = "type";
    private static final String PERSISTENCE_ID_KEY = "persistence_id";
    private static final String EXTRA_CONFIG_KEY   = "extra_config";

    @Override
    public Executable deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext ctx) throws JsonParseException {
        final JsonObject root = (JsonObject) jsonElement;
        final String typeValue     = GsonUtil.getAsOrDefault(root.get(TYPE_KEY),           JsonElement::getAsString, null);
        final String persistenceId = GsonUtil.getAsOrDefault(root.get(PERSISTENCE_ID_KEY), JsonElement::getAsString, null);
        final IExecutableTypeHolder<? extends Executable> holder = VisualTestSystem.getInstance().findDataHolder(typeValue);

        if(holder == null)
            throw new IllegalStateException("Unexpected null value (holder)");

        if(persistenceId == null)
            throw new IllegalStateException("Unexpected null value (persistenceId)");

        Executable template = holder.findExecutableByPersistenceId(persistenceId).orElse(null);
        if (template != null && root.has(EXTRA_CONFIG_KEY))
            return template.restoreFromConfig(root.getAsJsonObject(EXTRA_CONFIG_KEY));
        return template;
    }

    @Override
    public JsonElement serialize(Executable executable, Type type, JsonSerializationContext ctx) {
        final JsonObject root = new JsonObject();
        root.addProperty(TYPE_KEY,           executable.getHolder().getType());
        root.addProperty(PERSISTENCE_ID_KEY, executable.getPersistenceId());
        JsonObject extraConfig = executable.getExtraConfig();
        if (extraConfig != null) root.add(EXTRA_CONFIG_KEY, extraConfig);
        return root;
    }
}
