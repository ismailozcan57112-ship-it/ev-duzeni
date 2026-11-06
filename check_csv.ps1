$rows = Import-Csv 'e:\uygulamalar\Ev Düzeni\google_play_cevirileri.csv'
foreach ($r in $rows) {
    $tLen = $r.Title.Length
    $sLen = $r.'Short description'.Length
    $fLen = $r.'Full description'.Length
    $warn = ""
    if ($tLen -gt 30) { $warn += " TITLE > 30!" }
    if ($sLen -gt 80) { $warn += " SHORT > 80!" }
    if ($fLen -gt 4000) { $warn += " FULL > 4000!" }
    Write-Host "$($r.Language): Title=$tLen, Short=$sLen, Full=$fLen $warn"
}
