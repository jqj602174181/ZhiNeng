java -jar .\signapk.jar -w .\platform.x509.pem  .\platform.pk8 ..\bin\Update.apk  ..\bin\Update2.apk
move ..\bin\Update.apk ..\bin\Update_old.apk 
move ..\bin\Update2.apk ..\bin\Update.apk 
