package kr.ac.hansung.shoppingmall

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CategoryActivity : AppCompatActivity() {
    val categorys = ArrayList<String>()
    lateinit var adapter:ArrayAdapter<String>
    lateinit var categoryList:ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, categorys);
        categoryList = findViewById(R.id.categoryList)
        categoryList.adapter = adapter

        val categoryDatabase = FirebaseDatabase.getInstance().reference.child("category")

        val singleListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for(child in dataSnapshot.children) {
                    categorys.add(child.key.toString())
                }
                adapter.notifyDataSetChanged()
                adapter.notifyDataSetInvalidated()
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        categoryDatabase.addListenerForSingleValueEvent(singleListener)

        categoryList.onItemClickListener = AdapterView.OnItemClickListener{parent, view, position, id ->
            val nextIntent = Intent(this, CategorizedActivity::class.java)
            nextIntent.putExtra("category", categorys.get(position))
            startActivity(nextIntent)
        }
    }
}
