package com.moraware.mango.views

import android.graphics.Rect
import androidx.recyclerview.widget.RecyclerView
import android.view.View

/**
 * Created by luispalacios on 6/16/17.
 */

class StartPaddingItemDecorator(private val size: Int, private val orientation: Int, private val spanCount: Int) : androidx.recyclerview.widget.RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: androidx.recyclerview.widget.RecyclerView, state: androidx.recyclerview.widget.RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        // Apply offset only to first item
        if (androidx.recyclerview.widget.RecyclerView.HORIZONTAL == orientation && parent.getChildAdapterPosition(view) / spanCount == 0) {
            outRect.left += size
        }

        if (androidx.recyclerview.widget.RecyclerView.VERTICAL == orientation && parent.getChildAdapterPosition(view) / spanCount == 0) {
            outRect.top += size
        }
    }
}
