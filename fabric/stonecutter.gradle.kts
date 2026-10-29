plugins {
    id("dev.kikugie.stonecutter")
}

// The version that fabric/src is currently written for. Switch with the Stonecutter tasks
// (e.g. `./gradlew :fabric:"Set active project to 1.21.1"`), and run `:fabric:"Reset active project"` before committing.
stonecutter active "26.2"

stonecutter parameters {
    // Sources use current Mojang names; these map renamed classes and methods back for older versions
    replacements.regex(current.parsed >= "1.21.11") {
        replace("\\bResourceLocation\\b" to "Identifier", "\\bIdentifier\\b" to "ResourceLocation")
        // ResourceKey#location() was renamed with ResourceLocation
        replace("\\.location\\(\\)" to ".identifier()", "\\.identifier\\(\\)" to ".location()")
    }
    replacements.string(current.parsed >= "1.21.2") {
        replace("DimensionTransition", "TeleportTransition")
    }
}
