package io.github.wong1988.fileselector.listener;

import androidx.recyclerview.widget.RecyclerView;

public interface PreviewListener {

    void onAlphaChanged(float alpha);

    void onTranslationYChanged(float translationY);

    void onClosed();

    void onScrollStateChanged(RecyclerView recyclerView, int newState);

    void onScrolled(RecyclerView recyclerView, int dx, int dy);

    void onPageSelected(int position);
}
