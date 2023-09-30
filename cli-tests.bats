#!/usr/bin/env bats
# vi: ft=bash
# test runner is https://github.com/bats-core/bats-core

# tags:
#  expand: uses expansion
#  symbolic: requires cvc5
#  target: uses --target option

# bats test_tags=symbolic,buckets
@test "full symbolic buckets" {
  result="$(./color-unfolder buckets#4#3#5 | wc -l)"
  [ "$result" -eq 442 ]
}

# bats test_tags=expand,buckets
@test "full expanded buckets" {
  result="$(./color-unfolder buckets#4#3#5 --expand | wc -l)"
  [ "$result" -eq 442 ]
}

# bats test_tags=symbolic,target,buckets
@test "reachable goal symbolic buckets" {
  ./color-unfolder buckets#4#3#5 --output=none --target=goal
}

# bats test_tags=expand,target,buckets
@test "reachable goal expanded buckets" {
  ./color-unfolder buckets#4#3#5 --output=none --target=goal --expand
}

# bats test_tags=symbolic,target,buckets
@test "unreachable goal symbolic buckets" {
  run ./color-unfolder buckets#4#3#6 --output=none --target=goal
  [ "$status" -ne 0 ]
}

# bats test_tags=expand,target,buckets
@test "unreachable goal expanded buckets" {
  run ./color-unfolder buckets#4#3#6 --output=none --target=goal --expand
  [ "$status" -ne 0 ]
}

# bats test_tags=symbolic
@test "full symbolic parallel amnesia" {
  result="$(./color-unfolder parallelAmnesia#3 | wc -l)"
  [ "$result" -eq 25 ]
}

# bats test_tags=expand
@test "full expanded parallel amnesia" {
  result="$(./color-unfolder parallelAmnesia#3 --expand-with=0..5 | wc -l)"
  [ "$result" -eq 17285 ]
}

# bats test_tags=expand
@test "full expanded parallel amnesia no jit expand" {
  result="$(./color-unfolder parallelAmnesia#3 --expand-with=0..5 --no-jit-expand | wc -l)"
  [ "$result" -eq 17285 ]
}

# bats test_tags=symbolic,target,hobbits
@test "reachable symbolic hobbitsAndOrcs#3#2#2" {
  ./color-unfolder hobbitsAndOrcs#3#2#2 --output=none --target=goal
}

# bats test_tags=expand,target,hobbits
@test "reachable expanded hobbitsAndOrcs#3#2#2" {
  ./color-unfolder hobbitsAndOrcs#3#2#2 --output=none --target=goal --expand
}

# bats test_tags=symbolic,target,hobbits
@test "unreachable symbolic hobbitsAndOrcs#4#2#2" {
  run ./color-unfolder hobbitsAndOrcs#4#2#2 --output=none --target=goal
  [ "$status" -ne 0 ]
}

# bats test_tags=expand,target,hobbits
@test "unreachable expanded hobbitsAndOrcs#4#2#2" {
  run ./color-unfolder hobbitsAndOrcs#4#2#2 --output=none --target=goal --expand
  [ "$status" -ne 0 ]
}

# bats test_tags=symbolic,target,hobbits
@test "reachable symbolic hobbitsAndOrcs#3#3#2" {
  ./color-unfolder hobbitsAndOrcs#3#3#2 --output=none --target=goal
}

# bats test_tags=expand,target,hobbits
@test "reachable expanded hobbitsAndOrcs#3#3#2" {
  ./color-unfolder hobbitsAndOrcs#3#3#2 --output=none --target=goal --expand
}

# bats test_tags=symbolic,target,hobbits
@test "reachable symbolic hobbitsAndOrcs#4#3#2" {
  ./color-unfolder hobbitsAndOrcs#4#3#2 --output=none --target=goal
}

# bats test_tags=expand,target,hobbits
@test "reachable expanded hobbitsAndOrcs#4#3#2" {
  ./color-unfolder hobbitsAndOrcs#4#3#2 --output=none --target=goal --expand
}

# bats test_tags=symbolic,target,hobbits
@test "reachable symbolic hobbitsAndOrcs#5#3#2" {
  ./color-unfolder hobbitsAndOrcs#5#3#2 --output=none --target=goal
}

# bats test_tags=expand,target,hobbits
@test "reachable expanded hobbitsAndOrcs#5#3#2" {
  ./color-unfolder hobbitsAndOrcs#5#3#2 --output=none --target=goal --expand
}

# bats test_tags=symbolic,target,hobbits
@test "unreachable symbolic hobbitsAndOrcs#6#3#2" {
  run ./color-unfolder hobbitsAndOrcs#6#3#2 --output=none --target=goal
  [ "$status" -ne 0 ]
}
# bats test_tags=expand,target,hobbits
@test "unreachable expanded hobbitsAndOrcs#6#3#2" {
  run ./color-unfolder hobbitsAndOrcs#6#3#2 --output=none --target=goal --expand
  [ "$status" -ne 0 ]
}