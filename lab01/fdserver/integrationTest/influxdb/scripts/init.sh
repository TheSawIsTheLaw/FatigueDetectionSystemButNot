#!/bin/bash -x

set -e
influx write \
  -b testUser \
  -o subjects \
  -p ns \
  'pulse pulse=60'
influx write \
  -b testUser \
  -o subjects \
  -p ns \
  'pulse pulse=70'
influx write \
  -b testUser \
  -o subjects \
  -p ns \
  'pulse pulse=75'
influx write \
  -b testUser \
  -o subjects \
  -p ns \
  'pulse pulse=70'
influx write \
  -b testUser \
  -o subjects \
  -p ns \
  'pulse pulse=30'
influx write \
  -b testUser \
  -o subjects \
  -p ns \
  'pulse pulse=0'
influx write \
  -b testUser \
  -o subjects \
  -p ns \
  'botArterialPressure botArterialPressure=60'
influx write \
  -b testUser \
  -o subjects \
  -p ns \
  'botArterialPressure botArterialPressure=90'
influx write \
  -b testUser \
  -o subjects \
  -p ns \
  'botArterialPressure botArterialPressure=140'
influx write \
  -b testUser \
  -o subjects \
  -p ns \
  'botArterialPressure botArterialPressure=40'
influx write \
  -b testUser \
  -o subjects \
  -p ns \
  'botArterialPressure botArterialPressure=0'