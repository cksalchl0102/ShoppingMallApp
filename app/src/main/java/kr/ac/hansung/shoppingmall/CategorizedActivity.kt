package kr.ac.hansung.shoppingmall

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ListView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CategorizedActivity : AppCompatActivity() {
    var items = ArrayList<Goods>()
    lateinit var adapter: BaseAdapter
    lateinit var itemList:ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categorized)

        val category = intent.getStringExtra("category")
        val productDatabase = FirebaseDatabase.getInstance().reference.child("product")

        adapter = GoodsAdapter(this, items)
        itemList = findViewById<ListView>(R.id.itemList)
        itemList.adapter = adapter

        val singleListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for(child in dataSnapshot.children) {
                    if(child.child("sort").value.toString().equals(category)) {
                        items.add(Goods(child.key.toString(), child.child("name").value.toString()))
                    }
                }
                adapter.notifyDataSetChanged()
                adapter.notifyDataSetInvalidated()
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }

        productDatabase.addListenerForSingleValueEvent(singleListener)

        itemList.onItemClickListener = AdapterView.OnItemClickListener{ parent, view, position, id ->
            val nextIntent = Intent(this, DetailActivity::class.java)
            nextIntent.putExtra("goodsNo", items.get(position).goodsNo)
            startActivity(nextIntent)
        }
    }
}