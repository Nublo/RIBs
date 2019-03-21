package com.badoo.ribs.core.routing.backstack

import android.os.Bundle
import android.os.Parcelable
import com.badoo.ribs.core.Node
import com.badoo.ribs.core.routing.NodeConnector
import com.badoo.ribs.core.routing.action.RoutingAction
import com.badoo.ribs.core.routing.backstack.BackStackRibConnector.DetachStrategy.DESTROY
import com.badoo.ribs.core.routing.backstack.BackStackRibConnector.DetachStrategy.DETACH_VIEW

internal class BackStackRibConnector<C : Parcelable>(
    private val resolver: (C) -> RoutingAction<*>,
    private val connector: NodeConnector
) {

    enum class DetachStrategy {
        DESTROY, DETACH_VIEW
    }

    fun leave(backStackElement: BackStackElement<C>, detachStrategy: DetachStrategy): BackStackElement<C> {
        with(backStackElement) {
            routingAction?.cleanup()

            when (detachStrategy) {
                DESTROY -> destroyRibs()
                DETACH_VIEW -> saveAndDetachView()
            }
        }

        return backStackElement
    }

    private fun BackStackElement<C>.destroyRibs() {
        ribs?.forEach {
            connector.detachChildView(it)
            connector.detachChildNode(it)
        }
        ribs = null
    }

    private fun BackStackElement<C>.saveAndDetachView(): Unit? {
        return ribs?.forEach {
            it.saveViewState()
            if (routingAction!!.allowAttachView) {
                connector.detachChildView(it)
            }
        }
    }

    fun goTo(backStackElement: BackStackElement<C>): BackStackElement<C> {
        with(backStackElement) {
            if (routingAction == null) {
                routingAction = resolver.invoke(configuration)
            }

            if (ribs == null) {
                ribs = routingAction!!
                    .createRibs()
                    .also {
                        attachNodes(it, routingAction!!.allowAttachView)
                    }
            } else {
                if (routingAction!!.allowAttachView) {
                    ribs!!
                        .forEach {
                            connector.attachChildView(it)
                        }
                }
            }

            routingAction!!.execute()
        }

        return backStackElement
    }

    private fun BackStackElement<C>.attachNodes(it: List<Node<*>>, attachView: Boolean) {
        it.forEachIndexed { index, node ->
            connector.attachChildNode(node, bundleAt(index))

            if (attachView) {
                connector.attachChildView(node)
            }
        }
    }

    private fun BackStackElement<C>.bundleAt(index: Int): Bundle? =
        bundles.elementAtOrNull(index)?.also {
            it.classLoader = BackStackManager.State::class.java.classLoader
        }

    fun shrinkToBundles(backStack: List<BackStackElement<C>>): List<BackStackElement<C>> =
        saveInstanceState(backStack).apply {
            dropLast(1).forEach {
                it.ribs?.forEach {
                    connector.detachChildView(it)
                    connector.detachChildNode(it)
                }
                it.ribs = null
            }
        }

    fun saveInstanceState(backStack: List<BackStackElement<C>>): List<BackStackElement<C>> {
        backStack.forEach {
            it.bundles = it.ribs?.map { childNode ->
                Bundle().also {
                    childNode.onSaveInstanceState(it)
                }
            } ?: emptyList()
        }

        return backStack
    }

    fun tearDownRouting(backStack: List<BackStackElement<C>>) {
        backStack.lastOrNull()?.routingAction?.cleanup()
    }

    fun reinitRouting(backStack: List<BackStackElement<C>>) {
        backStack.lastOrNull()?.routingAction?.execute()
    }
}
