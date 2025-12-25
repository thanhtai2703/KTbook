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
            android.util.Log.e("GeminiService", "Exception in generateContent: ${e.message}", e)
            null
        }
    }

    /**
     * Dựa trên mô tả sách, gợi ý một bản phối âm thanh môi trường (tối đa 3 loại)
     * Trả về JSON string ví dụ: {"rain": 0.5, "wind": 0.2}
     */
    suspend fun getAmbientSuggestion(bookDescription: String): String {
        val prompt = """
            Bạn là một chuyên gia phối âm thanh. Dựa trên mô tả cuốn sách sau, hãy tạo một bản phối âm thanh môi trường phù hợp nhất để nghe kèm.
            Bạn có thể chọn tối đa 3 loại âm thanh từ danh sách: 
            rain (tiếng mưa), wind (tiếng gió), fire (tiếng lửa tí tách), birds (tiếng chim hót).
            
            YÊU CẦU:
            1. Phân bổ âm lượng (0.0 đến 1.0) cho mỗi loại bạn chọn.
            2. Tổng âm lượng các loại không nên quá 0.8 để tránh át tiếng sách.
            3. Trả về DUY NHẤT một chuỗi JSON thuần túy (Plain JSON), KHÔNG ĐƯỢC để trong block code ```json. 
            4. Chỉ bao gồm các key: "rain", "wind", "fire", "birds".
            Ví dụ: {"rain": 0.4, "wind": 0.1}
            
            MÔ TẢ SÁCH: $bookDescription
        """.trimIndent()

        return try {
            val response = model.generateContent(prompt)
            val cleanJson = response.text?.replace("```json", "")?.replace("```", "")?.trim() 
                ?: "{\"rain\": 0.5}"
            cleanJson
        } catch (e: Exception) {
            "{\"rain\": 0.5}"
        }
    }
}
