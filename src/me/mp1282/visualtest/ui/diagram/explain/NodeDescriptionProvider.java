package me.mp1282.visualtest.ui.diagram.explain;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.mp1282.visualtest.system.diagram.DiagramRefExecutable;
import me.mp1282.visualtest.system.executable.Executable;
import me.mp1282.visualtest.system.executable.MethodExecutable;
import me.mp1282.visualtest.system.inbuilt.special.SwitchExecutable;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Map;

public class NodeDescriptionProvider {

    private static final Map<String, String> TEMPLATES = loadTemplates();

    private NodeDescriptionProvider() {}

    public static String describe(Executable exe) {
        String key = exe.getClass().getSimpleName();
        String template = TEMPLATES.get(key);

        /* If no description exists for the executable type, return early. */
        if(template == null)
            return null;

        return resolvePlaceholders(template, exe);
    }

    private static String resolvePlaceholders(String template, Executable exe) {
        String result = template;
        if (exe instanceof SwitchExecutable s)
            result = result.replace("{case.count}", String.valueOf(s.getCaseCount()));
        if (exe instanceof MethodExecutable m)
            result = result.replace("{method.description}", m.getMethodDescription());
        if (exe instanceof DiagramRefExecutable r)
            result = result.replace("{diagram.name}", r.getName());
        result = result.replace("{executable.name}", exe.getName());
        return result;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, String> loadTemplates() {
        try (var reader = new InputStreamReader(
                NodeDescriptionProvider.class.getResourceAsStream("descriptions.json"))) {
            Type type = new TypeToken<Map<String, String>>(){}.getType();
            return new Gson().fromJson(reader, type);
        } catch (Exception e) {
            System.err.println("NodeDescriptionProvider: failed to load descriptions.json — " + e.getMessage());
            return Map.of();
        }
    }
}
