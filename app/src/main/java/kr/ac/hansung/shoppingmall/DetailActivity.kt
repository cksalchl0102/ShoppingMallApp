package kr.ac.hansung.shoppingmall

import android.content.Context
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_detail.*
class DetailActivity : AppCompatActivity() {

    val database = FirebaseDatabase.getInstance().reference.child("product")
    lateinit var goodsNo : String

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        goodsNo = intent.getStringExtra("goodsNo")


//        val storageRef = FirebaseStorage.getInstance().getReference("image/cat.jpg")
        ///val url = storageRef.child("image/cat.jpg")
        // val gsReference = storageRef.getReferenceFromUrl("gs://pbl2-5cde5.appspot.com/image/cat.jpg")
//        Glide.with(this@DetailActivity).load(storageRef.storage).into(imageViewGoods)
//        Toast.makeText(this, storageRef.storage.toString() , Toast.LENGTH_SHORT).show()

        // Read from the database
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (productData in dataSnapshot.children) {
                    // child 내에 있는 데이터만큼 반복
                    if(productData.key.equals(goodsNo)){
                        //textViewNo.setText(productData.key)

                        //textViewNo.setText(productData.key)

                        val imageUrl = productData.child("img").value.toString()
                        Glide.with(this@DetailActivity).load(imageUrl).into(imageView)

                        textViewCategory.text = "Category: "+productData.child("sort").value.toString()
                        textViewNameKey.text = productData.child("name").value.toString()+"("+productData.key+")"
                        textViewPrice.text = productData.child("price").value.toString() + "원"
                        textViewExp.text = productData.child("exp").value.toString()
                        //textViewSort.text = productData.child("sort").value.toString()
                        //이미지 넣기
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
            }
        })
    }
}
