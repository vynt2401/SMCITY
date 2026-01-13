const role = localStorage.getItem("currentRole");
if (role != 1) {
    alert("Bạn không có quyền truy cập trang này!");
    window.location.href = "chonThanhPho.html";
}

// Biến toàn cục
let pageUser = 1, pagePlace = 1;
let totalPageUser = 1, totalPagePlace = 1;
let myChart = null; // Biến giữ biểu đồ

// --- HÀM CHUNG ---
function showTab(tab) {
    // Ẩn hết
    document.getElementById('tab-users').style.display = 'none';
    document.getElementById('tab-places').style.display = 'none';
    document.getElementById('tab-stats').style.display = 'none';

    // Hiện tab được chọn
    document.getElementById('tab-' + tab).style.display = 'block';

    // Logic riêng cho từng tab
    if (tab === 'stats') {
        loadStatsCityList();
        loadStats();
    }
}

function openModal(id) { document.getElementById(id).style.display = 'block'; }
function closeModal(id) { document.getElementById(id).style.display = 'none'; }

// ================= 1. QUẢN LÝ USER =================
function loadUsers() {
    const kw = document.getElementById('searchUser').value;
    fetch(`/SMcity/api/admin-user?page=${pageUser}&q=${kw}`)
        .then(res => res.json())
        .then(res => {
            let html = "";
            totalPageUser = res.total_pages;
            res.data.forEach(u => {
                html += `<tr>
                    <td><b>${u.user}</b></td>
                    <td>${u.ten}</td>
                    <td style="text-align:center;">
                        <button onclick="deleteUser('${u.user}')" style="background:#dc3545; color:white; padding:5px 10px; font-size:12px;">🗑 Xóa</button>
                    </td>
                </tr>`;
            });
            document.getElementById('tblUsers').innerHTML = html;
            document.getElementById('pageInfoUser').innerText = `Trang ${pageUser} / ${totalPageUser}`;
        });
}

function addUser() {
    const u = document.getElementById('new_u').value;
    const p = document.getElementById('new_p').value;
    const n = document.getElementById('new_n').value;

    if(!u || !p) { alert("Vui lòng nhập đủ thông tin!"); return; }

    fetch('/SMcity/api/admin-user', {
        method: 'POST',
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        body: `action=add&username=${u}&password=${p}&hoten=${n}`
    }).then(res => res.json()).then(data => {
        alert(data.message);
        if(data.status==='success') {
            closeModal('modalAddUser');
            // Reset form
            document.getElementById('new_u').value="";
            document.getElementById('new_p').value="";
            document.getElementById('new_n').value="";
            loadUsers();
        }
    });
}

function deleteUser(username) {
    if (confirm("Bạn có chắc muốn xóa user: " + username + "?")) {
        fetch('/SMcity/api/admin-user', {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: `action=delete&username=${username}`
        }).then(res => res.json()).then(data => {
            alert(data.message);
            loadUsers();
        });
    }
}

function changePageUser(step) {
    let nextPage = pageUser + step;
    if (nextPage >= 1 && nextPage <= totalPageUser) {
        pageUser = nextPage;
        loadUsers();
    }
}

// ================= 2. QUẢN LÝ ĐỊA ĐIỂM =================

// Load danh sách thành phố vào Select box khi trang vừa tải
fetch('/SMcity/api/danh-sach-thanh-pho').then(res=>res.json()).then(data => {
    let sel = document.getElementById('adminCitySelect');
    data.forEach(c => {
        let opt = document.createElement('option');
        opt.value = c.id; opt.text = c.ten;
        sel.add(opt);
    });
    // Sau khi load xong city thì load luôn places
    loadPlaces();
});

function loadPlaces() {
    const idCity = document.getElementById('adminCitySelect').value;
    fetch(`/SMcity/api/admin-diadiem?page=${pagePlace}&id_city=${idCity}`)
        .then(res => res.json())
        .then(res => {
            let html = "";
            totalPagePlace = res.total_pages;

            res.data.forEach(p => {
                // Encode mô tả để truyền vào hàm JS không bị lỗi ký tự lạ/xuống dòng
                let encodedDesc = encodeURIComponent(p.mota);

                // Escape dấu nháy đơn cho các trường text
                let safeName = p.ten.replace(/'/g, "\\'");
                let safeAddr = p.diachi.replace(/'/g, "\\'");
                let safeMap = p.map ? p.map.replace(/'/g, "\\'") : "";

                // --- XỬ LÝ ẢNH THUMBNAIL (Lấy ảnh đầu tiên) ---
                let firstImg = 'default_place.jpg';
                if (p.anh && p.anh.trim() !== "") {
                    let imgs = p.anh.trim().split(/\s+/);
                    if (imgs.length > 0) firstImg = imgs[0];
                }

                html += `<tr>
                <td>${p.id}</td>
                <td>
                    <div style="display:flex; align-items:center;">
                        <img src="../images/${firstImg}" class="preview-img" style="width:50px; height:40px; object-fit:cover; margin-right:10px;" onerror="this.src='../images/default_place.jpg'">
                        <b>${p.ten}</b>
                    </div>
                </td>
                <td>${p.ten_loai}</td>
                <td style="text-align:center;">
                    <button class="btn-blue btn-action" onclick="openEditModal(${p.id}, '${safeName}', '${safeAddr}', ${p.id_loai}, '${encodedDesc}', '${p.anh}', '${safeMap}')">✏️ Sửa</button>
                    <button class="btn-green btn-action" style="background:#dc3545;" onclick="deletePlace(${p.id})">🗑 Xóa</button>
                </td>
            </tr>`;
            });
            document.getElementById('tblPlaces').innerHTML = html;
            document.getElementById('pageInfoPlace').innerText = `Trang ${pagePlace} / ${totalPagePlace}`;
        });
}

// --- LOGIC CHỌN ẢNH THÔNG MINH ---
// prefix: 'p' (cho Add) hoặc 'e' (cho Edit)
function handleFileSelect(prefix) {
    const fileInput = document.getElementById(prefix + '_fileInput');
    const nameInput = document.getElementById(prefix + '_anh');
    const previewDiv = document.getElementById(prefix + '_preview_container');

    if (fileInput.files && fileInput.files.length > 0) {
        let fileNames = [];
        previewDiv.innerHTML = ""; // Xóa preview cũ

        Array.from(fileInput.files).forEach(file => {
            fileNames.push(file.name); // Lấy tên file

            // Tạo ảnh xem trước từ máy tính (Blob URL)
            let img = document.createElement("img");
            img.src = URL.createObjectURL(file);
            img.className = "preview-img";
            previewDiv.appendChild(img);
        });

        // Nối tên file bằng dấu cách và điền vào ô input text
        nameInput.value = fileNames.join(" ");
    }
}

function clearImages(prefix) {
    document.getElementById(prefix + '_fileInput').value = "";
    document.getElementById(prefix + '_anh').value = "";
    document.getElementById(prefix + '_preview_container').innerHTML = "<span style='color:#999; font-size:12px; margin: auto;'>Đã xóa ảnh</span>";
}

// --- MỞ MODAL SỬA ---
function openEditModal(id, ten, diachi, idLoai, encodedDesc, anh, map) {
    document.getElementById('e_id').value = id;
    document.getElementById('e_ten').value = ten;
    document.getElementById('e_dc').value = diachi;
    document.getElementById('e_loai').value = idLoai;

    // Decode mô tả để hiển thị đúng xuống dòng trong textarea
    document.getElementById('e_mota').value = decodeURIComponent(encodedDesc);

    document.getElementById('e_anh').value = anh;
    document.getElementById('e_map').value = map;

    // Hiển thị ảnh cũ (Load từ Server)
    const previewDiv = document.getElementById('e_preview_container');
    previewDiv.innerHTML = "";

    if(anh && anh.trim() !== "") {
        let imgs = anh.trim().split(/\s+/);
        imgs.forEach(imgName => {
            let img = document.createElement("img");
            img.src = "../images/" + imgName;
            img.className = "preview-img";
            img.onerror = function() { this.src = '../images/default_place.jpg'; };
            previewDiv.appendChild(img);
        });
    } else {
        previewDiv.innerHTML = "<span style='color:#999; font-size:12px; margin: auto;'>Chưa có ảnh</span>";
    }

    openModal('modalEditPlace');
}

// --- API THÊM / SỬA / XÓA ---
function addPlace() {
    const idCity = document.getElementById('adminCitySelect').value;

    // Dùng encodeURIComponent cho mọi trường để tránh lỗi ký tự
    const ten = encodeURIComponent(document.getElementById('p_ten').value);
    const dc = encodeURIComponent(document.getElementById('p_dc').value);
    const idLoai = document.getElementById('p_loai').value;
    const mota = encodeURIComponent(document.getElementById('p_mota').value);
    const anh = encodeURIComponent(document.getElementById('p_anh').value);
    const map = encodeURIComponent(document.getElementById('p_map').value);

    fetch('/SMcity/api/admin-diadiem', {
        method: 'POST',
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        body: `action=add&id_city=${idCity}&ten=${ten}&diachi=${dc}&id_loai=${idLoai}&mota=${mota}&anh=${anh}&map=${map}`
    }).then(res => res.json()).then(data => {
        alert(data.message);
        if(data.status==='success') {
            closeModal('modalAddPlace');
            loadPlaces();
            // Reset form
            document.getElementById('p_ten').value="";
            document.getElementById('p_dc').value="";
            document.getElementById('p_mota').value="";
            clearImages('p');
        }
    });
}

function updatePlace() {
    const id = document.getElementById('e_id').value;
    // Encode dữ liệu
    const ten = encodeURIComponent(document.getElementById('e_ten').value);
    const dc = encodeURIComponent(document.getElementById('e_dc').value);
    const loai = document.getElementById('e_loai').value;
    const mota = encodeURIComponent(document.getElementById('e_mota').value);
    const anh = encodeURIComponent(document.getElementById('e_anh').value);
    const map = encodeURIComponent(document.getElementById('e_map').value);

    fetch('/SMcity/api/admin-diadiem', {
        method: 'POST',
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        body: `action=update&id=${id}&ten=${ten}&diachi=${dc}&id_loai=${loai}&mota=${mota}&anh=${anh}&map=${map}`
    }).then(res => res.json()).then(data => {
        alert(data.message);
        if(data.status==='success') { closeModal('modalEditPlace'); loadPlaces(); }
    });
}

function deletePlace(id) {
    if(confirm("Bạn có chắc muốn xóa địa điểm này không?")) {
        fetch('/SMcity/api/admin-diadiem', {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: `action=delete&id=${id}`
        }).then(res => res.json()).then(data => { alert(data.message); loadPlaces(); });
    }
}

function changePagePlace(step) {
    let nextPage = pagePlace + step;
    if (nextPage >= 1 && nextPage <= totalPagePlace) {
        pagePlace = nextPage;
        loadPlaces();
    }
}

// ================= 3. THỐNG KÊ (CHART.JS) =================

// Load list thành phố cho tab thống kê (chỉ load 1 lần)
function loadStatsCityList() {
    let sel = document.getElementById('statsCitySelect');
    if (sel.options.length > 1) return;

    fetch('/SMcity/api/danh-sach-thanh-pho').then(res => res.json()).then(data => {
        data.forEach(c => {
            let opt = document.createElement('option');
            opt.value = c.id; opt.text = c.ten;
            sel.add(opt);
        });
    });
}

function loadStats() {
    const idCity = document.getElementById('statsCitySelect').value;
    const days = document.getElementById('statsDays').value;

    fetch(`/SMcity/api/admin-stats?id_city=${idCity}&days=${days}`)
        .then(res => res.json())
        .then(data => {
            if(data.status === 'success') {
                renderChart(data.labels, data.data);
            }
        })
        .catch(err => console.error("Lỗi tải thống kê:", err));
}

function renderChart(labels, dataValues) {
    const ctx = document.getElementById('usageChart').getContext('2d');

    // Hủy biểu đồ cũ nếu có để vẽ cái mới
    if (myChart) {
        myChart.destroy();
    }

    myChart = new Chart(ctx, {
        type: 'line', // Biểu đồ đường
        data: {
            labels: labels,
            datasets: [{
                label: 'Số lượt Tương tác & Đánh giá',
                data: dataValues,
                backgroundColor: 'rgba(54, 162, 235, 0.2)',
                borderColor: 'rgba(54, 162, 235, 1)',
                borderWidth: 2,
                pointBackgroundColor: '#ff6384',
                pointRadius: 5,
                tension: 0.3,
                fill: true
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { position: 'bottom' }
            },
            scales: {
                y: { beginAtZero: true, ticks: { stepSize: 1 } }
            }
        }
    });
}

// Mặc định chạy Load Users
loadUsers();