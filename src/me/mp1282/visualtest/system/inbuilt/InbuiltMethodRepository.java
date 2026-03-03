package me.mp1282.visualtest.system.inbuilt;

import me.mp1282.visualtest.system.IReset;
import me.mp1282.visualtest.system.executable.IExecutableTypeHolder;
import me.mp1282.visualtest.system.inbuilt.category.*;
import me.mp1282.visualtest.system.persistence.IPersistenceHandler;
import me.mp1282.visualtest.util.Pair;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class InbuiltMethodRepository implements IReset, IExecutableTypeHolder<InbuiltMethod> {

    private static final Set<Class<?>> CLASS_REGISTRY = new HashSet<>();
    /* Static constructor to add all classes that provide inbuilt functions
     * to the registry that will be parsed when this class is instantiated.
     */
    static {
        CLASS_REGISTRY.add(TestInbuiltMethods.class);

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
                    InbuiltMethod exe = new InbuiltMethod(annotation, method);
                    exe.init(this);
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

    @Override
    public Optional<InbuiltMethod> findExecutableByPersistenceId(String persistenceId) {
        for(Pair<InbuiltFunctionProvider, List<InbuiltMethod>> pair : repository) {
            for(InbuiltMethod method : pair.value()) {
                if(method.getPersistenceId().equals(persistenceId))
                    return Optional.of(method);
            }
        }

        return Optional.empty();
    }

    @Override
    public String getType() {
        return "inbuilt";
    }
}
