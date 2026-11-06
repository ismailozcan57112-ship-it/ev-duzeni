package com.evduzeni.planner

/**
 * Uygulama Yapılandırma ve Lansman Modu Yönetimi
 *
 * [IS_PROMOTIONAL_FREE_MODE]:
 * - true olduğunda: Lansmana özel ücretsiz deneme / önizleme sürümü modudur.
 *   Uygulamadaki tüm Pro kısıtlamaları (sınırsız oda ve görev ekleme, özel 1-365 gün tekrar aralıkları,
 *   özel saat hatırlatması) tamamen ücretsiz olarak herkese açıktır.
 *   Google Play faturalandırma (In-App Billing) çağrıları ve satın alma pencereleri devre dışıdır.
 *   Kullanıcıların ileride ücretli sürüme geçildiğinde olumsuz yorum (1 yıldız) yapmasını engellemek
 *   için uygulama içerisinde "Lansmana Özel Önizleme / Deneme Sürümü" bilgilendirmeleri gösterilir
 *   ve verilerinin asla silinmeyeceği garanti edilir.
 *
 * - false olduğunda: Normal Google Play In-App Billing / Pro kilit sistemi devrededir.
 */
object AppConfig {
    const val IS_PROMOTIONAL_FREE_MODE = true
    const val PRO_PRODUCT_ID = "evduzeni_pro_lifetime"
    const val DEFAULT_REMINDER_HOUR = 9
    const val DEFAULT_REMINDER_MINUTE = 0
}
