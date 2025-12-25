package com.kienvo.rosach.service

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig

class GeminiService {
    private val apiKey = "AIzaSyAxYr-DQhEIg6RsecTx7u1v86Z5RTjuCnw"
    
    // Sử dụng gemini-1.5-flash là model mới và ổn định nhất hiện nay
    private val model = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = apiKey,
        generationConfig = generationConfig {
            temperature = 0.7f
        }
    )

    /**
     * Gửi yêu cầu tư vấn sách tới AI
     * @param userQuery Câu hỏi của user
     * @param bookCatalog Danh sách thông tin sách để AI tham khảo
     */
    suspend fun getRecommendation(userQuery: String, bookCatalog: String): String? {
        val systemPrompt = """
            Bạn là RoSach Librarian, một quản thư thông minh và thân thiện.
            Nhiệm vụ của bạn là dựa vào yêu cầu của người dùng để gợi ý 1 cuốn sách phù hợp nhất từ danh sách bên dưới.
            
            QUY TẮC:
            1. Chỉ được gợi ý sách có trong danh sách được cung cấp.
            2. Phản hồi của bạn phải có 2 phần: 
               - Phần 1: Một lời giải thích ngắn gọn, hấp dẫn lý do tại sao cuốn sách này phù hợp (tối đa 2 câu).
               - Phần 2: ID của cuốn sách đó, đặt trong dấu ngoặc vuông ví dụ [audio_1].
            3. Nếu không tìm thấy sách phù hợp, hãy trả lời lịch sự và gợi ý họ thử tìm chủ đề khác.
            
            DANH SÁCH SÁCH:
            $bookCatalog
        """.trimIndent()

        return try {
            val response = model.generateContent(systemPrompt + "\n\nYêu cầu của người dùng: " + userQuery)
            
            val text = response.text
            if (text.isNullOrBlank()) {
                "Xin lỗi, tôi không tìm thấy nội dung phù hợp. Bạn hãy thử mô tả khác nhé!"
            } else {
                text
            }
        } catch (e: Exception) {
            android.util.Log.e("GeminiService", "Error: ${e.message}", e)
            null
        }
    }
}
