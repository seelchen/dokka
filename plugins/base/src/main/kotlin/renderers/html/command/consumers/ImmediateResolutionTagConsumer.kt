/*
 * Copyright 2014-2023 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.dokka.base.renderers.html.command.consumers

import kotlinx.html.TagConsumer
import kotlinx.html.visit
import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.base.renderers.html.TemplateBlock
import org.jetbrains.dokka.base.renderers.html.templateCommand
import org.jetbrains.dokka.base.renderers.html.templateCommandFor
import org.jetbrains.dokka.base.templating.Command
import org.jetbrains.dokka.plugability.DokkaContext
import org.jetbrains.dokka.plugability.plugin
import org.jetbrains.dokka.plugability.query

public class ImmediateResolutionTagConsumer<out R>(
    private val downstream: TagConsumer<R>,
    private val context: DokkaContext
): TagConsumer<R> by downstream {

    public fun processCommand(command: Command, block: TemplateBlock) {
        context.plugin<DokkaBase>().query { immediateHtmlCommandConsumer }
            .find { it.canProcess(command) }
            ?.processCommand(command, block, this)
            ?: run { templateCommandFor(command, downstream).visit(block) }
    }

    public fun processCommandAndFinalize(command: Command, block: TemplateBlock): R {
        return context.plugin<DokkaBase>().query { immediateHtmlCommandConsumer }
            .find { it.canProcess(command) }
            ?.processCommandAndFinalize(command, block, this)
            ?: downstream.templateCommand(command, block)
    }
}

