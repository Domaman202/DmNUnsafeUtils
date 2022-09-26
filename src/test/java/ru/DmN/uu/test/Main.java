package ru.DmN.uu.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import ru.DmN.bpl.ClassProcessor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

public class Main {
    public static final String UNSAFE_FILE = "ru/DmN/uu/Unsafe.class";
    public static final String MAGICACCESSOR_FILE = "ru/DmN/uu/internal/MagicAccessor.class";

    @Test
    public void main() throws Throwable {

        var loader = Main.class.getClassLoader();

        var dir = new File("out");
        if (dir.exists())
            Arrays.stream(dir.listFiles()).forEach(File::delete);
        else
            Assertions.assertTrue(dir.mkdir());

        var properties = new Properties();
        try (var stream = new FileInputStream("build.properties")) {
            properties.load(stream);
        }

        try (var jar = new JarOutputStream(new FileOutputStream("out/DmNUnsafeUtils-" + properties.getProperty("version") + ".jar"))) {
            jar.putNextEntry(new JarEntry(UNSAFE_FILE));
            try (var stream = loader.getResourceAsStream(UNSAFE_FILE)) {
                if (stream == null)
                    throw new ClassNotFoundException("ru.DmN.uu.Unsafe");
                var node = new ClassProcessor();
                new ClassReader(stream.readAllBytes()).accept(node, 0);
                ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
                node.accept(writer);
                jar.write(writer.toByteArray());
            }
            jar.closeEntry();
            //
            jar.putNextEntry(new JarEntry(MAGICACCESSOR_FILE));
            try (var stream = loader.getResourceAsStream(MAGICACCESSOR_FILE)) {
                if (stream == null)
                    throw new ClassNotFoundException("ru.DmN.uu.internal.MagicAccessor");
                var node = new ClassProcessor();
                new ClassReader(stream.readAllBytes()).accept(node, 0);
                ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
                node.accept(writer);
                jar.write(writer.toByteArray());
            }
            jar.closeEntry();
            //
            jar.putNextEntry(new JarEntry("META-INF/MANIFEST.MF"));
            jar.write(("Manifest-Version: 1.0\n").getBytes(StandardCharsets.UTF_8));
            jar.closeEntry();
            //
            try (var stream = new FileInputStream("build/classes/java/main/module-info.class")) {
                jar.putNextEntry(new JarEntry("module-info.class"));
                jar.write(stream.readAllBytes());
                jar.closeEntry();
            }
        }
    }
}
