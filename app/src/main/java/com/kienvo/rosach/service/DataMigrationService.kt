package com.kienvo.rosach.service

import com.google.firebase.firestore.FirebaseFirestore
import com.kienvo.rosach.data.SampleData
import com.kienvo.rosach.model.Book
import kotlinx.coroutines.tasks.await

class DataMigrationService(private val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {

    data class MigrationResult(
        val success: Boolean,
        val message: String,
        val booksUploaded: Int = 0,
        val categoriesCreated: Int = 0
    )

    suspend fun uploadAllDataToFirestore(): MigrationResult {
        return try {
            var totalBooks = 0

            // Upload tất cả sách từ allBooks (đã chuẩn hóa)
            for (book in SampleData.allBooks) {
                uploadBook(book)
                totalBooks++
            }

            // Create Categories
            val categoriesCount = createCategories()

            MigrationResult(
                success = true,
                message = "Upload thành công!",
                booksUploaded = totalBooks,
                categoriesCreated = categoriesCount
            )
        } catch (e: Exception) {
            MigrationResult(
                success = false,
                message = "Lỗi: ${e.message}"
            )
        }
    }

    private suspend fun uploadBook(book: Book) {
        // Xác định categories dựa trên ID và type
        val categories = mutableListOf<String>()
        categories.add(book.type) // "audiobook" hoặc "ebook"

        when {
            book.id.toIntOrNull() in 9..16 -> categories.add("popular")
            book.id.toIntOrNull() in 17..24 -> categories.add("healing")
            book.id.toIntOrNull() in 25..32 -> categories.add("detective")
            book.id == "33" || book.id == "34" || book.id == "35" || book.id == "36" -> categories.add("top")
            book.id.toIntOrNull() in 37..39 -> categories.add("free")
            book.id.toIntOrNull() in 40..42 -> categories.add("literature")
            book.id.toIntOrNull() in 43..44 -> categories.add("health")
            book.id.toIntOrNull() in 45..46 -> categories.add("psychology")
            book.id.toIntOrNull() in 47..48 -> categories.add("lifestyle")
            book.id.toIntOrNull() in 49..50 -> categories.add("philosophy")
            book.id.toIntOrNull() in 51..52 -> categories.add("business")
        }

        val bookData = hashMapOf(
            "id" to book.id,
            "title" to book.title,
            "author" to book.author,
            "coverUrl" to book.coverUrl,
            "type" to book.type,
            "categories" to categories,
            "tags" to emptyList<String>(),
            "rating" to book.rating,
            "totalRatings" to (100..1000).random(),
            "totalListens" to (500..5000).random(),
            "duration" to if (book.type == "audiobook") "${(2..8).random()}h ${(0..59).random()}m" else null,
            "totalParts" to if (book.type == "audiobook") (2..5).random() else null,
            "pageCount" to if (book.type == "ebook") (100..500).random() else null,
            "fileSize" to "${(5..50).random()} MB",
            "language" to "vi",
            "publishYear" to (2015..2024).random(),
            "isPremium" to (Math.random() > 0.7),
            "price" to if (Math.random() > 0.7) (50000..200000).random() else 0,
            "isFeatured" to (book.rank in 1..4 || Math.random() > 0.9),
            "rank" to book.rank,
            "createdAt" to com.google.firebase.Timestamp.now(),
            "updatedAt" to com.google.firebase.Timestamp.now()
        )

        db.collection("books").document(book.id).set(bookData).await()

        // Upload sample parts for audiobooks
        if (book.type == "audiobook") {
            val totalParts = bookData["totalParts"] as Int
            uploadSampleParts(book.id, totalParts)
        }
    }

    private suspend fun uploadSampleParts(bookId: String, totalParts: Int) {
        for (i in 1..totalParts) {
            val partData = hashMapOf(
                "id" to "${bookId}_part_$i",
                "bookId" to bookId,
                "partNumber" to i,
                "title" to "Phần $i",
                // Sử dụng URL audio thật từ Firebase Storage
                // Ví dụ: "https://firebasestorage.googleapis.com/v0/b/rosach-5d3e8.firebasestorage.app/o/..."
                "audioUrl" to getAudioUrlForBook(bookId, i),
                "duration" to "${(20..60).random()}:${(10..59).random()}",
                "durationSeconds" to (1200..3600).random(),
                "fileSize" to "${(10..30).random()} MB",
                "isPremium" to (i > 2), // 2 part đầu free
                "isFree" to (i <= 2),
                "order" to i,
                "createdAt" to com.google.firebase.Timestamp.now()
            )

            db.collection("books").document(bookId)
                .collection("parts").document("${bookId}_part_$i")
                .set(partData).await()
        }
    }

    /**
     * Lấy URL audio cho book và part cụ thể
     * TODO: Thay bằng URL thực từ Firebase Storage của bạn
     */
    private fun getAudioUrlForBook(bookId: String, partNumber: Int): String {
        // Ví dụ URL của Đắc Nhân Tâm
        return when (bookId) {
            "2" -> "https://firebasestorage.googleapis.com/v0/b/rosach-5d3e8.firebasestorage.app/o/DacNhanTam%2Fdac-nhan.mp3?alt=media&token=7673b069-8efe-4b4d-a9de-35ae516e47fd"
            // Thêm các sách khác ở đây
            else -> "https://example.com/audio/${bookId}_part_$partNumber.mp3"
        }
    }

    private suspend fun createCategories(): Int {
        val categories = listOf(
            hashMapOf(
                "id" to "cat_popular",
                "name" to "Top Thịnh Hành",
                "slug" to "top-thinh-hanh",
                "type" to "audiobook",
                "description" to "Những cuốn sách được nghe nhiều nhất",
                "order" to 1,
                "isActive" to true,
                "createdAt" to com.google.firebase.Timestamp.now()
            ),
            hashMapOf(
                "id" to "cat_healing",
                "name" to "Sách Chữa Lành",
                "slug" to "sach-chua-lanh",
                "type" to "audiobook",
                "description" to "Sách giúp thư giãn và chữa lành tâm hồn",
                "order" to 2,
                "isActive" to true,
                "createdAt" to com.google.firebase.Timestamp.now()
            ),
            hashMapOf(
                "id" to "cat_detective",
                "name" to "Tiểu Thuyết Trinh Thám",
                "slug" to "tieu-thuyet-trinh-tham",
                "type" to "audiobook",
                "description" to "Những câu chuyện trinh thám hấp dẫn",
                "order" to 3,
                "isActive" to true,
                "createdAt" to com.google.firebase.Timestamp.now()
            ),
            hashMapOf(
                "id" to "cat_top_ebook",
                "name" to "Top Ebook",
                "slug" to "top-ebook",
                "type" to "ebook",
                "description" to "Ebook được đánh giá cao nhất",
                "order" to 4,
                "isActive" to true,
                "createdAt" to com.google.firebase.Timestamp.now()
            ),
            hashMapOf(
                "id" to "cat_free_ebook",
                "name" to "Ebook Miễn Phí",
                "slug" to "ebook-mien-phi",
                "type" to "ebook",
                "description" to "Những cuốn sách kinh điển miễn phí",
                "order" to 5,
                "isActive" to true,
                "createdAt" to com.google.firebase.Timestamp.now()
            ),
            hashMapOf(
                "id" to "cat_literature",
                "name" to "Văn Học",
                "slug" to "van-hoc",
                "type" to "ebook",
                "description" to "Những tác phẩm văn học kinh điển",
                "order" to 6,
                "isActive" to true,
                "createdAt" to com.google.firebase.Timestamp.now()
            ),
            hashMapOf(
                "id" to "cat_business",
                "name" to "Kinh Doanh",
                "slug" to "kinh-doanh",
                "type" to "ebook",
                "description" to "Sách về kinh doanh và đầu tư",
                "order" to 7,
                "isActive" to true,
                "createdAt" to com.google.firebase.Timestamp.now()
            )
        )

        for (category in categories) {
            db.collection("categories").document(category["id"] as String).set(category).await()
        }

        return categories.size
    }

    suspend fun clearAllData(): MigrationResult {
        return try {
            // Delete all books
            val books = db.collection("books").get().await()
            for (doc in books.documents) {
                // Delete subcollection parts first
                val parts = db.collection("books").document(doc.id).collection("parts").get().await()
                for (part in parts.documents) {
                    part.reference.delete().await()
                }
                doc.reference.delete().await()
            }

            // Delete all categories
            val categories = db.collection("categories").get().await()
            for (doc in categories.documents) {
                doc.reference.delete().await()
            }

            MigrationResult(
                success = true,
                message = "Đã xóa tất cả dữ liệu"
            )
        } catch (e: Exception) {
            MigrationResult(
                success = false,
                message = "Lỗi khi xóa: ${e.message}"
            )
        }
    }
}
