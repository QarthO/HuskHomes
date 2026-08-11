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

import net.william278.toilet.DumpOptions;
import net.william278.toilet.dump.AttachedFile;
import net.william278.toilet.dump.ConfigFile;
import net.william278.toilet.file.FileReader;
import net.william278.toilet.util.FileReaderUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/** Dump config reader that censors passwords/IPs via {@link DumpConfigSanitizer}. */
public final class FilteredConfigFileReader implements FileReader {

    private static final int CONFIG_MAX_LINES = 1000;

    @Override
    public Optional<AttachedFile> read(@NotNull DumpOptions.FileInclusionRule.FileMeta meta,
                                       @NotNull Path configDirectory) {
        try {
            final Path file = meta.getPath(configDirectory);
            if (!Files.isRegularFile(file)) {
                return Optional.empty();
            }
            final String contents = DumpConfigSanitizer.sanitize(
                    FileReaderUtil.readLargeFile(file, CONFIG_MAX_LINES)
            );
            return Optional.of(new ConfigFile(
                    file.getFileName().toString(),
                    meta.fileLabel(),
                    contents
            ));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

}
