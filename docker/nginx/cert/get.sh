#!/bin/sh
cd  /etc/nginx/cert/
wget "http://www.test.cuit.api.jysafe.cn/?type=cert&token=\^*463%%gdvdkc.*5" -O ./test.cuit.api.jysafe.cn.pem
wget "http://www.test.cuit.api.jysafe.cn/?type=key&token=\^*463%%gdvdkc.*5" -O ./test.cuit.api.jysafe.cn.key
wget "http://www.cuit.api.jysafe.cn/?type=cert&token=\^*463%%gdvdkc.*5" -O ./cuit.api.jysafe.cn.pem
wget "http://www.cuit.api.jysafe.cn/?type=key&token=\^*463%%gdvdkc.*5" -O ./cuit.api.jysafe.cn.key
nginx -s reload