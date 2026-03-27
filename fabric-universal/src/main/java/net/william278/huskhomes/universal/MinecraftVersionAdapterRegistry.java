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

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class MinecraftVersionAdapterRegistry {

    private static final Map<String, String> ADAPTER_CLASSES = Map.of(
            "1.21.1", "net.william278.huskhomes.universal.impl.v12101.V12101MinecraftVersionAdapter",
            "1.21.8", "net.william278.huskhomes.universal.impl.v12108.V12108MinecraftVersionAdapter",
            "1.21.11", "net.william278.huskhomes.universal.impl.v12111.V12111MinecraftVersionAdapter"
    );

    private MinecraftVersionAdapterRegistry() {
    }

    @NotNull
    public static MinecraftVersionAdapter create(@NotNull String minecraftVersion) {
        final String adapterClassName = ADAPTER_CLASSES.get(minecraftVersion);
        if (adapterClassName == null) {
            throw new IllegalStateException("No universal adapter registered for Minecraft " + minecraftVersion);
        }

        try {
            final Class<?> adapterType = Class.forName(adapterClassName);
            final Object adapterInstance = adapterType.getDeclaredConstructor().newInstance();
            if (!(adapterInstance instanceof MinecraftVersionAdapter adapter)) {
                throw new IllegalStateException("Adapter does not implement MinecraftVersionAdapter: "
                        + adapterType.getName());
            }
            return adapter;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to create version adapter: " + adapterClassName, e);
        }
    }
}
