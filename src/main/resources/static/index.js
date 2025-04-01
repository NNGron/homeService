document.addEventListener("DOMContentLoaded", () => {
    const mainButton = document.getElementById("main-button");
    const userOptions = document.getElementById("user-options");

    // نمایش گزینه‌ها
    mainButton.addEventListener("click", () => {
        userOptions.classList.remove("d-none");
        mainButton.classList.add("d-none");
    });
});

function navigateToAdmin() {
    window.location.href = "signup-admin.html";
}

function navigateToCustomer() {
    window.location.href = "signup-admin.html";
}

function navigateToExpert() {
    window.location.href = "signup-admin.html";
}
