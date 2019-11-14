package kr.ac.hansung.shoppingmall

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.TextView

class GoodsAdapter(context: Context, item: ArrayList<Goods>) : BaseAdapter() {
    private val cont = context;
    private val item = item;

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        view = LayoutInflater.from(cont).inflate(R.layout.activity_categorized_list, parent, false)
        val textview = view.findViewById<TextView>(R.id.textViewName)
        textview.text = item[position].goodsName
        return view
    }

    override fun getItem(p0: Int) = item[p0]

    override fun getItemId(p0: Int) = p0.toLong()

    override fun getCount() = item.size
}