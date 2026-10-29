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

package net.william278.huskhomes.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.fabric.api.permissions.v0.PermissionCheckEvent;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.util.TriState;
//? if >=26.1.2 {
import net.minecraft.commands.Commands;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;
//?} elif >=1.21.11 {
/*import net.minecraft.command.permission.Permission;
import net.minecraft.command.permission.PermissionLevel;
*///?}
//? if >=26.1.2 {
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
//?} else {
/*import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
*///?}
import net.william278.huskhomes.FabricHuskHomes;
import net.william278.huskhomes.user.CommandUser;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Predicate;

import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
//? if >=26.1.2 {
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;
//?} else {
/*import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
*///?}

public class FabricCommand {

    private final FabricHuskHomes plugin;
    private final Command command;

    public FabricCommand(@NotNull Command command, @NotNull FabricHuskHomes plugin) {
        this.command = command;
        this.plugin = plugin;
    }

    public void register(
            //? if >=26.1.2 {
            @NotNull CommandDispatcher<CommandSourceStack> dispatcher
            //?} else {
            /*@NotNull CommandDispatcher<ServerCommandSource> dispatcher
            *///?}
    ) {
        // Register brigadier command
        final int permissionLevel = command.isOperatorCommand() ? 3 : 0;
        //? if >=26.1.2 {
        final Predicate<CommandSourceStack> predicate = Permissions
        //?} else {
        /*final Predicate<ServerCommandSource> predicate = Permissions
        *///?}
                .require(
                        command.getPermission(),
                        //? if >=26.1.2 {
                        PermissionLevel.byId(permissionLevel)
                        //?} elif >=1.21.11 {
                        /*PermissionLevel.fromLevel(permissionLevel)
                        *///?} else {
                        /*permissionLevel
                        *///?}
                );
        //? if >=26.1.2 {
        final RequiredArgumentBuilder<CommandSourceStack, String> requiredArgumentBuilder = argument(
                command.getRawUsage().replaceAll("[<>\\[\\]]", ""), greedyString()
        );
        final LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal(command.getName())
        //?} else {
        /*final RequiredArgumentBuilder<ServerCommandSource, String> requiredArgumentBuilder = argument(
               command.getRawUsage().replaceAll("[<>\\[\\]]", ""), greedyString()
        );
        final LiteralArgumentBuilder<ServerCommandSource> builder = literal(command.getName())
        *///?}
                .requires(predicate).executes(getBrigadierExecutor());
        plugin.getPermissions().put(command.getPermission(), command.isOperatorCommand());
        if (!command.getRawUsage().isBlank()) {
            builder.then(requiredArgumentBuilder.executes(getBrigadierExecutor()).suggests(getBrigadierSuggester()));
        }

        // Register additional permissions
        final Map<String, Boolean> permissions = command.getAdditionalPermissions();
        permissions.forEach((permission, isOp) -> plugin.getPermissions().put(permission, isOp));
        PermissionCheckEvent.EVENT.register((player, node) -> {
            if (permissions.containsKey(node) && permissions.get(node) &&
                    //? if >=26.1.2 {
                    (!(player instanceof CommandSourceStack source) || hasPermissionLevel(source, 3))
                    //?} else {
                    /*(!(player instanceof ServerCommandSource source) || hasPermissionLevel(source, 3))
                    *///?}
            ) {
                return TriState.TRUE;
            }
            return TriState.DEFAULT;
        });

        // Register aliases
        //? if >=26.1.2 {
        final LiteralCommandNode<CommandSourceStack> node = dispatcher.register(builder);
        final LiteralArgumentBuilder<CommandSourceStack> subcommandBuilder = literal("huskhomes:%s"
                .formatted(command.getName()));
        //?} else {
        /*final LiteralCommandNode<ServerCommandSource> node = dispatcher.register(builder);
        final LiteralArgumentBuilder<ServerCommandSource> subcommandBuilder = literal("huskhomes:%s"
           .formatted(command.getName()));
        *///?}
        dispatcher.register(subcommandBuilder.requires(predicate).executes(getBrigadierExecutor()).redirect(node));
        command.getAliases().forEach(alias -> {
            //? if >=26.1.2 {
            LiteralArgumentBuilder<CommandSourceStack> aliasBuilder = literal(alias);
            LiteralArgumentBuilder<CommandSourceStack> subcommandAliasBuilder = literal("huskhomes:%s".formatted(alias));
            //?} else {
            /*LiteralArgumentBuilder<ServerCommandSource> aliasBuilder = literal(alias);
            LiteralArgumentBuilder<ServerCommandSource> subcommandAliasBuilder = literal("huskhomes:%s"
               .formatted(alias));
            *///?}
            dispatcher.register(aliasBuilder.requires(predicate)
                    .executes(getBrigadierExecutor()).redirect(node));
            dispatcher.register(subcommandAliasBuilder.requires(predicate)
                    .executes(getBrigadierExecutor()).redirect(node));
        });
    }

    private boolean hasPermissionLevel(
            //? if >=26.1.2 {
            CommandSourceStack source,
            //?} else {
            /*ServerCommandSource source,
            *///?}
            int level
    ) {
        //? if >=26.1.2 {
        return source.permissions().hasPermission(new Permission.HasCommandLevel(PermissionLevel.byId(level)));
        //?} elif >=1.21.11 {
        /*return source.getPermissions().hasPermission(new Permission.Level(PermissionLevel.fromLevel(level)));
        *///?} else {
        /*return source.hasPermissionLevel(level);
        *///?}
    }

    //? if >=26.1.2 {
    private com.mojang.brigadier.Command<CommandSourceStack> getBrigadierExecutor() {
    //?} else {
    /*private com.mojang.brigadier.Command<ServerCommandSource> getBrigadierExecutor() {
    *///?}
        return (context) -> {
            command.onExecuted(
                    resolveExecutor(context.getSource()),
                    context.getNodes().stream()
                            .filter(node -> node.getNode() instanceof ArgumentCommandNode<?, ?>)
                            .map(node -> context.getInput().substring(node.getRange().getStart(),
                                    node.getRange().getEnd()).split(" "))
                            .findFirst().orElseGet(() -> new String[0])
            );
            return 1;
        };
    }

    //? if >=26.1.2 {
    private com.mojang.brigadier.suggestion.SuggestionProvider<CommandSourceStack> getBrigadierSuggester() {
    //?} else {
    /*private com.mojang.brigadier.suggestion.SuggestionProvider<ServerCommandSource> getBrigadierSuggester() {
    *///?}
        if (!(command instanceof TabCompletable provider)) {
            return (context, builder) -> com.mojang.brigadier.suggestion.Suggestions.empty();
        }
        return (context, builder) -> {
            final String[] args = builder.getRemaining().split(" ", -1);
            provider.getSuggestions(resolveExecutor(context.getSource()), args).stream()
                    .map(suggestion -> {
                        final String completedArgs = String.join(" ", args);
                        int lastIndex = completedArgs.lastIndexOf(" ");
                        if (lastIndex == -1) {
                            return suggestion;
                        }
                        return completedArgs.substring(0, lastIndex + 1) + suggestion;
                    })
                    .forEach(builder::suggest);
            return builder.buildFuture();
        };
    }

    private CommandUser resolveExecutor(
            //? if >=26.1.2 {
            @NotNull CommandSourceStack source
            //?} else {
            /*@NotNull ServerCommandSource source
            *///?}
    ) {
        //? if >=26.1.2 {
        if (source.getEntity() instanceof ServerPlayer player) {
        //?} else {
        /*if (source.getEntity() instanceof ServerPlayerEntity player) {
        *///?}
            return plugin.getOnlineUser(player);
        }
        return plugin.getConsole();
    }

}
