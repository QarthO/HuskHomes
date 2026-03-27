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

package net.william278.huskhomes.universal;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class UniversalFabricBootstrap implements DedicatedServerModInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger("HuskHomes");
    private static final Pattern MINECRAFT_VERSION_PATTERN = Pattern.compile("(\\d+\\.\\d+(?:\\.\\d+)?)");

    @Override
    public void onInitializeServer() {
        try {
            final String minecraftVersion = detectMinecraftVersion();
            final String implementationVersion = normalizeMinecraftVersion(minecraftVersion);
            final MinecraftVersionAdapter adapter = MinecraftVersionAdapterRegistry.create(implementationVersion);
            final UniversalVersionImplementation implementation =
                    UniversalVersionImplementationRegistry.create(implementationVersion);

            UniversalRuntimeContext.initialize(implementationVersion, adapter);
            LOGGER.info("Selected universal adapter: {}", adapter.describe());
            LOGGER.info("Selected universal implementation for Minecraft {}", implementationVersion);
            implementation.initialize();
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to bootstrap universal HuskHomes Fabric", e);
        }
    }

    @NotNull
    private String detectMinecraftVersion() {
        return FabricLoader.getInstance()
                .getModContainer("minecraft")
                .map(modContainer -> modContainer.getMetadata().getVersion().getFriendlyString())
                .orElseThrow(() -> new IllegalStateException("Failed to detect the Minecraft version"));
    }

    @NotNull
    private String normalizeMinecraftVersion(@NotNull String minecraftVersion) {
        final Matcher matcher = MINECRAFT_VERSION_PATTERN.matcher(minecraftVersion);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return minecraftVersion.trim();
    }
}
