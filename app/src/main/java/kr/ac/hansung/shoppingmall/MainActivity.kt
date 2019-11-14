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


class MainActivity : AppCompatActivity() {
    val context = this
    var searchText: String = ""
    var searchCategory: String = ""
    var arraylist = ArrayList<Goods?>()
    var productkeylist = ArrayList<String?>()
    var product: Goods? = null
    //var adapter = null

    val categorys = ArrayList<String>()
    lateinit var categoryAdapter:ArrayAdapter<String>
    lateinit var categorySpinner: Spinner

    // Write a message to the database
    val database = FirebaseDatabase.getInstance().reference.child("product")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val categoryDatabase = FirebaseDatabase.getInstance().reference.child("category")

        val singleListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for(child in dataSnapshot.children) {
                    categorys.add(child.key.toString())
                }
                categoryAdapter.notifyDataSetChanged()
                categoryAdapter.notifyDataSetInvalidated()
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        categoryDatabase.addListenerForSingleValueEvent(singleListener)

        categoryAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categorys);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner = findViewById(R.id.spinner)
        categorySpinner.adapter = categoryAdapter
        categorySpinner.setSelection(0)
        categorySpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var s : String = categorySpinner.selectedItem as String

                val nextIntent = Intent(context, CategorizedActivity::class.java)
                nextIntent.putExtra("category", s)
                startActivity(nextIntent)
            }
        }



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
}
