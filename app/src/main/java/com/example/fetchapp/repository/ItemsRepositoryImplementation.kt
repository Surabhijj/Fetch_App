import android.util.Log
import com.example.fetchapp.model.Item
import com.example.fetchapp.network.RetrofitInstance
import com.example.fetchapp.repository.ItemsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ItemsRepositoryImplementation : ItemsRepository {
    override suspend fun fetchItems(): Result<List<Item>> = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitInstance.apiService.fetchItems()
            if (response.isSuccessful) {
                val items = response.body()
                // Safely logging the response body by converting it to a string
                Log.d("ItemsRepository", "Fetched items: ${items?.toString() ?: "No data"}")

                // Using the Elvis operator to return an empty list if the response body is null
                Result.success(items ?: emptyList())
            } else {
                // Logging the error and returning an empty list to avoid nullability issues
                Log.e("ItemsRepository", "Failed to fetch items: HTTP ${response.code()}")
                Result.success(emptyList())
            }
        } catch (e: Exception) {
            Log.e("ItemsRepository", "Exception when fetching items: ${e.message}")
            // Wrapping the exception in Result.failure for type consistency
            Result.failure(e)
        }
    }
}