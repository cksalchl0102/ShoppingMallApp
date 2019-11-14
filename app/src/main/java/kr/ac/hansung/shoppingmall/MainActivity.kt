package kr.ac.hansung.shoppingmall

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager




class MainActivity : AppCompatActivity() {

    var searchText: String = ""
    var arraylist = ArrayList<Goods?>()
    var productkeylist = ArrayList<String?>()
    var list = ArrayList<String>()
    var product: Goods? = null

    //var adapter = null



    // Write a message to the database
    val database = FirebaseDatabase.getInstance().reference.child("product")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //if(arraylist!-)
        var adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, this.productkeylist)

        val listView = findViewById<ListView>(R.id.productListview)
        val searchbtn = findViewById<ImageButton>(R.id.searchbtn)
        listView.adapter = adapter

        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val Intent = Intent(this, DetailActivity::class.java)
            Intent.putExtra("goodsNo", arraylist.get(position)?.goodsNo)
            startActivity(Intent)
        }

        var valueEventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                adapter.clear()
                arraylist.clear()
                for (productData in dataSnapshot.children) {
                    val productkey = productData.key
                    val productName = productData.child("name").value.toString()

                    if (productkey != null) {
                        productkeylist.add(productName)
                        val goods = Goods(productkey, productName)
                        arraylist.add(goods)
                    }
                    else {
                        Toast.makeText(this@MainActivity, "검색하신 상품이 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                    adapter.notifyDataSetChanged()
                    adapter.notifyDataSetInvalidated()
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {}

        }

        searchbtn.setOnClickListener {
            val searchedittext = findViewById<EditText>(R.id.searchEdittext)
            searchText = searchedittext.text.toString()
            if(searchText == "") {
                database.addListenerForSingleValueEvent(valueEventListener)
            }
            else {
                val query =
                        database.orderByChild("name").startAt(searchText).endAt(searchText + "\uF8FF")
                                .limitToFirst(5)
                query.addListenerForSingleValueEvent(valueEventListener);
            }
        }

        // Read from the database
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (productData in dataSnapshot.children) {
                    // child 내에 있는 데이터만큼 반복
                    val productkey = productData.key
                    val productName = productData.child("name").value.toString()

                    if(productkey!=null) {
                        productkeylist.add(productName)
                        val goods = Goods(productkey,productName)
                        arraylist.add(goods)
                        adapter.notifyDataSetChanged()
                        adapter.notifyDataSetInvalidated()
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
            }
        })
    }
    private val onClickItem = View.OnClickListener { v ->
        val str = v.tag as String
        Toast.makeText(this@MainActivity, str, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.mainmenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.catemenu -> {
                Toast.makeText(applicationContext, "click on category", Toast.LENGTH_LONG).show()
                val Intent = Intent(this, CategoryActivity::class.java)
                startActivity(Intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
