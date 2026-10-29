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

package net.william278.huskhomes.user;

import com.pokeskies.fabricpluginmessaging.PluginMessagePacket;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.util.TriState;
import net.kyori.adventure.audience.Audience;
//? if >=26 {
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.world.entity.Entity;
//?} elif >=1.21.8 {
/*import net.minecraft.command.permission.Permission;
import net.minecraft.command.permission.PermissionLevel;
import net.minecraft.entity.Entity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
*///?} else {
/*import net.minecraft.entity.Entity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
*///?}
import net.minecraft.server.MinecraftServer;
import net.william278.huskhomes.FabricHuskHomes;
import net.william278.huskhomes.position.Location;
import net.william278.huskhomes.position.Position;
import net.william278.huskhomes.position.World;
import net.william278.huskhomes.teleport.TeleportationException;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class FabricUser extends OnlineUser {

    private final String invulnerableTag = plugin.getKey("invulnerable").asString();
    private final
            //? if >=26 {
            ServerPlayer
            //?} else {
            /*ServerPlayerEntity
            *///?}
            player;

    private FabricUser(@NotNull
            //? if >=26 {
            ServerPlayer
            //?} else {
            /*ServerPlayerEntity
            *///?}
            player, @NotNull FabricHuskHomes plugin) {
        //? if >=26 {
        super(player.getUUID(), player.getGameProfile().name(), plugin);
        //?} elif >=1.21.8 {
        /*super(player.getUuid(), player.getGameProfile().name(), plugin);
        *///?} else {
        /*super(player.getUuid(), player.getGameProfile().getName(), plugin);
        *///?}
        this.player = player;
    }

    @NotNull
    @ApiStatus.Internal
    public static FabricUser adapt(@NotNull
            //? if >=26 {
            ServerPlayer
            //?} else {
            /*ServerPlayerEntity
            *///?}
            player, @NotNull FabricHuskHomes plugin) {
        return new FabricUser(player, plugin);
    }

    @Override
    public Position getPosition() {
        return FabricHuskHomes.Adapter.adapt(
                //? if >=26 {
                player.position(),
                //?} elif >=1.21.8 {
                /*player.getEntityPos(),
                *///?} else {
                /*player.getPos(),
                *///?}
                getServerWorld(player),
                //? if >=26 {
                player.getYRot(), player.getXRot(),
                //?} else {
                /*player.getYaw(), player.getPitch(),
                *///?}
                plugin.getServerName()
        );
    }

    @NotNull
    private
            //? if >=26 {
            ServerLevel
            //?} else {
            /*ServerWorld
            *///?}
            getServerWorld(@NotNull
                    //? if >=26 {
                    ServerPlayer
                    //?} else {
                    /*ServerPlayerEntity
                    *///?}
                    player) {
        //? if >=26 {
        return player.level();
        //?} elif <=1.21.5 {
        /*return player.getServerWorld();
        *///?} elif >=1.21.8 {
        /*return player.getEntityWorld();
        *///?} else {
        /*return player.getWorld();
        *///?}
    }

    @Override
    public Optional<Position> getBedSpawnPosition() {
        //? if >=26 {
        final var respawnConfig = player.getRespawnConfig();
        if (respawnConfig == null) {
            return Optional.empty();
        }

        final BlockPos spawn = respawnConfig.respawnData().pos();
        final float angle = respawnConfig.respawnData().yaw();
        final ResourceKey<net.minecraft.world.level.Level> world = respawnConfig.respawnData().dimension();
        //?} elif <=1.21.4 {
        /*final BlockPos spawn = player.getSpawnPointPosition();
        final float angle = player.getSpawnAngle();
        final RegistryKey<net.minecraft.world.World> world = player.getSpawnPointDimension();
        *///?} elif >=1.21.8 {
        /*final BlockPos spawn = player.getRespawn() == null ? null : player.getRespawn().respawnData().getPos();
        final float angle = player.getRespawn() == null ? 0 : player.getRespawn().respawnData().yaw();
        final RegistryKey<net.minecraft.world.World> world = player.getRespawn() == null ? null : player.getRespawn().respawnData().getDimension();
        *///?} else {
        /*final BlockPos spawn = player.getRespawn() == null ? null : player.getRespawn().pos();
        final float angle = player.getRespawn() == null ? 0 : player.getRespawn().angle();
        final RegistryKey<net.minecraft.world.World> world = player.getRespawn() == null ? null : player.getRespawn().dimension();
        *///?}
        if (spawn == null || world == null) {
            return Optional.empty();
        }

        return Optional.of(Position.at(
                spawn.getX(), spawn.getY(), spawn.getZ(),
                angle, 0,
                World.from(
                        //? if >=26 {
                        world.identifier().asString(),
                        UUID.nameUUIDFromBytes(world.identifier().asString().getBytes())
                        //?} else {
                        /*world.getValue().asString(),
                        UUID.nameUUIDFromBytes(world.getValue().asString().getBytes())
                        *///?}
                ),
                plugin.getServerName()
        ));
    }

    @Override
    public double getHealth() {
        return player.getHealth();
    }

    @Override
    public boolean isPermissionSet(@NotNull String permission) {
        return Permissions.getPermissionValue(player, permission) != TriState.DEFAULT;
    }

    @Override
    public boolean hasPermission(@NotNull String node) {
        final boolean op = Boolean.TRUE.equals(((FabricHuskHomes) plugin).getPermissions().getOrDefault(node, true));
        //? if >=26 {
        final boolean hasPermission = player.permissions()
                .hasPermission(new Permission.HasCommandLevel(PermissionLevel.byId(3)));
        //?} elif >=1.21.8 {
        /*boolean hasPermission = player.getPermissions().hasPermission(new Permission.Level(PermissionLevel.fromLevel(3)));
        *///?} else {
        /*boolean hasPermission = player.hasPermissionLevel(3);
        *///?}
        return Permissions.check(player, node, !op || hasPermission);
    }

    @Override
    @NotNull
    public Map<String, Boolean> getPermissions() {
        return ((FabricHuskHomes) plugin).getPermissions().entrySet().stream()
                .filter(entry -> Permissions.check(player, entry.getKey(), entry.getValue()))
                .collect(HashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), HashMap::putAll);
    }

    @Override
    @NotNull
    protected List<Integer> getNumericalPermissions(@NotNull String nodePrefix) {
        final List<Integer> permissions = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            if (hasPermission(nodePrefix + i)) {
                permissions.add(i);
            }
        }
        return permissions.stream().sorted(Collections.reverseOrder()).toList();
    }

    @Override
    @NotNull
    public Audience getAudience() {
        return player;
    }

    @Override
    public CompletableFuture<Void> dismount() {
        final CompletableFuture<Void> future = new CompletableFuture<>();
        plugin.runSync(() -> {
            player.stopRiding();
            //? if >=26 {
            player.getPassengers().forEach(Entity::stopRiding);
            //?} else {
            /*player.getPassengerList().forEach(Entity::stopRiding);
            *///?}
            future.complete(null);
        }, this);
        return future;
    }

    @Override
    public void teleportLocally(@NotNull Location location, boolean async) throws TeleportationException {
        //? if >=26 {
        final MinecraftServer server = player.level().getServer();
        //?} elif >=1.21.8 {
        /*final MinecraftServer server = player.getEntityWorld().getServer();
        *///?} else {
        /*final MinecraftServer server = player.getServer();
        *///?}

        if (server == null) {
            throw new TeleportationException(TeleportationException.Type.ILLEGAL_TARGET_COORDINATES, plugin);
        }
        final
                //? if >=26 {
                ServerLevel
                //?} else {
                /*ServerWorld
                *///?}
                world = FabricHuskHomes.Adapter.adapt(location.getWorld(), server);
        if (world == null) {
            throw new TeleportationException(TeleportationException.Type.WORLD_NOT_FOUND, plugin);
        }

        // Synchronously teleport
        plugin.runSync(() -> {
            player.stopRiding();
            //? if >=26 {
            player.getPassengers().forEach(Entity::stopRiding);
            //?} else {
            /*player.getPassengerList().forEach(Entity::stopRiding);
            *///?}
            player.fallDistance = 0f;
            //? if >=26 {
            player.teleportTo(
                    world,
                    location.getX(),
                    location.getY(),
                    location.getZ(),
                    Set.of(),
                    location.getYaw(),
                    location.getPitch(),
                    true
            );
            //?} elif >=1.21.4 {
            /*player.teleport(
                    world, location.getX(), location.getY(), location.getZ(),
                    Set.of(),
                    location.getYaw(), location.getPitch(),
                    true
            );
            *///?} else {
            /*player.teleport(
                    world, location.getX(), location.getY(), location.getZ(),
                    location.getYaw(), location.getPitch()
            );
            *///?}
        }, this);
    }

    @Override
    public void sendPluginMessage(byte[] message) {
        ServerPlayNetworking.send(player, new PluginMessagePacket(message));
    }

    @Override
    public boolean isMoving() {
        //? if >=26 {
        return player.isInWater() || player.isFallFlying() || player.isSprinting() || player.isShiftKeyDown();
        //?} elif >=1.21.3 {
        /*return player.isTouchingWater() || player.isGliding() || player.isSprinting() || player.isSneaking();
        *///?} else {
        /*return player.isTouchingWater() || player.isSprinting() || player.isSneaking();
        *///?}
    }

    @Override
    public boolean isVanished() {
        return false;
    }

    @Override
    public boolean hasInvulnerability() {
        //? if >=26 {
        return markedAsInvulnerable || player.entityTags().contains(invulnerableTag);
        //?} else {
        /*return markedAsInvulnerable || player.getCommandTags().contains(invulnerableTag);
        *///?}
    }

    @Override
    public void handleInvulnerability() {
        final long invulnerableTicks = 20L * plugin.getSettings().getGeneral().getTeleportInvulnerabilityTime();
        if (invulnerableTicks <= 0) {
            return;
        }
        //? if >=26.3 {
        /*player.setPermanentlyInvulnerable(true);
        *///?} else {
        player.setInvulnerable(true);
        //?}
        //? if >=26 {
        player.addTag(invulnerableTag);
        //?} else {
        /*player.getCommandTags().add(invulnerableTag);
        *///?}
        markedAsInvulnerable = true;
        plugin.runSyncDelayed(this::removeInvulnerabilityIfPermitted, this, invulnerableTicks);
    }

    @Override
    public void removeInvulnerabilityIfPermitted() {
        if (this.hasInvulnerability()) {
            //? if >=26.3 {
            /*player.setPermanentlyInvulnerable(false);
            *///?} else {
            player.setInvulnerable(false);
            //?}
        }
        //? if >=26 {
        player.removeTag(invulnerableTag);
        //?} else {
        /*player.removeCommandTag(invulnerableTag);
        *///?}
        markedAsInvulnerable = false;
    }

    @NotNull
    public
            //? if >=26 {
            ServerPlayer
            //?} else {
            /*ServerPlayerEntity
            *///?}
            getPlayer() {
        return player;
    }

    /**
     * Check if the teleporter can teleport.
     *
     * @return true if the teleport may complete.
     */
    @Override
    public boolean isValid() {
        return player.isAlive();
    }
}
