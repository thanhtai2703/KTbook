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
            book.id.startsWith("popular_") -> categories.add("popular")
            book.id.startsWith("healing_") -> categories.add("healing")
            book.id.startsWith("detective_") -> categories.add("detective")
            book.id.startsWith("top_ebook_") -> categories.add("top")
            book.id.startsWith("free_ebook_") -> categories.add("free")
            book.id.startsWith("literature_") -> categories.add("literature")
            book.id.startsWith("health_") -> categories.add("health")
            book.id.startsWith("psychology_") -> categories.add("psychology")
            book.id.startsWith("lifestyle_") -> categories.add("lifestyle")
            book.id.startsWith("philosophy_") -> categories.add("philosophy")
            book.id.startsWith("business_") -> categories.add("business")
            book.type == "kid" -> categories.add("kids")
            book.type == "astronomy" -> categories.add("astronomy")
        }

        val bookData = hashMapOf(
            "id" to book.id,
            "title" to book.title,
            "author" to book.author,
            "coverUrl" to book.coverUrl,
            "type" to book.type,
            "description" to book.description,
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
            "audio_2" -> "https://firebasestorage.googleapis.com/v0/b/rosach-5d3e8.firebasestorage.app/o/DacNhanTam%2Fdac-nhan.mp3?alt=media&token=7673b069-8efe-4b4d-a9de-35ae516e47fd"
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
                "imageUrl" to "https://nld.mediacdn.vn/2018/3/24/sach-1521858607292758740290.jpg",
                "color" to "#6D4C41",
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
                "imageUrl" to "https://davibooks.vn/stores/uploads/z/z4729024325679_319a5b9666920fe8e785dcf3f0102996__97337_image2_800_big.jpg",
                "color" to "#2E7D32",
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
                "imageUrl" to "https://images.unsplash.com/photo-1455390582262-044cdead277a?q=80&w=1000&auto=format&fit=crop", // Hình ảnh Sherlock Holmes/Kính lúp từ Unsplash
                "color" to "#BF360C",
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
                "imageUrl" to "https://dtv-ebook.com.vn/images/files_2/2020/hieu-ve-trai-tim-minh-niem.jpg",
                "color" to "#0D47A1",
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
                "imageUrl" to "https://product.hstatic.net/200000017360/product/bia_sodo3-b1_b32d805ef78846fab8d0d6c1c7fc887b_master.jpg",
                "color" to "#546E7A",
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
                "imageUrl" to "https://thegioicotich.vn/wp-content/uploads/2019/09/de-men-phieu-luu-ky-chuong-4-cua-nha-van-to-hoai.png",
                "color" to "#004D40",
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
                "imageUrl" to "https://bizbooks.vn/uploads/images/2023/thang-10/1-nam-cham-tai-chinh-mt.jpg",
                "color" to "#37474F",
                "order" to 7,
                "isActive" to true,
                "createdAt" to com.google.firebase.Timestamp.now()
            ),
            hashMapOf(
                "id" to "cat_kids",
                "name" to "Truyện Thiếu Nhi",
                "slug" to "kids",
                "type" to "kid",
                "description" to "Truyện kể bé nghe",
                "imageUrl" to "https://cdn1.fahasa.com/media/flashmagazine/images/page_images/than_mong_mo_va_cuoc_chien_giac_mo/2023_05_09_16_40_10_1-390x510.jpg",
                "color" to "#D81B60",
                "order" to 8,
                "isActive" to true,
                "createdAt" to com.google.firebase.Timestamp.now()
            ),
            hashMapOf(
                "id" to "cat_astronomy",
                "name" to "Thiên Văn Học",
                "slug" to "astronomy",
                "type" to "astronomy",
                "description" to "Khám phá vũ trụ kỳ bí",
                "imageUrl" to "https://dtv-ebook.com.vn/images/files_2/2022/012022/vu-tru-carl-sagan.jpg",
                "color" to "#283593",
                "order" to 9,
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
