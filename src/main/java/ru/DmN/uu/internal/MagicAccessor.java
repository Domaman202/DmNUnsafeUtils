package ru.DmN.uu.internal;

import ru.DmN.bpl.BytecodeUtils;
import ru.DmN.bpl.CallBuilder;
import ru.DmN.bpl.FieldBuilder;
import ru.DmN.bpl.annotations.BytecodeProcessor;
import ru.DmN.bpl.annotations.Extends;
import ru.DmN.bpl.annotations.TRename;

import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;

@BytecodeProcessor
@Extends(extend = "jdk/internal/reflect/MagicAccessorImpl")
@TRename("jdk/internal/reflect/DmNMagicAccessor")
public class MagicAccessor {
    public static final int MethodHandles$ALL = new FieldBuilder("java/lang/invoke/MethodHandles$Lookup", "ALL_MODES", "I").getI();
    public static final Object IMPL_NAMES = new FieldBuilder("java/lang/invoke/MethodHandles", "IMPL_NAMES", "Ljava/lang/invoke/MemberName$Factory;").getA();
    public static final Object JavaLangAccess = new CallBuilder("getJavaLangAccess", "()Ljdk/internal/access/JavaLangAccess;", "jdk/internal/access/SharedSecrets").invokeStatic(false).endA();
    public static final Object JavaLangInvokeAccess = new CallBuilder("getJavaLangInvokeAccess", "()Ljdk/internal/access/JavaLangInvokeAccess;", "jdk/internal/access/SharedSecrets").invokeStatic(false).endA();


    public static void init() {
        new CallBuilder("addExportsToAllUnnamed0", "(Ljava/lang/Module;Ljava/lang/String;)V", "java/lang/Module").arg(BytecodeUtils.ldc$class("jdk/internal/reflect/DmNMagicAccessor").getModule()).arg("jdk.internal.reflect").invokeStatic(false).end();
    }

    public static Object createMemberName(Class<?> refc, String name, MethodType type, byte refKind) {
        return new CallBuilder("<init>", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/invoke/MethodType;B)V", "java/lang/invoke/MemberName").alloc().arg(refc).arg(name).arg(type).arg(refKind).invokeSpecial(false).endA();
    }

    public static Object resolveOrFail(byte refKind, Object member, Class<?> clazz) {
        return new CallBuilder("resolveOrFail", "(BLjava/lang/invoke/MemberName;Ljava/lang/Class;ILjava/lang/Class;)Ljava/lang/invoke/MemberName;", "java/lang/invoke/MemberName$Factory").arg(IMPL_NAMES).arg(refKind).arg(BytecodeUtils.checkcast(member, "java/lang/invoke/MemberName")).arg(clazz).arg(MethodHandles$ALL).arg(NoSuchMethodException.class).invokeVirtual().endA();
    }

    public static Object resolve(byte refKind, Object ref, Class<?> lookupClass, int allowedModes, boolean speculativeResolve) {
        return new CallBuilder("resolve", "(BLjava/lang/invoke/MemberName;Ljava/lang/Class;IZ)Ljava/lang/invoke/MemberName;", "java/lang/invoke/MemberName$Factory").arg(IMPL_NAMES).arg(refKind).arg(BytecodeUtils.checkcast(ref, "java/lang/invoke/MemberName")).arg(lookupClass).arg(allowedModes).arg(speculativeResolve).invokeVirtual().endA();
    }

    public static VarHandle makeFieldHandle(Field field, Class<?> refc, Class<?> type) {
        var member = new CallBuilder("<init>", "(Ljava/lang/reflect/Field;)V", "java/lang/invoke/MemberName").alloc().arg(field).invokeSpecial(false).endA();
        return (VarHandle) new CallBuilder("makeFieldHandle", "(Ljava/lang/invoke/MemberName;Ljava/lang/Class;Ljava/lang/Class;Z)Ljava/lang/invoke/VarHandle;", "java/lang/invoke/VarHandles").arg(member).arg(refc).arg(type).arg(true).invokeStatic(false).endA();
    }

    public static VarHandle unreflect(Field field) {
        return makeFieldHandle(field, field.getDeclaringClass(), field.getType());
    }
}
