const currentUser = localStorage.getItem("currentUser");
if (!currentUser) window.location.href = "login.html";
const currentRole = localStorage.getItem("currentRole");

// Xử lý nút Admin
if (currentRole == 1) {
    const adminDiv = document.getElementById('adminArea');
    if (adminDiv) {
        adminDiv.innerHTML = `
            <button class="btn-admin" onclick="window.location.href='admin.html'">
                <i class="fas fa-tools"></i> TRUY CẬP TRANG QUẢN TRỊ
            </button>
        `;
    }
}

document.getElementById('lblUser').innerText = currentUser;

// --- BIẾN TOÀN CỤC ---
let globalHistoryData = [];
let currentPage = 1;
const itemsPerPage = 10;

// 1. TẢI PROFILE (INFO + FAV)
function loadProfile() {
    fetch('/SMcity/api/lay-profile?username=' + currentUser)
        .then(res => res.json())
        .then(data => {
            document.getElementById('inpName').value = data.hoten;

            const container = document.getElementById('favList');
            container.innerHTML = "";
            if (data.favs.length === 0) {
                container.innerHTML = "<i style='color:#999'>Bạn chưa có địa điểm yêu thích nào.</i>";
            } else {
                data.favs.forEach(item => {
                    let div = document.createElement("div");
                    div.className = "fav-item";
                    div.innerHTML = `
                        <a href="DiaDiem.html?id=${item.id}" style="text-decoration: none; color: #333; font-weight: 500;">
                            <i class="fas fa-map-marker-alt" style="color:red; margin-right:5px;"></i> ${item.ten}
                        </a>
                        <button class="btn-remove-fav" onclick="removeFav(${item.id})">Xóa</button> 
                    `;
                    container.appendChild(div);
                });
            }
        });
}

// 2. TẢI LỊCH SỬ
function loadHistory() {
    const container = document.getElementById('historyList');
    container.innerHTML = "Đang tải...";

    fetch('/SMcity/api/lay-lich-su?username=' + currentUser)
        .then(res => res.json())
        .then(data => {
            globalHistoryData = data;

            // Mặc định chọn "Mới nhất trước" và Sort ngay
            document.getElementById('sortHistory').value = "date_desc";
            currentPage = 1;

            handleSortHistory();
        })
        .catch(err => {
            console.log(err);
            container.innerHTML = "Lỗi tải lịch sử.";
        });
}

// --- HÀM XỬ LÝ NGÀY THÁNG VIỆT NAM (QUAN TRỌNG NHẤT) ---
function parseDateVN(dateStr) {
    // Định dạng vào: "13/01/2026 05:23" hoặc "12/08/2025 15:00"
    if (!dateStr) return 0;

    try {
        // Tách ngày và giờ bằng khoảng trắng
        let parts = dateStr.split(' '); // ["13/01/2026", "05:23"]
        if (parts.length < 2) return 0;

        // Tách ngày/tháng/năm
        let dParts = parts[0].split('/'); // ["13", "01", "2026"]
        let day = parseInt(dParts[0], 10);
        let month = parseInt(dParts[1], 10) - 1; // Tháng trong JS bắt đầu từ 0
        let year = parseInt(dParts[2], 10);

        // Tách giờ/phút
        let tParts = parts[1].split(':'); // ["05", "23"]
        let hour = parseInt(tParts[0], 10);
        let minute = parseInt(tParts[1], 10);

        // Trả về Timestamp (số mili giây) để so sánh
        return new Date(year, month, day, hour, minute).getTime();
    } catch (e) {
        console.error("Lỗi format ngày:", dateStr);
        return 0;
    }
}

// 3. HÀM SẮP XẾP CHUẨN
function handleSortHistory() {
    const sortType = document.getElementById('sortHistory').value;

    globalHistoryData.sort((a, b) => {
        // Sử dụng hàm parseDateVN mới
        const timeA = parseDateVN(a.time);
        const timeB = parseDateVN(b.time);

        // Mới nhất trước (Số lớn lên đầu)
        if (sortType === 'date_desc') {
            return timeB - timeA;
        }
        // Cũ nhất trước (Số bé lên đầu)
        if (sortType === 'date_asc') {
            return timeA - timeB;
        }
        // Tên A-Z
        if (sortType === 'name_asc') {
            return a.ten.localeCompare(b.ten);
        }
        // Tên Z-A
        if (sortType === 'name_desc') {
            return b.ten.localeCompare(a.ten);
        }
        return 0;
    });

    renderHistoryPage();
}

// 4. HÀM VẼ GIAO DIỆN
function renderHistoryPage() {
    const container = document.getElementById('historyList');
    const controls = document.getElementById('paginationControls');

    container.innerHTML = "";

    if (globalHistoryData.length === 0) {
        container.innerHTML = "<i style='color:#999'>Bạn chưa có hoạt động nào.</i>";
        controls.style.display = "none";
        return;
    }

    const startIndex = (currentPage - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;
    const pageData = globalHistoryData.slice(startIndex, endIndex);

    pageData.forEach(item => {
        let div = document.createElement("div");
        div.className = "history-item";

        let icon = "";
        let content = "";

        if (item.type === 'review') {
            icon = `<div class="history-icon" style="color: orange;">✍️</div>`;
            let commentShow = item.comment ? `<div style="margin-top:5px; font-style:italic; color:#666; background:#f9f9f9; padding:5px; border-radius:4px;">"${item.comment}"</div>` : "";

            content = `
                <div class="history-content">
                    <span class="history-time">${item.time}</span>
                    Bạn đã đánh giá <b>${item.rate} ⭐</b> cho 
                    <a href="DiaDiem.html?id=${item.id_dia_diem}" class="history-link">${item.ten}</a>
                    ${commentShow}
                </div>
            `;
        } else {
            icon = `<div class="history-icon" style="color: #ff4d4f;">❤️</div>`;
            content = `
                <div class="history-content">
                    <span class="history-time">${item.time}</span>
                    Bạn đã thêm 
                    <a href="DiaDiem.html?id=${item.id_dia_diem}" class="history-link" style="color:#d63384;">${item.ten}</a>
                    vào danh sách Yêu thích.
                </div>
            `;
        }

        div.innerHTML = icon + content;
        container.appendChild(div);
    });

    // Phân trang
    controls.style.display = "flex";
    const totalPages = Math.ceil(globalHistoryData.length / itemsPerPage);
    document.getElementById('pageIndicator').innerText = `Trang ${currentPage} / ${totalPages}`;

    const btns = document.querySelectorAll('.page-btn');
    if(btns.length >= 2) {
        btns[0].disabled = (currentPage === 1);
        btns[1].disabled = (currentPage >= totalPages);
    }
}

function changePage(direction) {
    const totalPages = Math.ceil(globalHistoryData.length / itemsPerPage);
    let nextPage = currentPage + direction;
    if (nextPage >= 1 && nextPage <= totalPages) {
        currentPage = nextPage;
        renderHistoryPage();
    }
}

document.getElementById('btnSaveName').addEventListener('click', function() {
    const newName = document.getElementById('inpName').value;
    fetch('/SMcity/api/cap-nhat-thong-tin', {
        method: 'POST',
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        body: `username=${currentUser}&hoten=${newName}`
    }).then(res => res.json()).then(data => {
        alert(data.message);
        if(data.status === 'success') {
            localStorage.setItem("currentFullName", data.ten_moi);
        }
    });
});

document.getElementById('btnChangePass').addEventListener('click', function() {
    const oldP = document.getElementById('oldPass').value;
    const newP = document.getElementById('newPass').value;
    if(!oldP || !newP) { alert("Vui lòng nhập đủ thông tin!"); return; }

    fetch('/SMcity/api/doi-mat-khau', {
        method: 'POST',
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        body: `username=${currentUser}&pass_cu=${oldP}&pass_moi=${newP}`
    }).then(res => res.json()).then(data => {
        alert(data.message);
        if(data.status === 'success') {
            document.getElementById('oldPass').value = "";
            document.getElementById('newPass').value = "";
        }
    });
});

function removeFav(idDiaDiem) {
    if (confirm("Xóa địa điểm này khỏi danh sách Yêu thích?")) {
        fetch('/SMcity/api/xu-ly-so-thich', {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: `username=${currentUser}&id_dia_diem=${idDiaDiem}`
        }).then(res => res.json()).then(data => {
            if(data.status === "success") {
                loadProfile();
                loadHistory();
            } else alert("Lỗi: " + data.message);
        });
    }
}

function logout() {
    if(confirm("Đăng xuất khỏi hệ thống?")) {
        localStorage.clear();
        window.location.href = "login.html";
    }
}

loadProfile();
loadHistory();