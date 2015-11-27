java -jar .\signapk.jar -w .\platform.x509.pem  .\platform.pk8 ..\bin\AutoFillSetting.apk  ..\bin\AutoFillSetting2.apk
move ..\bin\AutoFillSetting.apk ..\bin\AutoFillSetting_old.apk 
move ..\bin\AutoFillSetting2.apk ..\bin\AutoFillSetting.apk 
