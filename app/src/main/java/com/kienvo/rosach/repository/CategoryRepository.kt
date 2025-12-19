package com.kienvo.rosach.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.kienvo.rosach.model.BookCategory
import kotlinx.coroutines.tasks.await

class CategoryRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    /**
     * Lấy tất cả categories
     */
    suspend fun getAllCategories(): List<BookCategory> {
        return try {
            val snapshot = db.collection("categories")
                .orderBy("order", Query.Direction.ASCENDING)
                .whereEqualTo("isActive", true)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                try {
                    BookCategory(
                        id = doc.getString("id") ?: "",
                        name = doc.getString("name") ?: "",
                        slug = doc.getString("slug") ?: "",
                        type = doc.getString("type") ?: "audiobook",
                        description = doc.getString("description") ?: "",
                        order = doc.getLong("order")?.toInt() ?: 0,
                        isActive = doc.getBoolean("isActive") ?: true
                    )
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}

