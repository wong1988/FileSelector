package io.github.wong1988.fileselector.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import io.github.wong1988.fileselector.R;
import io.github.wong1988.fileselector.attr.ImageType;

public class ImgInfo implements Parcelable {

    private String path;
    private ImageType type;
    private boolean forbidDelete;

    /**
     * 此为新增需求：支持部分图片不允许删除  forbidDelete = true
     *
     * @param forbidDelete 是否可以删除由 PreviewImgView setButton() 与 forbidDelete 共同决定
     */
    public ImgInfo(@NonNull String path, @NonNull ImageType type, boolean forbidDelete) {
        this(path, type);
        this.forbidDelete = forbidDelete;
    }

    /**
     * @param path 图片路径
     * @param type 图片类型
     */
    public ImgInfo(@NonNull String path, @NonNull ImageType type) {

        if (type == ImageType.ImageResource) {
            try {
                Integer.parseInt(path);
            } catch (Exception e) {
                path = String.valueOf(R.drawable.github_a_error);
            }
        }
        this.path = path;
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public ImageType getType() {
        return type;
    }

    public boolean isForbidDelete() {
        return forbidDelete;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.path);
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
        dest.writeByte(this.forbidDelete ? (byte) 1 : (byte) 0);
    }

    public void readFromParcel(Parcel source) {
        this.path = source.readString();
        int tmpType = source.readInt();
        this.type = tmpType == -1 ? null : ImageType.values()[tmpType];
        this.forbidDelete = source.readByte() != 0;
    }

    protected ImgInfo(Parcel in) {
        this.path = in.readString();
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : ImageType.values()[tmpType];
        this.forbidDelete = in.readByte() != 0;
    }

    public static final Creator<ImgInfo> CREATOR = new Creator<ImgInfo>() {
        @Override
        public ImgInfo createFromParcel(Parcel source) {
            return new ImgInfo(source);
        }

        @Override
        public ImgInfo[] newArray(int size) {
            return new ImgInfo[size];
        }
    };
}
