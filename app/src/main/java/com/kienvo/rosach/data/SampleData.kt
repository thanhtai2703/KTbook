package com.kienvo.rosach.data

import com.kienvo.rosach.model.Book
import com.example.rosach.R

/**
 * File tập trung chứa TẤT CẢ dữ liệu giả lập của ứng dụng
 * Đã sửa: Chuyển đổi dữ liệu Kids sang model Book để có ID
 */
object SampleData {

    // ============ AUDIOBOOK DATA ============
    private val audioBooksBase = listOf(
        Book("1", "Nhà Giả Kim", "Paulo Coelho", "https://nxbhcm.com.vn/Image/Biasach/nhagiakimTB2020.jpg", "audiobook", 4.5),
        Book("2", "Đắc Nhân Tâm", "Dale Carnegie", "https://nxbhcm.com.vn/Image/Biasach/dacnhantam86.jpg", "audiobook", 4.8),
        Book("3", "Sapiens", "Yuval Noah Harari", "https://images-na.ssl-images-amazon.com/images/I/811PTyrckTL.jpg", "audiobook", 4.7),
        Book("4", "Cây Cam Ngọt Của Tôi", "José Mauro", "https://nld.mediacdn.vn/2021/1/22/13-cay-cam-ngot-161132379604435791636.jpg", "audiobook", 4.6),
        Book("5", "Trí Tuệ Do Thái", "Eran Katz", "https://bizweb.dktcdn.net/thumb/1024x1024/100/197/269/products/tri-tue-do-thai.jpg?v=1510634413673", "audiobook", 4.4),
        Book("6", "Kinh Thánh Nói Gì Về Tương Lai", "Charles H. Dyer", "https://pos.nvncdn.com/d8267c-94460/ps/20230829_mcTihAZZ0o.jpeg?v=1693302878", "audiobook", 4.3),
        Book("7", "Đọc Vị Bất Kì Ai", "David J. Lieberman", "https://cdn.hstatic.net/products/200000900535/doc_vi_bat_ky_ai_de_khong_bi_loi_dung_-bia_1__tb_2025__899034494358448295b41a80dc16019e.jpg", "audiobook", 4.5),
        Book("8", "Muôn Kiếp Nhân Sinh", "Nguyên Phong", "https://product.hstatic.net/200000122283/product/bia1-muonkiepnhansinh3-01_d1a246c6abfd4621bed63b8ca3b73ba9_master.jpg", "audiobook", 4.6),
    )

    private val popularBooksBase = listOf(
        Book("9", "Nam Châm Tài Chính", "Marie-Claire Carlyle", "https://bizbooks.vn/uploads/images/2023/thang-10/1-nam-cham-tai-chinh-mt.jpg", "audiobook", 4.7),
        Book("10", "Hành Trình Trở Thành Người Giáo Viên", "Nguyễn Công Thái", "https://i0.wp.com/sachnoiviet.net/wp-content/uploads/2022/03/hanh-trinh-tro-thanh-nguoi-giao-vien-hanh-phuc-thinh-vuong-binh-an.jpg?fit=200%2C300&ssl=1", "audiobook", 4.5),
        Book("11", "Bách khoa thư về khoa học", "Nhiều tác giả", "https://cdn1.fahasa.com/media/catalog/product/i/m/image_195509_1_44248.jpg", "audiobook", 4.6),
        Book("12", "Tính Ưu Việt Của Hoài Nghi", "Tim Palmer", "https://www.nxbtre.com.vn/Images/Book/nxbtre_full_25122025_111216.jpg", "audiobook", 4.4),
        Book("13", "Tĩnh Lặng", "Thích Nhất Hạnh", "https://cdn.hstatic.net/products/200000900535/tinh_lang_-_bia_1_tb_2025__c4a6ae7b209f4a8792c707b6a9b69a6e.jpg", "audiobook", 4.8),
        Book("14", "Sống Chậm", "Melanie Barnes", "https://minhkhai.com.vn/hinhlon/8936186549434.jpg", "audiobook", 4.5),
        Book("15", "Tuổi Trẻ Đáng Giá Bao Nhiêu", "Rosie Nguyễn", "https://nld.mediacdn.vn/2018/3/24/sach-1521858607292758740290.jpg", "audiobook", 4.6),
        Book("16", "Chó Sủa Nhầm Cây", "Eric Barker", "https://cdn1.fahasa.com/media/flashmagazine/images/page_images/cho_sua_nham_cay_tb2023___tai_sao_nhung_gi_ta_biet_ve_thanh_cong_co_khi_lai_sai/2023_06_22_11_07_10_1-390x510.jpg", "audiobook", 4.7),
    )

    private val healingBooksBase = listOf(
        Book("17", "Một Cuốn Sách Chữa Lành", "Brianna Wiest", "https://davibooks.vn/stores/uploads/z/z4729024325679_319a5b9666920fe8e785dcf3f0102996__97337_image2_800_big.jpg", "audiobook", 4.5),
        Book("18", "Một Thoáng Rực Rỡ Giữa Nhân Gian", "Ocean Vuong", "https://bizweb.dktcdn.net/thumb/1024x1024/100/363/455/products/motthoangtarucroonhangian011.jpg?v=1705552591463", "audiobook", 4.6),
        Book("19", "Sức Mạnh Chữa Lành", "F. Batmanghelidj", "https://bizweb.dktcdn.net/thumb/1024x1024/100/465/223/products/86.png?v=1761215098273", "audiobook", 4.4),
        Book("20", "Hoàng Tử Bé", "Antoine De Saint-Exupéry", "https://bizweb.dktcdn.net/thumb/1024x1024/100/363/455/products/hoangtube.jpg?v=1705552581243", "audiobook", 4.9),
        Book("21", "Làm Lành Với Bản Thân", "Manuela Mischke-Reeds", "https://bizweb.dktcdn.net/thumb/1024x1024/100/465/223/products/24-4f07600c-0995-4bfa-9c34-785531ddaac0.png?v=1752574705253", "audiobook", 4.5),
        Book("22", "Luật Nhân Quả", "James Rondepierre", "https://bizweb.dktcdn.net/thumb/1024x1024/100/465/223/products/52-01df7223-c077-446b-b425-02eb1906df93.png?v=1755331057940", "audiobook", 4.3),
        Book("23", "Để Tâm Trí Là Nơi An Trú", "Morgan Harper Nichols", "https://minhkhai.com.vn/hinhlon/8935325026768-.jpg", "audiobook", 4.6),
        Book("24", "Chữa Lành Bản Thân", "Dr. Ahona Guha", "https://product.hstatic.net/200000696663/product/8936225390362_36cd29599252412f84c5647b0aa18f6b_1024x1024.jpg", "audiobook", 4.7),
    )

    private val detectiveBooksBase = listOf(
        Book("25", "Sherlock Holmes", "Arthur Conan Doyle", "https://rekhtabooks.com/cdn/shop/products/1111019984212.jpg?v=1680041886", "audiobook", 4.8),
        Book("26", "GOTH Những Kẻ Hắc Ám", "Otsuichi", "https://bizweb.dktcdn.net/thumb/1024x1024/100/363/455/products/gothnhungkehacam01.jpg?v=1705552558923", "audiobook", 4.6),
        Book("27", "Thú Tội", "Minato Kanae", "https://bizweb.dktcdn.net/thumb/1024x1024/100/363/455/products/thutoi01.jpg?v=1705552105693", "audiobook", 4.7),
        Book("28", "13.67", "Chan Ho Kei", "https://product.hstatic.net/200000287623/product/13.67_61e241f0b6d743d883517c6bbf260e44_master.jpg", "audiobook", 4.5),
        Book("29", "Ghi Chép Pháp Y", "Lưu Hiểu Huy", "https://minhkhai.com.vn/hinhlon/8935325009433-.jpg", "audiobook", 4.6),
        Book("30", "Ngôi Nhà Kỳ Quái", "Uketsu", "https://cdn1.fahasa.com/media/catalog/product/8/9/8935095632763.jpg", "audiobook", 4.4),
        Book("31", "Sự Im Lặng Của Bầy Cừu", "Thomas Harris", "https://salt.tikicdn.com/cache/w1200/ts/product/f4/e3/c2/c0397072522730248232930229342734.jpg", "audiobook", 4.9),
        Book("32", "Dữ liệu tử thần", "Jeffery Deaver", "https://i.ex-cdn.com/mientay.giadinhonline.vn/files/content/2021/08/19/42-1241.jpg", "audiobook", 4.5),
    )

    // ============ EBOOK DATA ============
    private val topEbooksBase = listOf(
        Book("33", "Minh Triết Trong Ăn Uống", "Ngô Đức Vượng", "https://voiz-prod.s3-wewe.cloud.cmctelecom.vn/uploads/avatar/filename/1077/webp_26db9a2ce19ef089fec27418e06920d26706db72.webp", "ebook", 4.0),
        Book("34", "Hiểu Về Trái Tim", "Minh Niệm", "https://dtv-ebook.com.vn/images/files_2/2020/hieu-ve-trai-tim-minh-niem.jpg", "ebook", 4.9),
        Book("35", "Tư Duy Ngược", "Adam Grant", "https://firstnews.vn/upload/products/original/-1729758499.jpg", "ebook", 4.5),
        Book("36", "Đắc Nhân Tâm", "Dale Carnegie", "https://nxbhcm.com.vn/Image/Biasach/dacnhantam86.jpg", "ebook", 4.8)
    )

    private val freeEbooksBase = listOf(
        Book("37", "Dế Mèn Phiêu Lưu Ký", "Tô Hoài", "https://thegioicotich.vn/wp-content/uploads/2019/09/de-men-phieu-luu-ky-chuong-4-cua-nha-van-to-hoai.png", "ebook", 4.8),
        Book("38", "Số Đỏ", "Vũ Trọng Phụng", "https://product.hstatic.net/200000017360/product/bia_sodo3-b1_b32d805ef78846fab8d0d6c1c7fc887b_master.jpg", "ebook", 4.7),
        Book("39", "Tắt Đèn", "Ngô Tất Tố", "https://dilib.vn/img/news/2022/11/larger/7820-tat-den-1.jpg?v=1370", "ebook", 4.6),
    )

    // (Giữ nguyên các list ebook khác nếu bạn có, tôi rút gọn để tập trung vào sửa lỗi chính)

    // ============ KIDS STORIES DATA (QUAN TRỌNG: Đã chuyển sang model Book) ============

    // Chúng ta tạo ID riêng cho sách thiếu nhi, ví dụ bắt đầu bằng "kid_"
    private val kidsBooksBase = listOf(
        Book("kid_1", "Thần mộng mơ", "William Joyce", R.drawable.thanmongmo, "kid", 4.8),
        Book("kid_2", "Người cung trăng", "William Joyce", R.drawable.moonman, "kid", 4.7),
        Book("kid_3", "Câu lạc bộ Phiêu lưu", "Nhiều tác giả", R.drawable.fantasy_image, "kid", 4.5),
        Book("kid_4", "Những bức thư của Họa My", "Tô Hoài", R.drawable.hoamy, "kid", 4.6),
        Book("kid_5", "Thăm nhà ông Mặt Trời", "Nguyễn Khoa Điềm", R.drawable.mattroi, "kid", 4.4),
        Book("kid_6", "Ngôi trường kỳ lạ", "Enid Blyton", R.drawable.myteriousschool, "kid", 4.3),
        Book("kid_7", "Mẹ yêu con", "Robert Munsch", R.drawable.mom, "kid", 4.9),
        Book("kid_8", "Bạn cún đi lạc", "Nhiều tác giả", R.drawable.dog, "kid", 4.5)
    )

    // ============ ASTRONOMY BOOKS DATA ============
    private val astronomyBooksBase = listOf(
        Book("astro_1", "Vũ Trụ Trong Vỏ Hạt Dẻ", "Stephen Hawking", "https://nhasachmienphi.com/images/thumbnail/nhasachmienphi-vu-tru-trong-vo-hat-de.jpg", "astronomy", 4.9),
        Book("astro_2", "Lược Sử Vũ Trụ", "Anne Rooney", R.drawable.luocsuvutru, "astronomy", 4.8),
        Book("astro_3", "Vũ Trụ", "Carl Sagan", "https://dtv-ebook.com.vn/images/files_2/2022/012022/vu-tru-carl-sagan.jpg", "astronomy", 4.9),
        Book("astro_4", "Sách Khởi Nguyên Của Vũ Trụ: Lịch Sử 14 Tỉ Năm Tiến Hóa", "Neil deGrasse Tyson", R.drawable.khoinguyenvutru, "astronomy", 4.7),
        Book("astro_5", "Thiên Văn Học Trực Quan", "Ian Ridpath", "https://dinhtibooks.com.vn/images/products/2025/06/large/z6740584211172_6ca8f37d73f2847372b77b758480d665_1750835364.webp", "astronomy", 4.6),
        Book("astro_6", "Từ Điển Thiên Văn Học Và Vật Lý Thiên Văn", "Đặng Vũ Tuấn Sơn", "https://product.hstatic.net/200000273991/product/92_e7a461a5aec645b4811f014ca5669188_master.png", "astronomy", 4.5),
        Book("astro_7", "Thiên Văn Học Cho Người Mới", "Carolyn Collins", "https://cdn0.fahasa.com/media/catalog/product/z/4/z4726478155308_f3f7b3c4a5a4c4a5a4c4a5a4c4a5a4c4.jpg", "astronomy", 4.4),
        Book("astro_8", "Những Bí Ẩn Vũ Trụ", "Brian Cox", "https://salt.tikicdn.com/cache/w1200/ts/product/8d/5e/26/7b5c5e26d5e26d5e26d5e26d5e26d5e2.jpg", "astronomy", 4.7)
    )

    // ============ PUBLIC PROPERTIES (EXPOSE RA NGOÀI) ============

    // List chứa TẤT CẢ sách trong hệ thống (bao gồm cả Kids)
    // Điều này giúp hàm getBookById(id) có thể tìm thấy sách Kids
    val allBooks: List<Book> = audioBooksBase + popularBooksBase + healingBooksBase +
            detectiveBooksBase + topEbooksBase + freeEbooksBase +
            kidsBooksBase + astronomyBooksBase

    // Các list lẻ để hiển thị theo từng mục (nếu cần)
    val audioBooks: List<Book> = audioBooksBase
    val popularBooks: List<Book> = popularBooksBase
    val healingBooks: List<Book> = healingBooksBase
    val detectiveBooks: List<Book> = detectiveBooksBase
    val topEbooks: List<Book> = topEbooksBase
    val freeEbooks: List<Book> = freeEbooksBase

    // QUAN TRỌNG: kidsStories bây giờ trả về List<Book> chứ không phải List<KidStory>
    val kidsStories: List<Book> = kidsBooksBase
    val astronomyBooks: List<Book> = astronomyBooksBase
}