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

package net.william278.huskhomes.universal.impl.v12108;

import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.william278.huskhomes.universal.MinecraftVersionAdapter;
import net.william278.huskhomes.universal.PlayerPosition;
import net.william278.huskhomes.universal.RespawnPosition;
import net.william278.huskhomes.universal.SpawnPosition;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class V12108MinecraftVersionAdapter implements MinecraftVersionAdapter {

    @Override
    @NotNull
    public String minecraftVersion() {
        return "1.21.8";
    }

    @Override
    @NotNull
    public String describe() {
        return "Universal adapter for Minecraft 1.21.8 using mixed respawn and world access APIs";
    }

    @Override
    public void setWorldSpawn(@NotNull Object serverWorld, @NotNull SpawnPosition spawnPosition) {
        final ServerWorld world = (ServerWorld) serverWorld;
        world.setSpawnPos(
                BlockPos.ofFloored(spawnPosition.x(), spawnPosition.y(), spawnPosition.z()),
                spawnPosition.yaw()
        );
    }

    @Override
    @NotNull
    public String getPlayerName(@NotNull Object player) {
        return ((ServerPlayerEntity) player).getGameProfile().getName();
    }

    @Override
    @NotNull
    public PlayerPosition getPlayerPosition(@NotNull Object player) {
        final ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
        final Vec3d position = serverPlayer.getPos();
        return new PlayerPosition(position.x, position.y, position.z, serverPlayer.getYaw(), serverPlayer.getPitch());
    }

    @Override
    @NotNull
    public Optional<RespawnPosition> getRespawnPosition(@NotNull Object player) {
        final ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
        if (serverPlayer.getRespawn() == null) {
            return Optional.empty();
        }

        final BlockPos spawn = serverPlayer.getRespawn().pos();
        final RegistryKey<net.minecraft.world.World> world = serverPlayer.getRespawn().dimension();
        if (spawn == null || world == null) {
            return Optional.empty();
        }

        return Optional.of(new RespawnPosition(
                spawn.getX(),
                spawn.getY(),
                spawn.getZ(),
                serverPlayer.getRespawn().angle(),
                world.getValue().toString()
        ));
    }
}
