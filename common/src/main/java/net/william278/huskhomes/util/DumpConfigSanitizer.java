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

package net.william278.huskhomes.util;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

/** Sanitizes passwords and IPs from config files included in system dumps. */
public final class DumpConfigSanitizer {

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "(?i)(\\b(?:password|pass)\\s*:\\s*)(\"[^\"]*\"|'[^']*'|\\S+)"
    );
    private static final Pattern IP_PATTERN = Pattern.compile(
            "(?:[0-9]{1,3}\\.){3}[0-9]{1,3}"
    );

    private DumpConfigSanitizer() {
    }

    @NotNull
    public static String sanitize(@NotNull String config) {
        String sanitized = PASSWORD_PATTERN.matcher(config).replaceAll("$1<Censored Password>");
        return IP_PATTERN.matcher(sanitized).replaceAll("<Censored IP>");
    }

}
