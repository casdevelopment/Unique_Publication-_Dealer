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
  <a href="#"><img src="https://img.shields.io/badge/Get%20it%20on-Google%20Play-414141?style=for-the-badge&logo=google-play&logoColor=white" /></a>
</p>

---

## 📱 Screenshots

<!--
Add your screenshots below. Easiest way: drag and drop each image directly into this
file while editing it on GitHub's web editor — GitHub uploads it and auto-inserts the
markdown line for you. Aim for 3–5 screenshots: dealer catalog view, product detail/PDF
view, admin panel, and notifications.
-->

<p align="center">
  <img src="screenshots/screenshot1.png" width="200" />
  <img src="screenshots/screenshot2.png" width="200" />
  <img src="screenshots/screenshot3.png" width="200" />
  <img src="screenshots/screenshot4.png" width="200" />
</p>

---

## ✨ Features

**Dealer workflow**
- Browse publication catalogs by category
- View product details and product PDFs
- Stay updated via real-time notifications for catalog and publication updates

**Admin workflow**
- Manage product listings and catalog content
- Push targeted notifications to dealers
- Oversee dealer accounts and content lifecycle

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

<!-- Brief note on how the code is organized, e.g.: -->
```
app/
 ├─ ui/          # Activities, Fragments, XML layouts
 ├─ viewmodel/   # ViewModels per screen
 ├─ data/        # Repositories, models, Shared Preferences
 └─ utils/       # Helpers, notification handling
```

---

<p align="center"><i>Built and maintained by <a href="https://github.com/Basit-Ali-android-Developer">Basit Ali</a></i></p>
