require 'digest/md5'

def convert(input)
  input
    .map(&:strip)
    .select { |line| !line.empty? }
end

PUZZLE = convert(File.readlines(File.expand_path("input.txt", __dir__)))

TESTS = [
  [
    %w(
      aba[bab]xyz
      aaa[kek]eke
      ixyxa[ryxyl]adsf
      zazbz[bzb]cdb
      xyx[xyx]xyx
      ixyxa[ryla]asdf
    ),
    4
  ],
]

VALID_REGEX = /(?:^|\])[a-z]*([a-z])([a-z])\1[a-z]*\[[a-z]*\2\1\2|\[[a-z]*([a-z])([a-z])\3[a-z]*\][a-z]*\4\3\4/
REPEATS_REGEX = /([a-z])\1{2}/

def remove_invalid_repeats(address)
  address.gsub(REPEATS_REGEX, '')
end

def valid?(address)
  VALID_REGEX =~ address
end

def solve(input)
  input
    .map(&method(:remove_invalid_repeats))
    .select(&method(:valid?))
    .size
end

def test(label, input, expected)
  $stdout.write "[#{label}]: expected #{expected}"
  actual = solve(input)
  $stdout.write ", actual #{actual.inspect}"
  if actual == expected
    $stdout.write " ✅"
  else
    $stdout.write " 💥"
  end
  puts
end

def run
  TESTS.each_with_index do |(input, expected), index|
    test("Test #{index}", input, expected)
  end
  puts "[Puzzle]: #{solve(PUZZLE)}"
end

run
