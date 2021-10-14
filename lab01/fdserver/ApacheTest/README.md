ab -n 1000 -c 20 http://localhost:9090/

*Finished 1000 requests*


Server Software:        nginx/1.21.3

Server Hostname:        localhost

Server Port:            9090


Document Path:          /

Document Length:        280 bytes


Concurrency Level:      20

Time taken for tests:   0.082 seconds

Complete requests:      1000

Failed requests:        0

Total transferred:      531000 bytes

HTML transferred:       280000 bytes

Requests per second:    12231.52 [#/sec] (mean)

Time per request:       1.635 [ms] (mean)

Time per request:       0.082 [ms] (mean, across all concurrent requests)

Transfer rate:          6342.71 [Kbytes/sec] received



Connection Times (ms)

min  mean[+/-sd] median   max

Connect:        0    0   0.4      0       3

Processing:     0    1   0.9      1       8

Waiting:        0    1   0.8      1       8

Total:          0    2   1.2      1       9



Percentage of the requests served within a certain time (ms)

50%      1

66%      1

75%      2

80%      2

90%      3

95%      4

98%      5

99%      7

100%      9 (longest request)