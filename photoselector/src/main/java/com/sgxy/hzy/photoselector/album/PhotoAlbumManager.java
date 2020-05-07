package com.sgxy.hzy.photoselector.album;

import java.util.ArrayList;
import java.util.List;

public class PhotoAlbumManager {
    private List<PhotoAlbum> photoAlbumList;
    private PhotoAlbum currentAlbum;

    private PhotoAlbumManager() {
    }

    public static final PhotoAlbumManager ins() {
        return PhotoAlbumManager.Holder.INSTANCE;
    }

    private static class Holder {
        private static final PhotoAlbumManager INSTANCE = new PhotoAlbumManager();
    }

    public List<PhotoAlbum> getPhotoAlbumList() {
        if (this.photoAlbumList == null) {
            this.photoAlbumList = new ArrayList<>();
        }
        return photoAlbumList;
    }

    public void setPhotoAlbumList(List<PhotoAlbum> photoAlbumList) {
        if (photoAlbumList != null && !photoAlbumList.isEmpty()) {
            if (this.photoAlbumList == null) {
                this.photoAlbumList = new ArrayList<>();
            }
            this.photoAlbumList.clear();
            this.photoAlbumList.addAll(photoAlbumList);
        }
    }

    public PhotoAlbum getCurrentAlbum() {
        return currentAlbum;
    }

    public void setCurrentAlbum(PhotoAlbum currentAlbum) {
        this.currentAlbum = currentAlbum;
    }

    public void release() {
        if (photoAlbumList != null) {
            photoAlbumList.clear();
            photoAlbumList = null;
        }
        currentAlbum = null;
    }
}
