package com.cyl.music_hnust.adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cyl.music_hnust.R;
import com.cyl.music_hnust.activity.MainActivity;
import com.cyl.music_hnust.activity.PlaylistDetailActivity;
import com.cyl.music_hnust.callback.SingerCallback;
import com.cyl.music_hnust.fragment.AlbumDetailFragment;
import com.cyl.music_hnust.model.music.Album;
import com.cyl.music_hnust.model.music.Artist;
import com.cyl.music_hnust.model.music.Singer;
import com.cyl.music_hnust.utils.Extras;
import com.cyl.music_hnust.utils.ImageUtils;
import com.cyl.music_hnust.utils.NavigateUtil;
import com.cyl.music_hnust.utils.SystemUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

import static android.app.ActivityOptions.makeSceneTransitionAnimation;

/**
 * Created by Monkey on 2015/6/29.
 */
public class MyStaggeredViewAdapter extends RecyclerView.Adapter<MyStaggeredViewAdapter.MyRecyclerViewHolder> {


    private Activity mContext;
    public List<Album> albums = new ArrayList<>();
    public List<Artist> artists = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private boolean isAlbum;

    public MyStaggeredViewAdapter(Activity mContext, List<Album> albums, List<Artist> artists, boolean isAlbum) {
        this.mContext = mContext;
        this.albums = albums;
        this.artists = artists;
        this.isAlbum = isAlbum;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    /**
     * 创建ViewHolder
     */
    @Override
    public MyRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = mLayoutInflater.inflate(R.layout.item_playlist, parent, false);
        MyRecyclerViewHolder mViewHolder = new MyRecyclerViewHolder(mView);
        return mViewHolder;
    }

    /**
     * 绑定ViewHoler，给item中的控件设置数据
     */
    @Override
    public void onBindViewHolder(final MyRecyclerViewHolder holder, final int position) {
        if (isAlbum) {
            holder.album.setVisibility(View.VISIBLE);
            holder.name.setText(albums.get(position).getName());
            holder.artist.setText(albums.get(position).getArtistName());
            loadBitmap(ImageUtils.getAlbumArtUri(albums.get(position).getId()).toString(), holder.album);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("album", albums.get(position).getId() + "");
                    NavigateUtil.navigateToAlbum(mContext,
                            albums.get(position).getId(),
                            true,
                            albums.get(position).getName());
                }
            });
        } else {
            holder.name.setText(artists.get(position).getName());
            holder.artist.setText(artists.get(position).getCount() + "首歌");
            if (!artists.get(position).getName().equals("<unknown>"))
                loadArtist(artists.get(position).getName(), holder.album);
            else {
                holder.album.setVisibility(View.GONE);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NavigateUtil.navigateToAlbum(mContext,
                            artists.get(position).getId(),
                            false,
                            artists.get(position).getName());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (isAlbum) {
            return albums.size();
        } else {
            return artists.size();
        }
    }

    private void loadBitmap(String uri, ImageView img) {
        Log.e("uri", uri);
        try {
            ImageLoader.getInstance().displayImage(uri, img,
                    new DisplayImageOptions.Builder().cacheInMemory(true)
                            .showImageOnFail(R.drawable.default_cover)
                            .showImageForEmptyUri(R.drawable.default_cover)
                            .showImageOnLoading(R.drawable.default_cover)
                            .build());
        } catch (Exception e) {
            Log.e("EEEE", uri);
        }
    }


    private void loadArtist(String title, final ImageView imgView) {
        OkHttpUtils.get().url("http://apis.baidu.com/geekery/music/singer")
                .addHeader("apikey", "0bbd28df93933b00fdbbd755f8769f1b")
                .addParams("name", title)
                .build()
                .execute(new SingerCallback() {
                    @Override
                    public void onError(Call call, Exception e) {

                    }

                    @Override
                    public void onResponse(Singer response) {
                        if (response.getCode() == 0)
                            loadBitmap(response.getData().getImage(), imgView);

                    }
                });
    }


    public class MyRecyclerViewHolder extends RecyclerView.ViewHolder {

        public ImageView album;
        public TextView name, artist;
        public CardView playlist_container;

        public MyRecyclerViewHolder(View mView) {
            super(mView);
            album = (ImageView) mView.findViewById(R.id.album);
            name = (TextView) mView.findViewById(R.id.name);
            artist = (TextView) mView.findViewById(R.id.artist);
        }
    }


}
