package cc.solart.nestedceiling.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import cc.solart.nestedceiling.app.page.NestedParentRecyclerViewActivity
import cc.solart.nestedceiling.app.page.NestedParentScrollViewActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btn1: Button = findViewById(R.id.btn1)
        val btn2: Button = findViewById(R.id.btn2)
        btn1.setOnClickListener {
            val intent = Intent(this, NestedParentScrollViewActivity::class.java)
            startActivity(intent)
        }

        btn2.setOnClickListener {
            val intent = Intent(this, NestedParentRecyclerViewActivity::class.java)
            startActivity(intent)
        }
    }
}