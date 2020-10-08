//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.cocos2dx.lib;

import android.graphics.Bitmap;
import java.util.Iterator;
import java.util.List;

public class SpineEventManager {
    public static final int CODE_PLAY_GIFT = 1;
    public static final int CODE_RELEASE = 2;
    public static final int CODE_CLEAR_CURRENT = 3;
    private static final SpineEventManager instance = new SpineEventManager();

    private SpineEventManager() {
    }

    public static SpineEventManager ins() {
        return instance;
    }

    public void postEvent(int eventCode) {
        this.postEvent(eventCode, (String)null);
    }

    public void postEvent(int eventCode, String resPath) {
        this.postEvent(eventCode, resPath, (List)null);
    }

    public void postEvent(int eventCode, String resPath, List<SpineHeadEntity> entities) {
        this.checkData(entities);
        this.post(eventCode, resPath, entities);
    }

    private void checkData(List<SpineHeadEntity> entities) {
        if (entities != null && entities.size() != 0) {
            Iterator iterator = entities.iterator();

            while(true) {
                SpineHeadEntity next;
                do {
                    if (!iterator.hasNext()) {
                        return;
                    }

                    next = (SpineHeadEntity)iterator.next();
                } while(next != null && next.bitmap instanceof Bitmap);

                iterator.remove();
            }
        }
    }

    private native void post(int var1, String var2, List<SpineHeadEntity> var3);
}
