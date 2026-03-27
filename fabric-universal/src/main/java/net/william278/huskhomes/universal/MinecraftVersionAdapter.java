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

import java.util.Optional;

public interface MinecraftVersionAdapter {

    @NotNull
    String minecraftVersion();

    @NotNull
    String describe();

    void setWorldSpawn(@NotNull Object serverWorld, @NotNull SpawnPosition spawnPosition);

    @NotNull
    String getPlayerName(@NotNull Object player);

    @NotNull
    PlayerPosition getPlayerPosition(@NotNull Object player);

    @NotNull
    Optional<RespawnPosition> getRespawnPosition(@NotNull Object player);
}
