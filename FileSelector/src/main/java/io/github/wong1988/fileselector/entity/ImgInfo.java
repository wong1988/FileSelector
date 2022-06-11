package io.github.wong1988.fileselector.entity;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import io.github.wong1988.fileselector.attr.ImageType;

public class ImgInfo {

    private final String path;
    private final ImageType type;

    /**
     * @param path 图片路径
     * @param type 图片类型
     */
    public ImgInfo(@NonNull String path, @NonNull ImageType type) {
        if (TextUtils.isEmpty(path) && type == ImageType.ImageResource)
            path = "0";
        this.path = path;
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public ImageType getType() {
        return type;
    }
}
