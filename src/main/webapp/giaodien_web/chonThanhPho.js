// Biến toàn cục lưu danh sách và chỉ số hiện tại
let citiesData = [];
let currentIndex = 0;

// 1. Khởi tạo khi trang tải
window.onload = function() {
    // Lấy tên người dùng từ LocalStorage (đã lưu ở bước Login)
    const userName = localStorage.getItem("currentFullName");
    if(userName) {
        document.getElementById('userDisplay').innerText = userName;
    }

    // Gọi API lấy danh sách
    fetch('/SMcity/api/danh-sach-thanh-pho')
        .then(response => response.json())
        .then(data => {
            if (data.length > 0) {
                citiesData = data;

                // Ưu tiên tìm Đà Lạt để hiển thị đầu tiên theo yêu cầu
                let daLatIndex = citiesData.findIndex(c => c.ten.includes("Đà Lạt"));
                if (daLatIndex !== -1) {
                    currentIndex = daLatIndex;
                } else {
                    currentIndex = 0;
                }

                updateUI(); // Cập nhật giao diện lần đầu
            } else {
                alert("Chưa có dữ liệu thành phố!");
            }
        })
        .catch(err => console.log(err));
};

// 2. Hàm cập nhật giao diện (Nền + Tên)
function updateUI() {
    const city = citiesData[currentIndex];

    // Cập nhật tên
    document.getElementById('cityNameDisplay').innerText = city.ten;

    // Cập nhật ảnh nền (Đường dẫn tương đối từ file HTML ra folder images)
    // Lưu ý: file html nằm trong giaodien_web/, ảnh nằm trong images/ -> dùng ../images/
    const bgUrl = `url('../images/${city.anh}')`;
    document.getElementById('bgBody').style.backgroundImage = bgUrl;
}

// 3. Xử lý nút Next (Tiếp theo)
function nextCity() {
    if (citiesData.length === 0) return;
    currentIndex++;
    if (currentIndex >= citiesData.length) {
        currentIndex = 0; // Quay về đầu nếu hết danh sách
    }
    updateUI();
}

// 4. Xử lý nút Prev (Trước đó)
function prevCity() {
    if (citiesData.length === 0) return;
    currentIndex--;
    if (currentIndex < 0) {
        currentIndex = citiesData.length - 1; // Quay về cuối nếu đang ở đầu
    }
    updateUI();
}

// 5. Xử lý nút "Khám phá ngay" (Go)
document.getElementById('btnGo').addEventListener('click', function() {
    if (citiesData.length > 0) {
        const cityId = citiesData[currentIndex].id;
        window.location.href = "thanhpho.html?id=" + cityId;
    }
});

// 6. Hàm Đăng xuất
function logout() {
    if(confirm("Bạn có chắc chắn muốn đăng xuất?")) {
        localStorage.removeItem("currentUser");
        localStorage.removeItem("currentFullName");
        localStorage.removeItem("currentRole");
        window.location.href = "login.html";
    }
}