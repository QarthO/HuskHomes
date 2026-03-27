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

public final class UniversalVersionImplementationRegistry {

    private static final Map<String, String> IMPLEMENTATION_CLASSES = Map.of(
            "1.21.1", "net.william278.huskhomes.universal.impl.v12101.V12101UniversalVersionImplementation",
            "1.21.8", "net.william278.huskhomes.universal.impl.v12108.V12108UniversalVersionImplementation",
            "1.21.11", "net.william278.huskhomes.universal.impl.v12111.V12111UniversalVersionImplementation"
    );

    private UniversalVersionImplementationRegistry() {
    }

    @NotNull
    public static UniversalVersionImplementation create(@NotNull String minecraftVersion) {
        final String implementationClassName = IMPLEMENTATION_CLASSES.get(minecraftVersion);
        if (implementationClassName == null) {
            throw new IllegalStateException("No universal implementation registered for Minecraft " + minecraftVersion);
        }

        try {
            final Class<?> implementationType = Class.forName(implementationClassName);
            final Object implementationInstance = implementationType.getDeclaredConstructor().newInstance();
            if (!(implementationInstance instanceof UniversalVersionImplementation implementation)) {
                throw new IllegalStateException("Implementation does not implement UniversalVersionImplementation: "
                        + implementationType.getName());
            }
            return implementation;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to create universal implementation: " + implementationClassName, e);
        }
    }
}
