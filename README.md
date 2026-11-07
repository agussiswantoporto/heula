# Heula — Teman Puasa Berkala (Intermittent Fasting)

<p align="center">
  <strong>Aplikasi pelacak puasa berkala modern, elegan, dan ilmiah dengan estetika Apple iOS Human Interface Guidelines (HIG).</strong><br>
  Dibangun dengan 100% <em>Kotlin Multiplatform (KMP)</em> dan <em>Compose Multiplatform</em> untuk Web (Wasm), Android, dan iOS.
</p>

---

## 🌟 Filosofi Desain & Karakter Heula

Heula dirancang khusus untuk memberikan pengalaman pengguna yang tenang, fokus, dan bebas distraksi:
- **Estetika Apple iOS HIG:** Tipografi kontras tinggi, tata letak kartu bersih (*grouped cards*), sudut lengkung halus, dan palet warna yang menyejukkan.
- **Bebas Emotikon & AI Sparkles:** Seluruh elemen visual, status biologis, suasana hati, dan ikon menggunakan grafis vektor murni (`ImageVector` & Compose Canvas), tanpa teks emoji ataupun ikon kilau AI generik.
- **Offline-First & Privasi:** Semua data puasa dan kesehatan tersimpan secara instan di perangkat lokal, dengan opsi sinkronisasi cloud yang aman ke Akun Google pribadi pengguna.
- **Bahasa Ramah & Bersahabat:** Menggunakan gaya bahasa Indonesia yang santai, memotivasi, dan mudah dipahami, serta dilengkapi dukungan penuh Bahasa Inggris.

---

## 📱 Fitur Utama

### 1. ⏱️ Smart Fasting Timer & Metode Fleksibel
- Mendukung berbagai protokol puasa populer:
  - **14:10** — Santai & ramah pemula
  - **16:8** — Protokol paling favorit dan terbukti efektif
  - **18:6** — Pembakaran lemak lebih cepat
  - **24 Jam** — Puasa seharian penuh untuk detoksifikasi mendalam
  - **OMAD (23:1)** — *One Meal A Day*
  - **Kustom** — Bebas mengatur target jam puasa dan jendela makan sendiri
- Lingkaran progres interaktif (*Progress Ring*) dengan indikator waktu real-time.

### 2. 🧬 5 Tahapan Biologis Metabolisme Tubuh
Pantau kondisi tubuh Anda secara transparan dengan panduan ilmiah dan estimasi waktu menuju tahap berikutnya:
1. **Pencernaan (0 - 4 Jam):** Insulin bekerja mengolah glukosa makanan terakhir.
2. **Bakar Lemak (4 - 12 Jam):** Gula darah turun normal, tubuh beralih membakar cadangan lemak.
3. **Ketosis Awal (12 - 16 Jam):** Hati mulai memproduksi keton sebagai bahan bakar super bersih untuk otak.
4. **Autophagy (16 - 24 Jam):** Mode pembersihan sel otomatis, mendaur ulang sel tua untuk peremajaan tubuh.
5. **Deep Fast (24+ Jam):** Puncak regenerasi sel, autophagy maksimal, dan sensitivitas insulin ter-reset optimal.

### 3. 🍽️ Panduan Jendela Makan (*Eating Window*)
- Menghitung waktu mulai dan berakhirnya jendela makan secara otomatis setelah puasa selesai.
- Pengingat agar tidak melewatkan hidangan bernutrisi seimbang.

### 4. 💧 Pelacak Hidrasi (*Water Tracker*)
- Target harian air minum otomatis (standar 2.000 ml).
- Tombol pencatatan cepat (+250 ml, +500 ml) atau input jumlah khusus.
- Reset otomatis setiap pergantian hari.

### 5. ⚖️ Pelacak Berat Badan & Tren Interaktif
- Pantau progres penurunan berat badan menuju target ideal.
- Kalkulator Indeks Massa Tubuh (BMI) otomatis berdasarkan data profiling.
- Grafik tren berat badan visual berbasis Compose Canvas.

### 6. 🔥 Smart Streak & Riwayat Puasa
- Perhitungan rentetan hari konsisten (*streak*) secara akurat.
- Riwayat puasa terperinci lengkap dengan durasi, jam mulai/selesai, status keberhasilan, pencatatan suasana hati (*mood*), dan catatan harian.

### 7. 🏆 Gamifikasi & Lencana Prestasi (*Badges*)
- Buka pencapaian saat mencapai tonggak sejarah baru:
  - *Puasa Pertama*
  - *10 Kali Puasa*
  - *100 Jam Puasa*
  - *Target 24 Jam Penuh*

### 8. ☁️ Sinkronisasi Cloud Dua Arah (Google Account & Firestore)
- Masuk dengan Akun Google (Google Identity Services di Web, Credential Manager di Android).
- Mesin *Smart Merge* cerdas: otomatis menggabungkan data lokal dan cloud tanpa risiko kehilangan sesi atau riwayat lampau.
- Integrasi Firestore REST API super efisien dan hemat kuota.

### 9. 🌐 Dwibahasa & Personalisasi Tema
- Penggantian bahasa instan antara **Bahasa Indonesia** dan **English**.
- Mendukung mode **Terang**, **Gelap**, dan **Ikuti Sistem**.

---

## 🛠️ Tech Stack & Arsitektur

| Komponen | Teknologi | Keterangan |
| :--- | :--- | :--- |
| **Bahasa Utama** | [Kotlin 2.1.0](https://kotlinlang.org/) | Multiplatform (KMP) |
| **UI Framework** | [Compose Multiplatform 1.7.3](https://www.jetbrains.com/lp/compose-multiplatform/) | Android, iOS, dan Web (WasmJs) |
| **Grafis Web** | Skiko Canvas & Webpack | Akselerasi perangkat keras via WebAssembly |
| **Dependency Injection** | [Koin 4.0.0](https://insert-koin.io/) | Core, Compose, ViewModel, Platform Modules |
| **Penyimpanan Lokal** | [Multiplatform Settings 1.2.0](https://github.com/russhwolf/multiplatform-settings) | Penyimpanan key-value reaktif lintas platform |
| **Coroutines & Asynchronous** | Kotlinx Coroutines 1.9.0 | Flow, StateFlow, Channel, IO Dispatchers |
| **Serialisasi** | Kotlinx Serialization JSON 1.7.3 | Encode/decode payload model |
| **Waktu & Tanggal** | Kotlinx DateTime 0.6.1 | Manajemen waktu epoch dan zona lokal |
| **Otentikasi** | Google Identity Services (GIS) / Android Credential Manager | OAuth 2.0 Popup Token Flow |
| **Cloud Database** | Google Cloud Firestore REST API v1 | Dokumen atomik per pengguna (`users/{uid}`) |

---

## 📂 Struktur Direktori Proyek

```text
heula-kmp/
├── composeApp/
│   ├── src/
│   │   ├── commonMain/              # Kode utama aplikasi (95% kode bersama)
│   │   │   ├── kotlin/id/heula/
│   │   │   │   ├── data/            # Repository implementasi lokal
│   │   │   │   ├── di/              # Koin Dependency Injection module
│   │   │   │   ├── domain/          # Model data, usecase, Smart SyncManager
│   │   │   │   └── presentation/    # Layar UI (Home, Stats, History, Profile)
│   │   │   └── composeResources/    # Font Inter & aset gambar bersama
│   │   ├── wasmJsMain/              # Interop Web Wasm, JS bindings, index.html
│   │   ├── androidMain/             # Android Activity, Manifest, Android Sync
│   │   └── iosMain/                 # iOS View Controller & iOS platform module
│   └── build.gradle.kts
├── docs/
│   └── AUTH_AND_SYNC.md             # Dokumentasi lengkap Login Google & Firestore
├── config.example.json              # Template konfigurasi kunci & client ID (JSON)
├── config.example.properties        # Template konfigurasi environment (Properties)
├── gradle.properties                # Konfigurasi JVM & Gradle Daemon heap
└── README.md
```

---

## 🚀 Cara Menjalankan Proyek

### Prasyarat
- **JDK 17** atau yang lebih baru (disarankan OpenJDK 17 atau 21).
- **Android Studio Ladybug / Meerkat** (untuk pengujian Android).
- **Xcode 15+** dan macOS (hanya jika ingin mengompilasi target iOS).
- Koneksi internet aktif untuk dependensi Gradle dan skrip Google Identity Services.

### 1. Menjalankan Versi Web (WasmJs)
```bash
./gradlew wasmJsBrowserDevelopmentRun
```
Setelah proses kompilasi selesai, buka browser di alamat:
```text
http://localhost:8080
```

### 2. Menjalankan Versi Android
Jalankan di emulator atau perangkat fisik yang terhubung:
```bash
./gradlew installDebug
```
Atau buka proyek di Android Studio dan tekan tombol **Run ▶**.

### 3. Menjalankan Versi iOS
Buka folder proyek atau framework di Xcode, pilih target simulator iOS, dan jalankan build.

---

## 🔐 Konfigurasi Kredensial & Cloud Database

Untuk menghubungkan autentikasi Google dan database Firestore pada instalasi baru:
1. Salin template konfigurasi dari `config.example.json` atau `config.example.properties`.
2. Isi `web_client_id`, `project_id`, dan fingerprint SHA-1 aplikasi Anda.
3. Baca panduan langkah demi langkah yang lengkap di [Dokumentasi Integrasi Auth & Database](docs/AUTH_AND_SYNC.md).

---

## 📄 Lisensi
Hak Cipta © 2026 Heula. Seluruh hak cipta dilindungi undang-undang.
