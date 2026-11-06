# Fiziksel Android cihaz test listesi

Durum: Otomatik GDScript ve ekran testleri yapıldı. Aşağıdaki gerçek Android etkileşimleri cihaz olmadan tamamlanmış sayılmaz.

## Önce veriyi koru

Eski test uygulamasını kaldırmadan yeni **test APK** dosyasını güncelleme olarak kur. Paket adı ve debug imzası aynı tutulur. **Release APK farklı yayın anahtarıyla imzalanır**; test APK üstüne kurulması beklenmez. Release denemesinden önce planını dışa aktar ve güvenli bir kopyasını sakla.

## Temel akış

1. İlk açılışta rehberi tamamla veya atla; yeniden açınca tekrar zorunlu gelmediğini kontrol et.
2. Oda ekle, adını değiştir, odaya görev ekle.
3. Görevi düzenle; silme onayında vazgeç ve sonra silmeyi onayla.
4. 1, 3, 7 ve özel 9 gün aralıklı görevler oluştur. Bugün tamamla; sonraki tarihleri kontrol et.
5. Tamamla, uygulamayı kapat/aç, geri al. Önceki tarih ve tamamlanma bilgisinin geri geldiğini kontrol et.
6. Uygulamayı kapat/aç; oda, görev, tarih, ayar ve durumların kaldığını gör.
7. Telefon tarihini ertesi güne ve geriye al; Bugün ekranını kontrol et. Test sonunda otomatik tarih ayarını geri aç.
8. Görev yazarken tarih değiştir; yazının kaybolmadığını kontrol et.

## Bildirim

1. Android 13 ve üzeri: hatırlatmayı aç; izin isteğini reddet. Planlayıcı çalışmaya devam etmeli ve izin durumu açıklanmalı.
2. İzni Android ayarlarından aç; Ayarlar'daki deneme bildirimini gönder.
3. Bildirime dokun; uygulama açılmalı.
4. Hatırlatma saatini ilerideki bir zamana ayarla, uygulamayı normal kapat. Bekleyen iş varsa günlük bildirim gelmeli. İnexact alarm olduğundan tam dakika garantisi yoktur.
5. O günkü tüm işleri bitir; aktif günlük bildirimin kalktığını, sonraki boş günde bildirim gelmediğini kontrol et.
6. Telefonu yeniden başlatıp kilidini aç; sonraki hatırlatma yeniden kurulmalı.
7. Saat dilimini/tarihi değiştir; bildirim yeni yerel saate göre kurulmalı.
8. Hatırlatmayı kapat; günlük alarm ve aktif günlük bildirim iptal olmalı.

Android'de **Zorla durdur** sonrası sistem, uygulama yeniden açılana kadar arka plan alarmını engelleyebilir. Pil tasarrufu veya üreticinin arka plan kısıtlamaları bildirimi geciktirebilir. Bu davranışlar gerçek cihazda ayrıca gözlenmelidir.

## Yedek ve ekran

1. Yedeği dosya seçiciden kaydet; yeniden içe aktar, önizlemeyi gör, önce vazgeç sonra onayla.
2. Geçersiz dosya seç; mevcut plan değişmemeli.
3. Android klavyesi açıkken uzun görev adı yaz, formu kaydır ve kaydet.
4. Geri tuşu önce klavyeyi/formu kapatmalı; Odalar/Planım'dan Bugün'e dönmeli; kökte çıkmalı.
5. Küçük telefon, büyük telefon ve tablette yazı/düğme taşmasını, kaydırmayı ve ekran çentiğini kontrol et.

Kaynaklar: [Android bildirim izni](https://developer.android.com/develop/ui/views/notifications/notification-permission), [Android alarm davranışı](https://developer.android.com/develop/background-work/services/alarms).

## 0.2.1 ek kontrolleri

- Temiz kurulumda son onboarding sayfasında bildirim açıklaması ve “Bildirimlere izin ver” görülür. Android13+ gerçek sistem penceresi açılmalı. İzin ver → Ayarlar’a gitmeden09:00 etkin. Reddet → normal kullanım ve kendiliğinden tekrar istememe. Kalıcı ret → yalnız isteğe bağlı ayarlar yönlendirmesi. Android12 ve altında da kanal kapalı/açık durumlarını kontrol et.
- İş olan gün09:00 civarı tek bildirim, metinde doğru iş sayısı. Boş plan ve tümü bitmiş durumda bildirim yok. Yeniden başlatma, tarih/saat dilimi değişimi, görev düzenleme ve bildirim saat değişimi sonrası kontrol et. Force-stop Android tarafından alarmı durdurabilir; yeniden açılana kadar bildirim beklenmez.
- Küçük telefonda en son karta, boş alan/kart/düğme üzerinden sürükleyerek ulaş. Dikey çubuk görünmemeli; yanlış tamamlanma/açılan form olmamalı. Alt menü sabit. Notch ve üç düğmeli/gesture navigation, büyük yazı/DPI, klavye açık formlar ve geri tuşu ayrı denenmeli.
- Free4.oda/16.görev temiz Pro ekranı; var olan fazla kayıt silinmemeli. Özel tekrar oluşturma Free’de engelli; önceki özel görevi düzenleme ve tamamlama korunmalı.
- Gerçek ödeme testlerinin tamamı PRO-VE-REKLAM-MODELI.md içindedir. Yerel test-double başarıları gerçek Play ödemesi değildir.
