package io.github.wong1988.fileselector.listener;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.github.wong1988.fileselector.entity.ImgInfo;

public interface PreviewListener {

    void onAlphaChanged(float alpha);

    void onTranslationYChanged(float translationY);

    void onClosed(List<ImgInfo> deleteList, List<ImgInfo> result);

    void onScrollStateChanged(RecyclerView recyclerView, int newState);

    void onScrolled(RecyclerView recyclerView, int dx, int dy);

    void onPageSelected(int position);
}
