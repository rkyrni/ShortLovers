package com.app.shortlovers.core.models

/**
 * Represents a title group used for categorizing titles into tabs.
 *
 * Title groups are fetched from the `/items/title_groups` endpoint and are used to organize content
 * into different categories/tabs in the home screen.
 *
 * @property id Unique identifier for the title group
 * @property name Display name of the group (shown as tab name)
 * @property key Unique key identifier for the group
 */
data class TitleGroup(val id: Int, val name: String, val key: String)
