package com.example.starledger.adapter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.starledger.R;

public abstract class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

    private Drawable deleteIcon;
    private int intrinsicWidth;
    private int intrinsicHeight;
    private ColorDrawable background;
    private int backgroundColor;

    public SwipeToDeleteCallback(Context context) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_delete_24);
        intrinsicWidth = deleteIcon.getIntrinsicWidth();
        intrinsicHeight = deleteIcon.getIntrinsicHeight();
        background = new ColorDrawable();
        backgroundColor = Color.parseColor("#eeeeee");
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        Log.d("SwipeToDeleteCallback", "position: dX = " + dX + ", dY = " + dY);
        RecyclerView.ViewHolder itemView = viewHolder;
        int itemHeight = itemView.itemView.getBottom() - itemView.itemView.getTop();

        // Draw the delete background
        background.setColor(backgroundColor);
        background.setBounds(itemView.itemView.getLeft(), itemView.itemView.getTop(), itemView.itemView.getRight(), itemView.itemView.getBottom());
        background.draw(c);

        // Draw the delete icon
        int iconSizePercentage;
        int iconWidth = intrinsicWidth;
        int iconHeight = intrinsicHeight;
        float slideDistance = dX;
        float slidePercentage = slideDistance / itemView.itemView.getWidth();

        if (slidePercentage > 0 && slidePercentage <= 0.5) {
            iconSizePercentage = (int) (slidePercentage / 0.5f * 100);
            iconWidth = (int) (iconWidth * (iconSizePercentage / 100f));
            iconHeight = (int) (iconHeight * (iconSizePercentage / 100f));
        }

        int deleteIconTop = itemView.itemView.getTop() + (itemHeight - iconHeight) / 2;
        int deleteIconMargin = (itemHeight - iconHeight) / 2;
        int deleteIconLeft = itemView.itemView.getLeft() + deleteIconMargin;
        int deleteIconRight = itemView.itemView.getLeft() + deleteIconMargin + iconWidth;
        int deleteIconBottom = deleteIconTop + iconHeight;

        deleteIcon.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);
        deleteIcon.draw(c);

        float newDx = dX;
        if (newDx <= -100f) {
            newDx = -100f;
        }
        super.onChildDraw(c, recyclerView, viewHolder, newDx, dY, actionState, isCurrentlyActive);
    }
}
