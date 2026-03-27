/*
 * This file is part of HuskHomes, licensed under the Apache License 2.0.
 *
 *  Copyright (c) William278 <will27528@gmail.com>
 *  Copyright (c) contributors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package net.william278.huskhomes.universal.runtime;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class EmbeddedLegacyRuntimeLauncher {

    private static final String ENTRYPOINT_CLASS = "net.william278.huskhomes.FabricHuskHomes";
    private static final Map<String, URLClassLoader> ACTIVE_CLASSLOADERS = new ConcurrentHashMap<>();

    private EmbeddedLegacyRuntimeLauncher() {
    }

    public static synchronized void launch(@NotNull String minecraftVersion, @NotNull String embeddedRuntimeJarPath) {
        URLClassLoader classLoader = null;
        try {
            if (ACTIVE_CLASSLOADERS.containsKey(minecraftVersion)) {
                return;
            }
            final ExtractedImplementation extracted = extractEmbeddedImplementation(minecraftVersion, embeddedRuntimeJarPath);
            classLoader = new URLClassLoader(
                    extracted.classpathUrls(),
                    EmbeddedLegacyRuntimeLauncher.class.getClassLoader()
            );
            ACTIVE_CLASSLOADERS.put(minecraftVersion, classLoader);
            final Thread thread = Thread.currentThread();
            final ClassLoader previousContextClassLoader = thread.getContextClassLoader();
            try {
                thread.setContextClassLoader(classLoader);
                final Class<?> delegateType = Class.forName(ENTRYPOINT_CLASS, true, classLoader);
                final Object delegateInstance = delegateType.getDeclaredConstructor().newInstance();
                if (!(delegateInstance instanceof DedicatedServerModInitializer initializer)) {
                    throw new IllegalStateException("Entrypoint does not implement DedicatedServerModInitializer: "
                            + delegateType.getName());
                }
                initializer.onInitializeServer();
            } finally {
                thread.setContextClassLoader(previousContextClassLoader);
            }
        } catch (ReflectiveOperationException | IOException e) {
            if (classLoader != null) {
                ACTIVE_CLASSLOADERS.remove(minecraftVersion, classLoader);
                try {
                    classLoader.close();
                } catch (IOException ignored) {
                }
            }
            throw new RuntimeException("Failed to launch embedded legacy Fabric runtime for " + minecraftVersion, e);
        }
    }

    @NotNull
    private static ExtractedImplementation extractEmbeddedImplementation(@NotNull String minecraftVersion,
                                                                        @NotNull String implementationJar)
            throws IOException {
        final Path cacheDirectory = FabricLoader.getInstance()
                .getGameDir()
                .resolve(".huskhomes")
                .resolve("universal-runtime")
                .resolve(minecraftVersion);
        Files.createDirectories(cacheDirectory);

        final Path extractedJar = cacheDirectory.resolve("huskhomes-runtime-" + minecraftVersion + ".jar");
        try (InputStream inputStream = EmbeddedLegacyRuntimeLauncher.class.getClassLoader()
                .getResourceAsStream(implementationJar)) {
            if (inputStream == null) {
                throw new IOException("Missing embedded runtime jar: " + implementationJar);
            }
            Files.copy(inputStream, extractedJar, StandardCopyOption.REPLACE_EXISTING);
        }

        final Path nestedJarDirectory = cacheDirectory.resolve("nested");
        Files.createDirectories(nestedJarDirectory);
        final Set<Path> classpathEntries = new LinkedHashSet<>();
        collectClasspathEntries(extractedJar, nestedJarDirectory, classpathEntries);
        final List<URL> classpathUrls = new ArrayList<>(classpathEntries.size());
        for (Path classpathEntry : classpathEntries) {
            classpathUrls.add(classpathEntry.toUri().toURL());
        }
        return new ExtractedImplementation(classpathUrls.toArray(URL[]::new));
    }

    private static void collectClasspathEntries(@NotNull Path jarPath,
                                                @NotNull Path extractionDirectory,
                                                @NotNull Set<Path> classpathEntries) throws IOException {
        if (!classpathEntries.add(jarPath)) {
            return;
        }

        try (JarFile jarFile = new JarFile(jarPath.toFile())) {
            final var entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                final JarEntry entry = entries.nextElement();
                if (entry.isDirectory() || !entry.getName().startsWith("META-INF/jars/") || !entry.getName().endsWith(".jar")) {
                    continue;
                }

                final String nestedJarName = entry.getName().substring("META-INF/jars/".length());
                final Path nestedJarPath = extractionDirectory.resolve(nestedJarName);
                final Path nestedParent = nestedJarPath.getParent();
                if (nestedParent != null) {
                    Files.createDirectories(nestedParent);
                }
                try (InputStream nestedJarInputStream = jarFile.getInputStream(entry)) {
                    Files.copy(nestedJarInputStream, nestedJarPath, StandardCopyOption.REPLACE_EXISTING);
                }
                collectClasspathEntries(nestedJarPath, extractionDirectory, classpathEntries);
            }
        }
    }

    private record ExtractedImplementation(URL[] classpathUrls) {
    }
}
