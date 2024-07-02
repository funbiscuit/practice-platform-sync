package org.CliSystem;

import com.google.gson.*;

import java.lang.reflect.Type;

public class ModuleDtoDeserializer implements JsonDeserializer<ModuleDto> {
    @Override
    public ModuleDto deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String name = jsonObject.get("name").getAsString();
        return new ModuleDto(name,null);
    }
}
