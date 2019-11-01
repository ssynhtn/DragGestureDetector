package com.ssynhtn.draggesturedetector;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupDrag(findViewById(R.id.dumb_view_one), true);
        setupDrag(findViewById(R.id.dumb_view_two), false);

    }

    private void setupDrag(final View view, final boolean constraintToParent) {

        final DragGestureDetector dragGestureDetector = new DragGestureDetector(this);
        dragGestureDetector.setOnDragListener(new DragGestureDetector.OnDragListener() {
            @Override
            public void onDrag(float dx, float dy) {
                DragGestureDetector.translateViewInParent(view, dx, dy, constraintToParent);
            }
        });

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return dragGestureDetector.onTouch(event);
            }
        });

    }
}
