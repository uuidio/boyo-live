package com.meihu.phonebeautyui.ui.filter;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;

import com.ksyun.media.streamer.capture.ImgTexSrcPin;
import com.ksyun.media.streamer.filter.imgtex.ImgTexFilter;
import com.ksyun.media.streamer.framework.ImgTexFormat;
import com.ksyun.media.streamer.framework.ImgTexFrame;
import com.ksyun.media.streamer.framework.SinkPin;
import com.ksyun.media.streamer.framework.SrcPin;
import com.ksyun.media.streamer.util.gles.GLRender;
import com.meihu.beautylibrary.filter.glfilter.utils.OpenGLUtils;
import com.meihu.beautylibrary.filter.glfilter.utils.TextureRotationUtils;
import com.meihu.beautylibrary.manager.MHBeautyManager;
import com.meihu.beautylibrary.render.filter.ksyFilter.GLImageVertFlipFilter;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

/**
 * Created by kxr on 2019/11/04.
 */

public class MHFilter extends ImgTexFilter {
    private static String TAG = MHFilter.class.getSimpleName();
//    private SrcPin<ImgTexFrame> mSrcPin;
    private GLRender mGLRender;
    private int mOutTexture = ImgTexFrame.NO_TEXTURE;
    private SinkPin<ImgTexFrame> mTexSinkPin;
    private final Object BUF_LOCK = new Object();
    private MHBeautyManager mhBeautyManager;
    int mOutFrameBuffer = -1;
    private int[] mViewPort = new int[4];
    private ImgTexSrcPin mSrcPin;
    private Context mContext;

    private GLImageVertFlipFilter glImageVertFlipFilter;

    public MHFilter(Context context, MHBeautyManager mhSDKManager, GLRender glRender) {
        super(glRender/*, FilterShaderManager.getInstance().kShaderString1, FilterShaderManager.getInstance().stringShader2*/);
        mContext =  context;
        mhBeautyManager = mhSDKManager;
        mGLRender = glRender;
        mTexSinkPin = new TiFancyTexSinPin();
//        mSrcPin = new SrcPin<>();
        mSrcPin = new ImgTexSrcPin(glRender);
    }

    @Override
    public SinkPin<ImgTexFrame> getSinkPin() {
        return mTexSinkPin;
    }

    @Override
    public SrcPin<ImgTexFrame> getSrcPin() {
//        return mSrcPin;
        return mSrcPin;
    }

    @Override
    public int getSinkPinNum() {
        return 2;
    }

    private class TiFancyTexSinPin extends SinkPin<ImgTexFrame> {
        @Override
        public void onFormatChanged(Object format) {
            ImgTexFormat fmt = (ImgTexFormat) format;
            mSrcPin.onFormatChanged(fmt);
        }

        public   Bitmap saveTexture(int texture, int width, int height) {
            int[] frame = new int[1];
            GLES20.glGenFramebuffers(1, frame, 0);
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frame[0]);
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, texture, 0);

            ByteBuffer buffer = ByteBuffer.allocate(width * height * 4);
            GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buffer);
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.copyPixelsFromBuffer(buffer);

            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
            GLES20.glDeleteFramebuffers(1, frame, 0);
            return bitmap;
        }

        @Override
        public void onFrameAvailable(ImgTexFrame frame) {
//            mOutTexture = frame.textureId;
            int  texId = frame.textureId;
            if (mhBeautyManager == null) {
                ImgTexFrame outFrame = new ImgTexFrame(frame.format, texId, null, frame.pts);
                mSrcPin.onFrameAvailable(outFrame);
                return;
            }
            if (mSrcPin.isConnected()) {
                synchronized (BUF_LOCK) {

                    if(glImageVertFlipFilter==null){
                        glImageVertFlipFilter = new GLImageVertFlipFilter(mContext);
                        glImageVertFlipFilter.onDisplaySizeChanged(frame.format.width,frame.format.height);
                        glImageVertFlipFilter.onInputSizeChanged(frame.format.width,frame.format.height);
                        glImageVertFlipFilter.initFrameBuffer(frame.format.width,frame.format.height);
                    }

                    FloatBuffer mVertexBuffer = OpenGLUtils.createFloatBuffer(TextureRotationUtils.CubeVertices);
                    FloatBuffer mTextureBuffer = OpenGLUtils.createFloatBuffer(TextureRotationUtils.TextureVertices);

                    texId  = glImageVertFlipFilter.drawFrameBuffer(frame.textureId,mVertexBuffer,mTextureBuffer);

                    if (mhBeautyManager != null) {
                         int ret =  mhBeautyManager.render(texId,  frame.format.width, frame.format.height);
                         if(ret!=-1){
                             texId = ret;
                         }
                    }
                    texId  = glImageVertFlipFilter.drawFrameBuffer(texId,mVertexBuffer,mTextureBuffer);

                }
            }

            ImgTexFrame outFrame = new ImgTexFrame(frame.format,texId, null, frame.pts);
            mSrcPin.onFrameAvailable(outFrame);
        }

        @Override
        public void onDisconnect(boolean recursive) {
            if (recursive) {
                mSrcPin.disconnect(true);
//                if (mOutTexture != ImgTexFrame.NO_TEXTURE) {
//                    mGLRender.getFboManager().unlock(mOutTexture);
//                    mOutTexture = ImgTexFrame.NO_TEXTURE;
//                }
                if (mhBeautyManager != null) {
                    mhBeautyManager.destroy();
                    mhBeautyManager = null;
                }
            }
        }
    }
}
