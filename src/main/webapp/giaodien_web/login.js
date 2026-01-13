document.getElementById('btn').addEventListener('click', function() {
    var u = document.getElementById('user').value;
    var p = document.getElementById('pass').value;

    // Lưu ý đường dẫn: /SMcity/api/dang-nhap
    fetch('/SMcity/api/dang-nhap', {
        method: 'POST',
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        body: 'username=' + u + '&password=' + p
    })
        .then(response => response.json())
        .then(data => {
            if(data.status == "success") {
                localStorage.setItem("currentUser", document.getElementById('user').value); // Lưu username
                localStorage.setItem("currentFullName", data.ten);
                localStorage.setItem("currentRole", data.role);
                window.location.href = "chonThanhPho.html";
            } else {
                alert("Lỗi: " + data.message);
            }
        })
        .catch(error => console.log(error));
});