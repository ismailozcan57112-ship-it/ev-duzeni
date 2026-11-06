# DEVAM — Ev Düzeni 0.2.1, 12 Eylül 2026

Mevcut proje ve 0.2.0 APK/AAB korunmuştur. Package ID com.evduzeni.planner; yeni sürüm 0.2.1/code3. Aynı RSA3072 upload key kullanılıyor; yeni anahtar üretimi kaldırıldı.

Bu oturumda: onboarding gerçek bildirim opt-in → izin verilince varsayılan09:00 otomatik etkin; izin reddi normal kullanım; kalıcı ret için isteğe bağlı Android ayarları. Ana ve modal scrollbar SHOW_NEVER; gerçek sürükleme/yanlış tıklama testleri geçti. Yan çentik ve modal güvenli alan desteği. Free3oda/15görev,1/2/3/7gün; mevcut fazla kayıt/özel tekrar/saat korunur. Pro özel tekrar ve saat, sınırsız ekleme. Resmi Billing3.3.0/Billing9.1.0 export etkin; satın alma, yerel fiyat, restore, pending/iptal/hata/reconnect/query/ack var. Google imzası native RSA ile doğrulanır; AndroidKeystore AESGCM önbelleği72saat. Sunucu doğrulaması yok, yapılmış sayılmıyor.

ÖNEMLİ BLOKAJ: Play Console RSA açık anahtarı verilmedi. native/assets/evduzeni-play-public-key.txt boş. Bu yüzden ödeme başlatma güvenli şekilde kapalı. Gerçekürün evduzeni_pro_lifetime Console’da oluşturulmalı, açıkanahtar eklenip yenidenbuild ve lisans test hesabı satınalma yapılmalı. Kullanıcıya async soruldu. Gizlilik üçplaceholderı kullanıcı doldurmalı. DataSafety SDK tanılama detayının Console tarafı kontrolü gerekli.

Testler: store51 + upgrade36 + UI51 + extended52 + release42 + nativeclock64 + receiptcrypto12 + touchscroll11 =319,0hata. Grafik ekran testinin tekrarı ayrıca çalışıyor. Loglar work/rc-*.log.

Derleme: tools/Build.ps1 -Mode Production çalıştırıldı; work/production-0.2.1.log ve work/EvDuzeni-0.2.1-release.apk.build.log kontrol et. Henüz paket doğrulama/final rapor yapılmadı. Sonraki adımlar: build tamamlanınca ValidatePackages.ps1 (paket/sürüm/izin/signature/16KB), öncekiAPK ile signer karşılaştır, bundletool universalAPK üret ve doğrula. Bağlı Android cihaz/AVD yok; fiziksel kurulum, bildirim/klavye/Google Play tests tamamlanmış gösterilmemeli.

Belgeler PlayStore klasöründe güncellendi; uygulama gizlilik metni assets/privacy.txt ile aynı. Final rapor ve kaynakZIP daha oluşturulmalı. PackageSource.ps1 artık DURUM-RAPORU-0.2.1.md bekliyor. 0.2.0 baseline artefaktlarını final0.2.1 diye verme.
