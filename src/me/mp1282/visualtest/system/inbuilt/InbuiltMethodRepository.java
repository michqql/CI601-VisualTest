package me.mp1282.visualtest.system.inbuilt;

import me.mp1282.visualtest.system.IReset;
import me.mp1282.visualtest.system.executable.IExecutableTypeHolder;
import me.mp1282.visualtest.system.inbuilt.category.*;
import me.mp1282.visualtest.util.Pair;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class InbuiltMethodRepository implements IReset, IExecutableTypeHolder<InbuiltMethodExecutable> {

    private static final Set<Class<?>> CLASS_REGISTRY = new HashSet<>();
    /* Static constructor to add all classes that provide inbuilt functions
     * to the registry that will be parsed when this class is instantiated.
     */
    static {
        CLASS_REGISTRY.add(TestInbuiltMethods.class);

        CLASS_REGISTRY.add(RandomInbuiltMethods.class);
        CLASS_REGISTRY.add(BooleanLogicInbuiltMethods.class);
        CLASS_REGISTRY.add(IntegerInbuiltMethods.class);
        CLASS_REGISTRY.add(LongInbuiltMethods.class);
        CLASS_REGISTRY.add(FloatInbuiltMethods.class);
        CLASS_REGISTRY.add(DoubleInbuiltMethods.class);
        CLASS_REGISTRY.add(MathInbuiltMethods.class);
        CLASS_REGISTRY.add(StringInbuiltMethods.class);
        CLASS_REGISTRY.add(CollectionsInbuiltMethods.class);
        CLASS_REGISTRY.add(ObjectInbuiltMethods.class);
        CLASS_REGISTRY.add(CharacterInbuiltMethods.class);
        CLASS_REGISTRY.add(TimeInbuiltMethods.class);
        CLASS_REGISTRY.add(FileInbuiltMethods.class);
        CLASS_REGISTRY.add(LoggingInbuiltMethods.class);
    }

    private final List<Pair<InbuiltFunctionProvider, List<InbuiltMethodExecutable>>> repository;

    public InbuiltMethodRepository() {
        this.repository = new ArrayList<>();

        for(Class<?> clazz : CLASS_REGISTRY) {
            InbuiltFunctionProvider annotation = clazz.getAnnotation(InbuiltFunctionProvider.class);
            if(annotation == null) {
                System.out.println("Missing annotation on inbuilt function provider class! " + clazz.getSimpleName());
                continue;
            }

            List<InbuiltMethodExecutable> methodList = new ArrayList<>();

            for(Method method : clazz.getDeclaredMethods()) {
                /* Only static methods should be considered inbuilt functions */
                if(Modifier.isStatic(method.getModifiers())) {
                    InbuiltMethodExecutable exe = new InbuiltMethodExecutable(annotation, method);
                    exe.init(this);
                    methodList.add(exe);
                }
            }

            repository.add(new Pair<>(annotation, methodList));
        }
    }

    public List<Pair<InbuiltFunctionProvider, List<InbuiltMethodExecutable>>> getRepository() {
        return repository;
    }

    @Override
    public void reset() {
        /* Do nothing */
    }

    @Override
    public Optional<InbuiltMethodExecutable> findExecutableByPersistenceId(String persistenceId) {
        for(Pair<InbuiltFunctionProvider, List<InbuiltMethodExecutable>> pair : repository) {
            for(InbuiltMethodExecutable method : pair.value()) {
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
