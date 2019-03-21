package com.badoo.ribs.example.rib.switcher

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import com.badoo.ribs.core.view.RibView
import com.badoo.ribs.example.R
import com.badoo.ribs.example.rib.switcher.SwitcherView.Event
import com.badoo.ribs.example.rib.switcher.SwitcherView.ViewModel
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer

interface SwitcherView : RibView,
    ObservableSource<Event>,
    Consumer<ViewModel> {

    sealed class Event {
    }

    data class ViewModel(
        val i: Int = 0
    )

    val menuContainer: ViewGroup
    val contentContainer: ViewGroup
    val blockerContainer: ViewGroup
}


class SwitcherViewImpl private constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0, private val events: PublishRelay<Event>
) : FrameLayout(context, attrs, defStyle),
    SwitcherView,
    ObservableSource<Event> by events,
    Consumer<ViewModel> {

    @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
    ) : this(context, attrs, defStyle, PublishRelay.create<Event>())

    override val androidView = this
    override val menuContainer: ViewGroup by lazy { findViewById<ViewGroup>(R.id.menu_container) }
    override val contentContainer: ViewGroup by lazy { findViewById<ViewGroup>(R.id.content_container) }
    override val blockerContainer: ViewGroup by lazy { findViewById<ViewGroup>(R.id.blocker_container) }

    override fun onFinishInflate() {
        super.onFinishInflate()
    }

    override fun accept(vm: ViewModel) {
    }
}
