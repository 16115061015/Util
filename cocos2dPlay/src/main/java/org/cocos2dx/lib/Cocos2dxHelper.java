//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.cocos2dx.lib;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.Process;
import android.preference.PreferenceManager.OnActivityResultListener;
import android.util.Log;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class Cocos2dxHelper {
    private static final String PREFS_NAME = "Cocos2dxPrefsFile";
    private static AssetManager sAssetManager;
    private static boolean sActivityVisible;
    private static String sPackageName;
    private static String sFileDirectory;
    private static Context sContext = null;
    private static volatile ArrayList<Cocos2dxHelper.Cocos2dxHelperListener> listeners = new ArrayList();
    private static Set<OnActivityResultListener> onActivityResultListeners = new LinkedHashSet();
    private static final Object lock = new Object();
    private static String sAssetsPath = "";
    private static boolean sInited = false;

    public Cocos2dxHelper() {
    }

    public static void init(Context context, Cocos2dxHelper.Cocos2dxHelperListener listener) {
        sContext = context;
        synchronized (lock) {
            listeners.add(listener);
        }

        if (!sInited) {
            ApplicationInfo applicationInfo = sContext.getApplicationInfo();
            sPackageName = applicationInfo.packageName;
            sFileDirectory = sContext.getFilesDir().getAbsolutePath();
            nativeSetApkPath(getAssetsPath());
            sAssetManager = sContext.getAssets();
            //TODO 这里可能有泄漏
            nativeSetContext(sContext, sAssetManager);
            sInited = true;
        }

    }

    public static void uninit(Cocos2dxHelper.Cocos2dxHelperListener listener) {
        sInited = false;
        sContext = null;
        synchronized (lock) {
            listeners.remove(listener);
            Log.i("forTest", "helper uninit " + listener);
        }
    }

    public static String getAssetsPath() {
        if (sAssetsPath == "") {
            int versionCode = 1;

            try {
                versionCode = sContext.getPackageManager().getPackageInfo(sPackageName, 0).versionCode;
            } catch (NameNotFoundException var3) {
                var3.printStackTrace();
            }

            String pathToOBB = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/obb/" + sPackageName + "/main." + versionCode + "." + sPackageName + ".obb";
            File obbFile = new File(pathToOBB);
            if (obbFile.exists()) {
                sAssetsPath = pathToOBB;
            } else {
                sAssetsPath = sContext.getApplicationInfo().sourceDir;
            }
        }

        return sAssetsPath;
    }

    public static void addOnActivityResultListener(OnActivityResultListener listener) {
        onActivityResultListeners.add(listener);
    }

    public static Set<OnActivityResultListener> getOnActivityResultListeners() {
        return onActivityResultListeners;
    }

    public static boolean isActivityVisible() {
        return sActivityVisible;
    }

    private static native void nativeSetApkPath(String var0);

    private static native void nativeSetEditTextDialogResult(byte[] var0);

    private static native void nativeSetContext(Context var0, AssetManager var1);

    private static native void nativeSetAudioDeviceInfo(boolean var0, int var1, int var2);

    public static String getCocos2dxPackageName() {
        return sPackageName;
    }

    public static String getCocos2dxWritablePath() {
        return sFileDirectory;
    }

    public static String getCurrentLanguage() {
        return Locale.getDefault().getLanguage();
    }

    public static String getDeviceModel() {
        return Build.MODEL;
    }

    public static AssetManager getAssetManager() {
        return sAssetManager;
    }

    public static void enableAccelerometer() {
    }

    public static void enableCompass() {
    }

    public static void setAccelerometerInterval(float interval) {
    }

    public static void disableAccelerometer() {
    }

    public static void setKeepScreenOn(boolean value) {
    }

    public static void vibrate(float duration) {
    }

    public static boolean openURL(String url) {
        boolean ret = false;

        try {
            Intent i = new Intent("android.intent.action.VIEW");
            i.setData(Uri.parse(url));
            sContext.startActivity(i);
            ret = true;
        } catch (Exception var3) {
        }

        return ret;
    }

    public static void preloadBackgroundMusic(String pPath) {
    }

    public static void playBackgroundMusic(String pPath, boolean isLoop) {
    }

    public static void resumeBackgroundMusic() {
    }

    public static void pauseBackgroundMusic() {
    }

    public static void stopBackgroundMusic() {
    }

    public static void rewindBackgroundMusic() {
    }

    public static boolean willPlayBackgroundMusic() {
        return false;
    }

    public static boolean isBackgroundMusicPlaying() {
        return false;
    }

    public static float getBackgroundMusicVolume() {
        return 0.0F;
    }

    public static void setBackgroundMusicVolume(float volume) {
    }

    public static void preloadEffect(String path) {
    }

    public static int playEffect(String path, boolean isLoop, float pitch, float pan, float gain) {
        return 0;
    }

    public static void resumeEffect(int soundId) {
    }

    public static void pauseEffect(int soundId) {
    }

    public static void stopEffect(int soundId) {
    }

    public static float getEffectsVolume() {
        return 0.0F;
    }

    public static void setEffectsVolume(float volume) {
    }

    public static void unloadEffect(String path) {
    }

    public static void pauseAllEffects() {
    }

    public static void resumeAllEffects() {
    }

    public static void stopAllEffects() {
    }

    static void setAudioFocus(boolean isAudioFocus) {
    }

    public static void end() {
    }

    public static void onResume() {
        sActivityVisible = true;
    }

    public static void onPause() {
        sActivityVisible = false;
    }

    public static void onEnterBackground() {
    }

    public static void onEnterForeground() {
    }

    public static void terminateProcess() {
        Process.killProcess(Process.myPid());
    }

    private static void showDialog(String pTitle, String pMessage) {
        synchronized (lock) {
            Iterator var3 = listeners.iterator();

            while (var3.hasNext()) {
                Cocos2dxHelper.Cocos2dxHelperListener listener = (Cocos2dxHelper.Cocos2dxHelperListener) var3.next();
                if (listener != null) {
                    listener.showDialog(pTitle, pMessage);
                }
            }

        }
    }

    private static void animComplete(String filepathName) {
        synchronized (lock) {
            Iterator var2 = listeners.iterator();

            while (var2.hasNext()) {
                Cocos2dxHelper.Cocos2dxHelperListener listener = (Cocos2dxHelper.Cocos2dxHelperListener) var2.next();
                if (listener != null) {
                    listener.animComplete(filepathName);
                }
            }

        }
    }

    private static void log(String logString) {
        synchronized (lock) {
            Iterator var2 = listeners.iterator();

            while (var2.hasNext()) {
                Cocos2dxHelper.Cocos2dxHelperListener listener = (Cocos2dxHelper.Cocos2dxHelperListener) var2.next();
                if (listener != null) {
                    listener.log(logString);
                }
            }

        }
    }

    public static void setEditTextDialogResult(String pResult) {
    }

    public static boolean getBoolForKey(String key, boolean defaultValue) {
        SharedPreferences settings = sContext.getSharedPreferences("Cocos2dxPrefsFile", 0);

        try {
            return settings.getBoolean(key, defaultValue);
        } catch (Exception var7) {
            var7.printStackTrace();
            Map allValues = settings.getAll();
            Object value = allValues.get(key);
            if (value instanceof String) {
                return Boolean.parseBoolean(value.toString());
            } else if (value instanceof Integer) {
                int intValue = (Integer) value;
                return intValue != 0;
            } else if (value instanceof Float) {
                float floatValue = (Float) value;
                return floatValue != 0.0F;
            } else {
                return defaultValue;
            }
        }
    }

    public static int getIntegerForKey(String key, int defaultValue) {
        SharedPreferences settings = sContext.getSharedPreferences("Cocos2dxPrefsFile", 0);

        try {
            return settings.getInt(key, defaultValue);
        } catch (Exception var7) {
            var7.printStackTrace();
            Map allValues = settings.getAll();
            Object value = allValues.get(key);
            if (value instanceof String) {
                return Integer.parseInt(value.toString());
            } else if (value instanceof Float) {
                return ((Float) value).intValue();
            } else {
                if (value instanceof Boolean) {
                    boolean booleanValue = (Boolean) value;
                    if (booleanValue) {
                        return 1;
                    }
                }

                return defaultValue;
            }
        }
    }

    public static float getFloatForKey(String key, float defaultValue) {
        SharedPreferences settings = sContext.getSharedPreferences("Cocos2dxPrefsFile", 0);

        try {
            return settings.getFloat(key, defaultValue);
        } catch (Exception var7) {
            var7.printStackTrace();
            Map allValues = settings.getAll();
            Object value = allValues.get(key);
            if (value instanceof String) {
                return Float.parseFloat(value.toString());
            } else if (value instanceof Integer) {
                return ((Integer) value).floatValue();
            } else {
                if (value instanceof Boolean) {
                    boolean booleanValue = (Boolean) value;
                    if (booleanValue) {
                        return 1.0F;
                    }
                }

                return defaultValue;
            }
        }
    }

    public static double getDoubleForKey(String key, double defaultValue) {
        return (double) getFloatForKey(key, (float) defaultValue);
    }

    public static String getStringForKey(String key, String defaultValue) {
        SharedPreferences settings = sContext.getSharedPreferences("Cocos2dxPrefsFile", 0);

        try {
            return settings.getString(key, defaultValue);
        } catch (Exception var4) {
            var4.printStackTrace();
            return settings.getAll().get(key).toString();
        }
    }

    public static void setBoolForKey(String key, boolean value) {
        SharedPreferences settings = sContext.getSharedPreferences("Cocos2dxPrefsFile", 0);
        Editor editor = settings.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static void setIntegerForKey(String key, int value) {
        SharedPreferences settings = sContext.getSharedPreferences("Cocos2dxPrefsFile", 0);
        Editor editor = settings.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static void setFloatForKey(String key, float value) {
        SharedPreferences settings = sContext.getSharedPreferences("Cocos2dxPrefsFile", 0);
        Editor editor = settings.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public static void setDoubleForKey(String key, double value) {
        SharedPreferences settings = sContext.getSharedPreferences("Cocos2dxPrefsFile", 0);
        Editor editor = settings.edit();
        editor.putFloat(key, (float) value);
        editor.apply();
    }

    public static void setStringForKey(String key, String value) {
        SharedPreferences settings = sContext.getSharedPreferences("Cocos2dxPrefsFile", 0);
        Editor editor = settings.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void deleteValueForKey(String key) {
        SharedPreferences settings = sContext.getSharedPreferences("Cocos2dxPrefsFile", 0);
        Editor editor = settings.edit();
        editor.remove(key);
        editor.apply();
    }

    public static byte[] conversionEncoding(byte[] text, String fromCharset, String newCharset) {
        try {
            String str = new String(text, fromCharset);
            return str.getBytes(newCharset);
        } catch (UnsupportedEncodingException var4) {
            var4.printStackTrace();
            return null;
        }
    }

    public static float[] getAccelValue() {
        return new float[0];
    }

    public static float[] getCompassValue() {
        return new float[0];
    }

    public static int getSDKVersion() {
        return VERSION.SDK_INT;
    }

    public interface Cocos2dxHelperListener {
        void showDialog(String var1, String var2);

        void runOnGLThread(Runnable var1);

        void animComplete(String var1);

        void log(String var1);
    }
}
