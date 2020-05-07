package com.sgxy.hzy.photoselector.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;

import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.executors.UiThreadImmediateExecutorService;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.Priority;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ImageUtil {

    public static final String FrescoLocalFileURI = "file://";

    public static void getBitmap(final String url, int width, int height, ImageRequest.CacheChoice CacheChoice,
                                 final BaseBitmapDataSubscriber subscriber) {
        if (TextUtils.isEmpty(url)) {
            subscriber.onFailure(null);
            return;
        }

        ImageRequest anchorImageRequest = ImageRequestBuilder
                .newBuilderWithSource(Uri.parse(url))
                .setCacheChoice(CacheChoice)
                .setResizeOptions(new ResizeOptions(width, height, 2048f * 5.0f))
                .build();
        ImagePipeline anchorImagePipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<CloseableImage>>
                anchorDataSource = anchorImagePipeline.fetchDecodedImage(anchorImageRequest, "ImageUtil");
        anchorDataSource.subscribe(subscriber, CallerThreadExecutor.getInstance());
    }

    public static void updateImageFromLocal(SimpleDraweeView view, String url, ImageRequest.CacheChoice CacheChoice) {
        if (view == null) {
            return;
        }
        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                .setCacheChoice(CacheChoice)
                .build();
        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                .setImageRequest(imageRequest)
                .setOldController(view.getController())
                .build();
        view.setController(draweeController);
    }

    public static void updateCompressImageFromLocal(SimpleDraweeView view, String url, int width, int height,
                                                    ImageRequest.CacheChoice CacheChoice) {
        String viewDispalyUrl = "";
        Object tagObject = view.getTag();
        if (tagObject != null) {
            viewDispalyUrl = (String) tagObject;
        }
        if (url.compareTo(viewDispalyUrl) != 0) {
            view.setTag(url);
            ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                    .setLocalThumbnailPreviewsEnabled(true)
                    .setCacheChoice(CacheChoice)
                    .setResizeOptions(new ResizeOptions(width, height))
                    .setRequestPriority(Priority.HIGH)
                    .build();
            DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                    .setOldController(view.getController())
                    .setImageRequest(imageRequest)
                    .build();
            view.setController(draweeController);
        }
    }

    public static void updateImageFromNetwork(SimpleDraweeView view, String url, int width, int height, ImageRequest.CacheChoice CacheChoice,
                                              ControllerListener controllerListener) {
        String viewDispalyUrl = "";
        Object tagObject = view.getTag();
        if (tagObject != null) {
            viewDispalyUrl = (String) tagObject;
        }
        if (url.compareTo(viewDispalyUrl) != 0) {
            view.setTag(url);

            ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                    .setCacheChoice(CacheChoice)
                    .setResizeOptions(new ResizeOptions(width, height, 2048f * 5.0f))
                    .build();
            DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(imageRequest)
                    .setOldController(view.getController())
                    .setAutoPlayAnimations(true)
                    .setControllerListener(controllerListener)
                    .build();
            view.setController(draweeController);
        }
    }

    /**
     * 获取屏幕的宽
     */
    public static int getScreenWidth(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    /**
     * 获取屏幕的高
     */
    public static int getScreenHeight(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) {
            return null;
        }
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    public static boolean fileIsExists(String filePath) {
        try {
            return new File(filePath).exists();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 异步读取预加载的图片
     */
    public static void getBitmap(final int picId, String picUrl, final int width, final int height,
                                 final BitmapLoadListener bitmapLoadListener) {
        ImageRequestBuilder imageRequestBuilder = ImageRequestBuilder.newBuilderWithSource(Uri.parse(picUrl));
        if (width > 0 && height > 0) {
            imageRequestBuilder.setResizeOptions(new ResizeOptions(width, height));
        }
        ImageRequest imageRequest = imageRequestBuilder.setAutoRotateEnabled(true)
                .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
                .setProgressiveRenderingEnabled(false)
                .build();
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline.fetchDecodedImage(imageRequest, "ImageUtil");
        dataSource.subscribe(new BaseBitmapDataSubscriber() {
            @Override
            protected void onNewResultImpl(Bitmap bitmap) {
                Bitmap resultBitmap = null;
                if (isBitmapAvailable(bitmap)) {
                    try {
                        resultBitmap = bitmap.copy(bitmap.getConfig(), bitmap.isMutable());
                    } catch (Exception e) {
                    }
                }
                if (null == bitmapLoadListener) {
                    return;
                }
                bitmapLoadListener.onBitmapLoaded(picId, resultBitmap);
            }

            @Override
            protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
                if (null == bitmapLoadListener) {
                    return;
                }
                bitmapLoadListener.onBitmapLoaded(picId, null);

            }
        }, UiThreadImmediateExecutorService.getInstance());
    }

    public static boolean isBitmapAvailable(Bitmap bitmap) {
        if (null == bitmap || "".equals(bitmap) || bitmap.isRecycled()) {// 如果为null或者是已经回收了的就证明是不可用的
            return false;
        }
        return true;
    }

    private static final Map<Integer, Long> lastClickMap = new HashMap<>();

    public static boolean isFastDoubleClick(View view) {
        final long currentClickTime = System.currentTimeMillis();
        final int viewId = System.identityHashCode(view);
        final long lastClickTime = lastClickMap.containsKey(viewId) ? lastClickMap.get(viewId) : 0L;
        final long offsetClickTime = currentClickTime - lastClickTime;

        if (offsetClickTime >= 0 && offsetClickTime <= 500L) {
            return true;
        }

        lastClickMap.put(viewId, currentClickTime);
        return false;
    }

    public interface BitmapLoadListener {
        void onBitmapLoaded(int id, Bitmap bitmap);
    }
}
