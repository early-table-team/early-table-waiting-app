package com.example.early_table_waiting.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.early_table_waiting.R
import com.example.early_table_waiting.model.Store

class StoreAdapter(
    private val storeList: MutableList<Store>,
    private val onItemClick: (Store) -> Unit // 가게 클릭 이벤트
) : RecyclerView.Adapter<StoreAdapter.StoreViewHolder>() {

    class StoreViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val storeImage: ImageView = view.findViewById(R.id.storeImage)
        val storeName: TextView = view.findViewById(R.id.storeName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_store, parent, false)
        return StoreViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
        val store = storeList[position]
        holder.storeName.text = store.name

        // Glide를 사용하여 이미지 로드
        Glide.with(holder.itemView.context)
            .load(store.imageUrl)
            .placeholder(R.drawable.ic_store_placeholder) // 로딩 중에 표시할 이미지
            .error(R.drawable.ic_store_placeholder) // 로드 실패 시 표시할 이미지
            .diskCacheStrategy(DiskCacheStrategy.ALL) // 디스크 캐시 전략 설정
            .into(holder.storeImage)


        // 아이템 클릭 이벤트 처리
        holder.itemView.setOnClickListener { onItemClick(store) }
    }

    override fun getItemCount(): Int = storeList.size
}
