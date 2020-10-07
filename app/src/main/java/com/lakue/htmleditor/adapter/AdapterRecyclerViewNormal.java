package com.lakue.htmleditor.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.lakue.htmleditor.R;
import com.lakue.htmleditor.util.MyItem;
import com.lakue.htmleditor.util.MyItemView;
import com.lakue.htmleditor.viewholder.YoutubeSearchViewHolder;

import java.util.ArrayList;

public class AdapterRecyclerViewNormal  extends RecyclerView.Adapter<MyItemView> {

    private ArrayList<MyItem> myItems = new ArrayList<>();
    private Context context;


    //RecyclerView 클래스 타입
    private int SEL_OBJ_TYPE = 0;
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;


    public AdapterRecyclerViewNormal(int objtype) {

        switch (objtype) {
            case 111:
                SEL_OBJ_TYPE = 111;
                break;
        }
    }

    @NonNull
    @Override
    public MyItemView onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();

        MyItemView holder = null;

        View view;

        switch (SEL_OBJ_TYPE) {
            case 111:
                view = LayoutInflater.from(context).inflate(R.layout.item_youtube_search, viewGroup, false);
                holder = new YoutubeSearchViewHolder(view);
                break;
        }
        return holder;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull MyItemView holder, final int position) {
        if (holder instanceof YoutubeSearchViewHolder) {
            YoutubeSearchViewHolder youtubeSearchViewHolder = (YoutubeSearchViewHolder) holder;
            youtubeSearchViewHolder.onBind(myItems.get(position));
        }

    }


    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return myItems == null ? 0 : myItems.size();
    }

    @Override
    public long getItemId(int position) {
        return myItems.get(position).hashCode();
//        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (isLoaderVisible) {
            return position == myItems.size() - 1 ? VIEW_TYPE_LOADING : VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    public void addItem(MyItem data) {
        // 외부에서 item을 추가시킬 함수입니다.
        myItems.add(data);
        //notifyDataSetChanged();
        //notifyDataSetChanged();
//        this.notifyItemInserted(myItems.size() - 1);
    }

    public void addItems(ArrayList<MyItem> data) {
        // 외부에서 item을 추가시킬 함수입니다.
        int old_count = myItems.size();
        int new_count = old_count + data.size();

        myItems.addAll(data);
        notifyDataSetChanged();
        notifyItemRangeChanged(old_count,myItems.size());
        //notifyDataSetChanged();
//        this.notifyItemInserted(myItems.size() - 1);
    }

    public void removeItem() {
        myItems.clear();
    }

}
