plugins {
    id("dev.kikugie.stonecutter")
}

// The version that fabric/src is currently written for. Switch with the Stonecutter tasks
// (e.g. `./gradlew :fabric:"Set active project to 1.21.1"`), and run `:fabric:"Reset active project"` before committing.
stonecutter active "26.2"

stonecutter parameters {
    // Versions below 26 use Yarn mappings; keep Javadoc links pointing at their class names.
    // Code references are handled with version conditions in the sources instead.
    replacements.string(current.parsed < "26") {
        replace("{@link ServerPlayer}", "{@link ServerPlayerEntity}")
        replace("{@link ServerLevel}", "{@link ServerWorld}")
        replace("{@link TeleportTransition}", "{@link TeleportTarget}")
        replace("{@link Vec3}", "{@link Vec3d}")
    }
}
