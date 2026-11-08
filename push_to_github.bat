@echo off
chcp 65001 >nul
cd /d "E:\uygulamalar\Ev Düzeni"
if not exist .git (
    echo Hata: .git klasoru bulunamadi!
    pause
    exit /b 1
)
echo ========================================================
echo Kodlar GitHub'a gonderiliyor...
echo.
echo NOT: Eger asagida 8 haneli bir kod (ornek: ABCD-1234)
echo yazarsa, tarayicindaki kutucuga o kodu girip onayla.
echo ========================================================
echo.
git push -u origin main
echo.
echo ========================================================
echo Islem tamamlandi! Bu pencereyi kapatabilirsin.
echo ========================================================
pause
