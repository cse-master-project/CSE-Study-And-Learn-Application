package com.cslu.cse_study_and_learn_application.utils

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Rect
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView

class HighlightHelper(
    private val context: Context,
    private val fragment: Fragment,
    private val highlightItems: List<HighlightItem>,
    private val bubbleMargin: Int = 50, // 말풍선 마진
    private val bubblePadding: Int = 20, // 말풍선 패딩
    private val debugMode: Boolean = false,
    private val screenName: String // 화면 이름
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private var currentHighlightIndex = 0

    companion object {
        private const val PREFS_NAME = "prefs"
        private const val PREF_HELP_SHOWN_PREFIX = "help_shown_"

        fun clearHelpShown(context: Context) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().clear().apply()
        }

        // 해상도별로 heightThreshold 값을 동적으로 계산하는 함수
        fun calculateHeightThreshold(context: Context): Int {
            val displayMetrics: DisplayMetrics = context.resources.displayMetrics
            val screenHeight = displayMetrics.heightPixels / displayMetrics.density // 화면 높이를 dp로 변환
            val densityDpi = displayMetrics.densityDpi

            // 밀도 및 해상도에 따른 heightThreshold 값을 설정
            return when {
                densityDpi >= DisplayMetrics.DENSITY_XXXHIGH -> (screenHeight * 0.16).toInt() // xxxhdpi 이상
                densityDpi >= DisplayMetrics.DENSITY_XXHIGH -> (screenHeight * 0.13).toInt() // xxhdpi 이상
                densityDpi >= DisplayMetrics.DENSITY_XHIGH -> (screenHeight * 0.09).toInt() // xhdpi
                densityDpi >= DisplayMetrics.DENSITY_HIGH -> (screenHeight * 0.02).toInt()  // hdpi
                else -> (screenHeight * 0.015).toInt() // 그 외 (mdpi 이하)
            }
        }
    }

    fun showHighlights() {
        if (!isHelpShown(screenName) || debugMode) {
            addAllHighlights()
            showNextHighlight()
        }
    }

    private fun saveHelpShown(screenName: String) {
        prefs.edit().putBoolean(PREF_HELP_SHOWN_PREFIX + screenName, true).apply()
    }

    private fun isHelpShown(screenName: String): Boolean {
        return prefs.getBoolean(PREF_HELP_SHOWN_PREFIX + screenName, false)
    }

    private fun addAllHighlights() {
        val fragmentRootView = fragment.requireActivity().window.decorView.findViewById<ViewGroup>(android.R.id.content)
        highlightItems.reversed().forEachIndexed { index, item ->
            val targetView = if (item.position != null) {
                getItemView(item.viewId, item.position)
            } else {
                fragment.view?.findViewById<View>(item.viewId)
            }
            if (targetView == null) {
                //Log.e("Highlight", "View with ID ${item.viewId} not found")
                return@forEachIndexed
            }

            val location = IntArray(2)
            // targetView.getLocationInWindow(location)

            val parentLocation = IntArray(2)
            (targetView.parent as View).getLocationOnScreen(parentLocation)
            Lg.d("test", HighlightHelper::class.java.simpleName,
                "ParentView location: X: ${parentLocation[0]}, Y: ${parentLocation[1]}")


            targetView.getLocationOnScreen(location)

            val targetRect = Rect(location[0], location[1], location[0] + targetView.width, location[1] + targetView.height)

            val helpOverlayView = HighlightView(context, targetRect, item.description, item.showPosition, calculateHeightThreshold(context), bubbleMargin.toFloat(), bubblePadding.toFloat(), item.scaleFactor).apply {
                visibility = View.INVISIBLE
                tag = "HighlightView$index"
            }

            fragmentRootView.addView(helpOverlayView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
            helpOverlayView.bringToFront() // 뷰를 최상위로 이동

            helpOverlayView.setOnClickListener {
                //Log.d("Highlight", "Overlay clicked")
                fragmentRootView.removeView(helpOverlayView)
                showNextHighlight()
            }
        }
    }

    private fun getItemView(parentViewId: Int, position: Int): View? {
        val parentView = fragment.view?.findViewById<View>(parentViewId)
        return when (parentView) {
            is RecyclerView -> parentView.findViewHolderForAdapterPosition(position)?.itemView
            // 여기서 다른 목록 뷰 유형을 처리할 수 있습니다. 예: ListView
            else -> null
        }
    }

    private fun showNextHighlight() {
        if (currentHighlightIndex >= highlightItems.size) {
            return
        }

        val fragmentRootView = fragment.requireActivity().window.decorView.findViewById<ViewGroup>(android.R.id.content)
        val nextHighlightView = fragmentRootView.findViewWithTag<View>("HighlightView$currentHighlightIndex")

        if (nextHighlightView != null) {
            nextHighlightView.visibility = View.VISIBLE
            currentHighlightIndex++
        } else {
            currentHighlightIndex++
            showNextHighlight() // 현재 하이라이트 뷰가 없으면 다음 것을 보여줌
        }

        if (currentHighlightIndex >= highlightItems.size && !debugMode) {
            saveHelpShown(screenName)
        }
    }
}
