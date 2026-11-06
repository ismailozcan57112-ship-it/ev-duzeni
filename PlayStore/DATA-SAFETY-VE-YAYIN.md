# Ev Düzeni 0.2.1 — Data Safety ve yayın beyanları

Paket: com.evduzeni.planner. Bu belge 0.2.0’ın “ödeme SDK’sı ve internet izni yok” beyanının yerine geçer.

## Veri akışları

| Veri | Cihazda işlenir | Cihaz dışına gönderim / paylaşım | Silme |
|---|---|---|---|
| Oda/görev adları, tekrar, tarihler, geçmiş, tercihler | Evet | Uygulama tarafından gönderilmez/paylaşılmaz | Uygulamadan silme; tüm eski kopyalar için Android verileri temizle |
| Bildirim izni ve saati | Evet | Hatırlatma yereldir; izin veri toplama değildir | Ayarlardan kapatma / verileri temizleme |
| Kullanıcının seçtiği JSON yedeği | Evet | Yalnız kullanıcının seçtiği konum; bulut seçilirse o hizmet | Dosyayı kullanıcı ayrıca siler |
| Pro ürün kimliği, satın alma belirteci, imza, işlem durumu | Evet; doğrulanmış kayıt şifreli | Google Play sahiplik ve onay işlemleri; yayıncı sunucusu yok | Yerel kayıt uygulama verileriyle silinir; Google işlem kayıtları Google kurallarına tabi |
| Kart numarası / ödeme aracı ayrıntısı | Uygulama almaz | Google Play ödeme arayüzü işler | Google hesap/ödeme kontrolleri |
| Billing teknik işlem tanılaması | Google Billing bileşeni | Google hizmetleri; ayrıca analytics/reklam SDK’sı yok | Google hizmet politikaları |

## Form için hazırlanmış açıklama

“Temizlik planı, odalar, görevler ve tercihler cihazda saklanır; yayıncı sunucusuna aktarılmaz. Kullanıcı hesabı ve reklam yoktur. Bildirimler yereldir. İsteğe bağlı Pro satın alımları Google Play tarafından işlenir. Uygulama kart bilgilerini almaz. Satın alma sahipliği Google Play’den sorgulanır ve doğrulanmış işlem kaydı cihazda şifreli tutulur. Kullanıcı yerel planı ve kayıtları Android uygulama verilerini temizleyerek silebilir; dışa aktardığı yedekleri ayrıca silmelidir.”

## Console cevap rehberi

- Reklam içeriyor mu? **Hayır**.
- Hesap oluşturma var mı? **Hayır**. Yayıncıya ait hesap silme sitesi gerektiğini varsayma.
- Temizlik görevi/oda içeriği toplanıyor/paylaşılıyor mu? **Hayır**, sadece cihazda işleniyor. Yedek dışa aktarma kullanıcı tarafından başlatılır.
- Uygulama ödeme kartı bilgilerini topluyor mu? **Hayır**. Google Play’in ayrı ödeme işleme verisini, uygulamanın kart toplaması gibi beyan etme.
- Satın alma geçmişi: Google Play üzerinden isteğe bağlı satın alma/sahiplik işleme var. Amaç: uygulama işlevselliği ve dolandırıcılık önleme. Uygulama bu bilgiyi kendi sunucusuna göndermiyor. Google’ın ödeme hizmeti istisnasını ve mevcut SDK beyanını Console’da kontrol ederek kapsamı kesinleştir.
- Paylaşım: yayıncı reklam/analitik amaçlı üçüncü tarafa plan veya satın alma kaydı aktarmıyor. Google ödeme hizmeti ve kullanıcı başlatmalı yedek istisnaları ayrıca değerlendirilir.
- Aktarım güvenliği: uygulamada yayıncı HTTP uç noktası yok; satın alma iletişimi Google Play üzerinden. Gelecekte doğrulama sunucusu kurulursa HTTPS zorunlu olmalı.
- Silme: yerel tüm veriler Android ayarlarından silinebilir. Yayıncı hesabı yok. Google’ın işlem kayıtları için uygulama “her şeyi Google’dan da siler” iddiasında bulunmaz.

**Son kontrol gerekli:** Billing 9.1.0 pakette etkin olduğundan eski “hiçbir kullanıcı verisi toplanmaz” toplu yanıtını körlemesine kullanma. SDK’nın teknik tanılama alanları ve Google ödeme hizmeti istisnasının bu dağıtım için kapsamı yayıncı tarafından Console SDK beyanı ile kontrol edilmelidir. Bu oturumda gerçek cihaz ağ trafiği ve Console SDK Data Safety ekranı doğrulanamadı; bunlar kesinleşmeden formun tüm kutuları onaylanmış sayılmaz. Ayrı crash/analytics/reklam SDK’sı eklenmemiştir.

## Yayın öncesi

[YAYINCI ADI], [DESTEK E-POSTASI], [GİZLİLİK POLİTİKASI HTTPS URL] alanlarını doldur. Politikanın uygulamadaki kaynağı `EvDuzeni/assets/privacy.txt` dosyasıdır; mağaza politikasıyla aynı tutulmalıdır. HTTPS sayfası herkese açık, oturum açmadan erişilebilir olmalıdır. Pro kurulumu için PRO-VE-REKLAM-MODELI.md belgesindeki açık anahtar/ürün/test adımları tamamlanmalıdır.

Target SDK 36, Eylül 2026 yeni uygulama/güncelleme gereksinimine uygundur. minSdk24 ve arm64-v8a korunmuştur. Yeni kişisel hesaplarda Console gerekli görürse en az 12 test kullanıcısıyla 14 gün kesintisiz kapalı test ve üretim erişim başvurusu yapılır. Hedef kitle, içerik derecelendirmesi, ülke/fiyat, geliştirici kimliği ve ödeme profili yayıncı tarafından doğru bilgilerle doldurulur.

## Resmi dayanaklar

- [Veri Güvenliği formu](https://support.google.com/googleplay/android-developer/answer/10787469?hl=en)
- [SDK verisinin beyanı](https://developer.android.com/privacy-and-security/declare-data-use)
- [Ödeme hizmeti istisnası](https://developer.android.com/about/versions/14/features/app-metadata)
- [Target API gereksinimi](https://support.google.com/googleplay/android-developer/answer/11926878?hl=en)
- [Yeni kişisel hesap testleri](https://support.google.com/googleplay/android-developer/answer/14151465?hl=en)
