package ru.DmN.uu;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.DmN.bpl.BytecodeUtils;
import ru.DmN.bpl.CallBuilder;
import ru.DmN.bpl.FieldBuilder;
import ru.DmN.bpl.annotations.BytecodeProcessor;
import ru.DmN.uu.internal.MagicAccessor;

import java.lang.invoke.*;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

@BytecodeProcessor
public class Unsafe {
    public static final sun.misc.Unsafe unsafe;
    public static final Class<MagicAccessor> accessor;
    public static final MethodHandles.Lookup lookup;
    public static final Object MethodHandles$MethodHandles;
    public static final Object JavaLangAccess;
    public static final Object JavaLangInvokeAccess;

    public static Object createMemberName(Class<?> refc, String name, MethodType type, byte refKind) {
        return new CallBuilder().arg(refc).arg(name).arg(type).arg(refKind).invokeDynamic("createMemberName", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/invoke/MethodType;B)Ljava/lang/Object;", "ru/DmN/uu/Unsafe", "bootstrap", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;").endA();
    }

    public static Object resolveOrFail(byte refKind, Object member, Class<?> clazz) {
        return new CallBuilder().arg(refKind).arg(member).arg(clazz).invokeDynamic("resolveOrFail", "(BLjava/lang/Object;Ljava/lang/Class;)Ljava/lang/Object;", "ru/DmN/uu/Unsafe", "bootstrap", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;").endA();
    }

    public static Object resolve(byte refKind, Object ref, Class<?> lookupClass, int allowedModes, boolean speculativeResolve) {
        return new CallBuilder().arg(refKind).arg(ref).arg(lookupClass).arg(allowedModes).arg(speculativeResolve).invokeDynamic("resolve", "(BLjava/lang/Object;Ljava/lang/Class;IZ)Ljava/lang/Object;", "ru/DmN/uu/Unsafe", "bootstrap", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;").endA();
    }

    public static void forceSetAccessible(@NotNull AccessibleObject object) {
        unsafe.putBoolean(object, 12, true);
    }

    public static @NotNull VarHandle unreflect(@NotNull Field field) {
        return (VarHandle) new CallBuilder().arg(field).invokeDynamic("unreflect", "(Ljava/lang/reflect/Field;)Ljava/lang/invoke/VarHandle;", "ru/DmN/uu/Unsafe", "bootstrap", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;").endA();
    }

    public static <O, T> T forceGetField(@NotNull Class<O> owner, @Nullable O instance, int mods, @NotNull Class<T> type) {
        for (Field field : owner.getDeclaredFields()) {
            if (field.getModifiers() == mods && field.getType() == type) {
                try {
                    forceSetAccessible(field);
                    return (T) field.get(instance);
                } catch (ReflectiveOperationException ignored) {
                }
            }
        }
        throw new RuntimeException(String.format(
                "Failed to get field from %s of type %s",
                owner.getName(),
                type.getName()
        ));
    }

    public static Object unsafeInvoke(@NotNull MethodHandle mh, Object... args) {
        return new CallBuilder().arg(mh).arg(args).invokeVirtual("invoke", "([Ljava/lang/Object;)Ljava/lang/Object;", "java/lang/invoke/MethodHandle").endA();
    }

    public static Object unsafeInvokeExact(@NotNull MethodHandle mh, Object... args) {
        return new CallBuilder().arg(mh).arg(args).invokeVirtual("invokeExact", "([Ljava/lang/Object;)Ljava/lang/Object;", "java/lang/invoke/MethodHandle").endA();
    }

    public static Object unsafeInvokeWithArguments(@NotNull MethodHandle mh, Object... args) {
        return new CallBuilder().arg(mh).arg(args).invokeVirtual("invokeWithArguments", "([Ljava/lang/Object;)Ljava/lang/Object;", "java/lang/invoke/MethodHandle").endA();
    }

    public static Object unsafeInvokeWithArguments(@NotNull MethodHandle mh, @NotNull List<?> args) {
        return new CallBuilder().arg(mh).arg(args).invokeVirtual("invokeWithArguments", "(Ljava/util/List;)Ljava/lang/Object;", "java/lang/invoke/MethodHandle").endA();
    }

    public static @NotNull CallSite bootstrap(@NotNull MethodHandles.Lookup caller, @NotNull String name, @NotNull MethodType type) throws Exception {
        return new ConstantCallSite(lookup.findStatic(accessor, name, type));
    }

    static {
        try {
            var field$unsafe = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
            field$unsafe.setAccessible(true);
            unsafe = (sun.misc.Unsafe) field$unsafe.get(null);
            lookup = forceGetField(MethodHandles.Lookup.class, null, Modifier.STATIC | Modifier.FINAL, MethodHandles.Lookup.class);
            var field$IMPL_NAMES = MethodHandles.class.getDeclaredField("IMPL_NAMES");
            forceSetAccessible(field$IMPL_NAMES);
            MethodHandles$MethodHandles = field$IMPL_NAMES.get(null);
            byte[] b;
            try (var stream = Unsafe.class.getClassLoader().getResourceAsStream("ru/DmN/uu/internal/MagicAccessor.class")) {
                b = stream.readAllBytes();
            }
            accessor = (Class<MagicAccessor>) lookup.in(Class.forName("jdk.internal.reflect.MagicAccessorImpl")).defineClass(b);
            var method$init = accessor.getMethod("init");
            forceSetAccessible(method$init);
            method$init.invoke(null);
            JavaLangAccess = new FieldBuilder("jdk/internal/reflect/DmNMagicAccessor", "JavaLangAccess", "Ljava/lang/Object;").getA();
            JavaLangInvokeAccess = new FieldBuilder("jdk/internal/reflect/DmNMagicAccessor", "JavaLangInvokeAccess", "Ljava/lang/Object;").getA();
        } catch (Throwable t) {
            BytecodeUtils.athrow(t);
            throw new Error();
        }
    }
}
