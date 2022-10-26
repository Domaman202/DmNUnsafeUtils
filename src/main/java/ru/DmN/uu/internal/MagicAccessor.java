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
    public static final Object JavaLangAccess = new CallBuilder().invokeStatic("getJavaLangAccess", "()Ljdk/internal/access/JavaLangAccess;", "jdk/internal/access/SharedSecrets", false).endA();
    public static final Object JavaLangInvokeAccess = new CallBuilder().invokeStatic("getJavaLangInvokeAccess", "()Ljdk/internal/access/JavaLangInvokeAccess;", "jdk/internal/access/SharedSecrets", false).endA();


    public static void init() throws NoSuchFieldException {
        new CallBuilder().arg(BytecodeUtils.ldc$class("jdk/internal/reflect/DmNMagicAccessor").getModule()).arg("jdk.internal.reflect").invokeStatic("addExportsToAllUnnamed0", "(Ljava/lang/Module;Ljava/lang/String;)V", "java/lang/Module", false).end();
    }

    public static Object createMemberName(Class<?> refc, String name, MethodType type, byte refKind) {
        return new CallBuilder().alloc("java/lang/invoke/MemberName").arg(refc).arg(name).arg(type).arg(refKind).invokeSpecial("<init>", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/invoke/MethodType;B)V", "java/lang/invoke/MemberName", false).endA();
    }

    public static Object resolveOrFail(byte refKind, Object member, Class<?> clazz) {
        return new CallBuilder().arg(IMPL_NAMES).arg(refKind).arg(BytecodeUtils.checkcast(member, "java/lang/invoke/MemberName")).arg(clazz).arg(MethodHandles$ALL).arg(NoSuchMethodException.class).invokeVirtual("resolveOrFail", "(BLjava/lang/invoke/MemberName;Ljava/lang/Class;ILjava/lang/Class;)Ljava/lang/invoke/MemberName;", "java/lang/invoke/MemberName$Factory").endA();
    }

    public static Object resolve(byte refKind, Object ref, Class<?> lookupClass, int allowedModes, boolean speculativeResolve) {
        return new CallBuilder().arg(IMPL_NAMES).arg(refKind).arg(BytecodeUtils.checkcast(ref, "java/lang/invoke/MemberName")).arg(lookupClass).arg(allowedModes).arg(speculativeResolve).invokeVirtual("resolve", "(BLjava/lang/invoke/MemberName;Ljava/lang/Class;IZ)Ljava/lang/invoke/MemberName;", "java/lang/invoke/MemberName$Factory").endA();
    }

    public static VarHandle makeFieldHandle(Field field, Class<?> refc, Class<?> type) {
        var member = new CallBuilder().alloc("java/lang/invoke/MemberName").arg(field).invokeSpecial("<init>", "(Ljava/lang/reflect/Field;)V", "java/lang/invoke/MemberName", false).endA();
        return (VarHandle) new CallBuilder().arg(member).arg(refc).arg(type).arg(true).invokeStatic("makeFieldHandle", "(Ljava/lang/invoke/MemberName;Ljava/lang/Class;Ljava/lang/Class;Z)Ljava/lang/invoke/VarHandle;", "java/lang/invoke/VarHandles", false).endA();
    }

    public static VarHandle unreflect(Field field) {
        return makeFieldHandle(field, field.getDeclaringClass(), field.getType());
    }
}
