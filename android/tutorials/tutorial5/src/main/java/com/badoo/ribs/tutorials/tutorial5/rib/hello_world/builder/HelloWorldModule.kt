package com.badoo.ribs.tutorials.tutorial5.rib.hello_world.builder

import android.os.Bundle
import com.badoo.ribs.core.Node
import com.badoo.ribs.tutorials.tutorial5.rib.hello_world.HelloWorld
import com.badoo.ribs.tutorials.tutorial5.rib.hello_world.HelloWorldInteractor
import com.badoo.ribs.tutorials.tutorial5.rib.hello_world.HelloWorldView
import com.badoo.ribs.tutorials.tutorial5.util.User
import dagger.Provides
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer

@dagger.Module
internal object HelloWorldModule {

    @HelloWorldScope
    @Provides
    @JvmStatic
    @Suppress("LongParameterList")
    internal fun interactor(
        savedInstanceState: Bundle?,
        user: User,
        config: HelloWorld.Config,
        input: ObservableSource<HelloWorld.Input>,
        output: Consumer<HelloWorld.Output>
    ): HelloWorldInteractor =
        HelloWorldInteractor(
            savedInstanceState = savedInstanceState,
            user = user,
            config = config,
            input = input,
            output = output
        )

    @HelloWorldScope
    @Provides
    @JvmStatic
    internal fun node(
        savedInstanceState: Bundle?,
        customisation: HelloWorld.Customisation,
        interactor: HelloWorldInteractor
    ) : Node<HelloWorldView> = Node(
        savedInstanceState = savedInstanceState,
        identifier = object : HelloWorld {},
        viewFactory = customisation.viewFactory(null),
        router = null,
        interactor = interactor
    )
}
