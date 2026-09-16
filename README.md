<p align="center"><img width="154" height="149" alt="Screenshot 2026-09-17 035650" src="https://github.com/user-attachments/assets/ffbc6a94-c419-4768-b7e2-22d9870e8226" />
</p>
<h1 align="center">Unique Publication Dealer</h1>
<p align="center">Android app for managing publication products and dealer operations — built with Kotlin & MVVM.</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=flat&logo=android&logoColor=white" />
  <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=flat&logo=kotlin&logoColor=white" />
  <img src="https://img.shields.io/badge/Architecture-MVVM-1F3864?style=flat" />
  <img src="https://img.shields.io/badge/Status-Live%20on%20Google%20Play-brightgreen?style=flat" />
</p>

<!-- If you have the Play Store listing link, replace the # below -->
<p align="center">
  <a href="https://play.google.com/store/apps/details?id=com.publication.dealer&hl=en"><img src="https://img.shields.io/badge/Get%20it%20on-Google%20Play-414141?style=for-the-badge&logo=google-play&logoColor=white" /></a>
</p>

---

## 📱 Screenshots

<p align="center">
   <img width="180" alt="Unique Publication Dealer screenshot" src="https://github.com/user-attachments/assets/26b7526b-f683-46e7-89c0-93005ecdc7d4" />
   <img width="180" alt="Unique Publication Dealer screenshot" src="https://github.com/user-attachments/assets/4b83f428-3eed-4485-afb4-769280decb4a" />
    <img width="180" alt="Unique Publication Dealer screenshot" src="https://github.com/user-attachments/assets/e3b5b59a-3319-4cd2-8f45-f41324923cbe" />
   <img width="180" alt="Unique Publication Dealer screenshot" src="https://github.com/user-attachments/assets/c63c9b04-b4cf-478b-ae46-b44d82541330" />
  <img width="180" alt="Unique Publication Dealer screenshot" src="https://github.com/user-attachments/assets/9bf4c4f9-21e3-471c-a929-e3ae9f119b02" />
 

 
 
</p>

---

## ✨ Features

**Dealer workflow**
- Browse publication catalogs by category
- View product details and product PDFs
- Sales tracking
- Stay updated via real-time notifications for catalog and publication updates

**Admin workflow**
- Manage product catalog and branding/content
- Push targeted notifications to dealers
- Oversee dealer accounts and content lifecycle

**Account management**
- User registration, login, and profile editing
- Password reset and password change
- Account deactivation

---

## 🛠️ Tech Stack

- **Language:** Kotlin
- **Architecture:** MVVM (Model-View-ViewModel)
- **Local Storage:** Shared Preferences
- **Notifications:** Firebase Cloud Messaging (FCM)
- **UI:** XML layouts, PDF rendering for product documents

---

## 🚀 Getting Started

```bash
git clone https://github.com/casdevelopment/Unique_Publication-_Dealer.git
```

1. Open the project in **Android Studio**
2. Let Gradle sync finish
3. Add your own `google-services.json` if you want to run FCM notifications locally
4. Run on an emulator or physical device (min SDK: *add your min SDK here*)

---

## 📂 Project Structure

Organized by feature/module rather than by layer — each package is a self-contained screen or capability:

```
com.publication.dealer/
 ├─ admin_dashboard/       # Admin home & overview
 ├─ admin_catalogue/       # Admin: manage publication catalog
 ├─ admin_branding/        # Admin: branding/content management
 ├─ admin_notification/    # Admin: push notifications to dealers
 ├─ user_dashboard/        # Dealer home & overview
 ├─ user_product/          # Dealer: browse products/catalog
 ├─ user_notification/     # Dealer: notifications
 ├─ login/                 # Authentication
 ├─ create_user/           # User registration
 ├─ reset_password/        # Password recovery
 ├─ update_user_password/  # Change password
 ├─ update_user_profile/   # Edit profile
 ├─ inactivate_user/       # Account deactivation
 ├─ firebase/              # FCM setup & handling
 ├─ image_upload/          # Image upload utilities
 ├─ image_function/        # Image processing helpers
 ├─ PDF_Upload/             # Product PDF handling
 ├─ sales/                 # Sales tracking
 ├─ branding/              # Shared branding assets/logic
 ├─ network/               # API/network layer
 ├─ splash/                # Splash screen
 └─ modules/               # Shared/common modules
```

---

<p align="center"><i>Built and maintained by <a href="https://github.com/Basit-Ali-android-Developer">Basit Ali</a></i></p>
