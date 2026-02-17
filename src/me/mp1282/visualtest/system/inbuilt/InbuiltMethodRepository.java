package me.mp1282.visualtest.system.inbuilt;

import me.mp1282.visualtest.system.IReset;
import me.mp1282.visualtest.system.inbuilt.category.BooleanLogicInbuiltMethods;
import me.mp1282.visualtest.system.inbuilt.category.CollectionsInbuiltMethods;
import me.mp1282.visualtest.system.inbuilt.category.NumberInbuiltMethods;
import me.mp1282.visualtest.system.inbuilt.category.RandomInbuiltMethods;
import me.mp1282.visualtest.util.Pair;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InbuiltMethodRepository implements IReset {

    private static final Set<Class<?>> CLASS_REGISTRY = new HashSet<>();
    /* Static constructor to add all classes that provide inbuilt functions
     * to the registry that will be parsed when this class is instantiated.
     */
    static {
        CLASS_REGISTRY.add(RandomInbuiltMethods.class);
        CLASS_REGISTRY.add(BooleanLogicInbuiltMethods.class);
        CLASS_REGISTRY.add(NumberInbuiltMethods.class);
        CLASS_REGISTRY.add(CollectionsInbuiltMethods.class);
    }

    private final List<Pair<InbuiltFunctionProvider, List<InbuiltMethod>>> repository;

    public InbuiltMethodRepository() {
        this.repository = new ArrayList<>();

        for(Class<?> clazz : CLASS_REGISTRY) {
            InbuiltFunctionProvider annotation = clazz.getAnnotation(InbuiltFunctionProvider.class);
            if(annotation == null) {
                System.out.println("Missing annotation on inbuilt function provider class! " + clazz.getSimpleName());
                continue;
            }

            List<InbuiltMethod> methodList = new ArrayList<>();

            for(Method method : clazz.getDeclaredMethods()) {
                /* Only static methods should be considered inbuilt functions */
                if(Modifier.isStatic(method.getModifiers())) {
                    InbuiltMethod exe = new InbuiltMethod(method);
                    exe.init();
                    methodList.add(exe);
                }
            }

            repository.add(new Pair<>(annotation, methodList));
        }
    }

    public List<Pair<InbuiltFunctionProvider, List<InbuiltMethod>>> getRepository() {
        return repository;
    }

    @Override
    public void reset() {
        /* Do nothing */
    }
}
