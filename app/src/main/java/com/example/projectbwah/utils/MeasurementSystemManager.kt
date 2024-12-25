import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Locale

enum class MeasurementSystem {
    METRIC, IMPERIAL, ALL
}

enum class WeightSystem {
    KILOGRAMS, POUNDS, ALL
}

class MeasurementSystemManager(context: Context) {

    companion object {
        const val SETTINGS_PREFS = "settings"
        const val MEASUREMENT_SYSTEM_KEY = "measurement_system"
        const val WEIGHT_SYSTEM_KEY = "weight_system"
        const val METRIC_UNITS_KEY = "metric_units"
        const val IMPERIAL_UNITS_KEY = "imperial_units"
        const val KILOGRAM_UNITS_KEY = "kilogram_units"
        const val POUND_UNITS_KEY = "pound_units"
    }

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(SETTINGS_PREFS, Context.MODE_PRIVATE)
    private val gson = Gson()

    init {
        // Initialize default units if not already set
        if (!sharedPreferences.contains(METRIC_UNITS_KEY)) {
            setUnits(METRIC_UNITS_KEY, listOf("m", "cm", "mm"))
        }
        if (!sharedPreferences.contains(IMPERIAL_UNITS_KEY)) {
            setUnits(IMPERIAL_UNITS_KEY, listOf("ft", "in"))
        }
        if (!sharedPreferences.contains(KILOGRAM_UNITS_KEY)) {
            setUnits(KILOGRAM_UNITS_KEY, listOf("kg", "g"))
        }
        if (!sharedPreferences.contains(POUND_UNITS_KEY)) {
            setUnits(POUND_UNITS_KEY, listOf("lb", "oz"))
        }
    }

    private fun setUnits(key: String, units: List<String>) {
        val json = gson.toJson(units)
        sharedPreferences.edit().putString(key, json).apply()
    }

    private fun getUnits(key: String): List<String> {
        val json = sharedPreferences.getString(key, null)
        return if (json != null) {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    fun getDefaultMeasurementSystem(): MeasurementSystem {
        val locale = Locale.getDefault()
        return if (locale.country in listOf("US", "LR", "MM")) {
            MeasurementSystem.IMPERIAL
        } else {
            MeasurementSystem.METRIC
        }
    }

    fun getMeasurementSystem(): MeasurementSystem {
        val system = sharedPreferences.getString(MEASUREMENT_SYSTEM_KEY, null)
        return if (system != null) {
            MeasurementSystem.valueOf(system)
        } else {
            getDefaultMeasurementSystem()
        }
    }

    fun setMeasurementSystem(system: MeasurementSystem) {
        sharedPreferences.edit().putString(MEASUREMENT_SYSTEM_KEY, system.name).apply()
    }

    fun getMeasurementUnits(): List<String> {
        return when (getMeasurementSystem()) {
            MeasurementSystem.METRIC -> getUnits(METRIC_UNITS_KEY)
            MeasurementSystem.IMPERIAL -> getUnits(IMPERIAL_UNITS_KEY)
            MeasurementSystem.ALL -> getUnits(METRIC_UNITS_KEY) + getUnits(IMPERIAL_UNITS_KEY)
        }
    }

    fun getDefaultWeightSystem(): WeightSystem {
        val locale = Locale.getDefault()
        return if (locale.country in listOf("US", "LR", "MM")) {
            WeightSystem.POUNDS
        } else {
            WeightSystem.KILOGRAMS
        }
    }

    fun getWeightSystem(): WeightSystem {
        val system = sharedPreferences.getString(WEIGHT_SYSTEM_KEY, null)
        return if (system != null) {
            WeightSystem.valueOf(system)
        } else {
            getDefaultWeightSystem()
        }
    }

    fun setWeightSystem(system: WeightSystem) {
        sharedPreferences.edit().putString(WEIGHT_SYSTEM_KEY, system.name).apply()
    }

    fun getWeightUnits(): List<String> {
        return when (getWeightSystem()) {
            WeightSystem.KILOGRAMS -> getUnits(KILOGRAM_UNITS_KEY)
            WeightSystem.POUNDS -> getUnits(POUND_UNITS_KEY)
            WeightSystem.ALL -> getUnits(KILOGRAM_UNITS_KEY) + getUnits(POUND_UNITS_KEY)
        }
    }
}