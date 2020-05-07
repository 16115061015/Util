package com.sgxy.hzy.photoselector.util;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.sgxy.hzy.photoselector.album.PhotoInfo;
import com.sgxy.hzy.photoselector.album.PhotoSelectorActivity;
import com.sgxy.hzy.photoselector.avoidonresult.AvoidOnResult;
import com.sgxy.hzy.photoselector.camerax.CameraXActivity;
import com.sgxy.hzy.photoselector.crop.PhotoCropActivity;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class PhotoSelectorUtil {

    /**
     * @param activity  上下文
     * @param isCrop    是否裁剪
     * @param sizeScale 裁剪的长宽比
     * @param column    图片选择列数
     * @param callback  回调
     */
    public static void selectSinglePhoto(Activity activity, boolean isCrop, float sizeScale, int column, final SelectedPhotoCallback callback) {
        if (checkStorage(activity)) {
            Intent intent = PhotoSelectorActivity.getSingleIntent(activity, isCrop, sizeScale, column);
            if (!canResolveActivity(activity, intent)) {
                return;
            }
            new AvoidOnResult(activity).startForResult(intent, new AvoidOnResult.Callback() {
                @Override
                public void onActivityResult(int resultCode, Intent data) {
                    if (data != null && data.hasExtra(PhotoSelectorActivity.SELECT_DATA)) {
                        List<PhotoInfo> selectedList = (List<PhotoInfo>) data.getSerializableExtra(PhotoSelectorActivity.SELECT_DATA);
                        callback.onActivityResult(selectedList);
                    }
                }
            });
        }
    }

    /***
     * @param activity
     * @param isCrop
     * @param sizeScale
     * @param column
     * @param isGif 是否支持Gif
     * @param callback
     */
    public static void selectSinglePhoto(Activity activity, boolean isCrop, float sizeScale, int column, Boolean isGif, final SelectedPhotoCallback callback) {
        if (checkStorage(activity)) {
            Intent intent = PhotoSelectorActivity.getSingleIntent(activity, isCrop, sizeScale, column, isGif);
            if (!canResolveActivity(activity, intent)) {
                return;
            }
            new AvoidOnResult(activity).startForResult(intent, new AvoidOnResult.Callback() {
                @Override
                public void onActivityResult(int resultCode, Intent data) {
                    if (data != null && data.hasExtra(PhotoSelectorActivity.SELECT_DATA)) {
                        List<PhotoInfo> selectedList = (List<PhotoInfo>) data.getSerializableExtra(PhotoSelectorActivity.SELECT_DATA);
                        callback.onActivityResult(selectedList);
                    }
                }
            });
        }
    }

    /**
     * @param activity       上下文
     * @param column         图片选择列数
     * @param maxSelectCount 最大选择数
     * @param callback       回调
     */
    public static void selectMultiplePhoto(Activity activity, int column, int maxSelectCount, final SelectedPhotoCallback callback) {
        selectMultiplePhoto(activity, column, maxSelectCount, false, callback);
    }

    /**
     * @param activity       上下文
     * @param column         图片选择列数
     * @param showGif        显示Gif图
     * @param maxSelectCount 最大选择数
     * @param callback       回调
     */
    public static void selectMultiplePhoto(Activity activity, int column, int maxSelectCount, boolean showGif, final SelectedPhotoCallback callback) {
        if (checkStorage(activity)) {
            Intent intent = PhotoSelectorActivity.getMultipleIntent(activity, column, maxSelectCount, showGif);
            if (!canResolveActivity(activity, intent)) {
                return;
            }
            new AvoidOnResult(activity).startForResult(intent, new AvoidOnResult.Callback() {
                @Override
                public void onActivityResult(int resultCode, Intent data) {
                    if (data != null && data.hasExtra(PhotoSelectorActivity.SELECT_DATA)) {
                        List<PhotoInfo> selectedList = (List<PhotoInfo>) data.getSerializableExtra(PhotoSelectorActivity.SELECT_DATA);
                        callback.onActivityResult(selectedList);
                    }
                }
            });
        }
    }

    /**
     * 启动前置摄像头拍照
     */
    @TargetApi(21)
    public static void takePhotoFront(final Activity activity, final String path,
                                      final boolean isCrop, final float sizeScale, final PhotoSelectorUtil.SelectedPhotoCallback callback) {
        if (hasPermissions(activity, Manifest.permission.CAMERA)) {
            if (!hasFrontCamera()) {
                return;
            }

            Intent intent = CameraXActivity.getStartIntent(activity, path);
            if (!canResolveActivity(activity, intent)) {
                return;
            }

            new AvoidOnResult(activity).startForResult(intent, new AvoidOnResult.Callback() {
                @Override
                public void onActivityResult(int resultCode, Intent data) {
                    if (ImageUtil.fileIsExists(path)) {
                        if (isCrop) {
                            showCrop(activity, ImageUtil.FrescoLocalFileURI + path, sizeScale, callback);
                        } else {
                            ArrayList<PhotoInfo> selectedDataList = new ArrayList<>();
                            PhotoInfo photoInfo = new PhotoInfo();
                            photoInfo.path = path;
                            selectedDataList.add(photoInfo);
                            callback.onActivityResult(selectedDataList);
                        }
                    } else if (resultCode == Activity.RESULT_CANCELED) {
                        ArrayList<PhotoInfo> selectedDataList = new ArrayList<>();
                        callback.onActivityResult(selectedDataList);
                    }
                }
            });
        } else {
            new ToastCompat().showToast(activity, "需要打开相机权限,才能正常使用");
        }
    }

    public static void takePhoto(final Activity activity, final Uri imageCaptureUri,
                                 final boolean isCrop, final float sizeScale, final PhotoSelectorUtil.SelectedPhotoCallback callback) {
        takePhoto(activity, imageCaptureUri, false, isCrop, sizeScale, callback);
    }

    /**
     * 检测是否有前置摄像头
     */
    private static boolean hasFrontCamera() {
        int num = Camera.getNumberOfCameras();
        for (int cameraId = 0; cameraId < num; cameraId++) {
            Camera.CameraInfo info = new CameraInfo();
            Camera.getCameraInfo(cameraId, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param activity        上下文
     * @param imageCaptureUri 保存文件位置
     * @param isCrop          是否裁剪
     * @param sizeScale       裁剪的长宽比
     * @param callback        回调
     */
    public static void takePhoto(final Activity activity, final Uri imageCaptureUri, boolean isFacing,
                                 final boolean isCrop, final float sizeScale, final PhotoSelectorUtil.SelectedPhotoCallback callback) {
        if (hasPermissions(activity, Manifest.permission.CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageCaptureUri);
            intent.putExtra("return-data", true);
            if (isFacing && hasFrontCamera()) {
                intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
                intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
                intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
            }

            if (!canResolveActivity(activity, intent)) {
                return;
            }

            new AvoidOnResult(activity).startForResult(intent, new AvoidOnResult.Callback() {
                @Override
                public void onActivityResult(int resultCode, Intent data) {
                    String realPath = ImageUtil.getRealFilePath(activity, imageCaptureUri);
                    if (ImageUtil.fileIsExists(realPath)) {
                        if (isCrop) {
                            showCrop(activity, ImageUtil.FrescoLocalFileURI + realPath, sizeScale, callback);
                        } else {
                            ArrayList<PhotoInfo> selectedDataList = new ArrayList<>();
                            PhotoInfo photoInfo = new PhotoInfo();
                            photoInfo.path = realPath;
                            selectedDataList.add(photoInfo);
                            callback.onActivityResult(selectedDataList);
                        }
                    } else if (resultCode == Activity.RESULT_CANCELED) {
                        ArrayList<PhotoInfo> selectedDataList = new ArrayList<>();
                        callback.onActivityResult(selectedDataList);
                    }
                }
            });
        } else {
            new ToastCompat().showToast(activity, "需要打开相机权限,才能正常使用");
        }
    }

    /**
     * @param activity  上下文
     * @param url       图片地址
     * @param sizeScale 裁剪的长宽比
     * @param callback  回调
     */
    public static void showCrop(Activity activity, String url, float sizeScale, final SelectedPhotoCallback callback) {
        if (checkStorage(activity)) {
            Intent intent = PhotoCropActivity.getStartIntent(activity, url, sizeScale);

            if (!canResolveActivity(activity, intent)) {
                return;
            }

            new AvoidOnResult(activity).startForResult(intent, new AvoidOnResult.Callback() {
                @Override
                public void onActivityResult(int resultCode, Intent data) {
                    if (resultCode == PhotoCropActivity.ACTION_COMPLETE) {
                        if (data != null && data.hasExtra(PhotoCropActivity.SAVE_PATH)) {
                            String path = data.getStringExtra(PhotoCropActivity.SAVE_PATH);
                            ArrayList<PhotoInfo> selectedDataList = new ArrayList<>();
                            PhotoInfo photoInfo = new PhotoInfo();
                            photoInfo.path = path;
                            selectedDataList.add(photoInfo);
                            callback.onActivityResult(selectedDataList);
                        }
                    } else if (resultCode == PhotoCropActivity.ACTION_BACK) {
                        ArrayList<PhotoInfo> selectedDataList = new ArrayList<>();
                        callback.onActivityResult(selectedDataList);
                    }
                }
            });
        }
    }

    public static boolean checkStorage(Context context) {
        boolean result = hasPermissions(context, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE);
        if (!result) {
            new ToastCompat().showToast(context, "需要打开存储权限,才能正常使用");
        }
        return result;
    }

    public static boolean hasPermissions(Context context, @NonNull String... perms) {
        // 如果低于版本M，就返回true，因为已经拥有权限
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }

        for (String perm : perms) {
            boolean hasPerm = (ContextCompat.checkSelfPermission(context.getApplicationContext(), perm) ==
                    PackageManager.PERMISSION_GRANTED);
            if (!hasPerm) {
                return false;
            }
        }

        return true;
    }


    private static boolean canResolveActivity(Context context, @NonNull Intent intent) {
        if (context == null) {
            return false;
        }

        return intent.resolveActivity(context.getPackageManager()) != null;
    }

    public interface SelectedPhotoCallback {

        void onActivityResult(List<PhotoInfo> selectedList);
    }


}
