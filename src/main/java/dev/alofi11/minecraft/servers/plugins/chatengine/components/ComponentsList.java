package dev.alofi11.minecraft.servers.plugins.chatengine.components;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ComponentsList extends ArrayList<Object> {

  public <T> T initializeFromListResources(@NotNull Class<T> initializedClass,
      int constructorIndex) {
    Constructor<?> constructor = initializedClass.getDeclaredConstructors()[constructorIndex];
    Class<?>[] parameterTypes = constructor.getParameterTypes();
    Object[] parameters = new Object[constructor.getParameterCount()];

    for (int i = 0; i < parameters.length; i++) {
      parameters[i] = this.get(parameterTypes[i]);
    }

    try {
      return (T) constructor.newInstance(parameters);
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
      return null;
    }
  }

  public <T> boolean initializeFromListResourcesToList(@NotNull Class<T> initializedClass,
      int constructor) {
    Object object = initializeFromListResources(initializedClass, constructor);
    if (object != null) {
      this.add(object);
      return true;
    }

    return false;
  }

  @Nullable
  public <T> T get(@NotNull Class<T> requiredObjectClass) {
    if (requiredObjectClass.equals(this.getClass())) {
      return (T) this;
    }

    for (Object object : this) {
      if (isRequiredObject(object.getClass(), requiredObjectClass)) {
        return (T) object;
      }
    }

    return null;
  }

  @NotNull
  public <T> T getNN(@NotNull Class<T> requiredObjectClass) {
    return Objects.requireNonNull(get(requiredObjectClass));
  }

  private boolean isRequiredObject(@NotNull Class<?> objectClass,
      @NotNull Class<?> requiredObjectClass) {
    if (objectClass.equals(requiredObjectClass) || objectClass.getSuperclass()
        .equals(requiredObjectClass)) {
      return true;
    } else {
      for (Class<?> anInterface : objectClass.getInterfaces()) {
        if (anInterface.equals(requiredObjectClass)) {
          return true;
        }
      }
    }

    return false;
  }

}
