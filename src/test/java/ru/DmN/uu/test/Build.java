package ru.DmN.uu.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import ru.DmN.bpl.ClassProcessor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

public class Build {
    public static final String UNSAFE_FILE = "ru/DmN/uu/Unsafe.class";
    public static final String MAGICACCESSOR_FILE = "ru/DmN/uu/internal/MagicAccessor.class";
    public static final String version;
    public static final String OUTPUT_FILE;

    static {
        var properties = new Properties();
        try (var stream = new FileInputStream("build.properties")) {
            properties.load(stream);
            version = (String) properties.get("version");
            OUTPUT_FILE = "out/DmNUnsafeUtils-" + version + ".jar";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void runBuild() {
        try {
            new Build().build();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    @Test
    public void build() throws Throwable {
        var dir = new File("out");
        if (dir.exists())
            Arrays.stream(dir.listFiles()).forEach(File::delete);
        else
            Assertions.assertTrue(dir.mkdir());

        var properties = new Properties();
        try (var stream = new FileInputStream("build.properties")) {
            properties.load(stream);
        }

        try (var jar = new JarOutputStream(new FileOutputStream(OUTPUT_FILE))) {
            jar.putNextEntry(new JarEntry(UNSAFE_FILE));
            jar.write(dumpUnsafe());
            jar.closeEntry();
            //
            jar.putNextEntry(new JarEntry(MAGICACCESSOR_FILE));
            jar.write(dumpMagicAccessor());
            jar.closeEntry();
            //
            jar.putNextEntry(new JarEntry("META-INF/MANIFEST.MF"));
            jar.write(("Manifest-Version: 1.0\n").getBytes(StandardCharsets.UTF_8));
            jar.closeEntry();
        }
    }

    public static byte[] dumpUnsafe() throws IOException, ClassNotFoundException {
        try (var stream = Build.class.getClassLoader().getResourceAsStream(UNSAFE_FILE)) {
            if (stream == null)
                throw new ClassNotFoundException("ru.DmN.uu.Unsafe");
            var node = new ClassProcessor();
            new ClassReader(stream.readAllBytes()).accept(node, 0);
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            node.accept(writer);
            return writer.toByteArray();
        }
    }

    public static byte[] dumpMagicAccessor() throws IOException, ClassNotFoundException {
        try (var stream = Build.class.getClassLoader().getResourceAsStream(MAGICACCESSOR_FILE)) {
            if (stream == null)
                throw new ClassNotFoundException("ru.DmN.uu.internal.MagicAccessor");
            var node = new ClassProcessor();
            new ClassReader(stream.readAllBytes()).accept(node, 0);
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            node.accept(writer);
            return writer.toByteArray();
        }
    }
}
