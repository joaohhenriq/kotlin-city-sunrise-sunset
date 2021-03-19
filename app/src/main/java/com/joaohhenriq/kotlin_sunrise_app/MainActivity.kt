package com.joaohhenriq.kotlin_sunrise_app

import android.content.Context
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun getSunset(view: View) {
        val city = editText.text.toString()

        FetchTask(this).execute(city)
    }

    inner class FetchTask: AsyncTask<String, String, String> {

        var context: Context

        constructor(context: Context) : super() {
            this.context = context
        }

        override fun doInBackground(vararg params: String?): String {
            try {
                var word = params[0]
                val url = URL("https://api.domainsdb.info/v1/domains/search?domain=$word")

                val urlConnect = url.openConnection() as HttpURLConnection
                urlConnect.connectTimeout = 7000

                var inString = convertStreamToString(urlConnect.inputStream)
                publishProgress(inString)
            } catch(e: Exception) {
                print(e.message)
            }

            return ""
        }

        override fun onProgressUpdate(vararg values: String?) {
            try {
                val domainListResult = ArrayList<String>()

                var json = JSONObject(values[0])
                val domains = json.getJSONArray("domains")

                var index = 0
                while(index < domains.length()) {
                    domainListResult.add(domains.getJSONObject(index).getString("domain"))
                    index++
                }

                textView.text = "${domains.length()} item(s) found"

                val adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, domainListResult)
                listView.adapter = adapter

            } catch(e: Exception) {
                print(e.message)
            }

        }
    }

    fun convertStreamToString(inputStream: InputStream): String {
        val bufferReader = BufferedReader(InputStreamReader(inputStream))
        var line: String
        var allString = ""

        try {
            do {
                line = bufferReader.readLine()

                if(line != null) {
                    allString += line
                }
            } while (line != null)

            inputStream.close()
        } catch(e: Exception) {
            print(e.message)
        }

        return allString
    }
}