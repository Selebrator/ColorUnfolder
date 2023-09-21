#!/usr/bin/env bats
# vi: ft=bash
# test runner is https://github.com/bats-core/bats-core

@test "full symbolic bucktes" {
  result="$(./color-unfolder buckets#4#3#5 | wc -l)"
  [ "$result" -eq 442 ]
}

@test "full expanded bucktes" {
  result="$(./color-unfolder buckets#4#3#5 --expand=0..8 | wc -l)"
  [ "$result" -eq 442 ]
}

@test "reachable goal symbolic bucktes" {
  ./color-unfolder buckets#4#3#5 --output=none --target=goal
}

@test "reachable goal expanded bucktes" {
  ./color-unfolder buckets#4#3#5 --output=none --target=goal --expand=0..8
}

@test "unreachable goal symbolic bucktes" {
  run ./color-unfolder buckets#4#3#6 --output=none --target=goal
  [ "$status" -ne 0 ]
}

@test "unreachable goal expanded bucktes" {
  run ./color-unfolder buckets#4#3#6 --output=none --target=goal --expand=0..8
  [ "$status" -ne 0 ]
}

@test "full symbolic parallel amnesia" {
  result="$(./color-unfolder parallelAmnesia#3 | wc -l)"
  [ "$result" -eq 25 ]
}

@test "full expanded parallel amnesia" {
  result="$(./color-unfolder parallelAmnesia#3 --expand=0..5 | wc -l)"
  [ "$result" -eq 17285 ]
}

@test "full expanded parallel amnesia no jit expand" {
  result="$(./color-unfolder parallelAmnesia#3 --expand=0..5 --jit-expand=false | wc -l)"
  [ "$result" -eq 17285 ]
}
