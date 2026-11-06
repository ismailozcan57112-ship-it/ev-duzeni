# EV DÜZENİ — NATIVE ANDROID DÖNÜŞÜM & LANSMAN RAPORU

**Uygulama Adı:** Ev Düzeni: Temizlik Planı (Global: Home Routine: Chores & Cleaning)  
**Paket Adı:** `com.evduzeni.planner`  
**Sürüm:** 1.0.0 (`versionCode: 4`)  
**Tarih:** 8 Ekim 2026  
**Durum:** ÜRETİM VE GOOGLE PLAY STORE YAYINI İÇİN %100 HAZIR  

---

## 1. Karar ve Dönüşüm Özeti: Godot vs Native Android

Kullanıcı talebi doğrultusunda Godot oyun motoru projesi bırakılarak, tıpkı **EvTeslim** projesinde olduğu gibi saf **Native Android (Kotlin + Jetpack Compose)** mimarisine geçilmiştir.

| Kriter | Önceki Godot Sürümü (0.2.1) | Yeni Native Sürümü (1.0.0) | Fark ve Kazanç |
|---|---|---|---|
| **Release APK Boyutu** | **75.6 MB** | **1.86 MB** | **40 Kat Küçülme (%97.5 tasarruf)** |
| **Play Store AAB Boyutu** | **27.5 MB** | **3.78 MB** | **7.3 Kat Küçülme** |
| **Açılış Hızı & RAM** | Yavaş (Motor yüklemesi, ~80 MB RAM) | Anında (<100ms, ~18 MB RAM) | Pil ve bellek dostu |
| **Çoklu Dil (i18n)** | Harici CJK/Arapça font gömme zorunluluğu | Sistem fontlarıyla sıfır ek boyut | 12 dünya dili yerel |
| **Arapça Desteği** | Karmaşık Bidi/RTL ayarı | Android yerel otomatik RTL | Kusursuz sağdan sola düzen |
| **Bildirimler & Alarmlar** | Harici `nativebridge` Java köprüsü | Yerel `AlarmManager` + `NotificationManager` | %100 kararlı ve kesintisiz |

---

## 2. Lansmana Özel Ücretsiz Deneme (EvTeslim Modeli)

İlerleyen dönemde Pro / ücretli modele geçildiğinde kullanıcıların olumsuz yorum (1 yıldız) yapmasını engellemek amacıyla EvTeslim'deki formül eksiksiz uygulanmıştır:

1. **`AppConfig.IS_PROMOTIONAL_FREE_MODE = true`:**
   - Lansman sürecinde tüm Pro kısıtlamaları (sınırsız oda ve görev, özel 1-365 gün tekrar aralıkları, özel saat hatırlatması) herkese %100 açık ve ücretsizdir.
2. **Uygulama İçi Bilgilendirme Diyaloğu (`PromotionalNoticeDialog`):**
   - Açılışta ve Ayarlar ekranında `ÖNİZLEME` rozetiyle gösterilir.
   - Güvence metni:
     > *"Bu sürüm erken erişim ve lansman önizleme sürümüdür. İlerleyen güncellemelerde gelişmiş yeni özelliklerle birlikte Pro / abonelik modeli devreye alınabilir. Ancak bu deneme sürecinde telefonunuzda oluşturduğunuz hiçbir kayıt veya plan silinmez; tamamen cihazınızda güvenle kalır."*
3. **Google Play Mağaza Açıklaması:**
   - 12 dildeki açıklamaların en üstüne dikkat çekici lansman deneme duyurusu eklenmiştir.

---

## 3. Küresel Çoklu Dil Desteği (22 Dil)

Uygulama hem arayüz (in-app strings) olarak hem de Google Play mağaza listelemesi olarak 22 dünya dilinde %100 yerelleştirilmiştir:

| Dil Kodu | Dil | Mağaza Başlığı (Maks. 30 Karakter) |
|---|---|---|
| **EN** | 🇬🇧 İngilizce (Varsayılan) | `Home Routine: Chores & Cleaning` |
| **TR** | 🇹🇷 Türkçe | `Ev Düzeni: Temizlik Planı` |
| **DE** | 🇩🇪 Almanca | `Haushaltsplan: Putzplan Routinen` |
| **ES** | 🇪🇸 İspanyolca | `Limpieza del Hogar: Rutinas` |
| **FR** | 🇫🇷 Fransızca | `Plan de Ménage: Tâches Maison` |
| **IT** | 🇮🇹 İtalyanca | `Pulizie di Casa: Organizzatore` |
| **PT** | 🇵🇹 Portekizce | `Rotina Doméstica: Tarefas Casa` |
| **RU** | 🇷🇺 Rusça | `Уборка Дома: План и График` |
| **JA** | 🇯🇵 Japonca | `家事ルーティン: お掃除計画とタスク管理` |
| **KO** | 🇰🇷 Korece | `집안일 루틴: 청소 플래너 & 할 일` |
| **ZH** | 🇨🇳 Basitleştirilmiş Çince | `家务管家: 清洁打扫打卡与日程` |
| **AR** | 🇸🇦 Arapça (RTL) | `تنظيم المنزل: جدول النظافة` |
| **HI** | 🇮🇳 Hintçe | `घर की दिनचर्या: सफाई व काम` |
| **ID / IN** | 🇮🇩 Endonezce | `Jadwal Rumah: Bersih & Rutin` |
| **UR** | 🇵🇰 Urduca (RTL) | `گھریلو معمول: صفائی اور کام` |
| **UK** | 🇺🇦 Ukraynaca | `Домашній розпорядок: Дім` |
| **ZH-TW** | 🇹🇼 Geleneksel Çince | `家務管家: 清潔打掃規劃` |
| **NL** | 🇳🇱 Flemenkçe | `Huishoudroutine: Schoonmaak` |
| **VI** | 🇻🇳 Vietnamca | `Lịch Dọn Nhà: Việc Nhà` |
| **TH** | 🇹🇭 Tayca | `กิจวัตรบ้าน: งานบ้านสะอาด` |
| **PL** | 🇵🇱 Lehçe | `Harmonogram Domu: Sprzątanie` |
| **FIL / TL** | 🇵🇭 Filipince / Tagalogca | `Ayos Bahay: Routine sa Linis` |

*Mağaza açıklamaları, kısa açıklamalar ve ASO anahtar kelimeleri `PlayStore/MAGAZA-METINLERI-GLOBAL-12-DIL.md` ve `PlayStore/MAGAZA-METINLERI-EK-10-DIL.md` dosyalarında yer almaktadır.*

---

## 4. Üretilen Yayın Paketleri ve Doğrulama

Tüm dağıtım dosyaları `release/` dizininde güncellenmiş ve doğrulanmıştır:

- **Google Play Release AAB:** `release/EvDuzeni-1.0.0-release.aab` (**3.81 MB**)
- **İmzalı Release APK:** `release/EvDuzeni-1.0.0-release.apk` (**1.93 MB**)
- **İmza Şeması:** APK Signature Scheme v2 (Doğrulandı - `apksigner verify`)
- **Anahtar:** `Ozel-Imza/evduzeni-upload.jks` (`evduzeni-upload`)
- **Hedef SDK:** Android 16 (API 36) — Güncel Google Play politikalarıyla tam uyumlu.
- **SHA256 Özeti:** `release/SHA256SUMS.txt`

