package com.kienvo.rosach.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.kienvo.rosach.model.Book
import com.kienvo.rosach.model.BookPart
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class BookRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    // Cache để giảm số lần đọc Firestore
    private var cachedBooks: List<Book>? = null
    private var cacheTimestamp: Long = 0
    private val CACHE_DURATION = 5 * 60 * 1000L // 5 phút

    /**
     * Lấy tất cả sách (với cache)
     */
    suspend fun getAllBooks(forceRefresh: Boolean = false): List<Book> {
        // Kiểm tra cache còn valid không
        if (!forceRefresh && cachedBooks != null && isCacheValid()) {
            return cachedBooks!!
        }

        return try {
            val snapshot = db.collection("books")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val books = snapshot.documents.mapNotNull { doc ->
                try {
                    Book(
                        id = doc.getString("id") ?: "",
                        title = doc.getString("title") ?: "",
                        author = doc.getString("author") ?: "",
                        coverUrl = doc.getString("coverUrl") ?: "",
                        type = doc.getString("type") ?: "audiobook",
                        rating = doc.getDouble("rating") ?: 0.0,
                        rank = doc.getLong("rank")?.toInt() ?: 0,
                        description = doc.getString("description") ?: ""
                    )
                } catch (e: Exception) {
                    null
                }
            }

            // Lưu vào cache
            cachedBooks = books
            cacheTimestamp = System.currentTimeMillis()

            books
        } catch (e: Exception) {
            // Nếu lỗi, trả về cache cũ (nếu có)
            cachedBooks ?: emptyList()
        }
    }

    /**
     * Lấy sách theo category
     */
    suspend fun getBooksByCategory(category: String): List<Book> {
        return try {
            val snapshot = db.collection("books")
                .whereArrayContains("categories", category)
                .orderBy("rating", Query.Direction.DESCENDING)
                .limit(20)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                try {
                    Book(
                        id = doc.getString("id") ?: "",
                        title = doc.getString("title") ?: "",
                        author = doc.getString("author") ?: "",
                        coverUrl = doc.getString("coverUrl") ?: "",
                        type = doc.getString("type") ?: "audiobook",
                        rating = doc.getDouble("rating") ?: 0.0,
                        rank = doc.getLong("rank")?.toInt() ?: 0,
                        description = doc.getString("description") ?: ""
                    )
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Lấy sách theo ID
     */
    suspend fun getBookById(bookId: String): Book? {
        return try {
            val doc = db.collection("books")
                .document(bookId)
                .get()
                .await()

            if (doc.exists()) {
                Book(
                    id = doc.getString("id") ?: "",
                    title = doc.getString("title") ?: "",
                    author = doc.getString("author") ?: "",
                    coverUrl = doc.getString("coverUrl") ?: "",
                    type = doc.getString("type") ?: "audiobook",
                    rating = doc.getDouble("rating") ?: 0.0,
                    rank = doc.getLong("rank")?.toInt() ?: 0,
                    description = doc.getString("description") ?: ""
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Lấy danh sách sách theo một mảng các IDs
     */
    suspend fun getBooksByIds(bookIds: List<String>): List<Book> {
        if (bookIds.isEmpty()) return emptyList()
        
        return try {
            // Firestore whereIn có giới hạn 30 item mỗi lần query.
            // Để đơn giản và an toàn, chúng ta fetch từng book (có thể tối ưu sau bằng batch/chunk)
            val books = mutableListOf<Book>()
            for (id in bookIds) {
                getBookById(id)?.let { books.add(it) }
            }
            books
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Lấy sách featured (isFeatured = true)
     */
    suspend fun getFeaturedBooks(): List<Book> {
        return try {
            val snapshot = db.collection("books")
                .whereEqualTo("isFeatured", true)
                .limit(10)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                try {
                    Book(
                        id = doc.getString("id") ?: "",
                        title = doc.getString("title") ?: "",
                        author = doc.getString("author") ?: "",
                        coverUrl = doc.getString("coverUrl") ?: "",
                        type = doc.getString("type") ?: "audiobook",
                        rating = doc.getDouble("rating") ?: 0.0,
                        rank = doc.getLong("rank")?.toInt() ?: 0,
                        description = doc.getString("description") ?: ""
                    )
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Search sách theo tên hoặc tác giả
     */
    suspend fun searchBooks(query: String): List<Book> {
        if (query.isBlank()) return emptyList()

        // Lấy tất cả sách từ cache hoặc Firestore
        val allBooks = getAllBooks()

        // Filter local (vì Firestore không support full-text search)
        return allBooks.filter {
            it.title.contains(query, ignoreCase = true) ||
            it.author.contains(query, ignoreCase = true)
        }
    }

    /**
     * Observe books real-time (optional)
     */
    fun observeBooks(): Flow<List<Book>> = callbackFlow {
        val listener = db.collection("books")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val books = snapshot.documents.mapNotNull { doc ->
                        try {
                            Book(
                                id = doc.getString("id") ?: "",
                                title = doc.getString("title") ?: "",
                                author = doc.getString("author") ?: "",
                                coverUrl = doc.getString("coverUrl") ?: "",
                                type = doc.getString("type") ?: "audiobook",
                                rating = doc.getDouble("rating") ?: 0.0,
                                rank = doc.getLong("rank")?.toInt() ?: 0,
                                description = doc.getString("description") ?: ""
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    trySend(books)
                }
            }

        awaitClose { listener.remove() }
    }

    /**
     * Lấy danh sách parts (chương) của một cuốn sách
     */
    suspend fun getBookParts(bookId: String): List<BookPart> {
        return try {
            val snapshot = db.collection("books")
                .document(bookId)
                .collection("parts")
                .orderBy("order", Query.Direction.ASCENDING)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                try {
                    BookPart(
                        id = doc.getString("id") ?: "",
                        bookId = doc.getString("bookId") ?: "",
                        partNumber = doc.getLong("partNumber")?.toInt() ?: 0,
                        title = doc.getString("title") ?: "",
                        audioUrl = doc.getString("audioUrl") ?: "",
                        duration = doc.getString("duration") ?: "",
                        durationSeconds = doc.getLong("durationSeconds")?.toInt() ?: 0,
                        fileSize = doc.getString("fileSize") ?: "",
                        isPremium = doc.getBoolean("isPremium") ?: false,
                        isFree = doc.getBoolean("isFree") ?: true,
                        order = doc.getLong("order")?.toInt() ?: 0
                    )
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Lấy một part cụ thể
     */
    suspend fun getBookPart(bookId: String, partId: String): BookPart? {
        return try {
            val doc = db.collection("books")
                .document(bookId)
                .collection("parts")
                .document(partId)
                .get()
                .await()

            if (doc.exists()) {
                BookPart(
                    id = doc.getString("id") ?: "",
                    bookId = doc.getString("bookId") ?: "",
                    partNumber = doc.getLong("partNumber")?.toInt() ?: 0,
                    title = doc.getString("title") ?: "",
                    audioUrl = doc.getString("audioUrl") ?: "",
                    duration = doc.getString("duration") ?: "",
                    durationSeconds = doc.getLong("durationSeconds")?.toInt() ?: 0,
                    fileSize = doc.getString("fileSize") ?: "",
                    isPremium = doc.getBoolean("isPremium") ?: false,
                    isFree = doc.getBoolean("isFree") ?: true,
                    order = doc.getLong("order")?.toInt() ?: 0
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Clear cache
     */
    fun clearCache() {
        cachedBooks = null
        cacheTimestamp = 0
    }

    private fun isCacheValid(): Boolean {
        return System.currentTimeMillis() - cacheTimestamp < CACHE_DURATION
    }
}
