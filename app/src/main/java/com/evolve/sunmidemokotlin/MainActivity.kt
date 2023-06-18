package com.evolve.sunmidemokotlin

import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.evolve.sunmidemokotlin.SupporterManager.IScanListener

class MainActivity : AppCompatActivity() {
    private var mScannerManager: SupporterManager? = null
    private var mTextView: TextView? = null
    private val clearText: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initScanner()
        mTextView = findViewById(R.id.result)
        mTextView?.text = """
        Manufacturer：${Build.MANUFACTURER}
        Model：${Build.MODEL}
        """.trimIndent()
    }

    private fun initScanner() {
        mScannerManager = SupporterManager(this, object : IScanListener {
            override fun onScannerResultChange(result: String?) {
                mTextView!!.text = result
                Toast.makeText(this@MainActivity, result, Toast.LENGTH_SHORT).show()
            }

            override fun onScannerServiceConnected() {
                Toast.makeText(
                    this@MainActivity,
                    "Scan head initialized successfully!",
                    Toast.LENGTH_SHORT
                ).show()
                val s = """
                    ${mTextView!!.text}
                    
                    """.trimIndent()
                mTextView!!.text = s + "Scan head initialized successfully!"
            }

            override fun onScannerServiceDisconnected() {}
            override fun onScannerInitFail() {
                Toast.makeText(
                    this@MainActivity,
                    "Can't get scan head\n，Please try again！",
                    Toast.LENGTH_SHORT
                ).show()
                val s = """
                    ${mTextView!!.text}
                    
                    """.trimIndent()
                mTextView!!.text = s + "Can't get scan head"
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mScannerManager != null) {
            mScannerManager!!.recycle()
        }
    }
}