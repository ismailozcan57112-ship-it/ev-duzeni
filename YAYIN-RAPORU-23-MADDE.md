# Ev Düzeni — 23 maddelik yayın raporu

Tarih: 12 Eylül 2026 · Sürüm: 0.2.0 (versionCode 2)

**İmzalı release APK ve AAB üretildi ve teknik paket kontrollerini geçti. Google Play'de herkese açık yayına çıkış henüz tamamlanmadı.** Gerçek Android cihaz testi, yayıncı bilgileri/gizlilik URL'si ve Play Console işlemleri bekliyor. İlk sürüm ücretsiz ve reklamsızdır; canlı Pro satışı açık değildir.

TAMAM: uygulama/kaynak ve belirtilen test kapsamı tamamlandı. KULLANICI İŞLEMİ GEREKLİ: kod hazır olsa da gerçek cihaz veya yayıncı hesabı/bilgisi gerekiyor. EKSİK: ayrıca geliştirme gerekiyor.

| No | Madde | Durum | Sonuç |
|---|---|---|---|
| 1 | Görev ekleme/düzenleme/silme | TAMAM | Geçerli/boş giriş, düzenleme, silmeden vazgeçme ve onay testleri geçti. |
| 2 | Oda ekleme/adlandırma/silme | TAMAM | Odaya bağlı görevler onayla silinir; eski kayıtlar korunur. |
| 3 | Tekrarlayan görevler | TAMAM | 1, 2, 3, 7, 14, 30 gün ve özel 1–365 gün. İki günlük seçenek doğrudan forma eklendi. |
| 4 | Sonraki tarih | TAMAM | Tamamlama günü + tekrar aralığı; geri alma ve son tamamlanmaya göre tekrar düzenleme doğrulandı. |
| 5 | Kalıcı yerel kayıt | TAMAM | Odalar, görevler, geçmiş, tarihler ve ayarlar; şema 1 geçişi, doğrulama, yedek/geçici dosyadan kurtarma ve başarısız yazmada geri dönüş. |
| 6 | Telefon tarihi değişimi | TAMAM | Yerel gün kontrolü ve uygulamaya dönüşte yenileme; açık form yazısı korunur. |
| 7 | Android bildirimleri | KULLANICI İŞLEMİ GEREKLİ | Native eklenti ve saat/izin arayüzü hazır; 64 takvim testi geçti. İzin, alarm, yeniden başlatma ve bildirime dokunma gerçek telefonda denenmeli. |
| 8 | Onboarding | TAMAM | Üç adım, atlama, hazır plan ve mevcut kullanıcının yeniden zorlanmaması test edildi. |
| 9 | Boş/hata/onay durumları | TAMAM | Geçersiz form/yedek, boş liste, kayıt hatası ve silme/geri yükleme onayları. |
| 10 | Responsive ve DPI | KULLANICI İŞLEMİ GEREKLİ | 320×568, 360×640, 800×1280, 1280×800 ve 1080×1920 masaüstü çizimlerinde kontrol edildi. Gerçek Android DPI, çentik ve sistem yazı boyutu testi yapılmadı. |
| 11 | Android geri tuşu | KULLANICI İŞLEMİ GEREKLİ | Formu kapatma, Bugün'e dönme ve kökte çıkma kod/testleri hazır. Gerçek Android klavyesiyle geri davranışı doğrulanmalı. |
| 12 | İkon ve splash | TAMAM | Özgün SVG/PNG, adaptive/monochrome ikon, özel splash; varsayılan Godot splash kapalı. 512 ikon ve 1024×500 özellik grafiği RGB PNG hazır. |
| 13 | Paket ve manifest | TAMAM | com.evduzeni.planner; Ev Düzeni; versionCode 2; versionName 0.2.0; minSdk 24; target/compileSdk 36; arm64-v8a; debug kapalı; Android kategorisi productivity. |
| 14 | Gereksiz izinler | TAMAM | Son release manifestinde yalnız POST_NOTIFICATIONS ve RECEIVE_BOOT_COMPLETED var. Telefon/depolama/internet/Billing izinleri yok. |
| 15 | Gizlilik politikası | KULLANICI İŞLEMİ GEREKLİ | Gerçek yerel veri davranışına göre metin hazır. Yayıncı adı, destek e-postası ve herkese açık HTTPS adresi eksik. Bu bilgiler uygulamadaki Gizlilik ekranıyla da eşleştirilmeli. |
| 16 | Ücretsiz/Pro ve Billing | EKSİK | Ücretsiz model ve Pro politika/adaptör altyapısı var. Canlı Pro faydaları, güvenli sunucu doğrulama/onaylama ve lisans testleri tamamlanmadı. Console ürünü ayrıca kullanıcı işlemi. SDK kaynakta korunuyor fakat ilk yayın paketinde yok; sahte satın alma/hak verilmez. |
| 17 | Reklam modeli | TAMAM | İlk sürüm ücretsiz ve reklamsız. Reklam SDK'sı, AD_ID ve reklam gösterimi yok. |
| 18 | Test ve bug düzeltmeleri | TAMAM | 254 otomatik kontrol geçti; arayüz grafik motoruyla da çalıştırıldı. Fiziksel Android testleri yukarıda ayrıca açıkça bekliyor. |
| 19 | Release signing | TAMAM | Mevcut RSA 3072 upload keystore yeniden kullanıldı. Parola kaynak kodda veya kaynak ZIP'te yok; özel klasörde. |
| 20 | Signed release APK/AAB | TAMAM | Her iki yayın çıktısı üretildi. AAB imzasının arşiv sırası uyarısı giderildi; tekrar doğrulandı. |
| 21 | Paket/kurulum doğrulaması | KULLANICI İŞLEMİ GEREKLİ | APK imza, sürüm, izin, mimari, ZIP/ELF 16KB; AAB yapı ve imza kontrolleri geçti. AAB'den universal APK üretimi, aynı paket/sürüm ve imza/hizalama da doğrulandı. Gerçek telefona kurulum ve uygulamayı açma yapılmadı. |
| 22 | Türkçe mağaza içeriği | TAMAM | Ad, kısa/uzun açıklama, kategori, anahtar kelime yaklaşımı, ekran metinleri, özellik grafiği, gizlilik metni ve Data Safety özeti hazır. Yayıncı alanları 15. maddede bekliyor. |
| 23 | Son politika kontrolü | KULLANICI İŞLEMİ GEREKLİ | Kod/paket incelemesinde gereksiz izin, aktif reklam/ödeme, veri gönderimi veya sahte Pro başarısı yok. Yayıncı beyanları, hedef kitle/IARC ve Console ön yayın/cihaz kontrolleri kullanıcı hesabında tamamlanmalı. Google onayı alınmış değildir. |

## Dosyalar

- Release APK: `EvDuzeni-0.2.0-release.apk`
- Signed AAB: `EvDuzeni-0.2.0.aab`
- Hashlar: `SHA256-0.2.0.txt`
- Kaynak: `EvDuzeni/` ve `EvDuzeni-0.2.0-Proje.zip`
- Mağaza: `PlayStore/MAGAZA-METINLERI.md`, `PlayStore/DATA-SAFETY-VE-YAYIN.md`, `PlayStore/GIZLILIK-POLITIKASI-TASLAK.md`
- Görseller: `PlayStore/Ekranlar/`, `PlayStore/ikon-512.png`, `PlayStore/ozellik-grafigi-1024x500.png`
- Özel imza: `Ozel-Imza/evduzeni-upload.jks` ve `Ozel-Imza/anahtar-bilgileri.json`. Bu klasörü yedekle; paylaşma veya Console mağaza dosyaları arasına koyma.

Eski `EvDuzeni.apk` ve 10 Eylül tarihli test APK'sı bu rapordaki release dosyaları değildir. Eski APK ve ilk kaynak arşivi çalışma alanında korunmuştur. Son kaynak ZIP özel anahtarları, derleme ağacını ve önbellekleri içermez.

## Test kanıtı ve sınırlar

51 veri + 36 geçiş/kurtarma + 51 temel UI + 52 genişletilmiş UI + 64 yerel takvim = 254 kontrol, 0 başarısızlık. Günlükler `../work/release-*.log`, `../work/validation-production.log` ve `../work/build-production.log` içinde. SCRIPT ERROR / Parse Error bulunmadı. Masaüstü kısıtlı ortamın kullanıcı log/önbellek ve sertifika deposu uyarıları Android çalışmasının sonucu değildir.

AAB yapılandırması PAGE_ALIGNMENT_16K kullanır; arm64 ELF yükleme bölümleri de en az 16KB hizalıdır. APK Signature Scheme v2 doğrulandı. AAB jarsigner doğrulaması başarılıdır; self-signed sertifika, zaman damgası yokluğu ve POSIX öznitelik uyarıları kayıtta tutulmuştur. Yayın sertifikası 2054'e kadar geçerlidir.

12 Eylül'de ADB'ye bağlı cihaz yoktu; emülatör için kurulu AVD/sistem imajı ve hızlandırma sürücüsü yoktu. Bu nedenle APK kuruldu/Android bildirim geldi denilemez. Ekran görüntüleri gerçek uygulamanın masaüstü çizimleridir; yayından önce telefon görüntüleriyle doğrula.

## Kalan buglar

Yapılan testlerde bilinen veri kaybı, tekrar/tarih hesabı veya ekran geçişi hatası kalmadı. Önceki paketleme hatası, oluşturulmuş ara derleme dosyaları ayrılarak giderildi; güncel Production derlemesi geçti. Eski gereksiz izinler kaldırıldı. AAB arşiv sırası uyarısı tekrar imzalama ile giderildi. Gerçek cihaz testleri yapılmadığından o alanda hata olmadığı iddia edilmiyor.

## Play Store'a yüklemeden önce

1. Eski debug uygulamasını silmeden planını koru. Release farklı imzalıdır; eski deneme üstüne kurulmaz. Gerekirse aynı debug anahtarıyla güncel Test APK üretip önce yedek al. Yedeksiz kaldırma.
2. Release APK'yı arm64 telefona kur; `PlayStore/MANUEL-ANDROID-TESTLERI.md` listesini uygula. AAB'yi ayrıca Play Console iç testinde dene ve ön yayın raporunu kontrol et.
3. Yayıncı adı/e-postasını gizlilik metnine ekle, herkese açık HTTPS sayfada yayınla; uygulamanın Gizlilik ekranına da iletişim/politika bağlantısını ekleyip bu değişiklik sonrası yeniden derle.
4. Play Console'da paket adının kullanılabilirliğini ve versionCode 2'nin daha önce kullanılmadığını kontrol et. Play App Signing ve upload anahtarını yapılandır; hesabın gerektiriyorsa kimlik/cihaz doğrulaması ve kapalı test sürecini tamamla.
5. Mağaza metni, görseller, iletişim adresi, gizlilik URL'si, Data Safety, reklam yok beyanı, hedef kitle ve IARC formunu doldur. Çocuklara yönelik uygunluk veya yaş derecesi uydurma; soruları gerçek hedef kitleye göre yanıtla.
6. İlk yayını ücretsiz/reklamsız tut. Pro satışı için ayrı geliştirme, gerçek Console ürünü, sunucu doğrulaması ve lisans testleri gerekir. Mevcut satış kapısını açmak tek başına yeterli değildir.

Güncel resmi politika bağlantıları ve beyan gerekçeleri `PlayStore/DATA-SAFETY-VE-YAYIN.md` içinde yer alır.

AAB kaynaklı kontrol paketi: ../work/install-check.apks; çıkarılan APK: ../work/aab-universal.apk. Bu kontrol gerçek cihaza kurulum değildir. Eski 10 Eylül test APK dosyası karışmaması için Arsiv/EvDuzeni-0.2.0-test-20260910.apk konumuna taşındı; silinmedi.

