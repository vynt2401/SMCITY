const params = new URLSearchParams(window.location.search);
const idDiaDiem = params.get('id');
const currentUser = localStorage.getItem("currentUser");
const currentFullName = localStorage.getItem("currentFullName");

// Check User
if (!currentUser) {
    alert("Vui lòng đăng nhập!"); window.location.href = "login.html";
} else {
    if(document.getElementById('userHello'))
        document.getElementById('userHello').innerText = "Xin chào " + (currentFullName || currentUser);
}

// Check ID
if (!idDiaDiem) {
    alert("Lỗi ID!"); window.location.href = "chonThanhPho.html";
}

// 1. TẢI CHI TIẾT ĐỊA ĐIỂM
fetch('/SMcity/api/chi-tiet-dia-diem?id=' + idDiaDiem + '&user=' + currentUser)
    .then(res => res.json())
    .then(data => {
        if (data.status === "success") {
            // Điền thông tin
            document.getElementById('ddTen').innerText = data.ten;
            document.getElementById('ddSao').innerText = data.sao;
            document.getElementById('ddLoai').innerText = data.loai;
            document.getElementById('ddDiaChi').innerText = data.diachi;
            document.getElementById('ddMoTa').innerText = data.mota;

            // Breadcrumb
            document.getElementById('breadcrumb').innerText = `${data.city_name} / ${data.loai} / ${data.ten}`;

            // Nút Back
            document.getElementById('btnBack').onclick = () => window.location.href = "thanhpho.html?id=" + data.id_city;

            // Map Embed
            if(data.map_link && data.map_link.trim() !== "") {
                document.getElementById('mapContainer').style.display = 'block';
                document.getElementById('googleMapFrame').src = data.map_link;
            }

            // XỬ LÝ ẢNH (GALLERY & BANNER)
            let images = [];
            if (data.anh_list && data.anh_list.trim() !== "") {
                images = data.anh_list.trim().split(/\s+/);
            }
            if(images.length === 0 || images[0] === "") images = ['default_place.jpg'];

            // Ảnh nền mờ (Lấy ảnh đầu tiên)
            document.getElementById('placeBgBlur').style.backgroundImage = `url('../images/${images[0]}')`;

            // List ảnh nhỏ
            const gallery = document.getElementById('galleryList');
            gallery.innerHTML = "";
            images.forEach(img => {
                let imgTag = document.createElement('img');
                imgTag.src = `../images/${img}`;
                imgTag.className = 'gallery-item';
                imgTag.onclick = () => window.open(imgTag.src, '_blank');
                imgTag.onerror = () => { imgTag.src = '../images/default_place.jpg'; };
                gallery.appendChild(imgTag);
            });

            // Button Yêu thích
            updateFavoriteUI(data.is_fav);
        } else {
            document.getElementById('ddTen').innerText = "Không tìm thấy dữ liệu";
        }
    })
    .catch(err => console.error(err));

// 2. NÚT YÊU THÍCH
const btnFav = document.getElementById('btnFavorite');
function updateFavoriteUI(isFav) {
    if (isFav) {
        btnFav.innerHTML = '<i class="fas fa-heart"></i> Đã thích';
        btnFav.classList.add('active');
    } else {
        btnFav.innerHTML = '<i class="far fa-heart"></i> Thêm vào yêu thích';
        btnFav.classList.remove('active');
    }
}
btnFav.onclick = function() {
    fetch('/SMcity/api/xu-ly-so-thich', {
        method: 'POST',
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        body: `username=${currentUser}&id_dia_diem=${idDiaDiem}`
    }).then(res => res.json()).then(data => {
        if (data.status === "success") updateFavoriteUI(data.action === "added");
    });
};

// 3. GỢI Ý (CAROUSEL) - ĐÃ SỬA THEO LOGIC THANHPHO.JS
function loadRecommendation() {
    fetch('/SMcity/api/lay-de-xuat?id=' + idDiaDiem)
        .then(res => res.json())
        .then(data => {
            let container = document.getElementById('recList');
            container.innerHTML = "";
            if(data.length === 0) {
                container.innerHTML = "<p style='padding:10px; color:#999'>Chưa có gợi ý.</p>";
                return;
            }

            data.forEach(item => {
                // --- FIX LỖI ẢNH ---
                let img = 'default_place.jpg';

                // Ở API này tên biến là 'anh_dd'
                let rawImg = item.anh_dd;

                if (rawImg && rawImg.trim() !== "") {
                    // Cắt chuỗi lấy ảnh đầu tiên (giống thanhpho.js)
                    img = rawImg.trim().split(/\s+/)[0];
                }

                let html = `
                <a href="DiaDiem.html?id=${item.id}" class="place-card">
                    <div class="card-img-container">
                        <img src="../images/${img}" class="card-img" onerror="this.src='../images/default_place.jpg'">
                    </div>
                    <div class="card-body">
                        <h4 class="card-title">${item.ten}</h4>
                        <div class="card-rating">
                            <span class="rating-star-icon">★</span> Click xem chi tiết
                        </div> 
                    </div>
                </a>`;
                container.innerHTML += html;
            });
        })
        .catch(err => console.error(err));
}
loadRecommendation();

// 4. BÌNH LUẬN
function loadComments() {
    fetch('/SMcity/api/lay-binh-luan?id=' + idDiaDiem)
        .then(res => res.json())
        .then(data => {
            let div = document.getElementById('commentList');
            div.innerHTML = "";
            if(data.length === 0) { div.innerHTML = "<p style='font-style:italic; color:#999'>Chưa có đánh giá nào.</p>"; return; }
            data.forEach(item => {
                div.innerHTML += `
                <div style="border-bottom: 1px solid #eee; padding: 10px 0;">
                    <b>${item.user}</b> <span style="color: orange;">(${item.rate} ⭐)</span>
                    <span style="font-size: 0.8em; color: gray; float: right;">${item.ngay}</span>
                    <p style="margin-top: 5px;">${item.comment}</p>
                </div>`;
            });
        });
}

// --- XỬ LÝ GỬI BÌNH LUẬN ---
document.getElementById('btnSendComment').onclick = function() {
    const checkedStar = document.querySelector('input[name="cmtRate"]:checked');

    if (!checkedStar) {
        alert("Vui lòng chọn số sao!"); return;
    }
    const rate = checkedStar.value;
    const comment = document.getElementById('cmtText').value;
    if(comment.trim() === "") { alert("Vui lòng nhập nội dung!"); return; }

    fetch('/SMcity/api/gui-binh-luan', {
        method: 'POST', headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        body: `username=${currentUser}&id_dia_diem=${idDiaDiem}&rate=${rate}&comment=${comment}`
    }).then(res => res.json()).then(data => {
        if(data.status === "success") {
            alert("Đánh giá thành công!");
            document.getElementById('cmtText').value="";
            checkedStar.checked = false; // Reset sao
            loadComments();
        }
        else alert(data.message);
    });
};
loadComments();

// --- CÁC HÀM HỖ TRỢ ---
function logout() {
    if(confirm("Đăng xuất?")) {
        localStorage.clear();
        window.location.href="login.html";
    }
}

function scrollGallery(amount) {
    const gallery = document.getElementById('galleryList');
    if(gallery) gallery.scrollBy({ left: amount, behavior: 'smooth' });
}

function scrollRec(id, amount) {
    const el = document.getElementById(id);
    if(el) el.scrollBy({ left: amount, behavior: 'smooth' });
}