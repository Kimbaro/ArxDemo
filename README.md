###인증서 발급 관련(자체공인발급, 개인인증서발급, jks발급)
1) sudo openssl genrsa -out ca.key 2048
2) sudo openssl req -new -x509 -days 365 -key ca.key -out ca.crt
3) sudo openssl req -new -newkey rsa:2048 -nodes -keyout server.key -out server.csr
4) sudo -S openssl x509 -req -in server.csr -extfile test.ext -CA ca.crt -CAkey ca.key -CAcreateserial -out server.crt -days 365
5) sudo -S openssl verify -CAfile ca.crt server.crt
6) openssl pkcs12 -export -in server.crt -inkey server.key -out keystore.p12 -name "sampleAlias"
7) keytool -importkeystore -srckeystore keystore.p12 -srcstoretype pkcs12 -destkeystore keystore.jks