package com.kienvo.fonosclone.data

import com.kienvo.fonosclone.model.Book
import com.example.rosach.R

/**
 * File tập trung chứa TẤT CẢ dữ liệu giả lập của ứng dụng
 * Mục đích: Dễ quản lý, tái sử dụng, và cập nhật dữ liệu
 */
object SampleData {

    // ============ AUDIOBOOK DATA ============

    val audioBooks = listOf(
        Book("1", "Nhà Giả Kim", "Paulo Coelho", "https://nxbhcm.com.vn/Image/Biasach/nhagiakimTB2020.jpg"),
        Book("2", "Đắc Nhân Tâm", "Dale Carnegie", "https://nxbhcm.com.vn/Image/Biasach/dacnhantam86.jpg"),
        Book("3", "Sapiens", "Yuval Noah Harari", "https://images-na.ssl-images-amazon.com/images/I/811PTyrckTL.jpg"),
        Book("4", "Cây Cam Ngọt Của Tôi", "José Mauro", "https://nld.mediacdn.vn/2021/1/22/13-cay-cam-ngot-161132379604435791636.jpg"),
        Book("5", "Trí Tuệ Do Thái", "Eran Katz", "https://bizweb.dktcdn.net/thumb/1024x1024/100/197/269/products/tri-tue-do-thai.jpg?v=1510634413673"),
        Book("6", "Kinh Thánh Nói Gì Về Tương Lai", "Charles H. Dyer", "https://pos.nvncdn.com/d8267c-94460/ps/20230829_mcTihAZZ0o.jpeg?v=1693302878"),
        Book("7", "Đọc Vị Bất Kì Ai", "David J. Lieberman", "https://cdn.hstatic.net/products/200000900535/doc_vi_bat_ky_ai_de_khong_bi_loi_dung_-bia_1__tb_2025__899034494358448295b41a80dc16019e.jpg"),
        Book("8", "Muôn Kiếp Nhân Sinh", "Nguyên Phong", "https://product.hstatic.net/200000122283/product/bia1-muonkiepnhansinh3-01_d1a246c6abfd4621bed63b8ca3b73ba9_master.jpg"),
    )

    val popularBooks = listOf(
        Book("9", "Nam Châm Tài Chính", "Marie-Claire Carlyle", "https://bizbooks.vn/uploads/images/2023/thang-10/1-nam-cham-tai-chinh-mt.jpg"),
        Book("10", "Hành Trình Trở Thành Người Giáo Viên", "Nguyễn Công Thái", "https://i0.wp.com/sachnoiviet.net/wp-content/uploads/2022/03/hanh-trinh-tro-thanh-nguoi-giao-vien-hanh-phuc-thinh-vuong-binh-an.jpg?fit=200%2C300&ssl=1"),
        Book("11", "Bách khoa thư về khoa học- Trái Đất và vũ trụ", "Nhiều tác giả", "https://cdn1.fahasa.com/media/catalog/product/i/m/image_195509_1_44248.jpg"),
        Book("12", "Tính Ưu Việt Của Hoài Nghi", "Tim Palmer", "https://www.nxbtre.com.vn/Images/Book/nxbtre_full_25122025_111216.jpg"),
        Book("13", "Tĩnh Lặng", "Thích Nhất Hạnh", "https://cdn.hstatic.net/products/200000900535/tinh_lang_-_bia_1_tb_2025__c4a6ae7b209f4a8792c707b6a9b69a6e.jpg"),
        Book("14", "Sống Chậm", "Melanie Barnes", "https://minhkhai.com.vn/hinhlon/8936186549434.jpg"),
        Book("15", "Tuổi Trẻ Đáng Giá Bao Nhiêu", "Rosie Nguyễn", "https://nld.mediacdn.vn/2018/3/24/sach-1521858607292758740290.jpg"),
        Book("16", "Chó Sủa Nhầm Cây", "Eric Barker", "https://cdn1.fahasa.com/media/flashmagazine/images/page_images/cho_sua_nham_cay_tb2023___tai_sao_nhung_gi_ta_biet_ve_thanh_cong_co_khi_lai_sai/2023_06_22_11_07_10_1-390x510.jpg"),
    )

    val healingBooks = listOf(
        Book("17", "Một Cuốn Sách Chữa Lành", "Brianna Wiest", "https://davibooks.vn/stores/uploads/z/z4729024325679_319a5b9666920fe8e785dcf3f0102996__97337_image2_800_big.jpg"),
        Book("18", "Một Thoáng Rực Rỡ Giữa Nhân Gian", "Ocean Vuong", "https://bizweb.dktcdn.net/thumb/1024x1024/100/363/455/products/motthoangtarucroonhangian011.jpg?v=1705552591463"),
        Book("19", "Sức Mạnh Chữa Lành Cơ Thể Của Nước", "F. Batmanghelidj, M.D", "https://bizweb.dktcdn.net/thumb/1024x1024/100/465/223/products/86.png?v=1761215098273"),
        Book("20", "Hoàng Tử Bé", "Antoine De Saint-Exupéry", "https://bizweb.dktcdn.net/thumb/1024x1024/100/363/455/products/hoangtube.jpg?v=1705552581243"),
        Book("21", "Làm Lành Với Bản Thân", "Manuela Mischke-Reeds", "https://bizweb.dktcdn.net/thumb/1024x1024/100/465/223/products/24-4f07600c-0995-4bfa-9c34-785531ddaac0.png?v=1752574705253"),
        Book("22", "Luật Nhân Quả - Tự Vấn Nghiệp Duyên, Xoay Chuyển Số Mệnh", "James Rondepierre", "https://bizweb.dktcdn.net/thumb/1024x1024/100/465/223/products/52-01df7223-c077-446b-b425-02eb1906df93.png?v=1755331057940"),
        Book("23", "Để Tâm Trí Là Nơi An Trú", "Morgan Harper Nichols", "https://minhkhai.com.vn/hinhlon/8935325026768-.jpg"),
        Book("24", "Chữa Lành Bản Thân Trong Thế Giới Đầy Tổn Thương", "Dr. Ahona Guha", "https://product.hstatic.net/200000696663/product/8936225390362_36cd29599252412f84c5647b0aa18f6b_1024x1024.jpg"),
    )

    val detectiveBooks = listOf(
        Book("25", "Những Cuộc Phiêu Lưu Của Sherlock Holmes", "Arthur Conan Doyle", "https://rekhtabooks.com/cdn/shop/products/1111019984212.jpg?v=1680041886"),
        Book("26", "GOTH Những Kẻ Hắc Ám", "Otsuichi", "https://bizweb.dktcdn.net/thumb/1024x1024/100/363/455/products/gothnhungkehacam01.jpg?v=1705552558923"),
        Book("27", "Thú Tội", "Minato Kanae", "https://bizweb.dktcdn.net/thumb/1024x1024/100/363/455/products/thutoi01.jpg?v=1705552105693"),
        Book("28", "13.67", "Chan Ho Kei", "https://product.hstatic.net/200000287623/product/13.67_61e241f0b6d743d883517c6bbf260e44_master.jpg"),
        Book("29", "Ghi Chép Pháp Y", "Lưu Hiểu Huy", "https://minhkhai.com.vn/hinhlon/8935325009433-.jpg"),
        Book("30", "Ngôi Nhà Kỳ Quái", "Uketsu", "https://cdn1.fahasa.com/media/catalog/product/8/9/8935095632763.jpg"),
        Book("31", "Sự Im Lặng Của Bầy Cừu", "Thomas Harris", "https://lh3.googleusercontent.com/pw/ACtC-3dbCNcGCYZntEXQfkARgdQD1yEN0n-pRltjNUTlpshRWAFXo-c6HsNoE7Jw6fzMm-bL1QlIrJdG0a5-_73YenG6e9-a5ayZkRVu7cmnum3aiFjvR95BWFhOoE7U2R7Fja1gNKAfpvoAnXZKSFXiGl3zVQ=w384-h576-no?authuser=0"),
        Book("32", "Dữ liệu tử thần", "Jeffery Deaver", "https://i.ex-cdn.com/mientay.giadinhonline.vn/files/content/2021/08/19/42-1241.jpg"),
    )

    // ============ EBOOK DATA ============

    data class EbookData(
        val id: String,
        val title: String,
        val author: String,
        val coverUrl: String,
        val rating: Double,
        val rank: Int = 0
    )

    val topEbooks = listOf(
        EbookData("e1", "Minh Triết Trong Ăn Uống", "Ngô Đức Vượng", "https://voiz-prod.s3-wewe.cloud.cmctelecom.vn/uploads/avatar/filename/1077/webp_26db9a2ce19ef089fec27418e06920d26706db72.webp", 4.0, 1),
        EbookData("e2", "Hiểu Về Trái Tim", "Minh Niệm", "https://dtv-ebook.com.vn/images/files_2/2020/hieu-ve-trai-tim-minh-niem.jpg", 4.9, 2),
        EbookData("e3", "Tư Duy Ngược", "Adam Grant", "https://firstnews.vn/upload/products/original/-1729758499.jpg", 4.5, 3),
        EbookData("e4", "Đắc Nhân Tâm", "Dale Carnegie", "https://nxbhcm.com.vn/Image/Biasach/dacnhantam86.jpg", 4.8, 4)
    )

    val freeEbooks = listOf(
        EbookData("e5", "Dế Mèn Phiêu Lưu Ký", "Tô Hoài", "https://thegioicotich.vn/wp-content/uploads/2019/09/de-men-phieu-luu-ky-chuong-4-cua-nha-van-to-hoai.png", 4.8),
        EbookData("e6", "Số Đỏ", "Vũ Trọng Phụng", "https://product.hstatic.net/200000017360/product/bia_sodo3-b1_b32d805ef78846fab8d0d6c1c7fc887b_master.jpg", 4.7),
        EbookData("e7", "Tắt Đèn", "Ngô Tất Tố", "https://dilib.vn/img/news/2022/11/larger/7820-tat-den-1.jpg?v=1370", 4.6),
    )

    val literatureEbooks = listOf(
        EbookData("e8", "Rừng Na Uy", "Haruki Murakami", "https://cdn1.fahasa.com/media/catalog/product/8/9/8936024919047_1_1.jpg", 4.5),
        EbookData("e9", "Trăm Năm Cô Đơn", "G.G. Marquez", "https://bizweb.dktcdn.net/thumb/1024x1024/100/363/455/products/tramnamcodon01-0ce89a31-455d-4492-b0ce-1f6329222273.jpg?v=1705552510463", 4.8),
        EbookData("e10", "Chí Phèo", "Nam Cao", "https://book.sachgiai.com/uploads/book/truyen-ngan-chi-pheo/truyen-ngan-chi-pheo-nam-cao.jpg", 4.9),
    )

    val healthEbooks = listOf(
        EbookData("e11", "Nhân Tố Enzyme", "Hiromi Shinya", "https://product.hstatic.net/200000900535/product/1876029511_bia_nhan-to-enzyme-2_f659d7186380445db4644d05997e448f_1024x1024.jpg", 4.4),
        EbookData("e12", "Cơ Thể 4 Giờ", "Tim Ferriss", "https://minhkhai.com.vn/hinhlon/8935246915936.jpg", 4.3),
    )

    val psychologyEbooks = listOf(
        EbookData("e13", "Tư Duy Nhanh & Chậm", "Daniel Kahneman", "https://bizweb.dktcdn.net/thumb/1024x1024/100/197/269/products/462558750-1083111936819329-1957541486232979466-n.png?v=1730363480047", 4.7),
        EbookData("e14", "Phi Lý Trí", "Dan Ariely", "https://bizweb.dktcdn.net/thumb/1024x1024/100/197/269/products/11.png?v=1676087122193", 4.6),
    )

    val lifestyleEbooks = listOf(
        EbookData("e15", "Lối Sống Tối Giản", "Sasaki Fumio", "https://product.hstatic.net/200000900535/product/bia_loi-song-toi-gian-01-1-2_647828c3e1dc4b0182e4333e49ebe6f6.jpg", 4.2),
        EbookData("e16", "Lagom", "Nhiều tác giả", "https://cdn1.fahasa.com/media/catalog/product/8/9/8934974184706.jpg", 4.1),
    )

    val philosophyEbooks = listOf(
        EbookData("e17", "Sự An Ủi Của Triết Học", "Alain de Botton", "https://bizweb.dktcdn.net/thumb/1024x1024/100/363/455/products/suanuitriethoc01-8b3e5c36-50b5-4eb4-8c04-9b17c21b31c2.jpg?v=1736215302887", 4.5),
        EbookData("e18", "Thế Giới Của Sophie", "Jostein Gaarder", "https://bizweb.dktcdn.net/thumb/1024x1024/100/363/455/products/thegioicuasophie01.jpg?v=1710306286017", 4.8),
    )

    val businessEbooks = listOf(
        EbookData("e19", "Cha Giàu Cha Nghèo", "Robert Kiyosaki", "https://bookfun.vn/wp-content/uploads/2024/10/cha-giau-cha-ngheo.jpg", 4.9),
        EbookData("e20", "Nhà Đầu Tư Thông Minh", "Benjamin Graham", "https://bizweb.dktcdn.net/thumb/1024x1024/100/363/455/products/nhadaututhongminhscaled.jpg?v=1705552576643", 4.8),
    )

    // ============ KIDS STORIES DATA ============

    data class KidStory(
        val title: String,
        val imageUrl: Any,
        val duration: String
    )

    val kidsStories = listOf(
        KidStory("Thần mộng mơ", "https://salt.tikicdn.com/cache/750x750/ts/product/5d/63/6a/35eafcb4ac833df01c59d2c08dc6ff51.jpg.webp", "16 phút"),
        KidStory("Người cung trăng", "https://salt.tikicdn.com/cache/750x750/ts/product/17/ce/ce/819b023eebd62f62eb927f9e0e4fea44.jpg.webp", "19 phút"),
        KidStory("Câu lạc bộ Phiêu lưu",R.drawable.fantasy_image , "32 phút"),
        KidStory("Những bức thư của Họa My", R.drawable.hoamy, "29 phút"),
        KidStory("Thăm nhà ông Mặt Trời", R.drawable.mattroi, "32 phút"),
        KidStory("Ngôi trường kỳ lạ", R.drawable.myteriousschool, "29 phút"),
        KidStory("Mẹ yêu con", R.drawable.mom, "31 phút"),
        KidStory("Bạn cún đi lạc", R.drawable.dog, "29 phút")
    )
}

