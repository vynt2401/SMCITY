document.getElementById('btnSignup').addEventListener('click', function() {
    var u = document.getElementById('reg_user').value;
    var p = document.getElementById('reg_pass').value;
    var n = document.getElementById('reg_name').value;

    if(u == "" || p == "" || n == "") {
        alert("Vui lòng nhập đủ thông tin!");
        return;
    }

    fetch('/SMcity/api/dang-ky', {
        method: 'POST',
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        body: 'username=' + u + '&password=' + p + '&hoten=' + n
    })
        .then(res => res.json())
        .then(data => {
            if(data.status == "success") {
                alert(data.message);
                window.location.href = "login.html"; // Chuyển về trang login
            } else {
                alert("Lỗi: " + data.message);
            }
        })
        .catch(err => console.log(err));
});