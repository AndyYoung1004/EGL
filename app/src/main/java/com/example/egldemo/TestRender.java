package com.example.egldemo;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.opengl.GLES30;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class TestRender implements EGLHelper.Renderer {
    private static final int TEXTURE_DIMENSION = 2; // 纹理坐标维度
    private static final int VERTEX_DIMENSION = 3; // 顶点坐标维度
    private Resources mResources;
    private float mVertex[] = {-1.0f, 1.0f, 0.0f, -1.0f, -1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, -1.0f, 0.0f};
    private float[] mFboTexture = {0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f};
    protected FloatBuffer mVertexBuffer;
    protected FloatBuffer mFboTextureBuffer;
    // 帧缓冲对象 - 颜色、深度、模板附着点，纹理对象可以连接到帧缓冲区对象的颜色附着点
    private int[] mFrameBufferId = new int[1];
    private int[] mTextureId = new int[2];
    private int mProgramId;
    private Point mBitmapSize = new Point();
    private Bitmap bitmap;

    public TestRender(Resources resources) {
        mResources = resources;
        mVertexBuffer = ByteBuffer.allocateDirect(mVertex.length * Float.BYTES)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mVertexBuffer.put(mVertex);
        mVertexBuffer.position(0);
        mFboTextureBuffer = ByteBuffer.allocateDirect(mFboTexture.length * Float.BYTES)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mFboTextureBuffer.put(mFboTexture);
        mFboTextureBuffer.position(0);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    private void onModelCreate() {
        mProgramId = ShaderUtil.createProgram(mResources, R.raw.pic_vertex_shader, R.raw.pic_fragment_shader);
        TextureUtils.loadTexture(mResources, R.drawable.thelittleprince, mBitmapSize, mTextureId, mFrameBufferId);
    }

    // 模型参数变化
    private void onModelChange(int width, int height) {
        GLES30.glViewport(0, 0, mBitmapSize.x, mBitmapSize.y);
    }

    // 模型绘制
    private void onModelDraw() {
        GLES30.glUseProgram(mProgramId);
        // 准备顶点坐标和纹理坐标
        GLES30.glVertexAttribPointer(0, VERTEX_DIMENSION, GLES30.GL_FLOAT, false, 0, mVertexBuffer);
        GLES30.glVertexAttribPointer(1, TEXTURE_DIMENSION, GLES30.GL_FLOAT, false, 0, mFboTextureBuffer);
        // 激活纹理
        GLES30.glActiveTexture(GLES30.GL_TEXTURE);
        // 绑定纹理
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextureId[0]);
        // 绑定缓存
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, mFrameBufferId[0]);
        // 绘制贴图
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4);
        showBitmap();
    }

    private void showBitmap() {
        // 分配字节缓区大小， 一个像素4个字节
        ByteBuffer byteBuffer = ByteBuffer.allocate(mBitmapSize.x * mBitmapSize.y * Integer.BYTES);
        GLES30.glReadPixels(0, 0, mBitmapSize.x, mBitmapSize.y, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, byteBuffer);
        bitmap = Bitmap.createBitmap(mBitmapSize.x, mBitmapSize.y, Bitmap.Config.ARGB_8888);
        // 从缓存区读二进制缓冲数据
        bitmap.copyPixelsFromBuffer(byteBuffer);
        // 回调
    }

    @Override
    public void onSurfaceCreated() {
        //设置背景颜色
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        //启动深度测试
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        //创建程序id
        onModelCreate();
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        onModelChange(width, height);
    }

    @Override
    public void onDrawFrame() {
        GLES30.glClearColor(0.5f, 0.7f, 0.3f, 1.0f);
        // 将颜色缓存区设置为预设的颜色
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
        // 启用顶点的数组句柄
        GLES30.glEnableVertexAttribArray(0);
        GLES30.glEnableVertexAttribArray(1);
        // 绘制模型
        onModelDraw();
        // 禁止顶点数组句柄
        GLES30.glDisableVertexAttribArray(0);
        GLES30.glDisableVertexAttribArray(1);
    }
}