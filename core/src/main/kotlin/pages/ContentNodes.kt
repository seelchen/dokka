package org.jetbrains.dokka.pages

import org.jetbrains.dokka.links.DRI

interface ContentNode {
    val dci: DCI
    val annotations: List<Annotation>
}

/** Comment consisting of parts, eg. [ContentText]s, [ContentLink]s and so on */
data class ContentComment(val parts: List<ContentNode>,
                          override val dci: DCI,
                          override val annotations: List<Annotation> = emptyList()
): ContentNode

/** Simple text */
data class ContentText(val text: String,
                       override val dci: DCI,
                       override val annotations: List<Annotation> = emptyList()
): ContentNode

///** Headers */  TODO for next iteration
data class ContentHeader(val items: List<ContentNode>,
                         val level: Int,
                         override val dci: DCI,
                         override val annotations: List<Annotation> = emptyList()
): ContentNode

/** Lists */
data class ContentList(val items: List<ContentNode>,
                       val ordered: Boolean,
                       override val dci: DCI,
                       override val annotations: List<Annotation> = emptyList()
): ContentNode

/** Styled elements, eg. bold, strikethrough, emphasis and so on **/
data class ContentStyle(val items: List<ContentNode>,
                        val style: IStyle,
                        override val dci: DCI,
                        override val annotations: List<Annotation> = emptyList()
): ContentNode

/** Code blocks */
data class ContentCode(val code: String,
                       val language: String,
                       override val dci: DCI,
                       override val annotations: List<Annotation> = emptyList()
): ContentNode

/** Symbols, eg. `open fun foo(): String`, or `class Bar`, consisting of parts like [ContentText] and [ContentLink] */
data class ContentSymbol(val parts: List<ContentNode>,
                         override val dci: DCI,
                         override val annotations: List<Annotation> = emptyList()
): ContentNode

/** All links to classes, packages, etc. that have te be resolved */
data class ContentLink(val text: String,
                       val address: DRI,
                       override val dci: DCI,
                       override val annotations: List<Annotation> = emptyList()
): ContentNode

/** All links that do not need to be resolved */
data class ContentResolvedLink(val text: String,
                               val address: String,
                               override val dci: DCI,
                               override val annotations: List<Annotation> = emptyList()
): ContentNode

/** Blocks of [ContentNode]s with name, eg. Functions, Types, Properties, etc. */
data class ContentBlock(val name: String,
                        val children: List<ContentNode>,
                        override val dci: DCI,
                        override val annotations: List<Annotation> = emptyList()
): ContentNode

/** Logical grouping of [ContentNode]s, eg. [ContentLink], [ContentText] and [ContentSymbol] for one entity */
data class ContentGroup(val children: List<ContentNode>,
                        override val dci: DCI,
                        override val annotations: List<Annotation> = emptyList()
): ContentNode

/** All annotations */
data class Annotation(val name: String)

interface IStyle

enum class Style: IStyle {
    Emphasis, Strong, Paragraph
}