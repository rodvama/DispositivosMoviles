package NetworkUtility

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import java.io.IOException
import java.net.URL

class NetworkConnection {


    companion object {
        const val BASE_URL = "https://fierce-chamber-37767.herokuapp.com/"

        fun buildUrlClinics(): URL =
                URL("$BASE_URL/clinics/?format=json")

        fun buildUrlPatients(): URL =
                URL("$BASE_URL/patients/?format=json")

        fun buildUrlPressures(): URL =
                //http://www.mocky.io/v2/5cc3b67a3400003700765452
            URL("http://www.mocky.io/v2/5cc3b67a3400003700765452")

        fun getResponseFromHttpUrl(url: URL): String =

                try {
                    //readText() does have an internal limit of 2 GB file size.
                    url.readText()
                } catch (e: IOException) {
                    e.printStackTrace()
                    throw IOException("Not connected")
                }

        fun isNetworkConnected(context: Context): Boolean {
            val connectivityManager: ConnectivityManager = context.getSystemService(
                    Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo

            return networkInfo?.isConnectedOrConnecting ?: false
        }
    }
}