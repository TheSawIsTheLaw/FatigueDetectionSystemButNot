ab -n 3000 -c 100 http://localhost:9090/            ✔  04:57:08  

This is ApacheBench, Version 2.3 <$Revision: 1879490 $>

Copyright 1996 Adam Twiss, Zeus Technology Ltd, http://www.zeustech.net/

Licensed to The Apache Software Foundation, http://www.apache.org/



Benchmarking localhost (be patient)

Completed 300 requests

Completed 600 requests

Completed 900 requests

Completed 1200 requests

Completed 1500 requests

Completed 1800 requests

Completed 2100 requests

Completed 2400 requests

Completed 2700 requests

Completed 3000 requests

Finished 3000 requests





Server Software:        nginx/1.21.3

Server Hostname:        localhost

Server Port:            9090



Document Path:          /

Document Length:        280 bytes



Concurrency Level:      100

Time taken for tests:   0.208 seconds

Complete requests:      3000

Failed requests:        0

Total transferred:      1593000 bytes

HTML transferred:       840000 bytes

Requests per second:    14433.97 [#/sec] (mean)

Time per request:       6.928 [ms] (mean)

Time per request:       0.069 [ms] (mean, across all concurrent requests)

Transfer rate:          7484.80 [Kbytes/sec] received



Connection Times (ms)

              min  mean[+/-sd] median   max

Connect:        0    1   0.6      2       3

Processing:     1    5   5.6      3      43

Waiting:        1    5   5.6      3      43

Total:          1    7   5.6      5      45

WARNING: The median and mean for the initial connection time are not within a normal deviation

        These results are probably not that reliable.



Percentage of the requests served within a certain time (ms)

  50%      5

  66%      5

  75%      6

  80%      6

  90%     12

  95%     22

  98%     26

  99%     26

 100%     45 (longest request)