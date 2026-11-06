# Ev Düzeni 0.2.1 — Ücretsiz / Pro ve yayın işlemleri

## Gerçek durum

Android paketinde resmi Godot Google Play Billing 3.3.0 ve Google Play Billing 9.1.0 vardır. Ürün `evduzeni_pro_lifetime`, tür INAPP, tüketilmeyen tek seferlik satın almadır. Abonelik ve reklam yoktur.

**Bu adayda gerçek Play Console RSA açık anahtarı sağlanmadığı için ödeme başlatma kapalıdır. Kod entegrasyonu var; satın alınabilir üretim ürünü henüz TAMAM değildir.** Fiyat uydurulmaz. Ürün/katalog veya doğrulama yapılandırması yokken ekran açıkça satışın kullanılamadığını gösterir. Console’a bir ürün eklemek tek başına bu aday APK’nın satışını açmaz: aşağıdaki açık anahtarın eklenip paketlerin yeniden üretilmesi gerekir.

## Özellikler

| Özellik | Ücretsiz | Pro |
|---|---|---|
| Odalar | 3 | Sınırsız |
| Aktif, tekrarlayan görevler | 15 | Sınırsız |
| Tekrar | 1 / 2 / 3 / 7 gün | 1–365 gün |
| Günlük bildirim | 09:00, izinle | Saat/dakika seçimi |
| Tamamlama, yerel kayıt, onboarding | Var | Var |
| Dışa aktarma / geri yükleme | Var | Var |

Tamamlanan tekrarlayan görev bir sonraki tarihe planlandığından aktif görev limitinde kalır. Limitler yeni eklemeler içindir. Eski fazla odalar/görevler silinmez; düzenleme, silme, tamamlama ve mevcut özel tekrar korunur. Pro hakkı kaybolduğunda veriler ve önceki bildirim saati sıfırlanmaz. İstatistik/tema henüz uygulanmadığı için satış avantajı olarak vaat edilmez. Yeni Pro özellikleri feature_policy katmanına eklenebilir.

## Sahiplik ve güvenlik

- Uygulama başlangıcı, öne dönüş ve açıkken dakikalık kontrol Google Play sahipliğini sorgular; bağlantı koparsa sonraki kontrolde yeniden bağlanır.
- Kullanıcı iptali ve PENDING durumunda hak verilmez.
- Satın almanın original_json içeriği, bu uygulamanın Console RSA açık anahtarıyla SHA1withRSA imza kontrolünden geçer. Paket, ürün, satın alma durumu ve belirteç eşleşmesi kontrol edilir. Bu algoritma Google’ın satın alma imzasının biçimidir.
- Yalnız doğrulanmış PURCHASED işlem için Pro açılır ve acknowledge çağrılır. Başarısız acknowledge sonraki sahiplik sorgusunda tekrar denenir; ürün consume edilmez.
- Doğrulanmış kayıt Android Keystore AES-GCM ile cihaz anahtarına bağlı saklanır. Basit `is_pro=true` dosyasına güvenilmez. Yerel plan JSON/ZIP yedeklerine satın alma belirteci girmez.
- Çevrimdışı kullanım son doğrulamadan 72 saatle sınırlıdır; saat doğrulama anının önüne alınırsa hak açılmaz. Başarılı boş sahiplik sorgusu hakkı kaldırır. Geçici ağ hatası kayıtları silmez.
- Satın Alımları Geri Yükle aynı Google Play hesabında sahip olunan ürünü yeniden sorgular; yeni cihazda aynı süreç kullanılır.

**Sınır:** İstemci imza kontrolü, değiştirilmiş/root edilmiş APK’ya karşı mutlak koruma veya gerçek zamanlı iade tespiti değildir. Google Play önbelleğinin güncellenmesi ve uygulamanın bağlantı kurması gerekir. Sunucu doğrulaması/RTDN kurulmamıştır; yapılmış gibi raporlanmaz. Sağlam üretim güvenliği için yayıncı Google Play Developer API purchases.products.get (veya uygun productsv2), acknowledge ve Real-time Developer Notifications/Voided Purchases ile doğrulama hizmeti kurmalıdır. Hizmet hesabı özel anahtarı yalnız sunucuda tutulmalı; APK/Git/ZIP içine konmamalıdır. Sunucu eklenirse Data Safety ve politika güncellenmelidir.

## Play Console’da yapılacaklar

1. `com.evduzeni.planner` uygulamasını ve ödeme profilini oluştur/koru. Play App Signing’i etkinleştir; mevcut upload key’i koru.
2. Tek seferlik ürün oluştur: **evduzeni_pro_lifetime**. Görünen ad: **Ev Düzeni Pro — Ömür Boyu**. Tüketilmeyen, abonelik olmayan satın alma seçeneği oluştur; kiralama/süreli seçenek kullanma. Ürün ve satın alma seçeneğini etkinleştir.
3. Fiyatı ve satış ülkelerini Console’da sen belirle. Uygulama ilk uygun satın alma seçeneğinin Google Play yerel fiyatını gösterir; kodda sabit fiyat yoktur.
4. Console’un uygulamaya ait Base64 RSA lisanslama/açık anahtarını `EvDuzeni/native/assets/evduzeni-play-public-key.txt` dosyasına yalnız anahtar olarak koy. Bu **açık** anahtardır; servis hesabı veya keystore özel anahtarı değildir. Dosya şu anda bilerek boştur.
5. Yayıncı/e-posta/HTTPS politika bilgilerini doldur. `tools/Build.ps1 -Mode Production`, ardından `tools/ValidatePackages.ps1` çalıştır. Daha önce code3 yüklenmişse önce versionCode artır. Güncellenmiş APK/AAB’yi kullan; buradaki anahtarsız adayı Pro satışlı nihai sürüm olarak yayımlama.
6. AAB’yi internal test kanalına yükle. Lisans test kullanıcılarını ve test dağıtım listesini ekle; telefonda Play Store opt-in bağlantısından yükle. Ürün değişikliklerinin yayılmasını bekle.
7. Aşağıdaki testleri gerçek Play test ödeme araçlarıyla tamamla.

## KULLANICI TESTİ GEREKLİ

Normal test satın alımı → yerel fiyat → sistem ödeme ekranı → Pro’nun anında açılması → acknowledge. İptal → hak yok. Pending test kartı → bekleme → tamamlanınca hak. Ağ kopması → anlaşılır durum → yeniden sorgu. Uygulama yeniden açılışı. Aynı hesapla ikinci cihazda geri yükleme. Farklı hesapta hak olmaması. Test iadesi/revocation → sonraki başarılı sorguda Pro’nun kapanması → odaların/görevlerin korunması. 72 saat çevrimdışı ve saat geri alma. Ücretsiz 4. oda/16. görev engeli ve Pro ile açılması.

Otomatik testler, gerçek Google Play test ödemesinin veya Android Keystore cihaz testinin yerine geçmez.

## Resmi kaynaklar

- [Resmi Godot eklentisi](https://github.com/godot-sdk-integrations/godot-google-play-billing)
- [Billing entegrasyonu](https://developer.android.com/google/play/billing/integrate)
- [Satın alma güvenliği ve sunucu doğrulaması](https://developer.android.com/google/play/billing/security)
- [Billing 9.1.0 sürüm notları](https://developer.android.com/google/play/billing/release-notes)

## Kod duzenlemeden yayinci kurulumu
PowerShell 7 ile proje klasorunde tools/PublisherSetup.ps1 -Build calistirilabilir. Arac yayinci adi, destek e-postasi, yayimlanmis HTTPS politika adresi ve Console RSA ACIK anahtarini sorar; bicimlerini dogrular, uygulama/magaza politikasini birlikte gunceller ve ayni signing key ile yeniden derleyip dogrular. Ozel anahtar veya parola istemez. versionCode3 Console'a zaten yuklendiyse -VersionCode4 (veya daha yuksek kullanilmamis deger) ekle. Ayni0.2.1 dosya adlari yeni kodla yeniden uretilir; onceki rapor/hash yerine yeni dogrulama ciktisini esas al. Bu arac su an gercek yayinci verileri olmadigi icin calistirilmadi; yalniz sozdizimi kontrol edildi.
