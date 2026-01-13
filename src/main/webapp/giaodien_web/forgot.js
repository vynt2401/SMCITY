document.getElementById('btnReset').addEventListener('click', function() {
    var u = document.getElementById('f_user').value;
    var np = document.getElementById('f_newpass').value;

    if(u == "" || np == "") {
        alert("Vui lòng nhập đủ thông tin!");
        return;
    }

    fetch('/SMcity/api/quen-mat-khau', {
        method: 'POST',
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        body: 'username=' + u + '&newpass=' + np
    })
        .then(res => res.json())
        .then(data => {
            if(data.status == "success") {
                alert(data.message);
                window.location.href = "login.html";
            } else {
                alert("Lỗi: " + data.message);
            }
        })
        .catch(err => console.log(err));
});