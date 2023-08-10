import android.content.Context
import android.content.SharedPreferences
import com.github.mikephil.charting.data.PieEntry
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

class StatisticsDataUtils {

    companion object {
        private const val PREF_NAME = "CounterDataPref"
        private const val KEY_COUNTER_LIST = "counterList"

        fun writeCounterToSharedPreferences(context: Context, counter: Counter) {
            val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            val gson = Gson()
            val counterListJson = sharedPreferences.getString(KEY_COUNTER_LIST, null)
            val counterList = mutableListOf<Counter>()

            if (counterListJson != null) {
                counterList.addAll(gson.fromJson(counterListJson, Array<Counter>::class.java).toList())
            }

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val currentDate = dateFormat.format(Date())

            //compare last one entry date with current date
            if(counterList.isEmpty()){
                counterList.add(Counter(currentDate, counter.forwardheadCounter, counter.crosslegCounter, counter.standardCounter, counter.missingCounter))
            }else{
                var lastOne = counterList.last()
                if(lastOne.date == currentDate){
                    lastOne.forwardheadCounter += counter.forwardheadCounter
                    lastOne.crosslegCounter += counter.crosslegCounter
                    lastOne.standardCounter += counter.standardCounter
                    lastOne.missingCounter += counter.missingCounter
                    counterList[counterList.size - 1] = lastOne
                }else{
                    counterList.add(Counter(currentDate, counter.forwardheadCounter, counter.crosslegCounter, counter.standardCounter, counter.missingCounter))
                }
            }

            //check size is it more than 30, if it is then remove the first one
            if (counterList.size > 30) {
                counterList.removeAt(0)
            }

            val updatedCounterListJson = gson.toJson(counterList)
            editor.putString(KEY_COUNTER_LIST, updatedCounterListJson)
            editor.apply()
        }

        fun getLast30Entries(context: Context): List<Counter> {
            val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

            val counterListJson = sharedPreferences.getString(KEY_COUNTER_LIST, null)
            if (counterListJson != null) {
                val gson = Gson()
                return gson.fromJson(counterListJson, Array<Counter>::class.java).toList()
            }
            return emptyList()
        }


        //get last one entry
        fun getLastEntry(context: Context): Counter? {
            val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

            val counterListJson = sharedPreferences.getString(KEY_COUNTER_LIST, null)
            if (counterListJson != null) {
                val gson = Gson()
                val counterList = gson.fromJson(counterListJson, Array<Counter>::class.java).toList()
                return counterList.last()
            }
            return null
        }
    }
}

data class Counter(
    val date: String = "",
    var forwardheadCounter: Float = 1f,
    var crosslegCounter: Float = 1f,
    var standardCounter: Float = 1f,
    var missingCounter: Float = 1f
)

fun Counter.reset() {
    forwardheadCounter = 1f
    crosslegCounter = 1f
    standardCounter = 1f
    missingCounter = 1f
}


//add function for Counter , reset

//add function for CounterEntry to convert to float array
fun Counter.toDataArray(): FloatArray {
    return floatArrayOf(standardCounter, crosslegCounter,forwardheadCounter,  missingCounter)
}
