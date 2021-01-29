docker pull chenxiaoyue/chester-svc-plc
docker tag chenxiaoyue/chester-svc-plc registry.cn-zhangjiakou.aliyuncs.com/chester_csy/chester-svc-plc:laster
docker login --username=18657120293 --password=tom36634938@qq registry.cn-zhangjiakou.aliyuncs.com
docker push registry.cn-zhangjiakou.aliyuncs.com/chester_csy/chester-svc-plc:laster