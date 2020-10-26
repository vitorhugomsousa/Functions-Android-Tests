fun awaitOnView(matcher: Matcher<View>, maxTime: Long = 10000): ViewInteraction {
    val startTime = Date().time
    var lastException: Exception? = null
    while (Date().time - startTime < maxTime) {
        Thread.sleep(100)
        try {
            val view = onView(matcher)
            view.check(matches(isDisplayed()))
            return view
        } catch (e: Exception) {
            lastException = e
            e.printStackTrace()
        }
    }
    throw lastException ?: IllegalStateException()
}

inline fun waitUntilLoaded(crossinline recyclerProvider: () -> RecyclerView) {
    Espresso.onIdle()

    lateinit var recycler: RecyclerView

    InstrumentationRegistry.getInstrumentation().runOnMainSync {
        recycler = recyclerProvider()
    }

    while (recycler.hasPendingAdapterUpdates()) {
        Thread.sleep(10)
    }
}

fun ViewInteraction.isDisplayed(): Boolean {
    try {
        check(matches(ViewMatchers.isDisplayed()))
        return true
    } catch (e: NoMatchingViewException) {
        return false
    }
}


fun ViewInteraction.checkHasText(contents: String): Boolean {
    return try {
        check((matches(hasTextEqualTo(contents))))
        true
    } catch (e: NoMatchingViewException) {
        false
    }
}


private fun hasTextEqualTo(content: String): Matcher<View> {
    return object : TypeSafeMatcher<View>() {
        override fun describeTo(description: Description) {
            description.appendText("Has EditText/TextView the value:  $content")
        }

        override fun matchesSafely(view: View?): Boolean {

            val textView = view as? TextView ?: return false
            val text = textView.text.toString()
            return text.equals(content, ignoreCase = false)
        }
    }
}

fun clickChildViewWithId(id: Int): ViewAction {
    return object : ViewAction {
        override fun getConstraints(): Matcher<View>? {
            return null
        }

        override fun getDescription(): String {
            return "Click on a child view with specified id."
        }

        override fun perform(uiController: UiController, view: View) {



            val v = view.findViewById<View>(id)
            v.performClick()
        }
    }
}