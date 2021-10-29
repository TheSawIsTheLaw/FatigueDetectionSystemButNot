# На один сервер
Итог запуска 30000 запросов с 1000 конкурирующих.
```
Server Software:        
Server Hostname:        localhost
Server Port:            8080

Document Path:          /
Document Length:        141 bytes

Concurrency Level:      1000
Time taken for tests:   3.625 seconds
Complete requests:      30000
Failed requests:        0
Non-2xx responses:      30000
Total transferred:      14430000 bytes
HTML transferred:       4230000 bytes
Requests per second:    8275.36 [#/sec] (mean)
Time per request:       120.841 [ms] (mean)
Time per request:       0.121 [ms] (mean, across all concurrent requests)
Transfer rate:          3887.15 [Kbytes/sec] received

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0   54 254.0      3    3189
Processing:     3   57  42.1     45     360
Waiting:        3   53  40.9     41     353
Total:          5  111 257.5     52    3312

Percentage of the requests served within a certain time (ms)
  50%     52
  66%     66
  75%     79
  80%     93
  90%    146
  95%    225
  98%   1061
  99%   1094
 100%   3312 (longest request)
```

# На 3 сервера
Круто. Сработал DDoSDeflate. Видимо. Я не знаю. За всё это время ничего, ни 
единое приложение нормально не заработало. Я сдаюсь.
```
Benchmarking localhost (be patient)
apr_socket_recv: Connection reset by peer (104)
Total of 491 requests completed
```