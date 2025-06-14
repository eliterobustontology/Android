function requestContactPermission() {
    Android.requestContactPermission();
};

function checkContactPermission(callback) {
    if (Android.hasContactPermission()) {
        callback(true);
    } else {
        Android.showToast("Permission required to access contacts.");
        requestContactPermission();
        callback(false);
    };
};

function fetchContacts() {
    checkContactPermission(function(granted) {
        if (granted) {
            var contacts = Android.getContacts();
            document.getElementById("contactList").innerText = contacts || "No contacts found.";
        };
    });
};

function addContact() {
    checkContactPermission(function(granted) {
        if (granted) {
                let name = prompt("Enter Contact Name:");
                let phone = prompt("Enter Contact Number:");
            if (name && phone) {
                let success = Android.createContact(name, phone);
                Android.showToast(success ? "Contact added successfully!" : "Failed to add contact.");
                fetchContacts();
            };
        };
    });
};

function updateExistingContact() {
    checkContactPermission(function(granted) {
        if (granted) {
                let name = prompt("Enter Contact Name to Update:");
                let newPhone = prompt("Enter New Contact Number:");
            if (name && newPhone) {
                let success = Android.updateContact(name, newPhone);
                Android.showToast(success ? "Contact updated successfully!" : "Failed to update contact.");
                fetchContacts();
            };
        };
    });
};

function removeContact() {
    checkContactPermission(function(granted) {
        if (granted) {
            let name = prompt("Enter Contact Name to Delete:");
            if (name) {
                let success = Android.deleteContact(name);
                Android.showToast(success ? "Contact deleted successfully!" : "Failed to delete contact.");
                fetchContacts();
            };
        };
    });
};

function showToastMessage() {
    Android.showToast("Hello from JavaScript!");
};

function triggerVibration() {
    Android.vibrate(500);
};

function showAlertMessage() {
    Android.showAlert("This is an alert!");
};

function showConfirmMessage() {
    Android.showConfirm("Do you want to proceed?");
};

function showPromptMessage(input) {
    Android.showPrompt("Enter your name:");
};

function showNotification() {
    Android.showNotification("New Message", "Hello from WebView!");
};

function handleFileSelection(event) {
    const file = event.target.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = function (e) {
            const imgElement = document.getElementById("selectedImage");
            imgElement.src = e.target.result; 
            imgElement.style.display = "block";
        };
        reader.readAsDataURL(file);
    };
};

function handleCameraResult(imagePath) {
    console.log("Captured Image: " + imagePath);
    const imgElement = document.getElementById("capturedImage");
    imgElement.src = imagePath.startsWith("data:image") ? imagePath : imagePath;
    imgElement.style.display = "block";
};

function getBatteryInfo() {
    var batteryStatus = Android.getBatteryStatus();
    Android.showToast(batteryStatus);
};

function checkWiFiStatus() {
    var wifiStatus = Android.isWiFiEnabled();
    Android.showToast("WiFi is " + (wifiStatus ? "ON" : "OFF"));
};

function toggleWiFi() {
    let enable = confirm("Turn WiFi ON?");
    Android.setWiFiEnabled(enable);
    Android.showToast("WiFi updated!");
};

function getNetworkStatus() {
    var networkInfo = Android.getNetworkStatus();
    Android.showToast(networkInfo);
};

function showAvailableWiFi() {
    if (Android.hasLocationPermission()) {
        var networks = Android.getAvailableNetworks();
        Android.showToast(networks);
    } else {
        Android.requestLocationPermission();
        Android.showToast("Permission requested. Try again after granting access.");
    };
};

function site() {

    open('https://eroinnovations.site')

    setTimeout(() => {
        close();
    }, 3000);
        
};

window.close = function() {
    EliteWebInterface.closeWebView();
};

function changeWebViewBackground(Colors) {
       
    Android.setSystemBarColor(Colors, Colors);

};

const BATTERY = (callback) => {
    var batteryStatus = Android.getBatteryStatus();
    callback(batteryStatus);
};

const VIBRATION = (TIME) => {
    if (localStorage.getItem("Environment") === "Production") {
        Android.vibrate(TIME || 500);
    }
};

const NOTIFICATION = (TITLE, MESSAGE) => {
    Android.showNotification(TITLE, MESSAGE);
};
