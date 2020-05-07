package com.sgxy.hzy.photoselector.album;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.sgxy.hzy.photoselector.album.ScannerTypeKt.IMAGE_GIF;
import static com.sgxy.hzy.photoselector.album.ScannerTypeKt.IMAGE_JPEG;
import static com.sgxy.hzy.photoselector.album.ScannerTypeKt.IMAGE_JPG;
import static com.sgxy.hzy.photoselector.album.ScannerTypeKt.IMAGE_PNG;

/**
 * @Description 图片扫描工具
 */
public class ImageScanner {

    private static final String TAG = "ImageScanner";

    private Context mContext;
    private ScanImagesTask scanImagesTask;
    private boolean hasCancel = false;
    private ArrayList<String> scannerTypes;

    public ImageScanner(Context context) {
        this.mContext = context;
    }

    public void onDestroy() {
        hasCancel = true;
        if (scanImagesTask != null) {
            scanImagesTask.cancel(true);
            scanImagesTask = null;
        }
    }

    public void scanImages(final ScanCompleteCallBack callback) {
        scanImages(false, callback);
    }

    public void scanImages(boolean supportGif, final ScanCompleteCallBack callback) {
        if (supportGif) addScannerType(IMAGE_GIF);
        //  添加默认图片类型
        addScannerType(IMAGE_PNG);
        addScannerType(IMAGE_JPEG);
        addScannerType(IMAGE_JPG);
        scanImagesTask = new ScanImagesTask(callback);
        scanImagesTask.execute();
    }

    private void addScannerType(@ScannerType String type) {
        if (scannerTypes == null) scannerTypes = new ArrayList<>();
        scannerTypes.add(type);
    }

    class ScanImagesTask extends AsyncTask<Void, Integer, List<PhotoAlbum>> {

        ScanCompleteCallBack callback;

        public ScanImagesTask(ScanCompleteCallBack callback) {
            this.callback = callback;
        }

        @Override
        protected void onPreExecute() {
            callback.onPreExecute();
        }

        @Override
        protected List<PhotoAlbum> doInBackground(Void... voids) {
            return getPhotoAlbum();
        }

        @Override
        protected void onPostExecute(List<PhotoAlbum> photoAlbums) {
            callback.onScanComplete(photoAlbums);
        }
    }

    private List<PhotoAlbum> getPhotoAlbum() {
        ArrayList<PhotoAlbum> photoAlbumList = new ArrayList<PhotoAlbum>();//相册列表
        PhotoAlbum allPhotoAlbum = new PhotoAlbum("所有照片");//所有照片的相册

        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver mContentResolver = mContext.getContentResolver();
        Cursor cursor = null;
        try {
            cursor = mContentResolver.query(mImageUri, null,
                    getQuerySelection(scannerTypes),
                    getSelectionArgs(scannerTypes),
                    MediaStore.Images.Media.DATE_MODIFIED + " DESC");

            //cursor.moveToFirst();
            HashMap<String, PhotoAlbum> photoAlbumMap = new HashMap<String, PhotoAlbum>();
            PhotoAlbum photoAlbum = null;
            while (cursor.moveToNext()) {
                if (hasCancel) {
                    return photoAlbumList;
                }
                String dirId = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID));
                String dirName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED));
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                String type = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE));

                if (!fileIsExists(path)) {
                    continue;
                }

                if (!photoAlbumMap.containsKey(dirId)) {
                    //添加相册
                    photoAlbum = new PhotoAlbum();
                    photoAlbum.name = dirName;
                    photoAlbum.count++;
                    photoAlbum.path = path;

                    PhotoInfo info = new PhotoInfo(path, date, type);
                    photoAlbum.photoList.add(info);

                    allPhotoAlbum.photoList.add(info);
                    allPhotoAlbum.count++;

                    photoAlbumMap.put(dirId, photoAlbum);
                } else {
                    //相册添加图片
                    photoAlbum = photoAlbumMap.get(dirId);
                    photoAlbum.count++;

                    PhotoInfo info = new PhotoInfo(path, date, type);
                    photoAlbum.photoList.add(info);

                    allPhotoAlbum.photoList.add(info);
                    allPhotoAlbum.count++;
                }
            }

            //遍历输出
            Iterable<String> it = photoAlbumMap.keySet();
            for (String key : it) {
                photoAlbumList.add(photoAlbumMap.get(key));
            }

            if (!photoAlbumList.isEmpty()) {
                allPhotoAlbum.path = photoAlbumList.get(0).photoList.get(0).path;
                photoAlbumList.add(0, allPhotoAlbum);
            }
        } catch (Exception e) {// 可能有数据库读取异常
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return photoAlbumList;
    }

    private String getQuerySelection(ArrayList<String> types) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < types.size() - 1; i++) {
            sb.append(MediaStore.Images.Media.MIME_TYPE + "=? or ");
        }
        sb.append(MediaStore.Images.Media.MIME_TYPE + "=? ");
        return sb.toString();
    }

    private String[] getSelectionArgs(ArrayList<String> types) {
        return types.toArray(new String[0]);
    }

    public boolean fileIsExists(String filePath) {
        try {
            return new File(filePath).exists();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @Description 扫描结束监听
     */
    public interface ScanCompleteCallBack {
        /**
         * 当扫描之前需要进行的操作
         */
        void onPreExecute();

        /**
         * 当扫描结束
         */
        void onScanComplete(List<PhotoAlbum> photoAlbumList);
    }
}
