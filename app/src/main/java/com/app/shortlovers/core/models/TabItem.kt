package com.app.shortlovers.core.models

/**
 * Represents a tab item in the home screen.
 *
 * Tabs can be either:
 * - [Group]: A tab that displays titles from a specific [TitleGroup]
 * - [Latest]: A special tab that displays the most recently added titles
 *
 * @property name The display name of the tab
 */
sealed class TabItem(val name: String) {
    /**
     * A tab that displays titles belonging to a specific [TitleGroup].
     *
     * @property group The title group this tab represents
     */
    data class Group(val group: TitleGroup) : TabItem(group.name)

    /**
     * A special tab that displays the most recently added titles.
     *
     * Titles in this tab are sorted by [Title.dateCreated] in descending order.
     */
    data object Latest : TabItem("Terbaru")
}
