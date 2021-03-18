package com.joaohhenriq.kotlin_sunrise_app

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
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

        FetchTask().execute(city)
    }

    inner class FetchTask: AsyncTask<String, String, String>() {

        override fun doInBackground(vararg params: String?): String {
            try {
                var city = params[0]
                val url = URL("https://api.domainsdb.info/v1/domains/search?domain=$city")

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
                var json = JSONObject(values[0])
                val domains = json.getJSONArray("domains")
                val domain = domains.getJSONObject(0)
                val result = domain.getString("domain")

                textView.text = "Result: $result"
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