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

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.william278.desertwell.util.Version;
import net.william278.huskhomes.HuskHomes;
import net.william278.huskhomes.command.Command;
import net.william278.huskhomes.config.Locales;
import net.william278.huskhomes.config.Server;
import net.william278.huskhomes.config.Settings;
import net.william278.huskhomes.config.Spawn;
import net.william278.huskhomes.database.Database;
import net.william278.huskhomes.hook.Hook;
import net.william278.huskhomes.listener.EventListener;
import net.william278.huskhomes.manager.Manager;
import net.william278.huskhomes.network.Broker;
import net.william278.huskhomes.random.RandomTeleportEngine;
import net.william278.huskhomes.user.OnlineUser;
import net.william278.huskhomes.user.SavedUser;
import net.william278.huskhomes.util.UnsafeBlocks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LoggingEventBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public abstract class UniversalFabricPluginBase implements HuskHomes {

    public static final Logger LOGGER = LoggerFactory.getLogger("HuskHomes");

    private final ModContainer modContainer = FabricLoader.getInstance()
            .getModContainer("huskhomes")
            .orElseThrow(() -> new RuntimeException("Failed to get Mod Container"));
    private final Map<String, Boolean> permissions = new HashMap<>();
    private final Set<SavedUser> savedUsers = new HashSet<>();
    private final Set<UUID> currentlyOnWarmup = ConcurrentHashMap.newKeySet();
    private final Set<UUID> warmupDamagedUsers = ConcurrentHashMap.newKeySet();
    private final Map<UUID, OnlineUser> onlineUserMap = new HashMap<>();
    private final Map<String, List<net.william278.huskhomes.user.User>> globalUserList = new ConcurrentHashMap<>();
    private final List<Command> commands = new ArrayList<>();

    private Set<Hook> hooks = new HashSet<>();
    private Settings settings;
    private Locales locales;
    private Database database;
    private Manager manager;
    private EventListener eventListener;
    private RandomTeleportEngine randomTeleportEngine;
    private Spawn serverSpawn;
    private UnsafeBlocks unsafeBlocks;
    @Nullable
    private Broker broker;
    @Nullable
    private Server serverName;

    @Override
    public void loadMetrics() {
        // No metrics on Fabric
    }

    @Override
    @NotNull
    public Version getPluginVersion() {
        return Version.fromString(modContainer.getMetadata().getVersion().getFriendlyString(), "-");
    }

    @Override
    @NotNull
    public String getServerName() {
        return serverName == null ? "server" : serverName.getName();
    }

    @Override
    @NotNull
    public Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir().resolve("huskhomes");
    }

    @Override
    @Nullable
    public InputStream getResource(@NotNull String name) {
        return this.modContainer.findPath(name)
                .map(path -> {
                    try {
                        return Files.newInputStream(path);
                    } catch (IOException e) {
                        log(Level.WARNING, "Failed to load resource: " + name, e);
                    }
                    return null;
                })
                .orElse(this.getClass().getClassLoader().getResourceAsStream(name));
    }

    @Override
    public boolean isDependencyAvailable(@NotNull String name) {
        return FabricLoader.getInstance().isModLoaded(name);
    }

    @Override
    public void log(@NotNull Level level, @NotNull String message, @NotNull Throwable... exceptions) {
        LoggingEventBuilder logEvent = LOGGER.makeLoggingEventBuilder(
                switch (level.getName()) {
                    case "WARNING" -> org.slf4j.event.Level.WARN;
                    case "SEVERE" -> org.slf4j.event.Level.ERROR;
                    default -> org.slf4j.event.Level.INFO;
                }
        );
        if (exceptions.length >= 1) {
            logEvent = logEvent.setCause(exceptions[0]);
        }
        logEvent.log(message);
    }

    @Override
    public void closeDatabase() {
        if (database != null) {
            database.close();
        }
    }

    @Override
    public void closeBroker() {
        if (broker != null) {
            broker.close();
        }
    }

    @Override
    @NotNull
    public Optional<Broker> getBroker() {
        return Optional.ofNullable(broker);
    }

    @Override
    @NotNull
    public List<Command> getCommands() {
        return commands;
    }

    @NotNull
    public Map<String, Boolean> getPermissions() {
        return permissions;
    }

    @NotNull
    public Set<SavedUser> getSavedUsers() {
        return savedUsers;
    }

    @Override
    @NotNull
    public Set<UUID> getCurrentlyOnWarmup() {
        return currentlyOnWarmup;
    }

    @Override
    @NotNull
    public Set<UUID> getWarmupDamagedUsers() {
        return warmupDamagedUsers;
    }

    @NotNull
    public Map<UUID, OnlineUser> getOnlineUserMap() {
        return onlineUserMap;
    }

    @NotNull
    public Map<String, List<net.william278.huskhomes.user.User>> getGlobalUserList() {
        return globalUserList;
    }

    @Override
    public void setHooks(@NotNull Set<Hook> hooks) {
        this.hooks = hooks;
    }

    @Override
    @NotNull
    public Set<Hook> getHooks() {
        return hooks;
    }

    @Override
    public void setSettings(@NotNull Settings settings) {
        this.settings = settings;
    }

    @Override
    @NotNull
    public Settings getSettings() {
        return settings;
    }

    @Override
    public void setLocales(@NotNull Locales locales) {
        this.locales = locales;
    }

    @Override
    @NotNull
    public Locales getLocales() {
        return locales;
    }

    @Override
    public void setDatabase(@NotNull Database database) {
        this.database = database;
    }

    @Override
    @NotNull
    public Database getDatabase() {
        return database;
    }

    @Override
    public void setManager(@NotNull Manager manager) {
        this.manager = manager;
    }

    @Override
    @NotNull
    public Manager getManager() {
        return manager;
    }

    public void setEventListener(@NotNull EventListener eventListener) {
        this.eventListener = eventListener;
    }

    @NotNull
    public EventListener getEventListener() {
        return eventListener;
    }

    @Override
    public void setRandomTeleportEngine(@NotNull RandomTeleportEngine randomTeleportEngine) {
        this.randomTeleportEngine = randomTeleportEngine;
    }

    @Override
    @NotNull
    public RandomTeleportEngine getRandomTeleportEngine() {
        return randomTeleportEngine;
    }

    @Override
    public void setServerSpawn(@NotNull Spawn spawn) {
        this.serverSpawn = spawn;
    }

    @Override
    @NotNull
    public Optional<Spawn> getServerSpawn() {
        return Optional.ofNullable(serverSpawn);
    }

    @Override
    public void setUnsafeBlocks(@NotNull UnsafeBlocks unsafeBlocks) {
        this.unsafeBlocks = unsafeBlocks;
    }

    @Override
    @NotNull
    public UnsafeBlocks getUnsafeBlocks() {
        return unsafeBlocks;
    }

    @Override
    public void setBroker(@Nullable Broker broker) {
        this.broker = broker;
    }

    @Override
    public void setServerName(@NotNull Server serverName) {
        this.serverName = serverName;
    }
}
