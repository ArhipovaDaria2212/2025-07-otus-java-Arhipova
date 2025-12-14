package ru.otus.appcontainer;

import lombok.SneakyThrows;
import ru.otus.appcontainer.api.AppComponent;
import ru.otus.appcontainer.api.AppComponentsContainer;
import ru.otus.appcontainer.api.AppComponentsContainerConfig;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"squid:S1068", "java:S3011", "java:S112", "unchecked"})
public class AppComponentsContainerImpl implements AppComponentsContainer {

    private final List<Object> appComponents = new ArrayList<>();
    private final Map<String, Object> appComponentsByName = new HashMap<>();

    public AppComponentsContainerImpl(Class<?> initialConfigClass) {
        processConfig(initialConfigClass);
    }

    @SneakyThrows
    private void processConfig(Class<?> configClass) {
        checkConfigClass(configClass);

        Constructor<?> constructor = configClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        Object instance = constructor.newInstance();

        Arrays.stream(configClass.getMethods())
                .filter(method -> method.isAnnotationPresent(AppComponent.class))
                .sorted(Comparator.comparing(
                        method -> method.getAnnotation(AppComponent.class).order()))
                .forEach(method -> {
                    try {
                        method.setAccessible(true);
                        Parameter[] parameters = method.getParameters();
                        String componentName =
                                method.getAnnotation(AppComponent.class).name();
                        Object component;

                        Object[] args = Arrays.stream(parameters)
                                .map(parameter -> getAppComponent(parameter.getType()))
                                .toArray();
                        component = method.invoke(instance, args);

                        appComponents.add(component);
                        appComponentsByName.put(componentName, component);
                    } catch (Exception e) {
                        throw new RuntimeException(
                                String.format("Failed to parse the configuration file %s", configClass.getName()), e);
                    }
                });
    }

    private void checkConfigClass(Class<?> configClass) {
        if (!configClass.isAnnotationPresent(AppComponentsContainerConfig.class)) {
            throw new IllegalArgumentException(String.format("Given class is not config %s", configClass.getName()));
        }
    }

    @Override
    public <C> C getAppComponent(Class<C> componentClass) {
        List<Object> components =
                appComponents.stream().filter(componentClass::isInstance).toList();

        if (components.isEmpty()) {
            throw new RuntimeException("No such component found in container");
        }

        if (components.size() > 1) {
            throw new RuntimeException(
                    String.format("Too many component with type %s found in container", componentClass.getName()));
        }

        return (C) components.getFirst();
    }

    @Override
    public <C> C getAppComponent(String componentName) {
        Object component = appComponentsByName.get(componentName);

        if (component == null) {
            throw new RuntimeException("No such component found in container");
        }

        return (C) component;
    }
}
