package com.placesware.voteus.models;

import android.graphics.Bitmap;
import android.util.LruCache;

import java.util.UUID;

/**
 * Created by kathrynkillebrew on 8/13/14.
 */
public class CandidatePhotoCache {
    private final LruCache<UUID, Bitmap> mMemoryCache;

    public CandidatePhotoCache() {
        // Get max available VM memory; exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes, as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<UUID, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(UUID key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than number of items.
                return bitmap.getByteCount() / 1024;
            }
        };

    }

    public UUID addBitmapToMemoryCache(Bitmap bitmap) {
        UUID uuid = UUID.randomUUID();
        mMemoryCache.put(uuid, bitmap);
        return uuid;
    }

    public Bitmap getBitmapFromMemoryCache(UUID key) {
        return mMemoryCache.get(key);
    }
}
