package com.kienvo.rosach.service

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig

class GeminiService {
    private val apiKey = "AIzaSyB53v-WQzlw8GkOfp65IQKJGptjjOI9Cck"

    // Sử dụng gemini-1.5-flash là model mới và ổn định nhất
    private val model = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = apiKey,
        generationConfig = generationConfig {
            temperature = 0.8f // Tăng tính sáng tạo cho việc phối âm
        }
    )

    /**
     * Gửi yêu cầu tư vấn sách tới AI
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
     * Dựa trên mô tả sách, gợi ý một bản phối âm thanh môi trường đa lớp (Layered Ambience)
     */
    suspend fun getAmbientSuggestion(bookDescription: String): String {
        val prompt = """
            Bạn là một chuyên gia thiết kế âm thanh cho phim (Sound Designer). 
            Dựa trên mô tả cuốn sách sau, hãy tạo một "Không gian âm thanh" (Ambient Mix) đa lớp (tối đa 3 loại) để làm nổi bật tâm trạng của câu chuyện.
            
            DANH SÁCH ÂM THANH CÓ SẴN:
            - rain (tiếng mưa)
            - wind (tiếng gió)
            - fire (tiếng lửa tí tách)
            - birds (tiếng chim hót)
            
            YÊU CẦU QUAN TRỌNG:
            1. Hãy cố gắng chọn ít nhất 2 loại âm thanh để tạo sự sống động (ví dụ: mưa + gió, hoặc chim hót + gió).
            2. Phân bổ âm lượng (0.1 đến 0.6) cho mỗi loại.
            3. Tổng âm lượng các loại không nên quá 0.8.
            4. Trả về DUY NHẤT một chuỗi JSON thuần túy (Plain JSON), không có markdown. 
            
            Ví dụ mẫu: {"rain": 0.4, "wind": 0.2, "birds": 0.1}
            
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