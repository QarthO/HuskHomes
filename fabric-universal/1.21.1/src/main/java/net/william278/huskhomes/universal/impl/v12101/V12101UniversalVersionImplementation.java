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

package net.william278.huskhomes.universal.impl.v12101;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.william278.huskhomes.universal.UniversalImplementationContext;
import net.william278.huskhomes.universal.UniversalRuntimeContext;
import net.william278.huskhomes.universal.UniversalVersionImplementation;
import net.william278.huskhomes.universal.runtime.EmbeddedLegacyRuntimeLauncher;

public final class V12101UniversalVersionImplementation implements UniversalVersionImplementation {

    @Override
    public void initialize() {
        UniversalImplementationContext.logger().info(
                "Initializing non-nested universal Fabric runtime scaffold for Minecraft {}",
                UniversalRuntimeContext.getMinecraftVersion()
        );
        ServerLifecycleEvents.SERVER_STARTING.register(server -> UniversalImplementationContext.logger().info(
                "Universal Fabric 1.21.1 scaffold attached to {}",
                server.getVersion()
        ));
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> UniversalImplementationContext.logger().info(
                "Universal Fabric 1.21.1 scaffold stopping"
        ));
        EmbeddedLegacyRuntimeLauncher.launch(
                "1.21.1",
                "META-INF/huskhomes/runtime/runtime-1.21.1.jar"
        );
    }
}
