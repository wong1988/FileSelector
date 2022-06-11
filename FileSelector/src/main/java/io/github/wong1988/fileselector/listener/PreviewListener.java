package io.github.wong1988.fileselector.listener;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.github.wong1988.fileselector.entity.ImgInfo;

public interface PreviewListener {

    void onAlphaChanged(float alpha);

    void onTranslationYChanged(float translationY);

    /**
     * 关闭页面时同时回调删除的图片以及未删除的图片集合
     */
    void onClosed(List<ImgInfo> deleteList, List<ImgInfo> result);

    void onScrollStateChanged(RecyclerView recyclerView, int newState);

    void onScrolled(RecyclerView recyclerView, int dx, int dy);

    /**
     * 当前选中的页面index
     */
    void onPageSelected(int position);
}
