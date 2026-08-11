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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DisplayName("Dump Config Sanitizer Tests")
public class DumpConfigSanitizerTests {

    @Test
    @DisplayName("Test password values are censored")
    void testPasswordCensoring() {
        assertAll(
                () -> assertEquals("    password: <Censored Password>",
                        DumpConfigSanitizer.sanitize("    password: pa55w0rd")),
                () -> assertEquals("    password: <Censored Password>",
                        DumpConfigSanitizer.sanitize("    password: 'p@ss-w0rd!'")),
                () -> assertEquals("    password: <Censored Password>",
                        DumpConfigSanitizer.sanitize("    password: \"complex!pass@123\"")),
                () -> assertEquals("    Password: <Censored Password>",
                        DumpConfigSanitizer.sanitize("    Password: Secret")),
                () -> assertEquals("    pass: <Censored Password>",
                        DumpConfigSanitizer.sanitize("    pass: abc"))
        );
    }

    @Test
    @DisplayName("Test password keys are preserved")
    void testPasswordKeyPreserved() {
        final String sanitized = DumpConfigSanitizer.sanitize("    password: p@ssw0rd!");
        assertAll(
                () -> assertEquals("    password: <Censored Password>", sanitized),
                () -> assertFalse(sanitized.contains("p@ssw0rd!"))
        );
    }

    @Test
    @DisplayName("Test unrelated keys ending in pass are not censored")
    void testNoFalsePositives() {
        assertAll(
                () -> assertEquals("compass: north", DumpConfigSanitizer.sanitize("compass: north")),
                () -> assertEquals("bypass: true", DumpConfigSanitizer.sanitize("bypass: true")),
                () -> assertEquals("passenger: bob", DumpConfigSanitizer.sanitize("passenger: bob"))
        );
    }

    @Test
    @DisplayName("Test IP addresses are censored")
    void testIpCensoring() {
        assertEquals("host: <Censored IP>", DumpConfigSanitizer.sanitize("host: 79.21.112.166"));
    }

}
