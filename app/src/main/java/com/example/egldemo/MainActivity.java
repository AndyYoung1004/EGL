package com.example.egldemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    TestRender render;
    EGLHelper eglHelper;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);
        render = new TestRender(getResources());
        eglHelper = new EGLHelper(getApplicationContext());
        eglHelper.setRenderer(render);
        findViewById(R.id.test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eglHelper.requestRender();
                imageView.setImageBitmap(render.getBitmap());
            }
        });
    }
}