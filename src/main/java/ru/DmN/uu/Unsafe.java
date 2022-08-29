package ru.DmN.uu;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import ru.DmN.bpl.BytecodeUtils;
import ru.DmN.bpl.CallBuilder;
import ru.DmN.bpl.ClassProcessor;
import ru.DmN.bpl.annotations.BytecodeProcessor;
import ru.DmN.bpl.annotations.FMRename;
import ru.DmN.uu.internal.MagicAccessor;

import java.lang.invoke.*;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@BytecodeProcessor
public class Unsafe {
    public static final sun.misc.Unsafe unsafe;
    public static final Class<MagicAccessor> accessor;
    public static final MethodHandles.Lookup lookup;
    public static final Object MethodHandles$MethodHandles;
//    public static final Object JavaLangAccess = new CallBuilder().invokeStatic("getJavaLangAccess", "()Ljdk/internal/access/JavaLangAccess;", "jdk/internal/access/SharedSecrets", false).endA();
//    public static final Object JavaLangInvokeAccess = new CallBuilder().invokeStatic("getJavaLangInvokeAccess", "()Ljdk/internal/access/JavaLangInvokeAccess;", "jdk/internal/access/SharedSecrets", false).endA();

    public static Object createMemberName(Class<?> refc, String name, MethodType type, byte refKind) {
        return new CallBuilder().arg(refc).arg(name).arg(type).arg(refKind).invokeDynamic("createMemberName", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/invoke/MethodType;B)Ljava/lang/Object;", "ru/DmN/uu/Unsafe", "bootstrap", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;").endA();
    }

    public static Object resolveOrFail(byte refKind, Object member, Class<?> clazz) {
        return new CallBuilder().arg(refKind).arg(member).arg(clazz).invokeDynamic("resolveOrFail", "(BLjava/lang/Object;Ljava/lang/Class;)Ljava/lang/Object;", "ru/DmN/uu/Unsafe", "bootstrap", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;").endA();
    }

    public static Object resolve(byte refKind, Object ref, Class<?> lookupClass, int allowedModes, boolean speculativeResolve) {
        return new CallBuilder().arg(refKind).arg(ref).arg(lookupClass).arg(allowedModes).arg(speculativeResolve).invokeDynamic("resolve", "(BLjava/lang/Object;Ljava/lang/Class;IZ)Ljava/lang/Object;", "ru/DmN/uu/Unsafe", "bootstrap", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;").endA();
    }

    public static void forceSetAccessible(AccessibleObject object) {
        unsafe.putBoolean(object, 12, true);
    }

    public static VarHandle unreflect(Field field) {
        return (VarHandle) new CallBuilder().arg(field).invokeDynamic("unreflect", "(Ljava/lang/reflect/Field;)Ljava/lang/invoke/VarHandle;", "ru/DmN/uu/Unsafe", "bootstrap", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;").endA();
    }

    public static <O, T> T forceGetField(Class<O> owner, O instance, int mods, Class<T> type) {
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

    public static CallSite bootstrap(MethodHandles.Lookup caller, String name, MethodType type) throws Exception {
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
//            var method$addExports0 = Module.class.getDeclaredMethod("addExports0", Module.class, String.class, Module.class);
//            forceSetAccessible(method$addExports0);
//            method$addExports0.invoke(null, Class.forName("java.lang.invoke.MemberName").getModule(), "")
            var node = new ClassProcessor();
            try (var stream = Unsafe.class.getClassLoader().getResourceAsStream("ru/DmN/uu/internal/MagicAccessor.class")) {
                new ClassReader(stream.readAllBytes()).accept(node, 0);
            }
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            node.accept(writer);
            var b = writer.toByteArray();
            accessor = (Class<MagicAccessor>) lookup.in(Class.forName("jdk.internal.reflect.MagicAccessorImpl")).defineClass(b);
            var method$init = accessor.getMethod("init");
            forceSetAccessible(method$init);
            method$init.invoke(null);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
}
