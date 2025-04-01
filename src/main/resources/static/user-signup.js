document.addEventListener("DOMContentLoaded", () => {
    const signupBtn = document.getElementById("signup-btn");
    const statusMessage = document.getElementById("status-message");

    signupBtn.addEventListener("click", () => {
        const userName = document.getElementById("userName").value;
        const password = document.getElementById("password").value;
        const firstName = document.getElementById("firstName").value;
        const lastName = document.getElementById("lastName").value;
        const email = document.getElementById("email").value;
        const address = document.getElementById("address").value;
        const userType = document.getElementById("userType").value; // نوع کاربر از صفحه دریافت می‌شود
        const subCategoryNames = document.getElementById("subCategoryNames")?.value.split(',').map(item => item.trim()) || [];

        const userData = {
            userName,
            password,
            firstName,
            lastName,
            email,
            address,
            userType,
            subCategoryNames
        };

        fetch('http://localhost:8080/v1/users/signup', {
            method: 'POST',
            headers: {
                'Accept': '*/*',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(userData)
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to sign up');
                }
                return response.json();
            })
            .then(data => {
                statusMessage.innerHTML = `<div class="alert alert-success">Welcome, ${userType} ${data.firstName}! Your account has been successfully created.</div>`;
                setTimeout(() => {
                    window.location.href = "/role-selection.html"; // بازگشت به صفحه نقش‌ها
                }, 3000);
            })
            .catch(error => {
                statusMessage.innerHTML = `<div class="alert alert-danger">Error: ${error.message}</div>`;
            });
    });
});
