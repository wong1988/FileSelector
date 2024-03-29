package io.github.wong1988.fileselector.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.github.wong1988.fileselector.FingerQQGroup;
import io.github.wong1988.fileselector.R;
import io.github.wong1988.fileselector.entity.ImgInfo;
import io.github.wong1988.fileselector.manager.ScrollLinearLayoutManager;

public class PreviewAdapter extends RecyclerView.Adapter<PreviewAdapter.ViewHolder> {

    private final List<ImgInfo> mData;
    private final ScrollLinearLayoutManager mManger;
    private Listener mListener;
    private boolean mVerticalMove = true;

    private Context mContext;

    public PreviewAdapter(ScrollLinearLayoutManager manager) {
        this.mData = new ArrayList<>();
        this.mManger = manager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext = parent.getContext()).inflate(R.layout.github_a_item_img, parent, false);
        return new ViewHolder(view);
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ImgInfo imgInfo = mData.get(position);
        holder.photoView.setZoomable(false);
        holder.qqGroup.canVerticalMove(false);
        switch (imgInfo.getType()) {
            case ImageResource:
                Glide.with(mContext)
                        .load(mContext.getResources().getDrawable(Integer.parseInt(imgInfo.getPath())))
                        .placeholder(R.drawable.github_a_loading_anim)
                        .error(R.drawable.github_a_error)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                holder.photoView.setZoomable(true);
                                if (mVerticalMove)
                                    holder.qqGroup.canVerticalMove(true);
                                return false;
                            }
                        })
                        .into(holder.photoView);
                break;
            case HTTP:
                Glide.with(mContext)
                        .load(imgInfo.getPath())
                        .placeholder(R.drawable.github_a_loading_anim)
                        .error(R.drawable.github_a_error)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                holder.photoView.setZoomable(true);
                                if (mVerticalMove)
                                    holder.qqGroup.canVerticalMove(true);
                                return false;
                            }
                        })
                        .into(holder.photoView);
                break;
            case File:
                Glide.with(mContext)
                        .load(new File(imgInfo.getPath()))
                        .placeholder(R.drawable.github_a_loading_anim)
                        .error(R.drawable.github_a_error)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                holder.photoView.setZoomable(true);
                                if (mVerticalMove)
                                    holder.qqGroup.canVerticalMove(true);
                                return false;
                            }
                        })
                        .into(holder.photoView);
                break;
        }

        holder.qqGroup.setOnAlphaChangeListener(new FingerQQGroup.onAlphaChangedListener() {
            @Override
            public void onAlphaChanged(float alpha) {
                if (alpha == 1)
                    mManger.setCanScroll(true);

                if (mListener != null)
                    mListener.onAlphaChanged(alpha);
            }

            @Override
            public void onTranslationYChanged(float translationY) {
                mManger.setCanScroll(false);

                if (mListener != null && holder.photoView.isZoomable())
                    mListener.onTranslationYChanged(translationY);
            }

            @Override
            public void onCloseListener() {
                if (mListener != null)
                    mListener.onCloseListener();
            }
        });

        holder.photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null)
                    mListener.onClick(position, imgInfo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setVerticalMove(boolean b) {
        if (mVerticalMove != b) {
            mVerticalMove = b;
            notifyDataSetChanged();
        }
    }


    public List<ImgInfo> getAttachData() {
        return mData;
    }


    public void addData(List<ImgInfo> data) {
        if (data != null && data.size() > 0) {
            mData.addAll(data);
            notifyDataSetChanged();
        }
    }

    public void setListener(Listener mListener) {
        this.mListener = mListener;
    }

    // Item中的控件
    static class ViewHolder extends RecyclerView.ViewHolder {
        FingerQQGroup qqGroup;
        PhotoView photoView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            qqGroup = itemView.findViewById(R.id.github_a_fg);
            photoView = itemView.findViewById(R.id.github_a_pv);
        }
    }

    public interface Listener {
        void onAlphaChanged(float alpha);

        void onTranslationYChanged(float translationY);

        void onCloseListener();

        void onClick(int position, ImgInfo imgInfo);
    }
}
