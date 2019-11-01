package com.ssynhtn.draggesturedetector;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

public class DragGestureDetector {


    private float initialTouchX;
    private float initialTouchY;
    private float lastTouchX;
    private float lastTouchY;
    private int activePointerId = -1;
    private boolean isDragging;
    private float minDragDistSquared;

    private boolean isDragEnabled;


    public interface OnDragListener {
        void onDrag(float dx, float dy);
    }

    private OnDragListener onDragListener;


    public DragGestureDetector(Context context) {
        this(context, true);
    }

    public DragGestureDetector(Context context, boolean isDragEnabled) {
        float minDragDist = ViewConfiguration.get(context).getScaledTouchSlop();
        minDragDistSquared = minDragDist * minDragDist;

        this.isDragEnabled = isDragEnabled;
    }

    public void setOnDragListener(OnDragListener onDragListener) {
        this.onDragListener = onDragListener;
    }

    public void setDragEnabled(boolean dragEnabled) {
        isDragEnabled = dragEnabled;
    }


    public boolean onTouch(MotionEvent event) {
        if (!isDragEnabled) {
            isDragging = false;
            return false;
        }

        float rawX = event.getRawX();
        float rawY = event.getRawY();

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                lastTouchX = rawX;
                lastTouchY = rawY;
                initialTouchX = rawX;
                initialTouchY = rawY;
                activePointerId = event.getPointerId(event.getActionIndex());

                break;
            }
            case MotionEvent.ACTION_POINTER_DOWN: {
                activePointerId = event.getPointerId(event.getActionIndex());
                lastTouchX = getRawX(event, activePointerId);
                lastTouchY = getRawY(event, activePointerId);
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                int upPointerIndex = event.getActionIndex();
                int upPointerId = event.getPointerId(upPointerIndex);

                if (upPointerId == activePointerId) {   // current finger up, change finger
                    int activePointerIndex = (upPointerIndex == 0) ? 1 : 0;
                    activePointerId = event.getPointerId(activePointerIndex);
                    lastTouchX = event.getRawX() + event.getX(activePointerIndex) - event.getX();
                    lastTouchY = event.getRawY() + event.getY(activePointerIndex) - event.getY();

                    if (!isDragging) {
                        initialTouchX = lastTouchX;
                        initialTouchY = lastTouchY;
                    }
                }

                break;
            }

            case MotionEvent.ACTION_UP: {
                isDragging = false;
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                isDragging = false;
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                float activeRawX = getRawX(event, activePointerId);
                float activeRawY = getRawY(event, activePointerId);

                if (!isDragging && shouldStartDragging(activeRawX - initialTouchX, activeRawY - initialTouchY)) {
                    isDragging = true;
                }

                if (isDragging) {
                    float dx = activeRawX - lastTouchX;
                    float dy = activeRawY - lastTouchY;
                    if (onDragListener != null) {
                        onDragListener.onDrag(dx, dy);
                    }
                }

                lastTouchX = activeRawX;
                lastTouchY = activeRawY;
                break;
            }
        }

        return true;
    }


    private boolean shouldStartDragging(float dx, float dy) {
        return dx * dx + dy * dy >= minDragDistSquared;
    }

    private static float getRawX(MotionEvent event, int pointerId) {
        int index = event.findPointerIndex(pointerId);
        return event.getRawX() + event.getX(index) - event.getX();
    }


    private static float getRawY(MotionEvent event, int pointerId) {
        int index = event.findPointerIndex(pointerId);
        return event.getRawY() + event.getY(index) - event.getY();
    }



    public static void translateViewInParent(View v, float dx, float dy) {
        translateViewInParent(v, dx, dy, true);
    }
    public static void translateViewInParent(View v, float dx, float dy, boolean constraintToParent) {
        if (v.getParent() instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) v.getParent();

            float tx = v.getTranslationX() + dx;
            float ty = v.getTranslationY() + dy;

            // constraint the view to the bounds of its parent
            if (constraintToParent) {
                float txMin = -v.getLeft();
                float txMax = parent.getWidth() - v.getRight();
                float tyMin = -v.getTop();
                float tyMax = parent.getHeight() - v.getBottom();
                tx = clamp(tx, txMin, txMax);
                ty = clamp(ty, tyMin, tyMax);
            }

            v.setTranslationX(tx);
            v.setTranslationY(ty);
        }


    }

    private static float clamp(float x, float min, float max) {
        return Math.min(Math.max(x, min), max);
    }
}
