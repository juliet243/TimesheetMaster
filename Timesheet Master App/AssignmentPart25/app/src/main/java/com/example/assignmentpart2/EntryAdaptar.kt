package com.example.assignmentpart2

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EntryAdaptar: RecyclerView.Adapter<EntryAdaptar.EntryViewHolder>() {

    private var entryList = mutableListOf<Entry>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val itemView = inflater.inflate(R.layout.entry_item_holder, parent, false)
        return EntryViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return entryList.size
    }

    override fun onBindViewHolder(holder: EntryViewHolder, position: Int) {
        val entry = entryList[position]
        holder.setItem(entry)
        val bytes = Base64.decode(entry.itEntryImage,Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.size)
        holder.EntryImage.setImageBitmap(bitmap)
    }

    fun setItem(list: MutableList<Entry>){
        this.entryList = list
        notifyDataSetChanged()
    }

    class EntryViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var tvEntryName: TextView? = null
        private var tvEntryDesc: TextView? = null
        private var tvEntryCat: TextView? = null
        private var tvEntryTime: TextView? = null
        private var tvEntryDate: TextView? = null
        //private var EntryImage: TextView? = null
        val EntryImage: ImageView = itemView.findViewById(R.id.entryImage)

        fun setItem(entry: Entry) {
            tvEntryName = itemView.findViewById(R.id.tvEntryName)
            tvEntryDesc = itemView.findViewById(R.id.tvEntryDesc)
            tvEntryCat = itemView.findViewById(R.id.tvEntryCat)
            tvEntryTime = itemView.findViewById(R.id.tvEntryTime)
            tvEntryDate = itemView.findViewById(R.id.tvEntryDate)
            //EntryImage = itemView.findViewById(R.id.tvEntryImage)
            //val EntryImage:ImageView = itemView.findViewById(R.id.entryImage)

            tvEntryName?.text = entry.itEntryName
            tvEntryDesc?.text = entry.itEntryDesc
            tvEntryCat?.text = entry.itEntryCategory
            tvEntryTime?.text = entry.itEntryTime
            tvEntryDate?.text = entry.itEntryDate
            //tvEntryImage?.text = entry.itEntryImage
        }
    }
}