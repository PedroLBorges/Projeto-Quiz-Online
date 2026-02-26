package np.com.pedrolborges.quizonline

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromQuestionList(value: List<QuestionModel>?): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toQuestionList(value: String): List<QuestionModel> {
        val listType = object : TypeToken<List<QuestionModel>>() {}.type
        return Gson().fromJson(value, listType) ?: emptyList()
    }
}