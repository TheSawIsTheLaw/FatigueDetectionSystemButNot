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
Connect:        0   54 254.0      3    3189 Most typically the network latency
Processing:     3   57  42.1     45     360 Time to receive full response after connection was opened
Waiting:        3   53  40.9     41     353 Time-to-first-byte after the request was sent
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

Если рассматриваем 100 конкурирующих:
```
Server Software:        
Server Hostname:        localhost
Server Port:            8080

Document Path:          /
Document Length:        141 bytes

Concurrency Level:      100
Time taken for tests:   4.495 seconds
Complete requests:      30000
Failed requests:        0
Non-2xx responses:      30000
Total transferred:      14430000 bytes
HTML transferred:       4230000 bytes
Requests per second:    6674.08 [#/sec] (mean)
Time per request:       14.983 [ms] (mean)
Time per request:       0.150 [ms] (mean, across all concurrent requests)
Transfer rate:          3134.99 [Kbytes/sec] received

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0    1   1.1      0      15
Processing:     1   14  12.5     11     160
Waiting:        0   13  12.0     10     158
Total:          1   14  12.4     11     160

Percentage of the requests served within a certain time (ms)
  50%     11
  66%     14
  75%     15
  80%     17
  90%     22
  95%     33
  98%     52
  99%     76
 100%    160 (longest request)
```

# На 3 сервера
Круто. Сработал DDoSDeflate. Видимо. Я не знаю. За всё это время ничего, ни 
единое приложение нормально не заработало. Я сдаюсь.
```
Benchmarking localhost (be patient)
apr_socket_recv: Connection reset by peer (104)
Total of 491 requests completed
```

Если рассматриваем 100 конкурирующих:
```
Server Software:        nginx/1.21.3
Server Hostname:        localhost
Server Port:            9090

Document Path:          /
Document Length:        280 bytes

Concurrency Level:      100
Time taken for tests:   1.795 seconds
Complete requests:      30000
Failed requests:        0
Total transferred:      15930000 bytes
HTML transferred:       8400000 bytes
Requests per second:    16715.12 [#/sec] (mean)
Time per request:       5.983 [ms] (mean)
Time per request:       0.060 [ms] (mean, across all concurrent requests)
Transfer rate:          8667.70 [Kbytes/sec] received

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0    2   0.7      2       9
Processing:     0    4   2.3      4      43
Waiting:        0    3   2.1      3      38
Total:          1    6   2.2      6      45

Percentage of the requests served within a certain time (ms)
  50%      6
  66%      6
  75%      6
  80%      6
  90%      7
  95%      8
  98%     10
  99%     16
 100%     45 (longest request)
```

Видно, что сильно вырастает количество обрабатываемых в секунду запросов.
Время на обработку одного реквеста в среднем уменьшилось в 3 раза.
