//package ru.DmN.uu;
//
//import ru.DmN.bpl.BytecodeUtils;
//import ru.DmN.bpl.CallBuilder;
//import ru.DmN.bpl.annotations.BytecodeProcessor;
//import ru.DmN.bpl.annotations.Extends;
//
//import java.lang.invoke.*;
//import java.lang.module.ModuleDescriptor;
//import java.lang.reflect.*;
//import java.net.URI;
//import java.util.Arrays;
//import java.util.concurrent.ConcurrentHashMap;
//
//import static ru.DmN.uu.BaseUnsafe.IMPL_LOOKUP;
//import static ru.DmN.uu.BaseUnsafe.unsafe;
//
//@Extends(parent = "jdk/internal/reflect/DmNMagicAccessor")
//@BytecodeProcessor
//@SuppressWarnings("unused")
//public final class Unsafe {
//    public static final long AccessibleObject$override = BaseUnsafe.AccessibleObject$override;
//    public static final long ClassLoader$parent = findParent();
//    public static final Class<?> class$MemberName;
//    public static final Class<?> class$VarHandles;
//    public static final Class<?> class$JavaLangAccess;
//    public static final Class<?> class$JavaLangInvokeAccess;
//    public static final Object JavaLangAccess;
//    public static final Object JavaLangInvokeAccess;
//
//    public static Method findMethod(Class<?> clazz, String name) {
//        return Arrays.stream(clazz.getMethods()).filter(method -> method.getName().equals(name)).findFirst().orElse(null);
//    }
//
//    public static void openModule(Module module, String packet, Module to) {
//        new CallBuilder().arg(module).arg(packet).arg(to).arg(true).arg(true).invokeDynamic("implAddExportsOrOpens", "(Ljava/lang/Module;Ljava/lang/String;Ljava/lang/Module;ZZ)V", "ru/DmN/uu/Unsafe", "bootstrap", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;I)Ljava/lang/invoke/CallSite;", 2).arg("java.lang.Module").arg(false).end();
//    }
//
//
//    public static VarHandle makeFieldHandle(Field f, Class<?> refc, Class<?> type, boolean isWriteAllowedOnFinalFields) {
//        try {
//            var c = class$MemberName.getConstructor(Field.class);
//            c.setAccessible(true);
//            var m = class$VarHandles.getDeclaredMethod("makeFieldHandle", class$MemberName, Class.class, Class.class, boolean.class);
//            m.setAccessible(true);
//            return (VarHandle) m.invoke(null, c.newInstance(f), refc, type, isWriteAllowedOnFinalFields);
//        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public static long findParent() {
//        var thisLoader = Unsafe.class.getClassLoader();
//        var parent = thisLoader.getParent();
//        for (long cookie = 0; cookie < 64; cookie += 4)
//            if (unsafe.getObject(thisLoader, cookie) == parent)
//                return cookie;
//        return -1;
//    }
//
//    public static Object unsafeInvoke(MethodHandle mh, Object... args) {
//        try {
//            return mh.invoke((Object[]) args);
//        } catch (Throwable ignored) {
//            return null;
//        }
//    }
//
//    public static ConcurrentHashMap<?, ?> createOrGetClassLoaderValueMap(ClassLoader cl) {
//        return (ConcurrentHashMap<?, ?>) new CallBuilder().arg(JavaLangAccess).arg(cl).invokeInterface("createOrGetClassLoaderValueMap", "(Ljava/lang/ClassLoader;)Ljava/util/concurrent/ConcurrentHashMap;", "jdk/internal/access/JavaLangAccess").endA();
//    }
//
//    public static Module defineModule(ClassLoader loader, ModuleDescriptor descriptor, URI uri) {
//        return (Module) new CallBuilder().arg(JavaLangAccess).arg(loader).arg(descriptor).arg(uri).invokeInterface("defineModule", "(Ljava/lang/ClassLoader;Ljava/lang/module/ModuleDescriptor;Ljava/net/URI;)Ljava/lang/Module;", "jdk/internal/access/JavaLangAccess").endA();
//    }
//
//    public static Module defineUnnamedModule(ClassLoader loader) {
//        return (Module) new CallBuilder().arg(JavaLangAccess).arg(loader).invokeInterface("defineUnnamedModule", "(Ljava/lang/ClassLoader;)Ljava/lang/Module;", "jdk/internal/access/JavaLangAccess").endA();
//    }
//
//    public static void forceSetAccessible(AccessibleObject object, boolean accessible) {
//        BaseUnsafe.forceSetAccessible(object, accessible);
//    }
//
//    public static CallSite bootstrap(MethodHandles.Lookup lookup, String name, MethodType type, String clazz, int isStatic) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException {
//        var c = Class.forName(clazz);
//        var m = isStatic == 1
//                ? IMPL_LOOKUP.findStatic(c, name, type)
//                : IMPL_LOOKUP.findVirtual(c, name, type.dropParameterTypes(0, 1));
//        return new ConstantCallSite(m);
//    }
//
//    static {
//        try {
//            var module$this = Unsafe.class.getModule();
//            var module$jbase = Object.class.getModule();
//            openModule(module$jbase, "java.lang.invoke", module$this);
//            openModule(module$jbase, "jdk.internal.access", module$this);
//
//            class$MemberName = Class.forName("java.lang.invoke.MemberName");
//            class$VarHandles = Class.forName("java.lang.invoke.VarHandles");
//
//            class$JavaLangAccess = BytecodeUtils.ldc$class("jdk/internal/access/JavaLangAccess");
//            class$JavaLangInvokeAccess = BytecodeUtils.ldc$class("jdk/internal/access/JavaLangInvokeAccess");
//
//            JavaLangAccess = new CallBuilder().invokeStatic("getJavaLangAccess", "()Ljdk/internal/access/JavaLangAccess;", "jdk/internal/access/SharedSecrets", false).endA();
//            JavaLangInvokeAccess = new CallBuilder().invokeStatic("getJavaLangInvokeAccess", "()Ljdk/internal/access/JavaLangInvokeAccess;", "jdk/internal/access/SharedSecrets", false).endA();
//        } catch (ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//    }
//}
